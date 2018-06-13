package Bot.Utilities;

import java.util.ArrayList;
import java.util.regex.*;

public class ProcessSource {

    private static ArrayList<String> getMessages(String source) {

        ArrayList<String> allMatches = new ArrayList<String>();
        Matcher m = Pattern.compile("body=\".+?\"")
                .matcher(source);
        while (m.find()) {
            allMatches.add(m.group());
        }



        return allMatches;
    }

    private static String replaceEscaped(String s) {
        return s.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"");
    }

    private static String stripBodyTag(String s) {
        return s.substring(6, s.length() - 1);
    }

    public static ArrayList<String> getFormattedMessages (String source) {
        ArrayList<String> messages = getMessages(source);
        ArrayList<String> outputMessages = new ArrayList<String>();
        for (String s : messages) {
            if (s.matches("! (.+)")) {
                //If there is an accidental space between ! and the command, remove the space.
                outputMessages.add("!" + replaceEscaped(stripBodyTag(s)).substring(2));
            } else {
                outputMessages.add(replaceEscaped(stripBodyTag(s)));
            }
        }

        return outputMessages;
    }
}
