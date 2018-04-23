package Bot.Modules;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Bot.DogBot;
import com.mashape.unirest.http.Unirest;

public class Reddit {

    public static String getSubredditPicture(List<String> subreddits) throws Exception {
        boolean foundValidPicture = false;
        String formattedMatch = "";

        //Only allow jpegs for low image send time
        Pattern p = Pattern.compile("https://.+?(jpg)");

        while (!foundValidPicture) {
            //pick subreddit
            String subreddit = DogBot.pickRandom(subreddits);

            //Set up subreddit string
            String redditURL = "https://www.reddit.com/r/" + subreddit + "/random.json";
            String responseString = getRedditJSONString(redditURL);

            Matcher m = Pattern.compile(", \"url\": \".+?\"").matcher(responseString);
            m.find();

            String match = m.group();
            formattedMatch = match.substring(10, match.length() - 1);

            Matcher matcher = p.matcher(formattedMatch);
            if (matcher.matches()) {
                foundValidPicture = true;
            }


        }

        return formattedMatch;
    }

    private static String getRedditJSONString(String myURL) throws Exception {
        return Unirest.get(myURL).header("User-agent", "BotForDogsRedux").asString().getBody();
    }
}
