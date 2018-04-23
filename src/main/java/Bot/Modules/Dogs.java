package Bot.Modules;

import java.util.Random;
import Bot.DogBot;

public class Dogs {

    public static void sendDogSubreddits() {
        String subreddits = "Subreddits used in !dog:\n";
        for (String subreddit : DogBot.dogSubreddits) {
            subreddits = subreddits.concat("r/" + subreddit + "\n");
        }

        DogBot.sendText(subreddits);
    }

    public static void sendDog() throws Exception {
        if (new Random().nextInt(100) > 25) {
            DogBot.sendText("Woof.", 300);
            DogBot.waitFor("Woof.");
        } else {
            String response = DogBot.pickRandom(DogBot.dogResponses);
            DogBot.sendText(response, 250);
            DogBot.waitFor(response);
        }

        DogBot.sendImageFromURL(Reddit.getSubredditPicture(DogBot.dogSubreddits));

    }
}
