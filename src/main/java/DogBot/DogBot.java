package DogBot;

import java.awt.event.KeyEvent;
import org.ini4j.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.*;
import java.text.SimpleDateFormat;

import org.json.*;
import com.mashape.unirest.http.*;
import java.nio.file.*;
import java.nio.charset.*;

public class DogBot {
    private static String[] dogSubreddits, eightBallResponses, dogResponses, listOfSadness, quotes;
    private static ArrayList<String> messageHistory = new ArrayList<>();

    private static boolean running = true;
    private static String shutdownCode;
    private static String message = "";
    static private WebExplorer explore;
    private static String botVersion, dateBuilt, testThreadID, email, password, threadID;
    private static int updateRate;
    private static Date startTime;

    private static void loadFiles() throws Exception {
        List<String> dogResponseList = Files.readAllLines(Paths.get("config/dogResponses.txt"), StandardCharsets.UTF_8);
        dogResponses = dogResponseList.toArray(new String[dogResponseList.size()]);

        List<String> eightBallResponseList = Files.readAllLines(Paths.get("config/8BallResponses.txt"), StandardCharsets.UTF_8);
        eightBallResponses = eightBallResponseList.toArray(new String[eightBallResponseList.size()]);

        List<String> dogSubredditList = Files.readAllLines(Paths.get("config/dogSubreddits.txt"), StandardCharsets.UTF_8);
        dogSubreddits = dogSubredditList.toArray(new String[dogSubredditList.size()]);

        List<String> listOfSadnessList = Files.readAllLines(Paths.get("config/listOfSadness.txt"), StandardCharsets.UTF_8);
        listOfSadness = listOfSadnessList.toArray(new String[listOfSadnessList.size()]);

        List<String> quotesList = Files.readAllLines(Paths.get("config/quotes.txt"), StandardCharsets.UTF_8);
        quotes = quotesList.toArray(new String[quotesList.size()]);
    }

    private static void logInWithIni(File iniFile, boolean testing) throws Exception {

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

    private static void sendListOfSadness() {
        String dates = "Upcoming dates:\n";

        for (String thing : listOfSadness) {
            dates = dates.concat(thing + "\n");
        }

        sendText(dates);

    }

    private static void sendDogSubreddits() {
        String subreddits = "Subreddits used in !dog:\n";
        for (String subreddit : dogSubreddits) {
            subreddits = subreddits.concat("r/" + subreddit + "\n");
        }

        sendText(subreddits);
    }

    private static void sendCtrlV() {
        explore.robot.robo.keyPress(KeyEvent.VK_CONTROL);
        explore.robot.robo.keyPress(KeyEvent.VK_V);
        explore.robot.robo.keyRelease(KeyEvent.VK_V);
        explore.robot.robo.keyRelease(KeyEvent.VK_CONTROL);
    }

    private static void doQuote() {
        sendText(pickRandom(quotes));
    }

    private static void sendImageFromURL(String imageURL) throws Exception {
        System.out.println("Going to send image: " + imageURL);

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

    private static void sendAGoodDog() throws Exception {
        if (new Random().nextInt(100) > 25) {
            sendText("Woof.", 250);
        } else {
            sendText(pickRandom(dogResponses), 250);
        }

        Thread.sleep(250);
        sendImageFromURL(getSubredditPicture(dogSubreddits));

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


        }

        return formattedMatch;
    }

    private static void doGrab() throws Exception {
        String grabbedMessage = messageHistory.get(messageHistory.size() - 1);

        sendText("Grabbed \"" + grabbedMessage + "\"");

        Files.write(Paths.get("config/quotes.txt"), ("\"" + grabbedMessage + "\"" + System.getProperty("line.separator")).getBytes(), StandardOpenOption.APPEND);

        //Reload files
        loadFiles();
    }

    private static void doGrab(int offset) throws Exception {
        if (offset < messageHistory.size() - 1) {
            String grabbedMessage = messageHistory.get(messageHistory.size() - 1 - offset);
            sendText("Grabbed \"" + grabbedMessage + "\"");

            Files.write(Paths.get("config/quotes.txt"), ("\"" + grabbedMessage + "\"" + System.getProperty("line.separator")).getBytes(), StandardOpenOption.APPEND);

            //Reload files
            loadFiles();
        } else {
            sendText("Grab offset is out of range");
        }
    }


    private static void runCommandTriggers(String message) throws Exception {
        Matcher xkcdmatcher = Pattern.compile("!xkcd [1-9][0-9]{0,3}").matcher(message);
        Matcher eightballMatcher = Pattern.compile("(!8ball|!ask) .+").matcher(message);
        Matcher grabOffsetMatcher = Pattern.compile("!grab ([0-9])|(10)").matcher(message);

        switch (message) {
            case "!dog": sendAGoodDog(); break;

            case "!dogreddits":
            case "!subreddits": sendDogSubreddits(); break;

            case "!puptime":
            case "!uptime": sendUptime(); break;

            case "!stats": sendStats(); break;
            case "!tab": sendText("WEE WOO WEE WOO", 250); sendImage("resources/misc/tabulance.png");  break;
            case "!xkcd": getRandomXKCD(); break;

            case "!8ball":
            case "!ask": sendText("Please enter a question after the command"); break;

            case "!xkcd l":
            case "!xkcd latest": getLatestXKCD(); break;

            case "!grab": doGrab(); break;
            case "!quote": doQuote(); break;
            case "!dates": sendListOfSadness(); break;

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
                } else if (grabOffsetMatcher.matches()) {
                    doGrab(Integer.valueOf(message.substring(6)));
                }

                //Make sure this is the last check.
                else if ('!' == message.charAt(0)) {
                    sendText("Unknown command");
                }
        }
    }

    private static String getPageSource(String myURL) throws Exception {return Unirest.get(myURL).asString().getBody();}

    private static String getRedditJSONString(String myURL) throws Exception {
        return Unirest.get(myURL).header("User-agent", "BotForDogsRedux").asString().getBody();
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
        loadFiles();
        shutdownCode = (Integer.toString(new Random().nextInt(99999)));
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

            if (newMessage.compareTo(message) != 0 && !(newMessage.equals("\" class="))) {
                message = newMessage;

                System.out.println(message);

                runCommandTriggers(message.toLowerCase().trim());
                messageHistory.add(message);
            }

            Thread.sleep(updateRate);


        }
    }

}
