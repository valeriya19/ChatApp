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
        myText.setEnabled(false);
        sendButton.setEnabled(false);
	
        tableFriends.setModel(logicModel.getContactModel());
        tableFriends.setAutoscrolls(true);

        messageHistory.setAutoscrolls(true);
	messageContainer = logicModel.getMessageHistoryModel();

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
	      logicModel.sendMessage(myText.getText());
	      myText.setText("");
            }
        });

        disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
		logicModel.finishCall();
                textFieldIp.setEnabled(true);
                connect.setEnabled(true);
                disconnect.setEnabled(false);
                myText.setEnabled(false);
                sendButton.setEnabled(false);
                messageHistory.setEnabled(false);
		buttonChangeLocalNick.setEnabled(true);
		buttonChangeLocalNick.doClick();
            }
        });
	
	historyViewObserver = new Observer() {
	  @Override
	  public void update(Observable o, Object arg) {
	    if (((Vector<String>) arg).isEmpty())
	      messageHistory.setText("");
	    else {
	      String last = messageHistory.getText();
	      if (!last.isEmpty())
		last = last + Protocol.endOfLine;
	      HistoryModel.Message msgText = messageContainer.getMessage(messageContainer.getSize() - 1);
	      messageHistory.setText(last + msgText.getNick() + ". " + msgText.getDate().toString() + "." + Protocol.endOfLine);
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
                logicModel.addContact(textFieldNick.getText(), textFieldIp.getText());
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
                if (e.getKeyCode() == 127) { //delete
                    int sr = tableFriends.getSelectedRow();
		    logicModel.removeContact(sr);
		    if (sr >= 0)                        
                        tableFriends.clearSelection();
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
                textFieldIp.setEnabled(true);
                tableFriends.setEnabled(true);
                textFieldLocalNick.setEnabled(false);
                buttonChangeLocalNick.setEnabled(false);
            }
        });

        //Обработка клавиши Enter в поле ввода локального ника
        textFieldLocalNick.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == '\n')
                    buttonChangeLocalNick.doClick();
                super.keyPressed(e);
            }
        });

        myText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n')
                    sendButton.doClick();
                else
                    super.keyTyped(e);
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
	int n = JOptionPane.showOptionDialog(this, "Remote user is busy. Try again?", "New connection", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
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
    
    public void blockDialogComponents(boolean blockingFlag) {
        disconnect.setEnabled(! blockingFlag);
        connect.setEnabled(blockingFlag);
        buttonAddFriends.setEnabled(! blockingFlag);
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
