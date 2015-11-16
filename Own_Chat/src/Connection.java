import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author M-Sh-97
 */
public class Connection {

  private Socket connectionSocket;
  private final Scanner input;
  private final PrintWriter output;
  
  public Connection(Socket remoteConnection) throws IOException {
    connectionSocket = remoteConnection;
    input = new Scanner(this.connectionSocket.getInputStream(), "UTF-8");
    output = new PrintWriter(this.connectionSocket.getOutputStream(), true);
  }
  
  public boolean isOpen() {
    return connectionSocket != null;
  }
  
  public boolean isConnected() {
    return connectionSocket.isConnected();
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

  public void sendNickHello(String version, String nickName) throws IOException {
    output.write("ChatApp " + version + " user " + nickName + "\n");
  }

  public void sendNickBusy(String version, String nickName) throws IOException {
    output.write("ChatApp " + version + " user " + nickName + " busy" + "\n");
  }
  
  public void disconnect() throws IOException {
    output.write("DISCONNECT");
    connectionSocket.close();
  }
  
  public void close() {
    connectionSocket = null;
  }
  
  public void sendMessage(String message) throws IOException {
    output.write("MESSAGE\n");
    output.write(message + "\n");
  }
  
  public static void main(String[] args) {
    
  }
}
