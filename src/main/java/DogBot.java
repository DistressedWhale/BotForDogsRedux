import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import org.ini4j.*;

import java.io.File;
import java.util.ArrayList;

public class DogBot {

    static String message = "";
    static private WebExplorer explore;
    static String botVersion, dateBuilt, testThreadID, email, password, threadID;

    private static void logInWithIni(File iniFile) throws IOException, AWTException{

        Wini ini = new Wini(iniFile);

        email = ini.get("configuration", "email");
        password = ini.get("configuration", "password");
        threadID = ini.get("configuration", "threadID");
        testThreadID = ini.get("configuration", "testThreadID");

        botVersion = ini.get("information", "version");
        dateBuilt = ini.get("information",  "date");

        explore = new WebExplorer();
        explore.logIn(email, password, threadID);

    }

    private static String getLatestMessage() {
        ArrayList<String> a = ProcessSource.getFormattedMessages(explore.getPageSource());

        return a.get(a.size() - 1);
    }

    private static void sendImage(String filePath) throws Exception{
        //Copy image to clipboard
        ImageToClipboard.setImageToClipboard(filePath);

        //Ctrl + V
        explore.robot.robo.keyPress(KeyEvent.VK_CONTROL);
        explore.robot.robo.keyPress(KeyEvent.VK_V);
        explore.robot.robo.keyRelease(KeyEvent.VK_V);
        explore.robot.robo.keyRelease(KeyEvent.VK_CONTROL);

    }

    private static void sendText(String text) {
        explore.robot.typeMessage(text, 0);
    }

    private static void runCommandTriggers(String message) throws Exception {

        if (message.equals("_dog")) {
            sendText("A picture of a dog");
            sendImage("resources/dogs/dog.png");

        } else if (message.equals("woof")) {
            sendText("Woof!");
        }
    }

    public static void main(String[] args) throws Exception {
        logInWithIni(new File("config/config.ini"));

        while (true) {
            String newMessage = getLatestMessage();
            if (newMessage.compareTo(message) != 0) {
                message = newMessage;

                System.out.println(message);

                runCommandTriggers(message.toLowerCase());
            }

            Thread.sleep(500);


        }
    }

}
