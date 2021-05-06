[![CodeFactor](https://www.codefactor.io/repository/github/d1snin/corby/badge)](https://www.codefactor.io/repository/github/d1snin/corby)
[![Build Status](https://travis-ci.com/d1snin/corby.svg?branch=development)](https://travis-ci.com/d1snin/corby)
[![<ORG_NAME>](https://circleci.com/gh/d1snin/corby.svg?style=svg)](https://circleci.com/gh/d1snin/corby)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

![corby pic](https://raw.githubusercontent.com/d1snin/corby/development/src/main/resources/corby.jpg)

# Corby:

### Made in 🇷🇺 with ❤️.

Corby is a bot that aims to log any events that occur in your discord server and to moderate it, I am open to any of your pool requests

**Right now in active development!**

# Instructions to use and create a database:

###Intellij IDEA Ultimate:

If you have Intellij IDEA Ultimate, then you can create a database using it, here is an instruction on how to create a database using Intellij IDEA Ultimate.

Click on this tab

![dbguide1](https://i.imgur.com/5CaxWAA.png)

Now click on "+" to create a new database, then hover mouse over Data Source and select SQLite database.

![dbguide2](https://i.imgur.com/vTb64f8.png)

Now fill in the required fields as shown below.

![dbguide3](https://i.imgur.com/EbAJIsg.png)

Follow this path `scripts/setupdb.sql` and copy the content of the script and paste it into the field to execute the request and click `Execute`. Move the database file along the path `corby/src/main/resources`.

![dbguide4](https://i.imgur.com/RG172sF.png)

![dbguide5](https://i.imgur.com/350dikW.png)

### SQLite Workbench:

If you do not have a version, then you can download an editor for this database. I recommend using [db browser for SQLite](https://sqlitebrowser.org/). Go [here](https://sqlitebrowser.org/dl/) and download the required version of the program for your OS.

Now you have to create a db by clicking on the button `New Database.`

![dbguide6](https://i.imgur.com/fNruCDl.png)

Enter a name for the database `corby.db` and save it along the path `corby/src/main/resources/corby.db`. In the window that opens after creating, click `Cancel`.

![dbguide7](https://i.imgur.com/wcYR7Kz.png)

Go to the created database, click on the tab `Execute SQL` and click on the button `Open SQL file` and select the file that is located along the path `corby/scripts/setupdb.sql`. Click on the button `Execute all SQL`, and then press `Write Changes` or `Ctrl + S`.

![dbguide8](https://i.imgur.com/NYtbU50.png)

You can now use the database!
