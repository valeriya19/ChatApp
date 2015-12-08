import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * @author valeriya19
 */
class ChatForm extends JFrame {
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
    private JTextArea messageStory;
    //private JList friendList;

    private static String localNick;

    private Vector<Vector<String>> friends = new Vector<Vector<String>>();
    private Vector<String> header = new Vector<String>();
    private DefaultTableModel model;

    //объявления класса для взаимодействия с протоколом
    private Connection serverConnection = null;
    private Connection clientConnection = null;

    //Объявление классов-слушателей протокола
    private Caller caller = null;
    private CallListener callListener = null;
    private CommandListenerThread commandListenerServer = null;
    private CommandListenerThread commandListenerClient = null;

    //состояние программы
    private static enum Status {
        BUSY, SERVER_NOT_STARTED, OK, CLIENT_CONNECTED, REQUEST_FOR_CONNECT
    };

    private static enum ConnectionStatus {
        AS_SERVER,AS_CLIENT,AS_NULL
    };
    ConnectionStatus CurrentSuccessConnection=ConnectionStatus.AS_NULL;

    private Status status;

    private Observer clientObserver, serverObserver;

    public ChatForm() {
        super();
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

        header.add("Nick");
        header.add("IP");
        friends.add(header);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("friendList.chat"));
            while (bufferedReader.ready()) {
                Vector<String> tmp = new Vector<String>();
                String nick = bufferedReader.readLine();
                String ip = bufferedReader.readLine();
                tmp.add(nick);
                tmp.add(ip);
                friends.add(tmp);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (IOException e) {
            System.out.println("Error in reading file");
        }
        model = new DefaultTableModel(friends, header);
        tableFriends.setModel(model);

        messageStory.setAutoscrolls(true);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (CurrentSuccessConnection == ConnectionStatus.AS_SERVER) {
                        serverConnection.sendMessage(myText.getText() + "\n");
                    } else if (CurrentSuccessConnection == ConnectionStatus.AS_CLIENT) {
                        clientConnection.sendMessage(myText.getText() + "\n");
                    }
                    messageStory.setText(messageStory.getText() + localNick + ": " + myText.getText() + "\n");
                    myText.setText("");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldIp.setEnabled(true);
                connect.setEnabled(true);
                disconnect.setEnabled(false);
                myText.setEnabled(false);
                sendButton.setEnabled(false);
                messageStory.setEnabled(false);

                try {
                    System.out.println(CurrentSuccessConnection);
                    if (CurrentSuccessConnection == ConnectionStatus.AS_SERVER) {
                        serverConnection.disconnect();
                        serverConnection.close();
                        commandListenerServer.stop();
                        commandListenerServer.deleteObservers();
                        buttonChangeLocalNick.setEnabled(true);
                        buttonChangeLocalNick.doClick();
                    } else if (CurrentSuccessConnection == ConnectionStatus.AS_CLIENT) {
                        clientConnection.disconnect();
                        clientConnection.close();
                        commandListenerClient.stop();
                        commandListenerClient.deleteObservers();
                    }

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });

        clientObserver = new Observer() {
            @Override
            public void update(Observable o, Object arg) {

                if (((Command) arg).getType() == Command.CommandType.NICK) {
                    System.out.println("Nick is coming");
                    if (o instanceof CommandListenerThread) {
                        caller.setRemoteNick(((NickCommand)arg).getNick());
                        newConnection(clientConnection, ((InetSocketAddress) caller.getRemoteAddress()).getAddress().getHostAddress(), caller.getRemoteNick(),((NickCommand)arg).getBusyStatus());

                    }
                } else if (((Command) arg).getType() == Command.CommandType.ACCEPT) {
                    System.out.println("Accept is coming");
                    if (o instanceof CommandListenerThread) {
                        acceptConnection(clientConnection);
                    }
                } else if (((Command) arg).getType() == Command.CommandType.REJECT) {
                    System.out.println("Reject is coming");
                    if (o instanceof CommandListenerThread) {
                        rejectConnection(clientConnection, ((InetSocketAddress) caller.getRemoteAddress()).getAddress().getHostAddress(), caller.getRemoteNick());
                    }
                } else if (((Command) arg).getType() == Command.CommandType.MESSAGE) {
                    System.out.println("Message is coming");
                    if (o instanceof CommandListenerThread) {
                        newMessage(clientConnection, ((MessageCommand) arg).getMessage());
                    }
                } else if (((Command) arg).getType() == Command.CommandType.DISCONNECT) {
                    System.out.println("Disconnect is coming");
                    if (o instanceof CommandListenerThread) {
                        connectionRefused(clientConnection);
                    }
                }
            }
        };


