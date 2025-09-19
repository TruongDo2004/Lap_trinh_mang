package UDP;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MongoDBHelper {
    public static final String URI = "mongodb://localhost:27017";
    public static final String DB_NAME = "BTL_LTM"; // đổi nếu bạn dùng DB khác

    private static MongoClient client;
    private static MongoDatabase db;

    static {
        connect();
    }

    public static void connect() {
        if (client == null) {
            client = MongoClients.create(URI);
            db = client.getDatabase(DB_NAME);
            System.out.println("[MongoDBHelper] connected to " + URI + "/" + DB_NAME);
        }
    }

    public static MongoCollection<Document> getCollection(String name) {
        return db.getCollection(name);
    }

    // register user; returns inserted ObjectId or null if exists
    public static ObjectId registerUser(String username, String password, String role) {
        MongoCollection<Document> users = getCollection("users");
        Document found = users.find(new Document("username", username)).first();
        if (found != null) return null;
        Document d = new Document("username", username)
                .append("password", password)
                .append("role", role == null ? "user" : role)
                .append("created_at", new Date());
        users.insertOne(d);
        return d.getObjectId("_id");
    }

    public static Document findUser(String username) {
        return getCollection("users").find(new Document("username", username)).first();
    }

    public static void ensureDefaultAdmin() {
        Document admin = findUser("admin");
        if (admin == null) {
            registerUser("admin", "admin", "admin");
            System.out.println("[MongoDBHelper] created default admin/admin");
        }
    }

    // Save file bytes into 'files' collection with metadata
    public static ObjectId saveFileBytes(ObjectId userId, String username, String filename, byte[] data) {
        Document doc = new Document("user_id", userId)
                .append("username", username)
                .append("filename", filename)
                .append("created_at", new Date())
                .append("data", new Binary(data));
        getCollection("files").insertOne(doc);
        return doc.getObjectId("_id");
    }

    public static List<Document> listFilesByUser(String username) {
        List<Document> out = new ArrayList<>();
        MongoCursor<Document> it = getCollection("files").find(new Document("username", username))
                .sort(new Document("created_at", -1)).iterator();
        try {
            while (it.hasNext()) out.add(it.next());
        } finally { it.close(); }
        return out;
    }

    public static List<Document> listAllFiles() {
        List<Document> out = new ArrayList<>();
        MongoCursor<Document> it = getCollection("files").find().sort(new Document("created_at", -1)).iterator();
        try {
            while (it.hasNext()) out.add(it.next());
        } finally { it.close(); }
        return out;
    }

    public static void deleteFileById(ObjectId id) {
        getCollection("files").deleteOne(new Document("_id", id));
    }

    public static byte[] loadFileData(ObjectId id) {
        Document d = getCollection("files").find(new Document("_id", id)).first();
        if (d == null) return null;
        Binary b = d.get("data", Binary.class);
        return b == null ? null : b.getData();
    }
}
