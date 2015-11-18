import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;

/**
 *
 * @author katebudyanskaya
 */
class CallListener {
        private String remoteNick, localNick;
        private ServerSocket ss;
        private boolean busy;
        private SocketAddress listenAddress, remoteAddress;

        public CallListener() throws IOException{
                this("unnamed", null);
        }
	
	public CallListener(String localNick) throws IOException {
		this(localNick, null);
	}
	
	public CallListener(String localNick, String localIP) throws IOException {
		try {
                        ss = new ServerSocket(28411);
                } catch (IOException e) {
                        e.printStackTrace();
                }
		if (localIP != null)
		  ss.bind(new InetSocketAddress(localIP, 28411));
		this.localNick = localNick;
		this.listenAddress = ss.getLocalSocketAddress();
	}

        public Connection getConnection() throws IOException{
                Connection ic = new Connection(ss.accept());
		String tempMessage;
		if (ic.receive().getType() == Command.CommandType.NICK){
                        remoteNick = ic.receiveMessage();
			tempMessage = remoteNick.toUpperCase();
			remoteNick = remoteNick.substring(tempMessage.indexOf(" USER ", 9));
                        if (remoteNick.isEmpty()){
                                ic.close();
                        }
                        else{
                                if (busy){
                                        ic.sendNickBusy("2015", localNick);
					ic.disconnect();
                                        ic.close();
                                }
				else{
                                        ic.sendNickHello("2015", localNick);
					if (ic.receive().getType() != Command.CommandType.ACCEPT){
					  ic.disconnect();
					  ic.close();
					}
				}
                        }
                }
		else
		  ic.close();
                return ic;
        }

        public String getLocalNick() {
                return localNick;
        }

        public boolean isBusy() {
                return busy;
        }

        public SocketAddress getListenAddress(){
                return listenAddress;
        }


        public String getRemoteNick(){
                return remoteNick;
        }


        public SocketAddress getRemoteAddress(){
                return remoteAddress;
        }

        public void setLocalNick(String localNick){
                this.localNick = localNick;
        }

        public void setBusy(boolean busy){
                this.busy = busy;
        }

        public void setListenAddress(SocketAddress listenAddress){
                this.listenAddress = listenAddress;
        }
}
