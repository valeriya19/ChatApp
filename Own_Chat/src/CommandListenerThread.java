import java.io.IOException;
import java.util.Observable;

/**
 *
 * @author M-Sh-97
 */
class CommandListenerThread extends Observable implements Runnable {
  
  Command lastCommand;
  Connection connection;
  boolean stopped;
  
  public CommandListenerThread(Connection con) {
    connection = con;
  }
  
  public Command getLastCommand() {
    return lastCommand;
  }
  
  public boolean isDisconnected() {
    return stopped;
  }
  
  public void start() {
    run();
  }
  
  public void stop() {
    stopped = true;
  }
  
  public void run() {
    do {
      try {
	lastCommand = connection.receive();
      } catch (IOException ex) {}
      notifyObservers();
    } while (! stopped);
  }
  
  public static void main(String[] args) {
    
  }
}
