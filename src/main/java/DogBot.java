import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import org.ini4j.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.util.ArrayList;

public class DogBot {

    private static String message = "";
    static private WebExplorer explore;
    private static String botVersion, dateBuilt, testThreadID, email, password, threadID;
    private static int updateRate;
    private static Date startTime;

    private static void logInWithIni(File iniFile, boolean testing) throws IOException, AWTException{

        Wini ini = new Wini(iniFile);

        email = ini.get("configuration", "email");
        password = ini.get("configuration", "password");
        threadID = ini.get("configuration", "threadID");
        testThreadID = ini.get("configuration", "testThreadID");

        botVersion = ini.get("information", "version");
        dateBuilt = ini.get("information",  "date");

        updateRate = ini.get("botInfo", "updateRate", int.class);

        explore = new WebExplorer();

        if (testing) {
            explore.logIn(email, password, testThreadID);
        } else {
            explore.logIn(email, password, threadID);
        }

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

    private static void sendText(String text, int delay) {
        explore.robot.typeMessage(text, delay);
    }

    private static void runCommandTriggers(String message) throws Exception {

        switch (message) {
            case "!dog": sendImage("resources/dogs/dog.png"); break;
            case "!uptime": sendUptime(); break;
            case "!stats": sendStats(); break;
            case "!tab": sendText("WEE WOO WEE WOO", 250); sendImage("resources/misc/tabulance.png");  break;
        }
    }

    private static void sendUptime() {
        Date currentDate = new Date();
        long timeDiff = currentDate.getTime() - startTime.getTime();

        String s = String.format("I've been running for %d hours, %d minutes and %d seconds.",
                TimeUnit.MILLISECONDS.toHours(timeDiff),
                TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)),
                TimeUnit.MILLISECONDS.toSeconds(timeDiff) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDiff)));


        sendText(s, 250);
    }

    private static void sendStats() {
        sendText(   "Version: " + botVersion + "\n" +
                    "Built on: " + dateBuilt + "\n" +
                    "Update rate: " + updateRate + "ms");
    }

    public static void main(String[] args) throws Exception {

        boolean testingMode = false;
        for (String arg : args) {
            if (arg.equals("-t") || arg.equals("-testing")) {
                testingMode = true;
            }
        }

        logInWithIni(new File("config/config.ini"), testingMode);
        sendText("Dog Bot Redux online\nRunning version " + botVersion + " built on " + dateBuilt);
        startTime = new Date();

        while (true) {
            String newMessage = getLatestMessage();
            if (newMessage.compareTo(message) != 0) {
                message = newMessage;
                System.out.println(message);

                runCommandTriggers(message.toLowerCase());
            }

            Thread.sleep(updateRate);


        }
    }

}
