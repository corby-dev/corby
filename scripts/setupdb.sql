CREATE TABLE "guildprefix" ( "guildid" INTEGER UNIQUE, "prefix" TEXT NOT NULL, PRIMARY KEY("guildid") );
CREATE TABLE "starboards" ( "guildid" INTEGER NOT NULL UNIQUE, "channelid" INTEGER NOT NULL UNIQUE, "stars" INTEGER NOT NULL, PRIMARY KEY("guildid") );
