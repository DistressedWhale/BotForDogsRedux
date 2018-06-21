package Bot.Modules;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import Bot.DogBot;

public class Cats {
    public static void sendCat() throws Exception {
        if (new Random().nextInt(100) > 25) {
            DogBot.sendText("Meow.", 300);
            DogBot.waitFor("Meow.");
        } else {
            String response = DogBot.pickRandom(Files.readAllLines(Paths.get("config/catResponses.txt"), StandardCharsets.UTF_8));
            DogBot.sendText(response, 250);
            DogBot.waitFor(response);
        }

        Thread.sleep(250);
        DogBot.sendImageFromURL(Reddit.getSubredditPicture(Files.readAllLines(Paths.get("config/catSubreddits.txt"), StandardCharsets.UTF_8)));

    }
}
