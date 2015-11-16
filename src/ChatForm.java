import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.Scanner;
import java.util.Vector;


public class ChatForm extends JFrame {
    private JPanel rootPanel;
    private JButton Connect;
    private JButton Disconnect;
    private JTextField textFieldIp;
    private JTextField textFieldNick;
    private JButton buttonAddFriends;
    private JTextField textFieldLocalNick;
    private JButton ButtonChangeLocalNick;
    private JTable tableFriends;
    private JList FriendList;

    Vector<Vector<String>> friends=new Vector<Vector<String>>();
    Vector<String> header=new Vector<String>();
    DefaultTableModel model;
    public ChatForm() {
        //Отображение формы
        super();
        setContentPane(rootPanel);
        setSize(700, 500);
        //


        //Чтение файла с друзьями
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
        //


        Connect.addActionListener(new ActionListener() { //событие - нажатие на кнопку connect
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.form.setVisible(false);
                Main.friendsForm.setVisible(true);
            }
        });

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

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

                    //сохранение списка друзей
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


        buttonAddFriends.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<String> tmp = new Vector<String>();
                tmp.add(textFieldNick.getText());
                tmp.add(textFieldIp.getText());
                model.addRow(tmp);
            }
        });

        //обработка выделения ряда в таблице
        ListSelectionModel listSelectionModel=tableFriends.getSelectionModel();//
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
        //

        //удаление ряда из таблицы
        tableFriends.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==127)//клавиша delete
                    if (tableFriends.getSelectedRow()>0)
                    {
                        model.removeRow(tableFriends.getSelectedRow());
                        tableFriends.clearSelection();
                    }
            }
        });
        //

    }
}



