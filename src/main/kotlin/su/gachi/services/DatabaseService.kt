package su.gachi.services

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import su.gachi.core.Client

class DatabaseService(discordClient: Client) {
    val client: MongoClient
    val db: MongoDatabase

    init {
        client = MongoClients.create(discordClient.dotenv["DATABASE_URL"])
        db = client.getDatabase("data")
    }

    fun getCollection(name: String): MongoCollection<Document> {
        return db.getCollection(name)
    }
}