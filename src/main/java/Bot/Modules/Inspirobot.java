package Bot.Modules;

import org.json.JSONObject;
import Bot.DogBot;

public class Inspirobot {
    public static void getInspired() throws Exception {
        String imgURL = DogBot.getPageSource("http://inspirobot.me/api?generate=true");
        DogBot.sendImageFromURL(imgURL);
    }
    public static void main(String[] args) {
      getInspired();
    }
}
