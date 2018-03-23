import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

public class MrRobot {
    public Robot robo;

    public MrRobot() throws AWTException {
        try {
            robo = new Robot();
        } catch (AWTException e){
            throw e;
        }
    }


    public void typeMessage(String characters, int delay) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection( characters );
        clipboard.setContents(stringSelection, null);

        robo.delay(delay);

        robo.keyPress(KeyEvent.VK_CONTROL);
        robo.keyPress(KeyEvent.VK_V);
        robo.keyRelease(KeyEvent.VK_V);
        robo.keyRelease(KeyEvent.VK_CONTROL);

        robo.keyPress(KeyEvent.VK_ENTER);
        robo.keyRelease(KeyEvent.VK_ENTER);
    }
}
