package top.linl.qstorycloud.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import top.linl.qstorycloud.db.helper.UpdateDBHelper;
import top.linl.qstorycloud.hook.update.model.UpdateInfo;

public class UpdateInfoDAO {

    public static UpdateInfo getLastUpdateInfo() {
        //获取最新插入的更新信息
        try (
                SQLiteDatabase db = UpdateDBHelper.getInstance().getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from update_info order by id desc limit 1", null)
        ) {
            int hasUpdateIndex = cursor.getColumnIndex("has_update");
            int latestVersionCodeIndex = cursor.getColumnIndex("latest_version_code");
            int latestVersionNameIndex = cursor.getColumnIndex("latest_version_name");
            int mandatoryUpdateIndex = cursor.getColumnIndex("mandatory_update");
            int senderIndex = cursor.getColumnIndex("sender");
            int updateLogIndex = cursor.getColumnIndex("update_log");
            int updateUrlIndex = cursor.getColumnIndex("update_url");
            int updateTimeIndex = cursor.getColumnIndex("update_time");
            int haveReadIndex = cursor.getColumnIndex("have_read");
            if (cursor.moveToNext()) {
                UpdateInfo updateInfo = new UpdateInfo();
                updateInfo.setHaveRead(cursor.getInt(haveReadIndex) == 1);
                updateInfo.setHasUpdate(cursor.getInt(hasUpdateIndex) == 1);
                updateInfo.setLatestVersionCode(cursor.getInt(latestVersionCodeIndex));
                updateInfo.setLatestVersionName(cursor.getString(latestVersionNameIndex));
                updateInfo.setMandatoryUpdate(cursor.getInt(mandatoryUpdateIndex) == 1);
                updateInfo.setSender(cursor.getString(senderIndex));
                updateInfo.setUpdateLog(cursor.getString(updateLogIndex));
                updateInfo.setUpdateUrl(cursor.getString(updateUrlIndex));
                updateInfo.setUpdateTime(cursor.getLong(updateTimeIndex));
                return updateInfo;
            }
        }
        return null;
    }

    public static void updateUpdateInfo(UpdateInfo updateInfo) {
        //更新数据
        try (SQLiteDatabase db = UpdateDBHelper.getInstance().getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("has_update", updateInfo.getHasUpdate());
            cv.put("latest_version_code", updateInfo.getLatestVersionCode());
            cv.put("latest_version_name", updateInfo.getLatestVersionName());
            cv.put("mandatory_update", updateInfo.getMandatoryUpdate());
            cv.put("sender", updateInfo.getSender());
            cv.put("update_log", updateInfo.getUpdateLog());
            cv.put("update_url", updateInfo.getUpdateUrl());
            cv.put("update_time", updateInfo.getUpdateTime());
            cv.put("have_read", updateInfo.isHaveRead());
            //通过版本号更新
            db.update("update_info", cv, "latest_version_code = ?", new String[]{updateInfo.getLatestVersionCode() + ""});
        }
    }
    public static void insertUpdateInfo(UpdateInfo updateInfo) {
        //插入一条数据
        try (SQLiteDatabase db = UpdateDBHelper.getInstance().getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("has_update", updateInfo.getHasUpdate());
            cv.put("latest_version_code", updateInfo.getLatestVersionCode());
            cv.put("latest_version_name", updateInfo.getLatestVersionName());
            cv.put("mandatory_update", updateInfo.getMandatoryUpdate());
            cv.put("sender", updateInfo.getSender());
            cv.put("update_log", updateInfo.getUpdateLog());
            cv.put("update_url", updateInfo.getUpdateUrl());
            cv.put("update_time", updateInfo.getUpdateTime());
            cv.put("have_read", updateInfo.isHaveRead());
            db.insert("update_info", null, cv);
        }
    }

}
