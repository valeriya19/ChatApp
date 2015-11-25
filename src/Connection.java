import java.io.IOException;
import java.io.OutputStreamWriter;
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
    input = new Scanner(this.connectionSocket.getInputStream(), Protocol.encoding);
    output = new PrintWriter(new OutputStreamWriter(this.connectionSocket.getOutputStream(), Protocol.encoding), true);
  }
  
  public boolean isOpen() {
    return !connectionSocket.isClosed();
  }
  
  public boolean isConnected() {
    return connectionSocket.isConnected();
  }
  
  public Command receive() throws IOException {
    StringBuilder it = new StringBuilder(Protocol.encoding);
    do {
      it.append(input.nextLine());
      it.append(Protocol.endOfLine);
    } while (input.hasNextLine());
    return Command.getCommand(it.toString());
  }
  
  public void accept() throws IOException {
    output.write("ACCEPTED" + Protocol.endOfLine);
    output.flush();
  }
  
  public void reject() throws IOException {
    output.write("REJECTED" + Protocol.endOfLine);
    output.flush();
  }

  public void sendNickHello(String nickName) throws IOException {
    output.write(Protocol.programName + " " + Protocol.version + " user " + nickName + Protocol.endOfLine);
    output.flush();
  }
  
  public void sendNickBusy(String nickName) throws IOException {
    output.write(Protocol.programName + " " + Protocol.version + " user " + nickName + " busy" + Protocol.endOfLine);
    output.flush();
  }
  
  public void disconnect() throws IOException {
    output.write("DISCONNECT" + Protocol.endOfLine);
    output.flush();
  }
  
  public void close() throws IOException {
    connectionSocket.close();
  }
  
  public void sendMessage(String message) throws IOException {
    output.write("MESSAGE" + Protocol.endOfLine);
    output.write(message + Protocol.endOfLine);
    output.flush();
  }
}
