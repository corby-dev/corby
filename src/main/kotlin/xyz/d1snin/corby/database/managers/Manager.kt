/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.database.managers

import org.jongo.MongoCollection
import xyz.d1snin.corby.database.DatabaseManager

abstract class Manager(private val collectionName: String) {
    protected val collection: MongoCollection
        get() = DatabaseManager.jongo.getCollection(collectionName)
}