        serverObserver = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (((Command) arg).getType() == Command.CommandType.NICK) {
                    System.out.println("Nick is coming");
                    if (o instanceof CommandListenerThread) {
                        newConnection(serverConnection, ((InetSocketAddress) callListener.getRemoteAddress()).getAddress().getHostAddress(), ((NickCommand) arg).getNick(),false);
                    }
                } else if (((Command) arg).getType() == Command.CommandType.ACCEPT) {
                    System.out.println("Accept is coming");
                    if (o instanceof CommandListenerThread) {
                        acceptConnection(serverConnection);
                    }
                } else if (((Command) arg).getType() == Command.CommandType.REJECT) {
                    System.out.println("Reject is coming");
                    if (o instanceof CommandListenerThread) {
                        rejectConnection(serverConnection, ((InetSocketAddress) callListener.getRemoteAddress()).getHostName(), callListener.getRemoteNick());
                    }
                } else if (((Command) arg).getType() == Command.CommandType.MESSAGE) {
                    System.out.println("Message is coming");
                    if (o instanceof CommandListenerThread) {
                        newMessage(serverConnection, ((MessageCommand) arg).getMessage());
                    }
                } else if (((Command) arg).getType() == Command.CommandType.DISCONNECT) {
                    System.out.println("Disconnect is coming");
                    if (o instanceof CommandListenerThread) {
                        connectionRefused(serverConnection);
                    }
                }
            }
        };

        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            caller = new Caller(localNick, textFieldIp.getText());
                            try {
                                clientConnection=caller.call();
                                commandListenerClient = new CommandListenerThread(clientConnection);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            commandListenerClient.addObserver(clientObserver);

                            CurrentSuccessConnection = ConnectionStatus.AS_CLIENT;
                            commandListenerClient.start();
                        }
                    }).start();
                    status = Status.REQUEST_FOR_CONNECT;

            }
        });

        //dialog when we want to close the program
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                Object[] option = {"Yes", "No"};

                int n = JOptionPane.showOptionDialog(e.getComponent(), "Are you really want to exit?", "Close window?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);

                if (n == 0) {
                    e.getWindow().setVisible(false);

                    try (FileWriter fileWriter = new FileWriter("friendList.chat")) {
                        for (int i = 1; i < model.getRowCount(); i++) {
                            fileWriter.write(model.getValueAt(i, 0).toString() + "\n");
                            fileWriter.write(model.getValueAt(i, 1).toString() + "\n");
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    System.exit(0);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        //add user to friend list
        buttonAddFriends.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<String> tmp = new Vector<String>();
                tmp.add(textFieldNick.getText());
                tmp.add(textFieldIp.getText());
                model.addRow(tmp);
            }
        });

        //copy info from friend list to our textField
        ListSelectionModel listSelectionModel = tableFriends.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
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
                if (e.getKeyCode() == 127)//??????? delete
                    if (tableFriends.getSelectedRow() > 0) {
                        model.removeRow(tableFriends.getSelectedRow());
                        tableFriends.clearSelection();
                    }
            }
        });

        //Change local nick and activate next field
        buttonChangeLocalNick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textFieldLocalNick.getText().isEmpty()) {
                    localNick = textFieldLocalNick.getText();
                    connect.setEnabled(true);
                    textFieldIp.setEnabled(true);
                    tableFriends.setEnabled(true);

                    textFieldLocalNick.setEnabled(false);
                    buttonChangeLocalNick.setEnabled(false);

                    status = Status.SERVER_NOT_STARTED;
                    Runnable runnable = new Runnable() {
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
                    };
                    new Thread(runnable).start();
                    status = Status.OK;
                }
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

    void newMessage(Connection con, String msgText) {
        if (msgText != null) {
            messageStory.setText(messageStory.getText() + textFieldNick.getText() + ": " + msgText+"\n");
        }
    }

    void newConnection(Connection con, String ip, String nick,boolean remoteBusy) {

        if (remoteBusy)
        {
            Object[]    option = {"YES", "No"};
            int n = JOptionPane.showOptionDialog(this, "User " + nick + " from IP " + ip + " is busy. Try again&", "New connection", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
            if (n == 0) {
                try {
                    con.sendNickHello(localNick);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                status = Status.OK;
            }
            return;
        }

        if (status == Status.OK) {
            Object[] option1 = {"Connect", "Disconnect"};
            int n1 = JOptionPane.showOptionDialog(this, "User " + nick + " from IP " + ip + " wants to chat with you", "New connection", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option1, option1[1]);
            if (n1 == 0) {
                try {
                    con.accept();
                    status = Status.BUSY;
                    textFieldNick.setEnabled(false);
                    textFieldNick.setText(nick);
                    textFieldIp.setText(ip);
                    textFieldIp.setEnabled(false);
                    CurrentSuccessConnection = ConnectionStatus.AS_SERVER;
                    acceptConnection(con);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                try {
                    con.reject();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        } else if (status == Status.REQUEST_FOR_CONNECT) {
            status = Status.BUSY;
            textFieldNick.setEnabled(false);
            textFieldNick.setText(nick);
            textFieldIp.setText(ip);
            textFieldIp.setEnabled(false);
            try {
                con.sendNickHello(localNick);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (status == Status.BUSY)
            try {
                con.sendNickBusy(localNick);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    void connectionRefused(Connection con) {
        Object[] option = {"Retry", "Cancel"};

        int n = JOptionPane.showOptionDialog(this, "No successful connection", "Connection Refused", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);

        if (n == 0) {
            try {
                con.close();
                if (CurrentSuccessConnection==ConnectionStatus.AS_SERVER){
                    buttonChangeLocalNick.doClick();
                }
                connect.setEnabled(true);
                connect.doClick();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            connect.setEnabled(true);
            disconnect.setEnabled(false);
            myText.setEnabled(false);
            sendButton.setEnabled(false);
            messageStory.setEnabled(false);
            messageStory.setText("");
            textFieldIp.setEnabled(true);
            status=Status.OK;
        }
    }

    void rejectConnection(Connection con, String ip, String nick) {
        Object[] option = {"Retry", "Cancel"};

        int n = JOptionPane.showOptionDialog(this, "User " + nick + "from IP " + ip + " canceled the connection", "Canceled connection", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);

        if (n == 0) {
                connect.doClick();
        } else {
            textFieldIp.setEnabled(true);
            myText.setEnabled(false);
            sendButton.setEnabled(false);
            messageStory.setEnabled(false);
            messageStory.setText("");
            CurrentSuccessConnection = ConnectionStatus.AS_NULL;
        }
    }

    void acceptConnection(Connection con) {
        disconnect.setEnabled(true);
        connect.setEnabled(false);
        buttonAddFriends.setEnabled(true);
        myText.setEnabled(true);
        sendButton.setEnabled(true);
        messageStory.setEnabled(true);
        messageStory.setText("");
    }
}
