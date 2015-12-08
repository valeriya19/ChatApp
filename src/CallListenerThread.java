import java.io.IOException;
import java.util.Observable;

/**
 * @author katebudyanskaya
 */
class CallListenerThread extends Observable implements Runnable {
    private Connection lastConnection;
    private final CallListener cl;
    private boolean sleep;
    private final Thread thisThread;

    public CallListenerThread(CallListener listener) throws IOException {
        cl = listener;
        thisThread = new Thread(this);
    }

    public void start() {
        thisThread.start();
    }

    public void stop() {
        sleep = true;
    }

    public void run() {
        do {
            try {
                Connection checked = cl.getConnection();
                if (checked != null) {
                    lastConnection = checked;
                    setChanged();
                    notifyObservers();
                }
            } catch (IOException ex) {
                sleep = true;
            }
        } while (!sleep);
    }

    public Connection getLastConnection() {
        return lastConnection;
    }

    public CallListener getCallListener() {
        return cl;
    }
}
