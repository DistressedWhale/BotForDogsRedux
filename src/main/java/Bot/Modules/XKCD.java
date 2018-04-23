package Bot.Modules;

import org.json.JSONObject;

import java.util.Random;
import Bot.DogBot;

public class XKCD {
    public static void getLatestXKCD() throws Exception {
        String title, alt, imgURL, number;

        JSONObject xkcdLatest = new JSONObject(DogBot.getPageSource("https://xkcd.com/info.0.json"));
        number = xkcdLatest.get("num").toString();
        title = xkcdLatest.get("safe_title").toString();
        alt = xkcdLatest.get("alt").toString();
        imgURL = xkcdLatest.get("img").toString();

        String response = "Title: " + title + "\n" +
                "Number: " + number + "\n" +
                "Alt text: " + alt;
        DogBot.sendText(response, 100);
        DogBot.waitFor(response.replaceAll("\n", "&#10;"));

        DogBot.sendImageFromURL(imgURL);
    }

    public static void getRandomXKCD() throws Exception {
        String title, alt, imgURL;
        int highestNumber = Integer.valueOf(new JSONObject(DogBot.getPageSource("https://xkcd.com/info.0.json")).get("num").toString());
        int randomXKCDNumber = new Random().nextInt(highestNumber) + 1;

        JSONObject randomXKCD = new JSONObject(DogBot.getPageSource("https://xkcd.com/" + randomXKCDNumber + "/info.0.json"));

        title = randomXKCD.get("safe_title").toString();
        alt = randomXKCD.get("alt").toString();
        imgURL = randomXKCD.get("img").toString();

        String response = "Title: " + title + "\n" +
                "Number: " + randomXKCDNumber + "\n" +
                "Alt text: " + alt;
        DogBot.sendText(response, 100);
        DogBot.waitFor(response.replaceAll("\n", "&#10;")); //Replace is needed for formatting in the page source

        DogBot.sendImageFromURL(imgURL);
    }

    public static void getSpecificXKCD(int number) throws Exception {
        String title, alt, imgURL;
        int highestNumber = Integer.valueOf(new JSONObject(DogBot.getPageSource("https://xkcd.com/info.0.json")).get("num").toString());

        if (number > highestNumber || number < 1) {
            DogBot.sendText("XKCD Number out of range. Please try XKCDs in range 0-" + highestNumber);
        } else {
            JSONObject specificXKCD = new JSONObject(DogBot.getPageSource("https://xkcd.com/" + number + "/info.0.json"));

            title = specificXKCD.get("safe_title").toString();
            alt = specificXKCD.get("alt").toString();
            imgURL = specificXKCD.get("img").toString();

            String response = "Title: " + title + "\n" +
                    "Number: " + number + "\n" +
                    "Alt text: " + alt;

            DogBot.sendText(response, 100);
            DogBot.waitFor(response.replaceAll("\n", "&#10;"));

            DogBot.sendImageFromURL(imgURL);
        }
    }
}
