import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import Command.Command_Type;


public class Caller {
	private String localNick;
	private SocketAddress remoteAddress;
	private String ip;
	private boolean status;
	private Socket s;
	private Connection call;
	 private final byte code;
	 
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
	
	//”станавливает исход€щее соединение. ¬озвращает результат только после удаленного подтверждени€ (или неподтверждени€) беседы. ѕодробный результат звонка доступен в поле status.
	
	public Connection call() throws IOException{
		s.connect(new InetSocketAddress(ip, 28411));
		if (status) {
				return  call=new Connection(s);}
		//
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
		
	}
	
	public void setLocalNick(String localNick){
		this.localNick = localNick;
	}
	
	public void setRemoteAddress(SocketAddress remoteAddress){
		this.localNick = localNick;
	}
	
	protected Caller (byte callStatusIndex) {
	    code = callStatusIndex;
	  }
	 private static enum CallStatus{BUSY, NO_SERVICE, NOT_ACCESSIBLE, OK, REJECTED};
	
//	 public static Caller getCall(String text) {
//		    for (CallStatus cs: CallStatus.values())
//		      if (text.equals(cs.name()))
//			return new Caller((byte) cs.ordinal());
//		    return null;
//		  }
	
}
