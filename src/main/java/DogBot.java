import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

import org.ini4j.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.*;
import com.mashape.unirest.http.*;

public class DogBot {
    private static String[] dogSubreddits = {"rarepuppers", "dogswithjobs", "dog_irl"};
    private static String[] eightBallResponses =
            {"It is certain",
            "It is decidedly so",
            "Without a doubt",
            "Yes definitely",
            "You may rely on it",
            "As I see it, yes",
            "Most likely",
            "Outlook good",
            "Yes",
            "Signs point to yes",
            "Reply hazy try again",
            "Ask again later",
            "Better not tell you now",
            "Cannot predict now",
            "Concentrate and ask again",
            "Don't count on it",
            "My reply is no",
            "My sources say no",
            "Outlook not so good",
            "Very doubtful"};

    private static boolean running = true;
    private static int shutdownCode;
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

    private static void sendCtrlV() {
        explore.robot.robo.keyPress(KeyEvent.VK_CONTROL);
        explore.robot.robo.keyPress(KeyEvent.VK_V);
        explore.robot.robo.keyRelease(KeyEvent.VK_V);
        explore.robot.robo.keyRelease(KeyEvent.VK_CONTROL);
    }

    private static void sendImageFromURL(String imageURL) throws Exception {
        //Copy image to clipboard
        ImageToClipboard.setImageLinkToClipboard(imageURL);

        //Ctrl + V
        sendCtrlV();
    }

    private static void sendImage(String filePath) throws Exception{
        //Copy image to clipboard
        ImageToClipboard.setImageToClipboard(filePath);

        //Ctrl + V
        sendCtrlV();

    }

    private static void sendText(String text) {
        explore.robot.typeMessage(text, 0);
    }

    private static void sendText(String text, int delay) {
        explore.robot.typeMessage(text, delay);
    }

    private static <T> T pickRandom(T[] array) {
        return array[new Random().nextInt(array.length)];
    }

    private static String getSubredditPicture(String[] subreddits) throws Exception {
        boolean foundValidPicture = false;
        String formattedMatch = "";
        Pattern p = Pattern.compile("https://.+?(jpg|png)");

        while (!foundValidPicture) {
            //pick subreddit
            String subreddit = pickRandom(subreddits);

            //Set up subreddit string
            String redditURL = "https://www.reddit.com/r/" + subreddit + "/random.json";
            String responseString = getRedditJSONString(redditURL);

            Matcher m = Pattern.compile(", \"url\": \".+?\"").matcher(responseString);
            m.find();

            String match = m.group();
            formattedMatch = match.substring(10, match.length() - 1);

            Matcher matcher = p.matcher(formattedMatch);
            if (matcher.matches()) {
                foundValidPicture = true;
            }

            System.out.println(formattedMatch);



        }

        return formattedMatch;
    }

    private static void runCommandTriggers(String message) throws Exception {
        Matcher xkcdmatcher = Pattern.compile("!xkcd [1-9][0-9]{0,3}").matcher(message);
        Matcher eightballMatcher = Pattern.compile("(!8ball|!ask) .+").matcher(message);

        switch (message) {
            case "!dog": sendImageFromURL(getSubredditPicture(dogSubreddits)); Thread.sleep(500); break;

            case "!puptime":
            case "!uptime": sendUptime(); break;

            case "!stats": sendStats(); break;
            case "!tab": sendText("WEE WOO WEE WOO", 250); sendImage("resources/misc/tabulance.png");  break;
            case "!xkcd": getRandomXKCD(); break;

            case "!8ball":
            case "!ask": sendText("Please enter a question after the command"); break;

            case "!xkcd l":
            case "!xkcd latest": getLatestXKCD(); break;

            case "!github": sendText("Github repository: https://github.com/DistressedWhale/BotForDogsRedux"); break;
            case "!commands": sendText("A list of commands can be found at https://github.com/DistressedWhale/BotForDogsRedux/blob/master/README.md"); break;
            default:
                if (message.contains("good bot")) {
                    sendText("Thanks :D");
                } else if (message.contains("bad bot")) {
                    sendText("I'm sorry, I'll try and do better next time :( ");
                } else if (eightballMatcher.matches()) {
                    sendText(pickRandom(eightBallResponses));
                } else if (xkcdmatcher.matches()) {
                    int xkcdNumber = Integer.valueOf(message.substring(6));
                    getSpecificXKCD(xkcdNumber);
                } else if (message.equals("!shutdown " + shutdownCode)) {
                    running = false;
                    sendText("Shutting down");
                }

                //Make sure this is the last check.
                else if ('!' == message.charAt(0)) {
                    sendText("Unknown command");
                }
        }
    }

