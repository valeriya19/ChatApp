import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * @author valeriya19
 */
class MainForm extends JFrame {
    private JPanel rootPanel;
    private JButton connect,
                    disconnect,
                    buttonAddFriends,
		    buttonRemoveFriends,
                    buttonChangeLocalNick,
                    sendButton;
    private JTextField textFieldIp,
                       textFieldNick,
                       textFieldLocalNick,
                       myText;
    private JTable tableFriends;
    private JTextArea messageHistory;

    private HistoryModel messageContainer;

    private final Observer historyViewObserver;
    
    private Application logicModel;

    public MainForm(Application logic) {
        super();
	logicModel = logic;
        setContentPane(rootPanel);
        setSize(700, 500);

        connect.setEnabled(false);
        disconnect.setEnabled(false);
        textFieldIp.setEnabled(false);
        textFieldNick.setEnabled(false);
        tableFriends.setEnabled(false);
        buttonAddFriends.setEnabled(false);
      	buttonRemoveFriends.setEnabled(false);
        myText.setEnabled(false);
        sendButton.setEnabled(false);
	
        tableFriends.setModel(logicModel.getContactModel());
        tableFriends.setAutoscrolls(true);

        messageHistory.setAutoscrolls(true);
	messageContainer = logicModel.getMessageHistoryModel();

	historyViewObserver = new Observer() {
	  @Override
	  public void update(Observable o, Object arg) {
	    if (((Vector<String>) arg).isEmpty())
	      messageHistory.setText("");
	    else {
	      if (! messageHistory.getText().isEmpty())
		messageHistory.append(Protocol.endOfLine);
	      HistoryModel.Message msgData = messageContainer.getMessage(messageContainer.getSize() - 1);
	      messageHistory.append(msgData.getNick() + ". " + msgData.getDate().toString() + "." + Protocol.endOfLine + msgData.getText() + Protocol.endOfLine);
	    }
	  }
	};
	messageContainer.addObserver(historyViewObserver);
	
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fIP = textFieldIp.getText();
		logicModel.makeOutcomingCall(fIP);
            }
        });

	disconnect.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    blockDialogComponents(true);
	    logicModel.finishCall();
	    blockRemoteUserInfo(false);
	  }
	});

	sendButton.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    String text = myText.getText();
	    if (! text.isEmpty()) {
	      logicModel.sendMessage(text);
	      logicModel.addMessage(logicModel.getLocalNick(), text);
	      myText.setText("");
	    }
	  }
	});

	//Change local nick and activate next field
	buttonChangeLocalNick.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    String fln = textFieldLocalNick.getText();
	    logicModel.applyLocalNick(fln);
	    if (fln.isEmpty()) {
	      textFieldLocalNick.setText(Protocol.defaultLocalNick);
	    }
	    connect.setEnabled(true);
	    buttonAddFriends.setEnabled(true);
	    buttonRemoveFriends.setEnabled(true);
	    textFieldIp.setEnabled(true);
	    tableFriends.setEnabled(true);
	    textFieldLocalNick.setEnabled(false);
	    buttonChangeLocalNick.setEnabled(false);
	  }
	});

      	//dialog when we want to close the program
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {
                Object[] option = {"Yes", "No"};
                int n = JOptionPane.showOptionDialog(e.getComponent(), "Do you really want to exit?", "Close window?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
                if (n == 0) {
                    logicModel.saveContactsToFile();
		    e.getWindow().setVisible(false);
                    System.exit(0);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {}

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });

        //add user to friend list
        buttonAddFriends.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
		if (! ((textFieldIp.getText().isEmpty()) || (textFieldNick.getText().isEmpty())))
		    logicModel.addContact(textFieldNick.getText(), textFieldIp.getText());
            }
        });

	buttonRemoveFriends.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	      int sr = tableFriends.getSelectedRow();
	      if (sr >= 0) {
		logicModel.removeContact(sr);
		tableFriends.clearSelection();
	      }
	    }
	});

        //copy info from friend list to our textField
        tableFriends.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tableFriends.getSelectedRow() > 0) {
                    String nick = tableFriends.getModel().getValueAt(tableFriends.getSelectedRow(), 0).toString();
                    String ip = tableFriends.getModel().getValueAt(tableFriends.getSelectedRow(), 1).toString();
                    textFieldNick.setText(nick);
                    textFieldIp.setText(ip);
                }
            }
        });

        //deleting user from friend list
        tableFriends.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 127) //delete
                    buttonRemoveFriends.doClick();
            }
        });

        //Обработка клавиши Enter в поле ввода локального ника
        textFieldLocalNick.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) //enter
                    buttonChangeLocalNick.doClick();
            }
        });

        myText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) //enter
                    sendButton.doClick();
            }
        });
	
	textFieldIp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) //enter
                    connect.doClick();
            }
        });
    }

    public void showIncomingCallDialog(String nick, String IP) {
	Object[] option = {"Connect", "Disconnect"};
	int n = JOptionPane.showOptionDialog(this, "User " + nick + " from address " + IP + " wants to chat with you", "New connection", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
	if (n == 0) {
	  logicModel.acceptIncomingCall();
	  textFieldNick.setText(nick);
	  textFieldIp.setText(IP);
	  blockRemoteUserInfo(true);
	  blockDialogComponents(false);
	} else {
	  logicModel.rejectIncomingCall();
	}
    }
    
    public void showCallFinishDialog() {
	blockDialogComponents(true);
	JOptionPane.showMessageDialog(this, "Remote user disconnected", "Finished connection", JOptionPane.INFORMATION_MESSAGE);
        blockRemoteUserInfo(false);
    }
    
    public void showCallRetryDialog() {
	Object[] option = {"Yes", "No"};
	int n = JOptionPane.showOptionDialog(this, "Remote user is busy. Try again?", "Busy", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
	if (n == 0) {
	    connect.doClick();
	}
	else {
	    blockRemoteUserInfo(false);
	}
    }
    
    public void showRecallDialog() {
      Object[] option = {"Recall", "Cancel"};
      int n = JOptionPane.showOptionDialog(this, "Remote user cancelled the connection", "Cancelled connection", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
      if (n == 0)
	  connect.doClick();
      else {
	  blockRemoteUserInfo(false);
      }
    }

    public void showNoConnectionDialog() {
      JOptionPane.showMessageDialog(this, "Cannot connect", "Unsuccessful connection", JOptionPane.INFORMATION_MESSAGE);
      blockRemoteUserInfo(false);
    }
    
    public void blockDialogComponents(boolean blockingFlag) {
        disconnect.setEnabled(! blockingFlag);
        connect.setEnabled(blockingFlag);
        myText.setEnabled(! blockingFlag);
        sendButton.setEnabled(! blockingFlag);
	if (! blockingFlag)
	  messageContainer.clear();
        messageHistory.setEnabled(! blockingFlag);
    }
    
    public void blockRemoteUserInfo(boolean blockingFlag) {
	textFieldIp.setEnabled(! blockingFlag);
    }
    
    public void showRemoteNick(String nick) {
	textFieldNick.setText(nick);
    }
}
