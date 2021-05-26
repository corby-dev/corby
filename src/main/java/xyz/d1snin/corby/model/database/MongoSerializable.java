package xyz.d1snin.corby.model.database;

import com.mongodb.DBObject;

public interface MongoSerializable {

  DBObject toDBObject();

  MongoSerializable fromDBObject(DBObject object);
}
