# BotForDogsRedux

The Facebook messenger API doesn't allow you to have bots in group chats.  
This updated version of BotForDogs solves that, by using a combination of a web driver, automated mouse clicks and clipboard manipulation to read messages from the page source of a facebook messenger thread, and respond.

## Commands

| Command       | Response              |
| ------------- | --------------------- |
| !commands or !help     | Links to this readme  |
| !dog          | Sends a picture of a dog from reddit |
| !cat | Sends a picture of a cat from reddit |
| !stats        | Gives some information about the bot's configuration |
| !uptime       | Gives how long the bot has been up for |
| !xkcd l or !xkcd latest | Sends the most recent xkcd |
| !xkcd | Sends a random XKCD |
| !xkcd [number] | Sends a specific XKCD |
| !grab | Grabs the most recent text message |
| !grab [offset] | Grabs a message [offset] messages ago. !grab 0 is identical to !grab |
| !quote | Returns a random grabbed message |
| !tab | Wee woo |
| !8ball or !ask | Query the all knowing magic 8 ball |
| !react | Gives the cat's reaction |
| !github | Links to this github repository |
| !shutdown [code] | Shuts the bot down. The code is outputted to the console on bot startup. |
| !dogreddits | Shows the subreddits used in !dog |
| !extragooddog | Sends an image from a curated list of dogs |
| !ping | Pong! |
| !howgood | Just how good is dogbot? Based off "good dog" and "bad dog" ratings |
| !dates | Outputs a list of upcoming dates from listOfSadness.txt |
| !inspire | Returns an inspirational quote from http://inspirobot.me |
