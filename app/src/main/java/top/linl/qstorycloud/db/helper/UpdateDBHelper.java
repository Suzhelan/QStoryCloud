package top.linl.qstorycloud.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import top.linl.qstorycloud.hook.HookEnv;


/**
 * QQ使用的更新数据库
 */
public class UpdateDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE = "qstory_cloud.db";
    public static final String UPDATE_TABLE = "update_info";

    public static final String MODULE_INFO_TABLE = "module_info";

    public UpdateDBHelper(Context context) {
        super(context, DATABASE, null, 3);
    }

    public static UpdateDBHelper getInstance() {
        return new UpdateDBHelper(HookEnv.getHostAppContext());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //建表
        //更新信息表
        db.execSQL("CREATE TABLE " + UPDATE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT,has_update INTEGER,latest_version_code INTEGER,latest_version_name TEXT,mandatory_update INTEGER,sender TEXT,update_log TEXT,update_url TEXT,update_time INTEGER,have_read INTEGER)");
        //本地模块表
        db.execSQL("CREATE TABLE " + MODULE_INFO_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT,module_apk_path TEXT,module_name TEXT,module_version_code INTEGER,module_version_name TEXT,load INTEGER,update_time INTEGER,update_log_have_read INTEGER)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //升级
        //执行清理数据库语句
        db.execSQL("DROP TABLE IF EXISTS " + UPDATE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MODULE_INFO_TABLE);
        //建表
        onCreate(db);
    }


}
