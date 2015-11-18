import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 *
 * @author katebudyanskaya
 */
public class Caller {
	private String localNick, remoteNick;
	private SocketAddress remoteAddress;
	private CallStatus status;
	static enum CallStatus{OK, NOT_ACCESSIBLE, BUSY, REJECTED, NO_SERVICE};
	 
	public Caller(){
		this("unnamed", new InetSocketAddress("127.0.0.1", 28411)); 
	}

	public Caller(String localNick){
		this(localNick, new InetSocketAddress("127.0.0.1", 28411)); 
	}
	
	public Caller(String localNick, SocketAddress remoteAddress){
		this.localNick=localNick;
		this.remoteAddress=remoteAddress;
	}

	public Caller(String localNick, String ip){
		this(localNick, new InetSocketAddress(ip, 28411));
	}
	
	public Connection call() throws IOException{
		Socket s = new Socket();
		s.connect(remoteAddress);
		Connection oc = new Connection(s);
		oc.sendNickHello("2015", localNick);
		String tempMessage;
		Command tempCommand;
		if (oc.receive().getType() == Command.CommandType.NICK){
		        remoteNick = oc.receiveMessage();
			tempMessage = remoteNick.toUpperCase();
			remoteNick = remoteNick.substring(tempMessage.indexOf(" USER ", 9));
			if (remoteNick.isEmpty())
				oc.close();
			else{
				int bp = remoteNick.indexOf(" busy");
				if (bp > - 1) {
				  remoteNick = remoteNick.substring(0, bp);
				  status = CallStatus.BUSY;
				  oc.close();
				}
				else{
				  remoteNick = remoteNick.substring(0, remoteNick.length() - 1);
				  oc.accept();
				  tempCommand = oc.receive();
				  if (tempCommand.getType() == Command.CommandType.REJECT){
					  status = CallStatus.REJECTED;
					  oc.disconnect();
					  oc.close();
				  }
				  else{
				    if (tempCommand.getType() == Command.CommandType.ACCEPT)
				      status = CallStatus.OK;
				    else{
				      status = CallStatus.NO_SERVICE;
				      oc.disconnect();
				      oc.close();
				    }
				  } 
				}
			}
		}
		else
		    oc.close();
		return oc;
	}
	
	public String getLocalNick(){
		return localNick;
	}
	
	public SocketAddress getRemoteAddress(){
		return remoteAddress;
	}
	
	public String getRemoteNick(){
		return localNick;
	}
	
	public CallStatus getStatus(){
		return status;
	}
	
	public void setLocalNick(String localNick){
		this.localNick = localNick;
	}
	
	public void setRemoteAddress(SocketAddress remoteAddress){
		this.remoteAddress = remoteAddress;
	}
}
