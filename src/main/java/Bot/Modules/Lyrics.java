package Bot.Modules;

import Bot.DogBot;

public class Lyrics {
    public static void sendLyrics() throws Exception {
        DogBot.sendText("♫");
        DogBot.waitFor("♫");
        String response = DogBot.pickRandom(DogBot.lyrics);
        DogBot.sendText(response, 250);
        DogBot.waitFor(response);
    }
}