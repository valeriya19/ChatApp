import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Application {
    public static String localNick;
    public static ChatForm form = new ChatForm();
    
    public static void main(String[] args) {
      try {
	SwingUtilities.invokeAndWait(new Runnable() {
	  public void run() {
	    form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    form.setVisible(true);
	  }
	});
      } catch (InterruptedException | InvocationTargetException ex) {
	System.exit(1);
      }
      	
    }
}
