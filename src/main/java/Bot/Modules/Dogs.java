package Bot.Modules;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import Bot.DogBot;

public class Dogs {

    public static void sendDogSubreddits() throws IOException {
        String subreddits = "Subreddits used in !dog:\n";
        for (String subreddit : Files.readAllLines(Paths.get("config/dogSubreddits.txt"))) {
            subreddits = subreddits.concat("r/" + subreddit + "\n");
        }

        DogBot.sendText(subreddits);
    }

    public static void sendDog() throws Exception {
        if (new Random().nextInt(100) > 25) {
            DogBot.sendText("Woof.", 300);
            DogBot.waitFor("Woof.");
        } else {
            String response = DogBot.pickRandom(Files.readAllLines(Paths.get("config/dogResponses.txt"), StandardCharsets.UTF_8));
            DogBot.sendText(response, 250);
            DogBot.waitFor(response);
        }

        DogBot.sendImageFromURL(Reddit.getSubredditPicture(Files.readAllLines(Paths.get("config/dogSubreddits.txt"), StandardCharsets.UTF_8)));

    }
}
