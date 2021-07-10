/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
 */

package xyz.d1snin.corby.database

import com.mongodb.DB
import com.mongodb.MongoClient
import org.jongo.Jongo
import org.slf4j.event.Level
import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.Corby.log

object DatabaseManager {

    private const val DB_HOST = "localhost"
    private const val DB_PORT = 27017
    private const val DB_NAME = "corby"

    private lateinit var db: DB
    internal lateinit var jongo: Jongo

    internal fun init() {
        runCatching {
            db = MongoClient(DB_HOST, DB_PORT).getDB(DB_NAME).also {
                jongo = Jongo(it)
                log("Successfully connected to the database")
            }

        }.onFailure {
            log("Could not connect to the database.", Level.ERROR)
            Corby.shutdown(Corby.DATABASE_ERROR)
        }
    }
}