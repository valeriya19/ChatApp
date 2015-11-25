import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;

/**
 *
 * @author katebudyanskaya
 */
class CallListener{
        private String remoteNick, localNick;
        private ServerSocket ss;
        private boolean busy;
        private SocketAddress listenAddress, remoteAddress;

        public CallListener() throws IOException{
                this(Protocol.defaultLocalNick, Protocol.defaultLocalIPAddress);
        }
	
	public CallListener(String localNick) throws IOException {
		this(localNick, Protocol.defaultLocalIPAddress);
	}
	
	public CallListener(String localNick, String localIP) throws IOException {
                ss = new ServerSocket(Protocol.port);
		ss.bind(new InetSocketAddress(localIP, Protocol.port));
		this.localNick = localNick;
		this.listenAddress = ss.getLocalSocketAddress();
	}

        public Connection getConnection() throws IOException{
                return new Connection(ss.accept());
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
