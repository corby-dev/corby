CREATE TABLE "guildprefix" ( 
	"guildid" INTEGER UNIQUE, 
	"prefix" TEXT NOT NULL, 
	PRIMARY KEY("guildid") 
)