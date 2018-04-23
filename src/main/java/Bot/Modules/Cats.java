package Bot.Modules;

import java.util.Random;
import Bot.DogBot;

public class Cats {
    public static void sendCat() throws Exception {
        if (new Random().nextInt(100) > 25) {
            DogBot.sendText("Meow.", 300);
            DogBot.waitFor("Meow.");
        } else {
            String response = DogBot.pickRandom(DogBot.catResponses);
            DogBot.sendText(response, 250);
            DogBot.waitFor(response);
        }

        Thread.sleep(250);
        DogBot.sendImageFromURL(Reddit.getSubredditPicture(DogBot.catSubreddits));

    }
}
