package Bot.Modules;

import Bot.DogBot;

public class Sloths {
    public static void sendSloth() throws Exception {
        DogBot.sendText("lazy searching...", 300);
        DogBot.waitFor("lazy searching...");

        Thread.sleep(250);
        DogBot.sendImageFromURL(Reddit.getSubredditPicture(DogBot.slothSubreddits));

    }
}