CREATE TABLE "guildprefix" ( 
	"guildid" INTEGER UNIQUE, 
	"prefix" TEXT NOT NULL, 
	PRIMARY KEY("guildid") 
)
CREATE TABLE "cooldowns" (
    "userid" INTEGER NOT NULL UNIQUE,
    "cooldown" INTEGER NOT NULL,
    "commandid" INTEGER NOT NULL UNIQUE,
    PRIMARY KEY("userid")
);