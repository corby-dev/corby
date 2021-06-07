# *Corby* [![CodeFactor](https://www.codefactor.io/repository/github/corby-dev/corby/badge)](https://www.codefactor.io/repository/github/corby-dev/corby) [![<ORG_NAME>](https://circleci.com/gh/corby-dev/corby.svg?style=svg)](https://circleci.com/gh/corby-dev/corby) [![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause) [![lines of code](https://img.shields.io/tokei/lines/github/corby-dev/corby)](https://github.com/corby-dev/corby)

![corby pic](https://raw.githubusercontent.com/d1snin/corby/dev/src/main/resources/corby-header.png)

## *Made in 🇷🇺 with ❤️.*

*Corby is a bot that aims to log any events that occur in your discord server and to moderate it, I am open to any of your pool requests*

***Right now in active development!***

# *Instructions how to self host this bot:*

### *Step 1: Cloning the repository and installing the config.*

*First, clone the repository with the command:*

```shell
git clone https://github.com/corby-dev/corby.git && cd corby
```

*Then you need to create a config file, it should be named `config.json`.*

*Then in this file you need to register the following dependencies:*

```json
{
  "token": "",
  "testBotToken": "",
  "botPrefixDefault": "",
  "shardsTotal": 0,
  "ownerId": "",
  "defaultStarboardStars": 0,
  "defaultStarboardStatus": false,
  "defaultCooldown": 0,
  "emoteSuccess": "",
  "emoteError": "",
  "emoteBack": "",
  "emoteNext": "",
  "emoteDefaultStar": "",
  "emoteDefaultBack": "",
  "emoteDefaultNext": ""
}
```

*Here is a description and purpose of all fields in the config:*

***token:*** *Bot authentication token, you can find it in the Discord developers portal.*\
***testBotToken:*** *The second authentication token, which is intended for a test bot, on which you can check the functionality of the bot without using the token of the main bot.*\
***botPrefixDefault:*** *The default bot prefix, users can change it on their server.*\
***shardsTotal:*** *The number of shards used for the bot.*\
***ownerId:*** *Your ID, this is necessary for the bot to recognize you when you use the bot administration commands.*\
***defaultStarboardStars:*** *The number of stars for the starboard, more details in the bot by command `;help starboard`.*\
***defaultStarboardStatus:*** *The starboard on the server can be turned off or on by default, `false` - disabled, `true` - enabled.*\
***defaultCooldown:*** *Default cooldown for the command.*\
***emoteSuccess:*** *Successful emote id.*\
***emoteError:*** *Error emote id.*\
***emoteBack:*** *Emote id, which defines the left arrow, used for `;help` command.*\
***emoteNext:*** *Emote id, which defines the right arrow.*\
***emoteDefaultStar:*** ***Unicode** emoji notation that is used for the starboard.*\
***emoteDefaultBack:*** *Unicode designation of the left arrow, used for `;help` command if external emoji are disabled on the server.*\
***emoteDefaultNext:*** *Unicode designation of the right arrow.*

***Example how to get emote id:***
![epic_tutorial](https://i.imgur.com/GSAB5qz.png)

***An example of filling in the config, which you are most likely to use:***

```json
{
  "token": "YOUR_TOKEN_HERE",
  "testBotToken": "YOUR_TOKEN_HERE",
  "botPrefixDefault": ";",
  "shardsTotal": 10,
  "ownerId": "YOUR_ID_HERE",
  "defaultStarboardStars": 3,
  "defaultStarboardStatus": true,
  "defaultCooldown": 5,
  "emoteSuccess": "EMOTE_ID_HERE",
  "emoteError": "EMOTE_ID_HERE",
  "emoteBack": "EMOTE_ID_HERE",
  "emoteNext": "EMOTE_ID_HERE",
  "emoteDefaultStar": "⭐",
  "emoteDefaultBack": "⬅️",
  "emoteDefaultNext": "➡️"
}
```

*Congratulations, you have configured the bot.*

### Step 2: Installing MongoDB.

*If this database is already installed on your machine, then you can skip to step 3.*

*All you need is to download mongodb and run it. You can download it [here](https://docs.mongodb.com/manual/administration/install-community/).*

### Step 3: Running.

*You can run the bot using the `run` startup script:*

```shell
./run args...
```

*You can start the bot with the test bot token with the argument `-test`*.

*If you don't need shards then include the argument `-noshards`.*