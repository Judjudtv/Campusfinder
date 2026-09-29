package com.example.campusfind.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.campusfind.models.Claim;
import com.example.campusfind.models.Item;
import com.example.campusfind.models.User;

import java.util.ArrayList;
import java.util.List;
import com.example.campusfind.models.ClaimInfo;
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "campusfind.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ITEMS = "items";
    private static final String TABLE_CLAIMS = "claims";

    // Users columns
    private static final String COL_USER_ID = "user_id";
    private static final String COL_NAME = "name";
    private static final String COL_STUDENT_ID = "student_id";
    private static final String COL_CONTACT = "contact";

    // Items columns
    private static final String COL_ITEM_ID = "item_id";
    private static final String COL_ITEM_NAME = "item_name";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_LOCATION = "location";
    private static final String COL_DATE = "date";
    private static final String COL_STATUS = "status";
    private static final String COL_ITEM_USER_ID = "user_id"; // foreign key -> users

    // Claims columns
    private static final String COL_CLAIM_ID = "claim_id";
    private static final String COL_CLAIM_ITEM_ID = "item_id";   // foreign key -> items
    private static final String COL_CLAIM_USER_ID = "user_id";   // foreign key -> users
    private static final String COL_CLAIM_STATUS = "claim_status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // users table
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_STUDENT_ID + " TEXT, " +
                COL_CONTACT + " TEXT)";
        db.execSQL(createUsers);

        // items table (has foreign key to users)
        String createItems = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COL_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ITEM_NAME + " TEXT NOT NULL, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_LOCATION + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_STATUS + " TEXT, " +
                COL_ITEM_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_ITEM_USER_ID + ") REFERENCES " +
                TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createItems);

        // claims table (has foreign keys to items and users)
        String createClaims = "CREATE TABLE " + TABLE_CLAIMS + " (" +
                COL_CLAIM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CLAIM_ITEM_ID + " INTEGER, " +
                COL_CLAIM_USER_ID + " INTEGER, " +
                COL_CLAIM_STATUS + " TEXT, " +
                "FOREIGN KEY(" + COL_CLAIM_ITEM_ID + ") REFERENCES " +
                TABLE_ITEMS + "(" + COL_ITEM_ID + "), " +
                "FOREIGN KEY(" + COL_CLAIM_USER_ID + ") REFERENCES " +
                TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createClaims);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLAIMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ================= USER CRUD =================

    public long addUser(String name, String studentId, String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_STUDENT_ID, studentId);
        values.put(COL_CONTACT, contact);
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, COL_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                users.add(new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STUDENT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return users;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_STUDENT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT))
            );
        }
        cursor.close();
        db.close();
        return user;
    }

    public int updateUser(int userId, String name, String studentId, String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_STUDENT_ID, studentId);
        values.put(COL_CONTACT, contact);
        int rows = db.update(TABLE_USERS, values, COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
        db.close();
        return rows;
    }

    public void deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    // ================= ITEM CRUD =================

    public long addItem(String itemName, String description, String location,
                         String date, String status, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ITEM_NAME, itemName);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_LOCATION, location);
        values.put(COL_DATE, date);
        values.put(COL_STATUS, status);
        values.put(COL_ITEM_USER_ID, userId);
        long id = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return id;
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null, null, null, null, null, COL_ITEM_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                items.add(new Item(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_USER_ID))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    // Read filtered by status ("Lost" or "Found") - useful for the Lost/Found tabs
    public List<Item> getItemsByStatus(String status) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null, COL_STATUS + "=?",
                new String[]{status}, null, null, COL_ITEM_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                items.add(new Item(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_USER_ID))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }
    // Hinahanap ang items (sa loob ng Lost o Found tab) na may keyword
// sa pangalan, description, o location
    public List<Item> searchItems(String status, String keyword) {
        List<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String like = "%" + keyword + "%";
        Cursor cursor = db.query(TABLE_ITEMS, null,
                COL_STATUS + "=? AND (" + COL_ITEM_NAME + " LIKE ? OR "
                        + COL_DESCRIPTION + " LIKE ? OR " + COL_LOCATION + " LIKE ?)",
                new String[]{status, like, like, like},
                null, null, COL_ITEM_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                items.add(new Item(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_USER_ID))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    public Item getItemById(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null, COL_ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)}, null, null, null);

        Item item = null;
        if (cursor.moveToFirst()) {
            item = new Item(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ITEM_USER_ID))
            );
        }
        cursor.close();
        db.close();
        return item;
    }

    public int updateItem(int itemId, String itemName, String description, String location,
                           String date, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ITEM_NAME, itemName);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_LOCATION, location);
        values.put(COL_DATE, date);
        values.put(COL_STATUS, status);
        int rows = db.update(TABLE_ITEMS, values, COL_ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)});
        db.close();
        return rows;
    }

    public void deleteItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COL_ITEM_ID + "=?", new String[]{String.valueOf(itemId)});
        db.close();
    }

    // ================= CLAIM CRUD =================

    public long addClaim(int itemId, int userId, String claimStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CLAIM_ITEM_ID, itemId);
        values.put(COL_CLAIM_USER_ID, userId);
        values.put(COL_CLAIM_STATUS, claimStatus);
        long id = db.insert(TABLE_CLAIMS, null, values);
        db.close();
        return id;
    }

    public List<Claim> getAllClaims() {
        List<Claim> claims = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLAIMS, null, null, null, null, null, COL_CLAIM_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                claims.add(new Claim(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLAIM_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLAIM_ITEM_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLAIM_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLAIM_STATUS))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return claims;
    }

    // Kinukuha ang lahat ng claims kasama ang pangalan ng item at user
// gamit ang JOIN (pinagsasama ang 3 tables sa isang query)
    public List<ClaimInfo> getAllClaimInfo() {
        List<ClaimInfo> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT c." + COL_CLAIM_ID + ", c." + COL_CLAIM_ITEM_ID + ", c."
                + COL_CLAIM_USER_ID + ", c." + COL_CLAIM_STATUS + ", i." + COL_ITEM_NAME
                + ", u." + COL_NAME
                + " FROM " + TABLE_CLAIMS + " c"
                + " LEFT JOIN " + TABLE_ITEMS + " i ON c." + COL_CLAIM_ITEM_ID + " = i." + COL_ITEM_ID
                + " LEFT JOIN " + TABLE_USERS + " u ON c." + COL_CLAIM_USER_ID + " = u." + COL_USER_ID
                + " ORDER BY c." + COL_CLAIM_ID + " DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new ClaimInfo(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLAIM_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLAIM_ITEM_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CLAIM_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CLAIM_STATUS))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public int updateClaimStatus(int claimId, String claimStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CLAIM_STATUS, claimStatus);
        int rows = db.update(TABLE_CLAIMS, values, COL_CLAIM_ID + "=?",
                new String[]{String.valueOf(claimId)});
        db.close();
        return rows;
    }

    public void deleteClaim(int claimId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLAIMS, COL_CLAIM_ID + "=?", new String[]{String.valueOf(claimId)});
        db.close();
    }
}
