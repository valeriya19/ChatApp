import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;


public class ChatForm extends JFrame {
    private JPanel rootPanel;
    private JButton connect;
    private JButton disconnect;
    private JTextField textFieldIp;
    private JTextField textFieldNick;
    private JButton buttonAddFriends;
    private JTextField textFieldLocalNick;
    private JButton buttonChangeLocalNick;
    private JTable tableFriends;
    private JTextArea messageStory;
    private JButton sendButton;
    private JTextField myText;
    private JList friendList;

    Vector<Vector<String>> friends=new Vector<Vector<String>>();
    Vector<String> header=new Vector<String>();
    DefaultTableModel model;


    //объявления класса для взаимодействия с протоколом
    Connection connection=null;

    //Объявление классов-слушателей протокола
    Caller caller=null;
    CallListener callListener=null;
    CommandListenerThread commandListenerServer=null;
    CommandListenerThread commandListenerClient=null;

    //состояние программы
    int status=-1;
    /*Status value:
    * -1 - not logged in;
    * 0 - free and ready to connect;
    * 1 - try to connect another user;
    * 2 - already connected to another user*/


    public ChatForm() {
        //����������� �����
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

        //������ ����� � ��������
        header.add("Nick");
        header.add("IP");
        friends.add(header);
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("friendList.chat"));
            while (bufferedReader.ready()){
                Vector<String> tmp=new Vector<String>();
                String nick=bufferedReader.readLine();
                String ip=bufferedReader.readLine();
                tmp.add(nick);
                tmp.add(ip);
                friends.add(tmp);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (IOException e) {
            System.out.println("Error in reading file");
        }
        model=new DefaultTableModel(friends,header);
        tableFriends.setModel(model);
        status=-1;

        connect.addActionListener(new ActionListener() { //��� ������� �� connect ������� ����� � ����������� � �������
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connection!=null)
                    try {
                        connection.sendNickHello("2015", Main.LocalNick);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            caller = new Caller(Main.LocalNick, textFieldIp.getText());
                            try {
                                connection = caller.call();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            commandListenerClient = new CommandListenerThread(connection);

                            try {
                                connection.sendNickHello("2015", Main.LocalNick);//����������� ��� ����������� ���
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            commandListenerClient.start();
                        }
                    }).start();
                }
            }
        });

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //dialog when we want to close the program
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                Object[] option = {"Yes", "No"};

                int n = JOptionPane.showOptionDialog(e.getComponent(),"Are you really want to exit?","Close window?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,option,option[1]);

                if (n == 0) {
                    e.getWindow().setVisible(false);

                    //���������� ������ ������
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
        ListSelectionModel listSelectionModel=tableFriends.getSelectionModel();
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
                if (e.getKeyCode()==127)//������� delete
                    if (tableFriends.getSelectedRow()>0)
                    {
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
                    Main.LocalNick = textFieldLocalNick.getText();
                    connect.setEnabled(true);
                    textFieldIp.setEnabled(true);
                    textFieldNick.setEnabled(true);
                    tableFriends.setEnabled(true);

                    textFieldLocalNick.setEnabled(false);
                    buttonChangeLocalNick.setEnabled(false);

                    status=0;//��� ������ �� ����� ��������� ����� � ������� ����������� ��������� ��� �������
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                callListener= new CallListener();
                                callListener.setLocalNick(Main.LocalNick);//��������� ��� ������ �������� ���
                                commandListenerServer = new CommandListenerThread(callListener.getConnection());//����������� �������� ���������� ������ ������� ����� ������� �������� ����������
                                commandListenerServer.start();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    };
                    new Thread(runnable).start();
		}
            }
        });

        //Обработка клавиши Enter в поле ввода локального ника
        textFieldLocalNick.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar()=='\n')
                    buttonChangeLocalNick.doClick();
                super.keyPressed(e);
            }
        });
    }

    void NewMessage(String msgText){}

    void NewConnection(String ip,String nick) {
        Object[] option = {"Connect", "Disconnect"};

        int n = JOptionPane.showOptionDialog(this, "User "+nick+"from IP "+ip+"wants to chat with you", "New connection", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);

        if (n == 0) {
            try {
                connection.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            try {
                connection.reject();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    void ConnectionRefused(){
        Object[] option = {"Retry", "Cancel"};

        int n = JOptionPane.showOptionDialog(this, "No successful connection", "Connection Refused", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);

        if (n == 0) {
            try {
                connection.sendNickHello("2015",Main.LocalNick);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            myText.setEnabled(false);
            sendButton.setEnabled(false);
            messageStory.setEnabled(false);
            messageStory.setText("");
        }
    }

    void RejectConnection(String ip,String nick){
        Object[] option = {"Retry", "Cancel"};

        int n = JOptionPane.showOptionDialog(this, "User "+nick+"from IP "+ip+" canceled the connection", "Canceled connection", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);

        if (n == 0) {
            try {
                connection.sendNickHello("2015", Main.LocalNick);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            myText.setEnabled(false);
            sendButton.setEnabled(false);
            messageStory.setEnabled(false);
            messageStory.setText("");
        }
    }
}
