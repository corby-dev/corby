[![CodeFactor](https://www.codefactor.io/repository/github/d1snin/corby/badge)](https://www.codefactor.io/repository/github/d1snin/corby)
[![Build Status](https://travis-ci.com/d1snin/corby.svg?branch=development)](https://travis-ci.com/d1snin/corby)
[![<ORG_NAME>](https://circleci.com/gh/d1snin/corby.svg?style=svg)](https://circleci.com/gh/d1snin/corby)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

![corby pic](https://raw.githubusercontent.com/d1snin/corby/development/src/main/resources/corby.jpg)

# Corby:

### Made in 🇷🇺 with ❤️.

Corby is a bot that aims to log any events that occur in your discord server and to moderate it, I am open to any of your pool requests

**Right now in active development!**

# Instructions how to self host this bot:

You can install and run my bot on your machine and use it for your own purposes, for this you need to create a configuration file named `config.json`.
Then fill it in according to the following template:
```json
{
  "token": "",
  "botPrefixDefault": "",
  "ownerId": "",
  "defaultStarboardStars": "",
  "defaultStarboardIsEnabled": "",
}
```
#### Descriptions:
`token` - your bot token\
`bot_prefx_default` - bot prefix\
`ownerId` - your discord id\
`defaultStarboardStars` - the number of stars required for a message to appear on the starboard\
`defaultStarboardIsEnabled` - (boolean) default toggle for the starboard on the server\
#### An example of writing config.json
```json
{
  "token": "aaaaaaaaaaaaabbbbbbbbbccccccccccccccccdddddd",
  "botPrefixDefault": "!",
  "ownerId": "111111111111111111",
  "defaultStarboardStars": "3",
  "defaultStarboardIsEnabled": "false",
}
```

Then install the database for the bot, use [this](https://github.com/d1snin/corby/blob/dev/DATABASE.md) instruction.

### Bot launch:

You can build the jar using `./gradlew clean build`. Then run jar using `java -jar build/libs/corby-v1.1.jar`

Or you can start the bot simply by typing `./gradlew run`.
