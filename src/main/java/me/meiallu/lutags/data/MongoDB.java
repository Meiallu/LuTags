package me.meiallu.lutags.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.meiallu.lutags.LuTags;
import me.meiallu.lutags.util.Util;
import org.bson.Document;

public class MongoDB implements Storage {

    private final MongoCollection<Document> collection;

    @Override
    public void writeTag(String uuid, String tag) {
        String resolvedTag = (tag == null) ? Tag.getPlayerTag(uuid).name : tag;
        String resolvedMedal = Medal.getPlayerMedal(uuid).name;

        updateUser(uuid, resolvedTag, resolvedMedal);
    }

    @Override
    public void writeMedal(String uuid, String medal) {
        String resolvedTag = Tag.getPlayerTag(uuid).name;
        String resolvedMedal = (medal == null) ? Medal.getPlayerMedal(uuid).name : medal;

        updateUser(uuid, resolvedTag, resolvedMedal);
    }

    private void updateUser(String uuid, String tag, String medal) {
        Document query = new Document("uuid", uuid);
        Document update = new Document("$set", new Document("tag", tag).append("medal", medal));

        collection.updateOne(query, update, new com.mongodb.client.model.UpdateOptions().upsert(true));
    }

    @Override
    public String readTag(String uuid) {
        Document query = new Document("uuid", uuid);
        Document result = collection.find(query).first();

        return (result != null) ? result.getString("tag") : null;
    }

    @Override
    public String readMedal(String uuid) {
        Document query = new Document("uuid", uuid);
        Document result = collection.find(query).first();

        return (result != null) ? result.getString("medal") : null;
    }

    public MongoDB() {
        Util.log("Connecting to MongoDB database...");

        String uri = LuTags.sql.database.get("mongodb").get("uri");
        String databaseName = LuTags.sql.database.get("mongodb").get("database");

        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        collection = database.getCollection("user_data");
        Util.log("Successfully connected and setup!");
    }
}
