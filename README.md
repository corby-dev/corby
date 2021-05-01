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
  "bot_prefix_default": "",
  "owner_id": "",
  "default_cooldown_seconds": "",
  "default_color": "",
  "error_color": "",
  "success_color": "",
  "starboard_color": "",
  "emote_trash": "",
  "emote_star": "",
  "emote_white_check_mark": "",
  "default_starboard_stars": "",
  "default_starboard_isenabled": "",
  "help_page_url": ""
}
```
#### Descriptions:
`token` - your bot token\
`bot_prefx_default` - bot prefix\
`owner_id` - your discord id\
`default_cooldowm_seconds` - User cannot use more than one command at this time\
`default_color` - RGB color for embeds\
`error_color` - RGB color for embeds that inform the user that something went wrong\
`success_color` - RGB color for embeds that inform the user that something went well\
`starboard_color` - RGB color for embeds used for starboards\
`emote_trash` - emote of waste bascket (unicode)\
`emote_star` - emote of star (unicode)\
`emote_white_check_mark` - emote of white check mark (unicode)\
`default_starboard_stars` - the number of stars required for a message to appear on the starboard\
`default_starboard_isenabled` - (boolean) default toggle for the starboard on the server\
`help_page_url` - link to the page with information about commands
#### An example of writing config.json
```json
{
  "token": "aaaaaaaaaaaaabbbbbbbbbccccccccccccccccdddddd",
  "bot_prefix_default": "!",
  "owner_id": "111111111111111111",
  "default_cooldown_seconds": "2",
  "default_color": "74 129 248",
  "error_color": "255 0 0",
  "success_color": "70 255 0",
  "starboard_color": "255 215 0",
  "emote_trash": "\uD83D\uDDD1",
  "emote_star": "⭐",
  "emote_white_check_mark": "✅",
  "default_starboard_stars": "3",
  "default_starboard_isenabled": "false",
  "help_page_url": "https://d1snin.xyz/corby/"
}
```
The next step you need to create a database and execute a [script](https://github.com/d1snin/corby/blob/development/scripts/setupdb.sql) for this database to make it work for the bot and add the database file with the extension `.db` along the path `corby/src/main/resources/corby.db`.

Then you can start the bot with the command `./gradlew run`.

### Contributing:
I am open to any pull requests, just clone the repository and import gradle project.
Please observe [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html). 
Also make sure you test your changes to work.
