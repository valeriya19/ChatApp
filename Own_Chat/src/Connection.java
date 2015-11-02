import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *
 * @author M-Sh-97
 */
public class Connection {

  private Socket connection_socket;
  private short buffer_size;
  
  public Connection(Socket connection_socket) throws IOException {
    this.connection_socket = connection_socket;
    buffer_size = 1024;
  }
  
  public void set_buffer_size(short new_size) {
    if (new_size > 0)
      buffer_size = new_size;
  }
  
  public boolean is_valid() {
    return connection_socket != null;
  }
  
  public Command receive() throws IOException {
    byte[] buffer = new byte[buffer_size];
    connection_socket.getInputStream().read(buffer);
    return Command.get_command(new String(buffer, "UTF-8"));
  }
  
  public void accept() throws IOException {
    byte[] buffer = new String("ACCEPTION").getBytes("UTF-8");
    OutputStream ws = connection_socket.getOutputStream();
    for (short sp = 0; sp < buffer.length; sp += buffer_size)
      ws.write(buffer, sp, buffer.length - sp + 1);
    ws.flush();
  }
  
  public void reject() throws IOException {
    byte[] buffer = new String("REJECTION").getBytes("UTF-8");
    OutputStream ws = connection_socket.getOutputStream();
    for (short sp = 0; sp < buffer.length; sp += buffer_size)
      ws.write(buffer, sp, buffer.length - sp + 1);
    ws.flush();
  }
  
  public void connect(InetAddress ip, int port) throws IOException {
    connection_socket.connect(new InetSocketAddress(ip, 28411));
  }
  
  public void disconnect() throws IOException {
    connection_socket.close();
  }
  
  public void close() {
    connection_socket = null;
  }
  
  public void send_message(String message) throws IOException {
    byte[] buffer = new String("MESSAGE").getBytes("UTF-8");
    OutputStream ws = connection_socket.getOutputStream();
    for (short sp = 0; sp < buffer.length; sp += buffer_size)
      ws.write(buffer, sp, buffer.length - sp + 1);
    ws.flush();
    buffer = message.getBytes("UTF-8");
    for (short sp = 0; sp < buffer.length; sp += buffer_size)
      ws.write(buffer, sp, buffer.length - sp + 1);
    ws.flush();
  }
  
  public static void main(String[] args) {
    
  }
}
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.net.Socket;


//public class Connection {
	
//	public PrintWriter pw;
//	public Socket socket;
	
//	public Connection(Socket s)throws IOException{
//		socket=s;
//		pw = new PrintWriter(socket.getOutputStream());
		
//	}

//	public void close()throws IOException{
//		socket.close();
//	}
//	
//	public Command receive(){
//		
//	}
	
	
	
//	public void sendNickHello(String nick){
//		pw.println("ChatApp2015 user "+nick);
//	}
	
//	public void sendNickBusy(String nick){
//		pw.println("ChatApp2015 user "+nick+" busy");
//	}
	
//	public void accept(){
//		pw.println("Accepted");
//	}
	
//	public void reject(){
//		pw.println("Rejected");
//	}
	
//	public void disconnect(){
//		socket=null;
//	}
	
//	public void sendMessage(String msg){
//		pw.println(msg);
//	}
	
//	public static void main(String[] args){
		
//	}
//}
