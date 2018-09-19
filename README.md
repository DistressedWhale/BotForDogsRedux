# BotForDogsRedux

The Facebook messenger API doesn't allow you to have bots in group chats.  
This updated version of BotForDogs solves that, by using a combination of a web driver, automated mouse clicks and clipboard manipulation to read messages from the page source of a facebook messenger thread, and respond.

## Commands

### General commands

| Command       | Response              |
| ------------- | --------------------- |
| !commands or !help     | Links to this readme  |
| !stats        | Gives some information about the bot's configuration |
| !uptime       | Gives how long the bot has been up for |
| !shutdown [code] | Shuts the bot down. The code is outputted to the console on bot startup. |
| !github | Links to this github repository |
| !ping | Pong! |

### Images

| Command       | Response              |
| ------------- | --------------------- |
| !dog          | Sends a picture of a dog from reddit |
| !extragooddog | Sends an image from a curated list of dogs |
| !cat | Sends a picture of a cat from reddit |
| !birb | Sends a picture of a bird from reddit |
| !react | Gives the cat's reaction |
| !xkcd l or !xkcd latest | Sends the most recent xkcd |
| !xkcd | Sends a random XKCD |
| !xkcd [number] | Sends a specific XKCD |
| !inspire | Returns an inspirational quote from http://inspirobot.me |

### Chat commands

| Command       | Response              |
| ------------- | --------------------- |
| !grab | Grabs the most recent text message |
| !grab [offset] | Grabs a message [offset] messages ago. !grab 0 is identical to !grab |
| !quote | Returns a random grabbed message |
| !rtd | Returns a random number in the range 1-6 |
| !rtd [x] | Returns a random number inthe range 1-x (Limited by integer max) |
| !8ball or !ask | Query the all knowing magic 8 ball |
| !dates | Outputs a list of upcoming dates from listOfSadness.txt |
| !dogreddits | Shows the subreddits used in !dog |







