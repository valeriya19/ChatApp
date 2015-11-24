import java.io.IOException;
import java.util.Observable;

/**
 * @author M-Sh-97
 */
class CommandListenerThread extends Observable implements Runnable {

    private Command lastCommand;
    private final Connection connection;
    private boolean stopped;
    private final Thread thisThread;

    public CommandListenerThread(Connection con) {
        connection = con;
	thisThread = new Thread(this);
    }

    public Command getLastCommand() {
        return lastCommand;
    }

    public boolean isDisconnected() {
        return stopped;
    }

    public void start() {
        thisThread.start();
    }

    public void stop() {
        stopped = true;
    }

    public void run() {
        do {
            try {
                Command checked = connection.receive();
                if (checked != null) {
                    lastCommand = checked;
		    setChanged();
                    notifyObservers();
                }
            } catch (IOException ex) {
                stopped = true;
            }
        } while (!stopped);
    }
}
