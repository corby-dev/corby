[![CodeFactor](https://www.codefactor.io/repository/github/d1snin/corby/badge/development)](https://www.codefactor.io/repository/github/d1snin/corby/overview/development)
[![Build Status](https://travis-ci.com/d1snin/corby.svg?branch=development)](https://travis-ci.com/d1snin/corby)
[![<ORG_NAME>](https://circleci.com/gh/d1snin/corby.svg?style=svg)](https://circleci.com/gh/d1snin/corby)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

![corby pic](https://raw.githubusercontent.com/d1snin/corby/development/src/main/resources/corby.jpg)

# Corby:
Corby is a bot that aims to log any events that occur in your discord server and to moderate it, I am open to any of your pool requests

**Right now in active development!**

# Instructions:
Here you can find instructions for use
### Commands:
You can find the commands my bot supports on [this](https://d1snin.xyz/corby/) web page.

### Self hosting:
You can install and run my bot on your machine and use it for your own purposes, for this you need to create a configuration file named `config.json`.
Then fill it in according to the following template:
```json
{
  "token": "",
  "botPrefixDefault": "",
  "ownerId": "",
  "defaultCooldownSeconds": "",
  "defaultColor": "",
  "errorColor": "",
  "successColor": "",
  "starboardColor": "",
  "emoteTrash": "",
  "emoteStar": "",
  "emoteWhiteCheckMark": "",
  "defaultStarboardStars": "",
  "defaultStarboardIsEnabled": "",
  "helpPageUrl": ""
}
```
#### Descriptions:
`token` - your bot token\
`bot_prefx_default` - bot prefix\
`ownerId` - your discord id\
`default_cooldowm_seconds` - User cannot use more than one command at this time\
`defaultColor` - RGB color for embeds\
`errorColor` - RGB color for embeds that inform the user that something went wrong\
`successColor` - RGB color for embeds that inform the user that something went well\
`starboardColor` - RGB color for embeds used for starboards\
`emoteTrash` - emote of waste bascket (unicode)\
`emoteStar` - emote of star (unicode)\
`emoteWhiteCheckMark` - emote of white check mark (unicode)\
`defaultStarboardStars` - the number of stars required for a message to appear on the starboard\
`defaultStarboardIsEnabled` - (boolean) default toggle for the starboard on the server\
`helpPageUrl` - link to the page with information about commands
#### An example of writing config.json
```json
{
  "token": "aaaaaaaaaaaaabbbbbbbbbccccccccccccccccdddddd",
  "botPrefixDefault": "!",
  "ownerId": "111111111111111111",
  "defaultCooldownSeconds": "2",
  "defaultColor": "74 129 248",
  "errorColor": "255 0 0",
  "successColor": "70 255 0",
  "starboardColor": "255 215 0",
  "emoteTrash": "\uD83D\uDDD1",
  "emoteStar": "⭐",
  "emoteWhiteCheckMark": "✅",
  "defaultStarboardStars": "3",
  "defaultStarboardIsEnabled": "false",
  "helpPageUrl": "https://d1snin.xyz/corby/"
}
```
The next step you need to create a database and execute a [script](https://github.com/d1snin/corby/blob/development/scripts/setupdb.sql) for this database to make it work for the bot and add the database file with the extension `.db` along the path `corby/src/main/resources/corby.db`.

It must have a name `corby.db` as shown above.

You can create an empty database via Intellij IDEA. Click on this tab

![dbguide1](https://i.imgur.com/5CaxWAA.png)

Now click on "+" to create a new database, then hover mouse over Data Source and select SQLite database.

![dbguide2](https://i.imgur.com/vTb64f8.png)

Now fill in the required fields as shown below.

![dbguide3](https://i.imgur.com/EbAJIsg.png)

Follow this path `scripts/setupdb.sql` and copy the content of the script and paste it into the field to execute the request and click `Execute`. Move the database file along the path `corby/src/main/resources`.

![dbguide4](https://i.imgur.com/RG172sF.png)

![dbguide5](https://i.imgur.com/350dikW.png)

Then you can start the bot with the command `./gradlew run`.

### Contributing:
I am open to any pull requests, just clone the repository and import gradle project.
Please observe [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). 
Also make sure you test your changes to work.
