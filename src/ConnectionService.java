import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;


public class ConnectionService implements Runnable{
    public static int users=0;
    public static int ids=0;
    Socket socket;
    private int num;

    public ConnectionService(Socket socket) {
        this.socket = socket;
        users++;
        num=ids;
        ids++;
    }

    @Override
    public void run() {
        try{
            System.out.println("New connection\t #" + num+"\tUser connected  ("+socket.getRemoteSocketAddress()+")");

            Scanner in_socket=new Scanner(socket.getInputStream());
            OutputStreamWriter out_socket = new OutputStreamWriter(socket.getOutputStream());

            out_socket.write("ChatApp "+ChatProtocol.ver+" user "+ChatProtocol.LocalNick+"\n");
            out_socket.flush();

            if (in_socket.hasNextLine())
            {
                String task = in_socket.nextLine();
                System.out.println("Connection #"+num+"\tCommand: "+task);
                if (task.toUpperCase().indexOf("CHATAPP "+ChatProtocol.ver+" USER ")==0)
                {
                    String name = task.substring(18);
                    Connection connection= new Connection(socket,name);
                    ServerMVC_Model.New_Connection(connection);
                }
                else
                {
                    socket.close();
                }
            }
            users--;
        }
        catch (IOException e) {
            users--;
            System.out.println("Connection #"+num+" closed");
            e.printStackTrace();
        }
    }
}
