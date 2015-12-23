import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by 81k5_Pr0g3r on 22.12.15.
 */
public class ChatServer {
    private int port;
    public static boolean running=false;


    public ChatServer(int port) {
        this.port = port;
    }

    void runServer(){
        running=true;
        try (ServerSocket serverSocket = new ServerSocket(port)){
            while (running){
                Socket connection = serverSocket.accept();
                Thread thread = new Thread(new ConnectionService(connection));
                thread.start();
            }
        } catch(IOException e){
            e.printStackTrace();
        }

    }
}
