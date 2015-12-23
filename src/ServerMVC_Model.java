import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.Scanner;
import java.util.Vector;


public class ServerMVC_Model {
    static ServerMVC_View serverMVC_view;
    static ChatServer server = new ChatServer(ChatProtocol.port);

    public static DefaultTableModel tableModelBan;
    public static DefaultTableModel tableModelOnline;

    public ServerMVC_Model() {

        //Создаем модель для таблицы забаненных пользователей
        Vector<String> banHeaders= new Vector<String>(2); //создаем вектор на два элемента для заголовка таблицы забаненых
        banHeaders.add("nick");     banHeaders.add("address");
        Vector< Vector<String> > banList = new Vector<>();
        try (Scanner scanner= new Scanner(new File("BanList"))){
            while (scanner.hasNextLine())
            {
                Vector<String> tmp = new Vector<>(2);
                String nick = scanner.nextLine();
                String address = scanner.nextLine();
                tmp.add(nick);  tmp.add(address);
                banList.add(tmp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        tableModelBan= new DefaultTableModel(banList,banHeaders);
        tableModelBan.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                try (OutputStreamWriter writer= new OutputStreamWriter(new FileOutputStream(new File("BanList")))){
                    for (int i=0;i<tableModelBan.getRowCount();i++)
                    {
                        writer.write(tableModelBan.getValueAt(i, 0) + "\n");
                        writer.write(tableModelBan.getValueAt(i,1)+"\n");
                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        serverMVC_view=new ServerMVC_View(this);
        serverMVC_view.setVisible(true);
        serverMVC_view.setBanModel(tableModelBan);
    }

    public static void setMyNick(String myNick) {
        ChatProtocol.LocalNick = myNick;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                server.runServer();
            }
        });
        thread.start();

        //set server form to first state
    }

    public static void New_Connection(Connection connection){

        String ip_address=connection.getRemoteAddress();
        ip_address=ip_address.substring(0,ip_address.indexOf(':'));
        if (!isBanned(connection.nick,ip_address)){
            try{
                switch (serverMVC_view.Request_for_connect(connection.nick,connection.getRemoteAddress())){
                    case 0:
                        connection.Accept();
                        new ChatDialog(connection);
                        break;
                    case 1:
                        connection.Decline();
                        break;
                    case 2:
                        connection.Decline();
                        Vector<String> tmp = new Vector<>(2);
                        tmp.add(connection.nick);
                        tmp.add(ip_address);
                        tableModelBan.addRow(tmp);
                        break;
                }
            } catch (IOException e) {
                System.err.println("ERROR!!! Connection closed");
                connection.close();
            }
        } else {
            connection.close();
        }
    }

    public static boolean isBanned(String nick, String address){
        for (int i=0;i<tableModelBan.getRowCount();i++)
        {
            if ((tableModelBan.getValueAt(i, 0).toString().compareTo(nick)==0) &&
             (tableModelBan.getValueAt(i, 1).toString().compareTo(address)==0))
                return true;
        }
        return false;
    }

    public static void Create_Connection(String ip){
       new Caller(ip);
    }


}


