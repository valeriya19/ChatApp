import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by 81k5_Pr0g3r on 22.12.15.
 */
public class ChatDialog extends JFrame implements Observer {
    private JButton buttonDisconnect;
    private JTextField textFieldMessage;
    private JButton buttonSend;
    private JButton buttonSave;
    private JButton buttonClearHistory;
    private JPanel rootPanel;
    private JLabel InfoLabel;
    private JTextArea textAreaMessages;
    private Connection connection;
    private JScrollPane scroll;

    public ChatDialog(Connection connection){
        super();
        this.connection=connection;
        setContentPane(rootPanel);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400,500);

        this.setTitle(connection.nick+"("+connection.getRemoteAddress()+")");

        InfoLabel.setText("Chat with "+connection.nick+"("+connection.getRemoteAddress()+")");

        buttonDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connection.Disconnect();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    AddMessage(ChatProtocol.LocalNick,textFieldMessage.getText());
                    connection.Message(textFieldMessage.getText());
                    textFieldMessage.setText("");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        ConnectionListenerThread connectionListenerThread = new ConnectionListenerThread(connection); //создаем слушателя, который постоянно опрашивает соединение ()
        connectionListenerThread.addObserver(this);                                                   //говорим что все что услышит слушатель передать в этот класс
        Thread thread = new Thread(connectionListenerThread);
        thread.start();

        buttonClearHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textAreaMessages.setText("");
            }
        });

        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try(FileOutputStream outputStream = new FileOutputStream(connection.nick+"_"+(new Date().getTime()))) {  //записывает в файл с именем "имя пользователя"_<время>
                    outputStream.write(textAreaMessages.getText().getBytes());
                    JOptionPane.showConfirmDialog(buttonSave, "File with message history was saved! \nCheck program directory.", "History was saved!", JOptionPane.OK_OPTION);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void AddMessage(String login, String text){
        textAreaMessages.setText(textAreaMessages.getText() + "\n"
                                + new Date().toString()+"\n"
                                + login + ": " + text+"\n");
    }

    @Override
    public void update(Observable o, Object arg) {
        Command command=(Command)arg;
        switch (command.getType()){
            case CT_ACCEPT:
                System.out.println("accepting");
                break;
            case CT_DISCONNECT:
                connection.close();
                InfoLabel.setText("User "+connection.nick+" close chat with you!");

                //todo диалог востановления соединения(вызвать коллер)
                break;
            case CT_HELLO:
                try {
                    connection.Hello();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case CT_HELLO_BUSY:
                break;
            case CT_REJECT:
                //connection.close();
                break;
            case CT_MESSAGE:
                AddMessage(connection.nick,command.getParam());
                break;
        }
    }

}
