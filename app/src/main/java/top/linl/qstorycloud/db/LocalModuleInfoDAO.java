package top.linl.qstorycloud.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import top.linl.qstorycloud.db.helper.UpdateDBHelper;
import top.linl.qstorycloud.hook.moduleloader.model.LocalModuleInfo;

/**
 * 模块信息dao
 */
public class LocalModuleInfoDAO {

    public static UpdateDBHelper getInstance() {
        return UpdateDBHelper.getInstance();
    }

    //获取最新的一条本地模块信息
    public static LocalModuleInfo getLastModuleInfo() {
        try (
                SQLiteDatabase db = UpdateDBHelper.getInstance().getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from module_info order by id desc limit 1", null)
        ) {
            int moduleApkPath = cursor.getColumnIndex("module_apk_path");
            int moduleName = cursor.getColumnIndex("module_name");
            int moduleVersionCode = cursor.getColumnIndex("module_version_code");
            int moduleVersionName = cursor.getColumnIndex("module_version_name");
            int updateLogHaveRead = cursor.getColumnIndex("update_log_have_read");
            int updateTim = cursor.getColumnIndex("update_time");
            if (cursor.moveToFirst()) {
                LocalModuleInfo localModuleInfo = new LocalModuleInfo();
                localModuleInfo.setModuleApkPath(cursor.getString(moduleApkPath));
                localModuleInfo.setModuleName(cursor.getString(moduleName));
                localModuleInfo.setModuleVersionCode(cursor.getInt(moduleVersionCode));
                localModuleInfo.setModuleVersionName(cursor.getString(moduleVersionName));
                localModuleInfo.setUpdateLogHaveRead(cursor.getInt(updateLogHaveRead) == 1);
                localModuleInfo.setUpdateTime(cursor.getLong(updateTim));
                return localModuleInfo;
            }
        }
        return null;
    }

    //插入一条模块信息
    public static void insertModuleInfo(LocalModuleInfo localModuleInfo) {
        try (
                SQLiteDatabase db = UpdateDBHelper.getInstance().getWritableDatabase()
        ) {
            ContentValues cv = new ContentValues();
            cv.put("module_apk_path", localModuleInfo.getModuleApkPath());
            cv.put("module_name", localModuleInfo.getModuleName());
            cv.put("module_version_code", localModuleInfo.getModuleVersionCode());
            cv.put("module_version_name", localModuleInfo.getModuleVersionName());
            cv.put("update_log_have_read", localModuleInfo.isUpdateLogHaveRead() ? 1 : 0);
            cv.put("update_time", localModuleInfo.getUpdateTime());
            db.insert(UpdateDBHelper.MODULE_INFO_TABLE, null, cv);
        }
    }
}
