import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Application {
  public static ChatForm form;
  private String localNick;
  private Vector<Vector<String>> friends;
  private Vector<String> header;
  private ContactTableModel contactModel;
  private Connection serverConnection;
  private Connection clientConnection;
  private Caller caller;
  private CallListener callListener;
  private CommandListenerThread commandListenerServer;
  private CommandListenerThread commandListenerClient;
  private ServerConnection c;
  private static enum Status {BUSY, SERVER_NOT_STARTED, OK, CLIENT_CONNECTED, REQUEST_FOR_CONNECT};
  private static enum ConnectionStatus {AS_SERVER, AS_CLIENT, AS_NULL};
  private final Observer clientObserver, serverObserver;
  private HistoryModel messageContainer;
  private ConnectionStatus currentSuccessConnection = ConnectionStatus.AS_NULL;
  private Status status;

  public Application() {
    clientObserver = new Observer() {
      @Override
      public void update(Observable o, Object arg) {
	  if (((Command) arg).getType() == Command.CommandType.NICK) {
	      //System.out.println("Nick is coming");
	      if (o instanceof CommandListenerThread)
		try {
		  clientConnection.accept();
		} catch (IOException e1) {
		  e1.printStackTrace();
		}
	  } else if (((Command) arg).getType() == Command.CommandType.ACCEPT) {
	      //System.out.println("Accept is coming");
	      if (o instanceof CommandListenerThread) {
		form.acceptedCall();
	      }
	  } else if (((Command) arg).getType() == Command.CommandType.REJECT) {
	      //System.out.println("Reject is coming");
	      if (o instanceof CommandListenerThread) {
		currentSuccessConnection = ConnectionStatus.AS_NULL;
		form.showCallRetryDialog();
	      }
	  } else if (((Command) arg).getType() == Command.CommandType.MESSAGE) {
	      //System.out.println("Message is coming");
	      if (o instanceof CommandListenerThread) {
		addMessage(((MessageCommand) arg).getMessage());
	      }
	  } else if (((Command) arg).getType() == Command.CommandType.DISCONNECT) {
	      //System.out.println("Disconnect is coming");
	      if (o instanceof CommandListenerThread) {
		status=Status.OK;
		form.showCallFinishDialog();
	      }
	  }
      }
    };
    
    serverObserver = new Observer() {
      @Override
      public void update(Observable o, Object arg) {
	if (((Command) arg).getType() == Command.CommandType.NICK) {
	    //System.out.println("Nick is coming");
	    if (o instanceof CommandListenerThread) {
	      if (status == Status.BUSY)
		try {
		  serverConnection.sendNickBusy(localNick);
		} catch (IOException e1) {
		  e1.printStackTrace();
		}
	      else
		try {
		  serverConnection.sendNickHello(localNick);
		} catch (IOException e1) {
		  e1.printStackTrace();
		}
	    }
	} else if (((Command) arg).getType() == Command.CommandType.ACCEPT) {
	    //System.out.println("Accept is coming");
	    if (o instanceof CommandListenerThread) {
	      form.showIncomingCallDialog(callListener.getRemoteNick(), ((InetSocketAddress) callListener.getRemoteAddress()).getHostString());
	    }
	} else if (((Command) arg).getType() == Command.CommandType.REJECT) {
	    //System.out.println("Reject is coming");
	    if (o instanceof CommandListenerThread) {
	      form.rejectedCall();
	    }
	} else if (((Command) arg).getType() == Command.CommandType.MESSAGE) {
	    //System.out.println("Message is coming");
	    if (o instanceof CommandListenerThread) {
	      addMessage(((MessageCommand) arg).getMessage());
	    }
	} else if (((Command) arg).getType() == Command.CommandType.DISCONNECT) {
	    //System.out.println("Disconnect is coming");
	    if (o instanceof CommandListenerThread) {
	      try {
		serverConnection.close();
	      } catch (IOException e1) {
		e1.printStackTrace();
	      }
	    }
	}
      }
    };
    header = new Vector<String>(2);
    header.add("Nick");
    header.add("IP");
    friends = new Vector<Vector<String>>();
    
    form = new ChatForm(this);
  }
  
  public String getLocalNick() {
    return localNick;
  }
  
  public void acceptIncomingCall() {
    try {
      serverConnection.accept();
      status = Status.BUSY;
      currentSuccessConnection = ConnectionStatus.AS_SERVER;
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
  
  public void rejectIncomingCall() {
    try {
      serverConnection.reject();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
  
  public void applyLocalNick(String newNick) {
    if (newNick.isEmpty())
      localNick = Protocol.defaultLocalNick;
    else
      localNick = newNick;
    startListeningForCalls();
  }
  
  public void startListeningForCalls() {
    status = Status.SERVER_NOT_STARTED;
    new Thread (new Runnable() {
      @Override
      public void run() {
	try {
	    if (callListener==null)
		callListener = new CallListener();
	    callListener.setLocalNick(localNick);
	    serverConnection = callListener.getConnection();
	    serverConnection.sendNickHello(localNick);

	    commandListenerServer = new CommandListenerThread(serverConnection);
	    commandListenerServer.addObserver(serverObserver);
	    commandListenerServer.start();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

      }
    }).start();
    status = Status.OK;
    while (contactModel.getRowCount()>0)
      contactModel.removeRow(0);
    c = new ServerConnection(null,localNick);
    c.setServerAddress("jdbc:mysql://files.litvinov.in.ua/chatapp_server?characterEncoding=utf-8&useUnicode=true");
    c.connect();
    String[] nicknames = c.getAllNicks();
    for (String nick:nicknames) {
	Vector<String> row = new Vector<String>(2);
	row.add(nick);
	row.add(c.getIpForNick(nick));
	contactModel.addRow(row);
    }
    c.goOnline();
  }
  
  public void finishCall() {
    try {
      //System.out.println(currentSuccessConnection);
      if (currentSuccessConnection == ConnectionStatus.AS_SERVER) {
	serverConnection.disconnect();
	serverConnection.close();
	commandListenerServer.stop();
	commandListenerServer.deleteObservers();
      } else
	if (currentSuccessConnection == ConnectionStatus.AS_SERVER) {
	  clientConnection.disconnect();
	  clientConnection.close();
	  commandListenerClient.stop();
	  commandListenerClient.deleteObservers();
	}
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
  
  public void sendMessage(String text) {
    try {
      if (currentSuccessConnection == ConnectionStatus.AS_SERVER) {
	serverConnection.sendMessage(text + "\n");
      } else 
	if (currentSuccessConnection == ConnectionStatus.AS_CLIENT) {
	  clientConnection.sendMessage(text + "\n");
      }
      addMessage(text);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
  
  public void addContact(String newNick, String newIP) {
    Vector<String> nc = new Vector<String>(2);
    nc.add(newNick);
    nc.add(newIP);
    contactModel.addRow(nc);
  }
  
  public void removeContact(int pos) {
    if (pos >= 0)
      contactModel.removeRow(pos);
  }
  
  public void loadContactsFromFile() {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader("friendList.chat"))) {
      while (bufferedReader.ready()) {
	Vector<String> tmp = new Vector<String>();
	String nick = bufferedReader.readLine();
	String ip = bufferedReader.readLine();
	tmp.add(nick);
	tmp.add(ip);
	friends.add(tmp);
      }
    } catch (FileNotFoundException e) {
	//System.out.println("File Not Found");
    } catch (IOException e) {
	e.printStackTrace();
	//System.out.println("Error in reading file");
    }
    contactModel = new ContactTableModel(header, friends);
  }
  
  public void saveContactsToFile() {
    try (FileWriter fileWriter = new FileWriter("friendList.chat")) {
      for (int i = 1; i < contactModel.getRowCount(); i++) {
	  fileWriter.write(contactModel.getValueAt(i, 0).toString() + "\n");
	  fileWriter.write(contactModel.getValueAt(i, 1).toString() + "\n");
      }
    } catch (IOException e1) {
	e1.printStackTrace();
    }
    c.goOffline();
  }
  
  public void makeOutcomingCall(String remoteIP) {
    new Thread(new Runnable() {
      @Override
      public void run() {
	  caller = new Caller(localNick, remoteIP);
	  try {
	      clientConnection = caller.call();
	      commandListenerClient = new CommandListenerThread(clientConnection);
	      clientConnection.sendNickHello(localNick);
	  } catch (IOException e1) {
	      e1.printStackTrace();
	  }
	  commandListenerClient.addObserver(clientObserver);

	  currentSuccessConnection = ConnectionStatus.AS_CLIENT;
	  commandListenerClient.start();
      }
    }).start();
    status = Status.REQUEST_FOR_CONNECT;
  }
  
  private void addMessage(String msgText) {
    if (msgText != null) {
      messageContainer.addMessage(localNick, new Date(System.currentTimeMillis()), msgText);
    }
  }
  
  public ContactTableModel getContactModel() {
    return contactModel;
  }
  
  public HistoryModel getMessageHistoryModel() {
    return messageContainer;
  }

  public static void main(String[] args) {
    Application chatApp = new Application();
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
	public void run() {
	  form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  form.setVisible(true);
	}
      });
    } catch (InterruptedException | InvocationTargetException ex) {
      System.exit(1);
    }
  }
}
