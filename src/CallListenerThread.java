import java.io.IOException;
import java.net.*;
import java.util.Observable;

/**
 *
 * @author katebudyanskaya
 */
class CallListenerThread extends Observable implements Runnable{
  
    private Connection lastConnection;
    private CallListener cl;
    private boolean sleep;
    
    public CallListenerThread() throws IOException{
    	cl = new CallListener();
    }

    public CallListenerThread(String localNick) throws IOException{
    	cl = new CallListener(localNick);
    }
    		
    public CallListenerThread(String localNick, String localIP) throws IOException{
    	cl = new CallListener(localNick, localIP);
    }

    public void start(){
        run();
    }

    public void stop(){
	sleep = true;
    }
	
    public void run(){
	do {
	    try {
        	Connection checked = cl.getConnection();
                if (checked != null) {
                    lastConnection = checked;
                    notifyObservers();
                }
            } catch (IOException ex) {
                sleep = true;
            }
        } while (!sleep);
    }
	
	
    public boolean isBusy(){
	return cl.isBusy();
    }

    public SocketAddress getListenAddress(){
	return cl.getListenAddress();
    }

    public String getRemoteNick(){
	return cl.getRemoteNick();
    }

    public SocketAddress getRemoteAddress(){
	return cl.getRemoteAddress();
    }

    public void setLocalNick(String localNick){
	cl.setLocalNick(localNick);
    }
	
    public void setBusy(boolean busy){
  	cl.setBusy(busy);
    }
	
    public void setListenAddress(SocketAddress listenAddress){
	cl.setListenAddress(listenAddress);
    }
	
    public String getLocalNick(){
	return cl.getLocalNick();
    }
}
