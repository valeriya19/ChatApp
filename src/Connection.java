import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by 81k5_Pr0g3r on 22.12.15.
 */
public class Connection {
    Socket socket;
    Scanner scanner;
    OutputStreamWriter writer;
    String nick;

    public Connection(Socket socket, String nick) {
        this.socket = socket;
        this.nick = nick;

        try {
            scanner = new Scanner(socket.getInputStream());
            writer = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public Command readCommand()
    {
        if (scanner.hasNextLine()){

            String Input=scanner.nextLine();
            String input=Input.toUpperCase();

            if (input.compareTo("ACCEPT")==0){
                return new Command(Command.CommandType.CT_ACCEPT,"");
            } else if (input.compareTo("DECLINE")==0){
                return new Command(Command.CommandType.CT_REJECT,"");
            } else if (input.compareTo("DISCONNECT")==0){
                return new Command(Command.CommandType.CT_DISCONNECT,"");
            } else if (input.compareTo("MESSAGE")==0){
                return new Command(Command.CommandType.CT_MESSAGE,scanner.nextLine());
            }
            if (input.indexOf("CHATAPP "+ChatProtocol.ver+" USER ")==0)
            {
                String name = input.substring(18);
                this.nick=name;
                return new Command(Command.CommandType.CT_HELLO,"");
            }

        }
        return null;
    }

    public void Hello() throws IOException {
        writer.write("ChatApp "+ChatProtocol.ver+" user "+ChatProtocol.LocalNick+"\n");
        writer.flush();
    }
    public void Accept() throws IOException {
        writer.write("ACCEPT\n");
        writer.flush();
    }
    public void Decline() throws IOException {
        writer.write("DECLINE\n");
        writer.flush();
        close();
    }
    public void Message(String textMessage) throws IOException {
        writer.write("MESSAGE\n");
        writer.write(textMessage+"\n");
        writer.flush();
    }
    public void Disconnect() throws IOException {
        writer.write("DISCONNECT\n");
        writer.flush();
        close();
    }

    public String getRemoteAddress(){
        return socket.getRemoteSocketAddress().toString();
    }

    public void close(){
        try {
            socket.close();
            System.out.println(socket.isConnected());
            System.out.println(socket.isClosed());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
