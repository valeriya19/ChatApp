import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class Application {
    public static ChatForm form = new ChatForm();

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    form.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    form.setVisible(true);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            System.exit(1);
        }
    }
}
