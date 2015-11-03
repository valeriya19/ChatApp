import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
/*
import java.net.InetAddress;
import java.net.InetSocketAddress;
*/
import java.net.Socket;

/**
 *
 * @author M-Sh-97
 */
public class Connection {

  private Socket connection_socket;
  private final BufferedReader input;
  private final BufferedWriter output;
  
  /*
  public Connection() throws IOException {
    connection_socket = new Socket();
    connection_socket.bind(new InetSocketAddress(connection_socket.getLocalAddress(), 28411));
    input = new BufferedReader(new InputStreamReader(this.connection_socket.getInputStream(), "UTF-8"));
    output = new BufferedWriter(new OutputStreamWriter(this.connection_socket.getOutputStream(), "UTF-8"));
  }
  */
  
  public Connection(Socket remote_connection) throws IOException {
    connection_socket = remote_connection;
    input = new BufferedReader(new InputStreamReader(this.connection_socket.getInputStream(), "UTF-8"));
    output = new BufferedWriter(new OutputStreamWriter(this.connection_socket.getOutputStream(), "UTF-8"));
  }
  
  public boolean isOpen() {
    return connection_socket != null;
  }
  
  public boolean isConnected() {
    return connection_socket.isConnected();
  }
  
  public Command receive() throws IOException {
    return Command.getCommand(input.readLine());
  }
  
  public void accept() throws IOException {
    output.write("ACCEPTED\n");
    output.flush();
  }
  
  public void reject() throws IOException {
    output.write("REJECTED\n");
    output.flush();
  }
  
  /*
  public void connect(InetAddress ip) throws IOException {
    connection_socket.connect(new InetSocketAddress(ip, 28411));
    output.write("ChatApp 2015 user Anonymous");
    output.flush();
  }
  */

  public void sendNickHello(String version, String nick_name) {
    output.write("ChatApp " + version + " user " + nick_name + "\n");
  }

  public void sendNickBusy(String version, String nick_name) {
    output.write("ChatApp " + version + " user " + nick_name + " busy" + "\n");
  }
  
  public void disconnect() throws IOException {
    output.write("DISCONNECT");
    output.flush();
    connection_socket.close();
  }
  
  public void close() {
    connection_socket = null;
  }
  
  public void sendMessage(String message) throws IOException {
    output.write("MESSAGE\n");
    output.flush();
    output.write(message + "\n");
    output.flush();
  }
  
  public static void main(String[] args) {
    
  }
}
