package Bot.Modules;

import java.util.Random;
import Bot.DogBot;

public class Birbs {

    public static void sendBirb() throws Exception {
        if (new Random().nextInt(100) > 25) {
            DogBot.sendText("Swiggity swooty.", 300);
            DogBot.waitFor("Swiggity swooty.");
        } else {
            String response = DogBot.pickRandom(DogBot.birbResponses);
            DogBot.sendText(response, 250);
            DogBot.waitFor(response);
        }

        DogBot.sendImageFromURL(Reddit.getSubredditPicture(DogBot.birbSubreddits));
    }

}
