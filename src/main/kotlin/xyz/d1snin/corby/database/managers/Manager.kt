package xyz.d1snin.corby.database.managers

import org.jongo.MongoCollection
import xyz.d1snin.corby.database.DatabaseManager

abstract class Manager(private val collectionName: String) {
    protected val collection: MongoCollection
        get() = DatabaseManager.jongo.getCollection(collectionName)
}