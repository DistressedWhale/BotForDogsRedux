package Bot;

import java.awt.event.KeyEvent;

import Bot.Utilities.*;
import Bot.Modules.*;
import org.ini4j.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.*;
import org.json.*;
import com.mashape.unirest.http.*;
import java.nio.file.*;
import java.nio.charset.*;

public class DogBot {
    public static List<String> dogSubreddits, eightBallResponses, dogResponses, listOfSadness, quotes, dogOnlineMessages, dogShutdownMessages, extraGoodDogs, catSubreddits, catResponses, catReacts;
    public static ArrayList<String> messageHistory = new ArrayList<>();

    private static boolean running = true;
    private static String shutdownCode;
    private static String message = "";
    static private WebExplorer explore;
    private static String botVersion;
    private static int updateRate, goodcount, badcount;
    private static int messageCount = 0;
    private static Date startTime;

    public static void loadFiles() throws Exception {
        dogResponses = Files.readAllLines(Paths.get("config/dogResponses.txt"), StandardCharsets.UTF_8);

        catResponses = Files.readAllLines(Paths.get("config/catResponses.txt"), StandardCharsets.UTF_8);

        eightBallResponses = Files.readAllLines(Paths.get("config/8BallResponses.txt"), StandardCharsets.UTF_8);

        dogSubreddits = Files.readAllLines(Paths.get("config/dogSubreddits.txt"), StandardCharsets.UTF_8);

        catSubreddits = Files.readAllLines(Paths.get("config/catSubreddits.txt"), StandardCharsets.UTF_8);

        listOfSadness = Files.readAllLines(Paths.get("config/listOfSadness.txt"), StandardCharsets.UTF_8);

        quotes = Files.readAllLines(Paths.get("config/quotes.txt"), StandardCharsets.UTF_8);

        dogOnlineMessages = Files.readAllLines(Paths.get("config/dogOnlineMessages.txt"), StandardCharsets.UTF_8);

        dogShutdownMessages = Files.readAllLines(Paths.get("config/dogShutdownMessages.txt"), StandardCharsets.UTF_8);

        extraGoodDogs = Files.readAllLines(Paths.get("config/extraGoodDogs.txt"), StandardCharsets.UTF_8);

        catReacts = Files.readAllLines(Paths.get("config/catReacts.txt"), StandardCharsets.UTF_8);

        Wini ini = new Wini(new File("config/config.ini"));
        goodcount = ini.get("ratings", "goodratings", int.class);
        badcount = ini.get("ratings", "badratings", int.class);

    }

