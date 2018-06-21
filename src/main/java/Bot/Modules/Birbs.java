package Bot.Modules;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import Bot.DogBot;

public class Birbs {

    public static void sendBirb() throws Exception {
        if (new Random().nextInt(100) > 25) {
            DogBot.sendText("Swiggity swooty.", 300);
            DogBot.waitFor("Swiggity swooty.");
        } else {
            String response = DogBot.pickRandom(Files.readAllLines(Paths.get("config/birbResponses.txt"), StandardCharsets.UTF_8));
            DogBot.sendText(response, 250);
            DogBot.waitFor(response);
        }

        DogBot.sendImageFromURL(Reddit.getSubredditPicture(Files.readAllLines(Paths.get("config/birbSubreddits.txt"), StandardCharsets.UTF_8)));
    }

}
