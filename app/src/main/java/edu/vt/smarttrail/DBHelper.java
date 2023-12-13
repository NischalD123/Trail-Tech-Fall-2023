package edu.vt.smarttrail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {


    /**
     * Creates a new DBhelper object.
     * @param context
     */
    public DBHelper(Context context) {
        super(context, "Login.db", null, 1);
    }

    /**
     * Creates a database with a username as the primary key and password field.
     * @param login_db is the new database
     */
    @Override
    public void onCreate(SQLiteDatabase login_db) {
        login_db.execSQL("CREATE TABLE users(userkey Text primary key,username Text, password Text, email Text, usageData Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase login_db, int olddb, int newdb) {
        login_db.execSQL("DROP TABLE IF EXISTS users");
    }

    /**
     * Inserts data into database.
     * @param username
     * @param password
     * @return true if the data is inserted, false if not.
     */
    public Boolean insertUserData(String key, String username, String password, String email) {
        SQLiteDatabase login_db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userKey", key);
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("email", email);
        contentValues.put("usageData", "No Data Available");
        long result = login_db.insert("users", null, contentValues);
        login_db.close();

        return result != -1;
    }

    /**
     * Checks if there is a existing username in SQLite DB.
     * @param username
     * @return True if existing.
     */
    public Boolean check_username(String username) {
        SQLiteDatabase login_db = this.getWritableDatabase();
        Cursor cursor = login_db.rawQuery("SELECT * FROM users WHERE username = ?", new String[] {username});
        Boolean exist = cursor.getCount() > 0;
        cursor.close();
        return exist;
    }

    /**
     * Checks if there is a user and password to login into from SQLite DB.
     * @param username
     * @param password
     * @return True if existing.
     */
    public Boolean check_username_password(String username, String password) {
        SQLiteDatabase login_db = this.getWritableDatabase();;
        Cursor cursor = login_db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?",
                new String[] {username, password});
        Boolean exist = cursor.getCount() > 0;
        cursor.close();
        return exist;
    }

    /**
     * Checks existing email in SQLite DB
     * @param email
     * @return existing or not.
     */
    public Boolean check_existing_email(String email) {
        SQLiteDatabase login_db = this.getWritableDatabase();
        Cursor cursor = login_db.rawQuery("SELECT * FROM users WHERE email = ?", new String[] {email});
        Boolean exist = cursor.getCount() > 0;
        cursor.close();
        return exist;
    }

    /**
     * Gets the Firebase Primary Key for the user.
     * @param username
     * @param password
     * @return The Firebase Primary Key.
     */
    public String get_user_key(String username, String password) {
        SQLiteDatabase login_db = this.getWritableDatabase();;
        Cursor cursor = login_db.rawQuery("SELECT userKey FROM users WHERE username = ? AND password = ?",
                new String[] {username, password});
        cursor.moveToFirst();
        String key = cursor.getString(0);
        cursor.close();
        return key;
    }

    /**
     * Gets the email for the user.
     * @param username
     * @param password
     * @return The Firebase Primary Key.
     */
    public String getEmail(String username, String password) {
        SQLiteDatabase login_db = this.getWritableDatabase();;
        Cursor cursor = login_db.rawQuery("SELECT email FROM users WHERE username = ? AND password = ?",
                new String[] {username, password});
        cursor.moveToFirst();
        String key = cursor.getString(0);
        cursor.close();
        return key;
    }

    public String getUserKeyByEmail(String email) {
        SQLiteDatabase login_db = this.getWritableDatabase();
        Cursor cursor = login_db.rawQuery("SELECT userKey FROM users WHERE email = ?",
                new String[] {email});
        String key = null;
        if (cursor.moveToFirst()) {
            key = cursor.getString(0);
        }
        cursor.close();
        return key;
    }

    public void changePassword(String userKey, String newPassword) {
        SQLiteDatabase login_db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("password", newPassword);

        login_db.update("users", contentValues, "userKey=?", new String[]{userKey});
        login_db.close();
    }

    /**
     * Gets the user from primary key
     */
    public User getUser(String primary_key) {
        SQLiteDatabase login_db = this.getWritableDatabase();;
        Cursor cursor = login_db.rawQuery("SELECT * FROM users WHERE userkey = ?",
                new String[] {primary_key});
        cursor.moveToFirst();
        String key = cursor.getString(0);
        String username = cursor.getString(1);
        String password = cursor.getString(2);
        String email = cursor.getString(3);
        String usageStats = cursor.getString(4);
        cursor.close();
        User user = new User(key, username, password, email, usageStats);
        return user;
    }

    /**
     * Gets the user from primary key
     */
    public User getUserFromID(String userID) {
        SQLiteDatabase login_db = this.getWritableDatabase();;
        Cursor cursor = login_db.rawQuery("SELECT * FROM users WHERE username = ?",
                new String[] {userID});
        cursor.moveToFirst();
        String key = cursor.getString(0);
        String username = cursor.getString(1);
        String password = cursor.getString(2);
        String email = cursor.getString(3);
        String usageStats = cursor.getString(4);
        cursor.close();
        User user = new User(key, username, password, email, usageStats);
        return user;
    }

    /**
     * Insert Usage statistics into SQLite database.
     * @param user
     * @param password
     * @param email
     * @param usageStats is the String representation of the App Usage Statistics
     */
    public void insertUsageStats(String key, String user, String password, String email, String usageStats) {
        SQLiteDatabase login_db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("userKey", key);
        contentValues.put("username", user);
        contentValues.put("password", password);
        contentValues.put("email", email);
        contentValues.put("usageData", usageStats);

        login_db.update("users", contentValues, "userKey=?", new String[]{key});
        login_db.close();
    }

    public void changePassword(String key, String user, String usageStats, String email, String newPassword) {
        SQLiteDatabase login_db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("userKey", key);
        contentValues.put("username", user);
        contentValues.put("password", newPassword);
        contentValues.put("email", email);
        contentValues.put("usageData", usageStats);

        login_db.update("users", contentValues, "userKey=?", new String[]{key});
        login_db.close();
    }
}