    private static void logInWithIni(File iniFile, boolean testing) throws Exception {

        Wini ini = new Wini(iniFile);

        String email = ini.get("configuration", "email");
        String password = ini.get("configuration", "password");
        String threadID = ini.get("configuration", "threadID");
        String testThreadID = ini.get("configuration", "testThreadID");

        botVersion = ini.get("information", "version");
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

    public static boolean matches(String regex, String string) {
        Matcher matcher = Pattern.compile(regex).matcher(string);
        return matcher.matches();
    }

    private static void sendCtrlV() {
        explore.robot.keyPress(KeyEvent.VK_CONTROL);
        explore.robot.keyPress(KeyEvent.VK_V);

        explore.robot.delay(100);

        explore.robot.keyRelease(KeyEvent.VK_V);
        explore.robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public static void sendImageFromURL(String imageURL) throws Exception {
        System.out.println("Going to send image: " + imageURL);

        //Copy image to clipboard
        ImageToClipboard.setImageLinkToClipboard(imageURL);

        //Ctrl + V
        sendCtrlV();
    }

    public static void sendImage(String filePath) throws Exception{
        //Copy image to clipboard
        ImageToClipboard.setImageToClipboard(filePath);

        //Ctrl + V
        sendCtrlV();

    }

    public static void sendText(String text) {
        explore.typeMessage(text, 0);
    }

    public static void sendText(String text, int delay) {
        explore.typeMessage(text, delay);
    }

    public static <T> T pickRandom(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }


    private static void getGood() {
        sendText(String.format("I am %s%% good, based off %s ratings (%s good, %s bad)", (int) Math.round(100.0 * ((float) goodcount / (float) (goodcount+badcount))), (goodcount + badcount), goodcount, badcount));
    }

    private static void updateRatings() throws IOException {
        Wini ini = new Wini(new File("config/config.ini"));
        ini.remove("goodratings");
        ini.remove("badratings");
        ini.put("ratings", "goodratings", goodcount);
        ini.put("ratings", "badratings", badcount);
    }

    private static void doRTD(String message) {
        try {
            int number = Integer.valueOf(message.substring(5));
            sendText("You rolled " + String.valueOf(new Random().nextInt(number) + 1));
        } catch (NumberFormatException e) {
            sendText("RTD value out of range.");
        }
    }


    private static void runCommandTriggers(String message) throws Exception {

        switch (message) {
            case "!dog": Dogs.sendDog(); break;
            case "!cat": Cats.sendCat(); break;

            case "!dogreddits":
            case "!subreddits": Dogs.sendDogSubreddits(); break;

            case "!puptime":
            case "!uptime": sendUptime(); break;

            case "!stats": sendStats(); break;
            case "!tab":
                sendText("WEE WOO WEE WOO", 200);
                waitFor("WEE WOO WEE WOO");
                sendImage("resources/misc/tabulance.png");   break;
            case "!xkcd": XKCD.getRandomXKCD(); break;

            case "!rtd": sendText("You rolled " + String.valueOf(new Random().nextInt(6) + 1)); break;

            case "!8ball":
            case "!ask": sendText("Please enter a question after the command"); break;

            case "!ping": sendText("Pong!"); break;

            case "!xkcd l":
            case "!xkcd latest": XKCD.getLatestXKCD(); break;

            case "!grab": Quotes.doGrab(); break;
            case "!quote": Quotes.doQuote(); break;
            case "!dates": sendListOfSadness(); break;
            case "!extragooddog":
                sendText("Woof.",500);
                waitFor("Woof.");
                sendImageFromURL(pickRandom(extraGoodDogs));
                break;
            case "!reload": loadFiles(); break;

            case "!github": sendText("Github repository: https://github.com/DistressedWhale/BotForDogsRedux"); break;
            case "!howgood": getGood(); break;
            case "!help":
            case "!commands": sendText("A list of commands can be found at https://github.com/DistressedWhale/BotForDogsRedux/blob/master/README.md"); break;
            default:
                if (message.contains("good bot") || message.contains("good boy") || message.contains("good dog")) {
                    sendText("Thanks :D");
                    goodcount++;
                    updateRatings();

                } else if (message.contains("bad bot") || message.contains("bad dog")) {
                    sendText("I'm sorry, I'll try and do better next time :( ");
                    badcount++;
                    updateRatings();

                } else if (matches("(!8ball|!ask) .+", message)) {
                    sendText(pickRandom(eightBallResponses));

                } else if (matches("(!react|!catreact|!reaction|!reacc|catreacc) .+", message)) {
                    sendText("Judging.");
                    waitFor("Judging.");

                    sendImageFromURL(pickRandom(catReacts));

                } else if (matches("!xkcd [1-9][0-9]{0,3}", message)) {
                    int xkcdNumber = Integer.valueOf(message.substring(6));
                    XKCD.getSpecificXKCD(xkcdNumber);

                } else if (message.equals("!shutdown " + shutdownCode)) {
                    running = false;
                    sendText("Shutting down\n" + pickRandom(dogShutdownMessages));

                } else if (matches("!rtd [1-9][0-9]{0,10}", message)) {
                    doRTD(message);

                } else if (matches("!grab \\d+", message)) {
                    Quotes.doGrab(Integer.valueOf(message.substring(6)));

                }

                //Make sure this is the last check.
                else if ('!' == message.charAt(0)) {
                    sendText("Unknown command");
                }
        }
    }

    public static String getPageSource(String myURL) throws Exception {return Unirest.get(myURL).asString().getBody();}

    public static void waitFor(String expected) throws InterruptedException {
        Boolean stringFound = false;
        int cycles = 0;

        //If the message doesn't show up in 400 cycles, carry on to the next step anyway
        while ((!stringFound) && cycles <= 100) {

            if (getLatestMessage().equals(expected)) {
                stringFound = true;
            } else {
                Thread.sleep(updateRate / 4);
                cycles++;
            }
        }

        //Wait an extra tick to be safe
        Thread.sleep(updateRate);
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
                    "Update rate: " + updateRate + "ms\n" +
                    "Java version: " + System.getProperty("java.version") + "\n" +
                    "Operating system: " + System.getProperty("os.name") + "\n\n" +

                    "Unique messages read this session: " + messageCount);
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

        sendText("Dog Bot Redux version " + botVersion +  " online\n" + pickRandom(dogOnlineMessages));
        startTime = new Date();

        while (running) {
            String newMessage = getLatestMessage();

            if (newMessage.compareTo(message) != 0 && !(newMessage.equals("\" class="))) {
                message = newMessage;

                System.out.println(message);
                messageCount ++;

                runCommandTriggers(message.toLowerCase().trim());
                messageHistory.add(message);
            }

            //Scroll down to make sure the bot keeps responding
            explore.robot.mouseWheel(100);
            Thread.sleep(updateRate);


        }
    }

}