    private static String getPageSource(String myURL) throws Exception {
        return Unirest.get(myURL)
                .asString()
                .getBody();
    }

    private static String getRedditJSONString(String myURL) throws Exception {
        return Unirest.get(myURL)
                .header("User-agent", "BotForDogsRedux")
                .asString()
                .getBody();

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

    private static void getLatestXKCD() throws Exception {
        String title, alt, imgURL, number;

        JSONObject xkcdLatest = new JSONObject(getPageSource("https://xkcd.com/info.0.json"));
        number = xkcdLatest.get("num").toString();
        title = xkcdLatest.get("safe_title").toString();
        alt = xkcdLatest.get("alt").toString();
        imgURL = xkcdLatest.get("img").toString();

        sendText("Title: " + title + "\n" +
                "Number: " + number + "\n" +
                "Alt text: " + alt, 250);
        sendImageFromURL(imgURL);
    }

    private static void getRandomXKCD() throws Exception {
        String title, alt, imgURL;
        int highestNumber = Integer.valueOf(new JSONObject(getPageSource("https://xkcd.com/info.0.json")).get("num").toString());
        int randomXKCDNumber = new Random().nextInt(highestNumber) + 1;

        JSONObject randomXKCD = new JSONObject(getPageSource("https://xkcd.com/" + randomXKCDNumber + "/info.0.json"));

        title = randomXKCD.get("safe_title").toString();
        alt = randomXKCD.get("alt").toString();
        imgURL = randomXKCD.get("img").toString();

        sendText("Title: " + title + "\n" +
                "Number: " + randomXKCDNumber + "\n" +
                "Alt text: " + alt, 250);
        sendImageFromURL(imgURL);
    }

    private static void getSpecificXKCD(int number) throws Exception {
        String title, alt, imgURL;
        int highestNumber = Integer.valueOf(new JSONObject(getPageSource("https://xkcd.com/info.0.json")).get("num").toString());

        if (number > highestNumber || number < 1) {
            sendText("XKCD Number out of range. Please try XKCDs in range 0-" + highestNumber);
        } else {
            JSONObject specificXKCD = new JSONObject(getPageSource("https://xkcd.com/" + number + "/info.0.json"));

            title = specificXKCD.get("safe_title").toString();
            alt = specificXKCD.get("alt").toString();
            imgURL = specificXKCD.get("img").toString();

            sendText("Title: " + title + "\n" +
                    "Number: " + number + "\n" +
                    "Alt text: " + alt, 250);
            sendImageFromURL(imgURL);
        }
    }

    public static void main(String[] args) throws Exception {
        boolean lastMessageWasCommand = false;

        shutdownCode = new Random().nextInt(99999);
        System.out.println("Shutdown code: " + shutdownCode);

        boolean testingMode = false;
        for (String arg : args) {
            if (arg.equals("-t") || arg.equals("-testing")) {
                testingMode = true;
            }
        }

        logInWithIni(new File("config/config.ini"), testingMode);
        sendText("Dog Bot Redux online\nRunning version " + botVersion + " built on " + dateBuilt);
        startTime = new Date();

        while (running) {
            String newMessage = getLatestMessage();

            if (newMessage.compareTo(message) != 0) {
                message = newMessage;

                System.out.println(message);

                runCommandTriggers(message.toLowerCase().trim());
            }

            Thread.sleep(updateRate);


        }
    }

}
