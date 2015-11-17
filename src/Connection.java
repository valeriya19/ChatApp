import java.io.IOException;
import java.io.PrintWriter;
import java.lang.StringBuilder;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author M-Sh-97
 */
public class Connection {

  private Socket connectionSocket;
  private Scanner input;
  private PrintWriter output;
  
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
      return Command.getCommand(input.nextLine());//слушатель останавливается на этом моменте!!!!!!
  }
  
  public void accept() throws IOException {
    output.write("ACCEPTED\n");
  }
  
  public void reject() throws IOException {
    output.write("REJECTED\n");
  }

  public String getNick() {
    String hm = input.nextLine();
    int p = hm.indexOf("user ");
    if (p > -1)
      return hm.substring(p + 5);
    else
      return null;
  }

  public String receiveMessage() {
    StringBuilder m = new StringBuilder();
    while (input.hasNextLine())
      m.append(input.nextLine());
    return m.toString();
  }

  public void sendNickHello(String version, String nickName) throws IOException {
    output.write("NICK\n");
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
