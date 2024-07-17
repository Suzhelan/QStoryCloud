package top.linl.qstorycloud.db.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * 模块本体使用的
 */
public class CommonDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE = "qstory_cloud.db";

    public static final String COMMON_TABLE = "common";

    public CommonDBHelper(@Nullable Context context) {
        super(context, DATABASE, null, 1);
    }

    public static CommonDBHelper getInstance(Context context) {
        return new CommonDBHelper(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //建立常用表
        db.execSQL("CREATE TABLE " + COMMON_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,value TEXT)");
        //为name列建立唯一索引
        db.execSQL("CREATE UNIQUE INDEX name_index ON " + COMMON_TABLE + "(name)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //增删改查
    public void insert(String name, String value) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO " + COMMON_TABLE + "(name,value) VALUES (?,?)", new String[]{name, value});
        db.close();
    }

    public void update(String name, String value) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + COMMON_TABLE + " SET value = ? WHERE name = ?", new String[]{value, name});
        db.close();
    }
    public String query(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String value = null;
        Cursor cursor =db.rawQuery("SELECT value FROM " + COMMON_TABLE + " WHERE name = ?", new String[]{name});
        if (cursor.moveToNext()) {
            value = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return value;
    }

    public void delete(String name) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + COMMON_TABLE + " WHERE name = ?", new String[]{name});
        db.close();
    }

}
