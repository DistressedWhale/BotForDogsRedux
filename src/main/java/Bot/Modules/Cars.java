package Bot.Modules;

import java.util.Random;
import Bot.DogBot;

public class Cars {

    public static void sendCar() throws Exception {
        if (new Random().nextInt(100) < 25) {
            DogBot.sendText("Making cool car noises.", 300);
            DogBot.waitFor("Making cool car noises.");
        } else {
            String response = DogBot.pickRandom(DogBot.carResponses);
            DogBot.sendText(response, 250);
            DogBot.waitFor(response);
        }

        DogBot.sendImageFromURL(Reddit.getSubredditPicture(DogBot.carSubreddits));
    }

}
