import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Caller {
	private String localNick;
	private SocketAddress remoteAddress;
	private String ip;
	private boolean status;
	private Socket s;
	private Connection call;
	private final byte code=3;
	 
	public Caller(){
		this.localNick = "unnamed"; 
		this.remoteAddress = getRemoteAddress(); 
	}

	public Caller(String localNick){
		this.localNick=localNick;
		this.remoteAddress = getRemoteAddress(); 
	}
	public Caller(String localNick, SocketAddress remoteAddress){
		this.localNick=localNick;
		this.remoteAddress=remoteAddress;
	}

	public Caller(String localNick,String ip){
		this.localNick=localNick;
		this.ip=ip;
		this.remoteAddress = getRemoteAddress();
	}
	
	public Connection call() throws IOException{
		s = new Socket(ip, 28411);
		status=s.isConnected();
		if (status) {
			return  call=new Connection(s);}
		return null;
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
	
	public Caller.CallStatus getStatus(){
		return CallStatus.OK;
	}
	
	public void setLocalNick(String localNick){
		this.localNick = localNick;
	}
	
	public void setRemoteAddress(SocketAddress remoteAddress){
		this.remoteAddress = remoteAddress;
	}
	
	/*protected Caller (byte callStatusIndex) {
		code = callStatusIndex;
	}*/

	private static enum CallStatus{BUSY, NO_SERVICE, NOT_ACCESSIBLE, OK, REJECTED};
	
//	public static Caller getCall(String text) {
//		for (CallStatus cs: CallStatus.values())
//			if (text.equals(cs.name()))
//				return new Caller((byte) cs.ordinal());
//		return null;
//	}
	
}
