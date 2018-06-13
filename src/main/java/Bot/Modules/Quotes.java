package Bot.Modules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import Bot.DogBot;

public class Quotes {
    public static void doQuote() {
        DogBot.sendText(("Quote: \"" + DogBot.pickRandom(DogBot.quotes) + "\"").replaceAll("&#10;","\n"));
    }

    public static void doGrab() throws Exception {
        String grabbedMessage = DogBot.messageHistory.get(DogBot.messageHistory.size() - 1);
        if (!(DogBot.matches("Grabbed \".+?\"", grabbedMessage) || DogBot.matches("Quote: \".+?\"", grabbedMessage))) {
            DogBot.sendText("Grabbed \"" + grabbedMessage + "\"");

            Files.write(Paths.get("config/quotes.txt"), (grabbedMessage + System.getProperty("line.separator")).getBytes(), StandardOpenOption.APPEND);

            //Reload files
            DogBot.loadFiles();
        } else {
            DogBot.sendText("Don't do that >:(");
        }
    }

    public static void doGrab(int offset) throws Exception {
        if (offset < DogBot.messageHistory.size() - 1) {
            String grabbedMessage = DogBot.messageHistory.get(DogBot.messageHistory.size() - 1 - offset);

            if (!(DogBot.matches("Grabbed \".+?\"", grabbedMessage) || DogBot.matches("Quote: \".+?\"", grabbedMessage))) {
                DogBot.sendText("Grabbed \"" + grabbedMessage + "\"");

                Files.write(Paths.get("config/quotes.txt"), ( grabbedMessage + System.getProperty("line.separator")).getBytes(), StandardOpenOption.APPEND);

                //Reload files
                DogBot.loadFiles();
            } else {
                DogBot.sendText("Don't do that >:(");
            }
        } else {
            DogBot.sendText("Grab offset is out of range");
        }
    }

    public static void doGrabSearch(String searchString) throws Exception  {
        ArrayList<String> messages = DogBot.messageHistory;
        int i = messages.size() - 1;
        boolean found = false;

        while (i >= 0 && !found) {
            if (messages.get(i).contains(searchString)) {

                found = true;
                DogBot.sendText("Grabbed \"" + messages.get(i) + "\"");
                Files.write(Paths.get("config/quotes.txt"), ( messages.get(i) + System.getProperty("line.separator")).getBytes(), StandardOpenOption.APPEND);

                //Reload files
                DogBot.loadFiles();
            } else {
                i--;
            }
        }

        if (!found) {
            DogBot.sendText("Message not found");
        }
    }
}
