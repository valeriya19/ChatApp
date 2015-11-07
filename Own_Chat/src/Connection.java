import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author M-Sh-97
 */
public class Connection {

  private Socket connection_socket;
  private final Scanner input;
  private final PrintWriter output;
  
  public Connection(Socket remote_connection) throws IOException {
    connection_socket = remote_connection;
    input = new Scanner(this.connection_socket.getInputStream(), "UTF-8");
    output = new PrintWriter(this.connection_socket.getOutputStream(), true);
  }
  
  public boolean isOpen() {
    return connection_socket != null;
  }
  
  public boolean isConnected() {
    return connection_socket.isConnected();
  }
  
  public Command receive() throws IOException {
    return Command.getCommand(input.nextLine());
  }
  
  public void accept() throws IOException {
    output.write("ACCEPTED\n");
  }
  
  public void reject() throws IOException {
    output.write("REJECTED\n");
  }

  public void sendNickHello(String version, String nick_name) throws IOException {
    output.write("ChatApp " + version + " user " + nick_name + "\n");
  }

  public void sendNickBusy(String version, String nick_name) throws IOException {
    output.write("ChatApp " + version + " user " + nick_name + " busy" + "\n");
  }
  
  public void disconnect() throws IOException {
    output.write("DISCONNECT");
    connection_socket.close();
  }
  
  public void close() {
    connection_socket = null;
  }
  
  public void sendMessage(String message) throws IOException {
    output.write("MESSAGE\n");
    output.write(message + "\n");
  }
  
  public static void main(String[] args) {
    
  }
}
