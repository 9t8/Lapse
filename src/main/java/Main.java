import javax.swing.*;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws AWTException, IOException {
        File timeFile = new File(System.getProperty("user.home") + "/lapse-photos/.prevTime");
        if (!timeFile.getParentFile().mkdir() && !timeFile.getParentFile().exists()) {
            System.err.println(":(");
            return;
        }
        if (timeFile.createNewFile()) {
            FileWriter timeFileWriter = new FileWriter(timeFile);
            timeFileWriter.write("0");
            timeFileWriter.close();
        }

        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");

        TrayIcon trayIcon = new TrayIcon(image);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Lapse: Take Photo");

        WPanel wPanel = new WPanel();

        trayIcon.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JFrame frame = new JFrame("Click to Take Photo");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setSize(1280, 960);
                frame.setLocationRelativeTo(null);
                frame.add(wPanel);
                frame.setVisible(true);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
            }
        });

        SystemTray.getSystemTray().add(trayIcon);

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try (Scanner scanner = new Scanner(timeFile)) {
                    if (scanner.nextLong() / 20000 != System.currentTimeMillis() / 20000) {
                        trayIcon.displayMessage("Lapse: Take your daily photo", "Click the Lapse icon in your System tray.", MessageType.NONE);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}