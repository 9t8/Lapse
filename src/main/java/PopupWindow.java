import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class PopupWindow {

    PopupWindow(WPanel wPanel) throws IOException {
        wPanel.pause();
        BufferedImage pic = wPanel.getWebcam().getImage();


        JFrame frame = new JFrame("Keep Photo?");
        JButton keepB = new JButton();
        JButton discardB = new JButton();
        JLabel txt = new JLabel();

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 65);
        frame.setLayout(null);
        frame.setAlwaysOnTop(true);

        keepB.setBounds(0, 0, 100, 25);
        discardB.setBounds(100, 0, 100, 25);

        keepB.setText("Keep");
        discardB.setText("Discard");

        frame.add(keepB);
        frame.add(discardB);

        keepB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    long sysTime = System.currentTimeMillis();
                    ImageIO.write(pic, "PNG", new File(System.getProperty("user.home") + "/lapse-photos/" + sysTime + "-raw.png"));
                    BufferedImage b = ImageIO.read(new File(System.getProperty("user.home") + "/lapse-photos/" + sysTime + "-raw.png"));
                    ImageIO.write(Detection.crop(b), "PNG", new File(System.getProperty("user.home") + "/lapse-photos/" + sysTime + "-cropped.png"));
                    wPanel.resume();
                    frame.dispose();
                } catch (IOException | NoninvertibleTransformException ex) {
                    ex.printStackTrace();
                }
            }

        });

        discardB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                wPanel.resume();
                frame.dispose();
            }
        });

        frame.setVisible(true);
    }

}
