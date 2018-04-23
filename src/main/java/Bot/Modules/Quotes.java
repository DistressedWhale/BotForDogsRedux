package Bot.Modules;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
}
