import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by 81k5_Pr0g3r on 22.12.15.
 */
public class ServerMVC_View extends JFrame{
    private JTextField textFieldMyLogin;
    private JButton buttonLogin;
    private JPanel rootPanel;
    private JButton buttonConnect;
    private JTextField textFieldIp;
    private JTable tableBanList;
    private JTable tableOnlineUser;
    final ServerMVC_Model model;

    public ServerMVC_View(ServerMVC_Model model) {
        super();
        this.model=model;
        setContentPane(rootPanel);
        setSize(400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setMyNick(textFieldMyLogin.getText());
            }
        });
        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.Create_Connection(textFieldIp.getText());
            }
        });


        tableBanList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 127)//??????? delete
                    if (tableBanList.getSelectedRow() >= 0) {
                        model.tableModelBan.removeRow(tableBanList.getSelectedRow());
                    }
            }
        });
    }

    public int Request_for_connect(String nick,String ip){
        Object[] option = {"Accept", "Decline", "Ban"};
        int n = JOptionPane.showOptionDialog(this,
                "User " + nick + " from IP " + ip + " want to talk with you.", "New connection",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                option,
                option[0]);
        return n;
    }

    public void setBanModel(TableModel banModel)    //связывает модель таблицы с визуальным компонентом
    {
        tableBanList.setModel(banModel);
    }
}
