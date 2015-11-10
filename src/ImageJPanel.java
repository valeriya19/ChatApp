import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ImageJPanel extends JPanel {
    Image background;

    public void paintComponent(Graphics gc) {
        super.paintComponent(gc);
        File imagefile = new File("C:\\Users\\Mac\\workspace\\Laba5\\src\\GN.jpg");
        try {
            background = ImageIO.read(imagefile);
            gc.drawImage(background, 0, 0, 100, 100, null);
            gc.drawImage(background, 100, 100, 100, 100, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}