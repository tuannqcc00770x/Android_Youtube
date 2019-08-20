package funix.prm.project.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import funix.prm.project.model.User;
import funix.prm.project.model.Video;
import static funix.prm.project.util.Constants.DATABASE_VERSION;
import static funix.prm.project.util.Constants.DB_NAME;
import static funix.prm.project.util.Constants.PASSWORD;
import static funix.prm.project.util.Constants.TABLE_NAME;
import static funix.prm.project.util.Constants.USER_NAME;
import static funix.prm.project.util.Constants.VIDEO_DESCRIPTION;
import static funix.prm.project.util.Constants.VIDEO_ID;
import static funix.prm.project.util.Constants.VIDEO_THUMBNAIL;
import static funix.prm.project.util.Constants.VIDEO_TITLE;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper mHelper;

    //Set Singleton to get instance of helper
    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteHelper getInstance(Context context) {// singleOn in JAVA
        if (mHelper == null) {
            mHelper = new SQLiteHelper(context);
        }
        return mHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //TODO: Init structure
        String sqlQuery = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME +" ("+
                USER_NAME + " TEXT, "+
                PASSWORD +" TEXT," +
                VIDEO_ID +" TEXT," +
                VIDEO_TITLE +" TEXT," +
                VIDEO_DESCRIPTION +" TEXT," +
                VIDEO_THUMBNAIL + " TEXT)";
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public long addUser(User user) {
        //TODO: Logic save account info
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, user.getName());
        values.put(PASSWORD, user.getPassword());
        values.put(VIDEO_ID, user.getId());
        values.put(VIDEO_TITLE, user.getTitle());
        values.put(VIDEO_DESCRIPTION, user.getDescription());
        values.put(VIDEO_THUMBNAIL, user.getThumbnail());
        return db.insert(TABLE_NAME,null,values);
    }

    //Check if user is existed for register, not require password
    public boolean isCheckUserExist(String username) {
        //TODO: Check if user is existed
        boolean check = false;
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                if (username.equals(cursor.getString(0))){
                    check = true;
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return check;
    }

    //Check if user is existed for login, require password
    public boolean isCheckUserExist(String username, String password) {
        boolean check = false;
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                if (username.equals(cursor.getString(0))&&password.equals(cursor.getString(1))){
                    check = true;
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return check;
    }

    public long addVideo(Video video, String user) {
        boolean check = false;
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                if (user.equals(cursor.getString(0))&&video.getId().equals(cursor.getString(2))){
                    check = true;
                    break;
                }
            } while (cursor.moveToNext());
        }

        if (!check){
            ContentValues values = new ContentValues();
            values.put(USER_NAME, user);
            values.put(PASSWORD, "");
            values.put(VIDEO_ID, video.getId());
            values.put(VIDEO_TITLE, video.getTitle());
            values.put(VIDEO_DESCRIPTION, video.getDescription());
            values.put(VIDEO_THUMBNAIL, video.getThumbnailURL());
            return db.insert(TABLE_NAME,null,values);
        } else {
            return db.insert(TABLE_NAME,null,null);
        }

    }

    public List<Video> getVideosByUser(String username) {

        //TODO: Get played videolist depend on user name
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Video> listVideo = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                if (username.equals(cursor.getString(0))&&!cursor.getString(2).equals("")){
                    Video video = new Video(cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
                    listVideo.add(video);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listVideo;
    }

}
