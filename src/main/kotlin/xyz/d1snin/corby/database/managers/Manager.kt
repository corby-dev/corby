/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.database.managers

import org.jongo.MongoCollection
import xyz.d1snin.corby.database.DatabaseManager

abstract class Manager(collectionName: String) {
    protected val collection: MongoCollection = DatabaseManager.jongo.getCollection(collectionName)
}