
import java.util.Observable;

/**
 * Created by 81k5_Pr0g3r on 22.12.15.
 */
public class ConnectionListenerThread extends Observable implements Runnable{
    Connection connection;
    boolean stopped;

    public ConnectionListenerThread(Connection con) {
        connection = con;
    }

    public void stop() {
        stopped = true;
    }

    public void run() {
        do {
            Command checked = connection.readCommand();
            if (checked != null) {
                setChanged();
                notifyObservers(checked);
            }
        } while (!stopped);
    }
}
