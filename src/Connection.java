import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author M-Sh-97
 */
public class Connection {

  private final Socket connectionSocket;
  private final Scanner input;
  private final PrintWriter output;
  
  public Connection(Socket s) throws IOException {
    connectionSocket = s;
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
    output.flush();
  }
  
  public void reject() throws IOException {
    output.write("REJECTED\n");
    output.flush();
  }

  public String receiveMessage() {
    StringBuilder m = new StringBuilder();
    while (input.hasNextLine())
      m.append(input.nextLine());
    return m.toString();
  }

  public void sendNickHello(String version, String nickName) throws IOException {
    output.write("NICK\n");
    output.flush();
    output.write("ChatApp " + version + " user " + nickName + "\n");
    output.flush();
  }

  public void sendNickBusy(String version, String nickName) throws IOException {
    output.write("NICK\n");
    output.flush();
    output.write("ChatApp " + version + " user " + nickName + " busy" + "\n");
    output.flush();
  }
  
  public void disconnect() throws IOException {
    output.write("DISCONNECT\n");
    output.flush();
  }
  
  public void close() throws IOException {
    connectionSocket.close();
  }
  
  public void sendMessage(String message) throws IOException {
    output.write("MESSAGE\n");
    output.flush();
    output.write(message + "\n");
    output.flush();
  }
}
