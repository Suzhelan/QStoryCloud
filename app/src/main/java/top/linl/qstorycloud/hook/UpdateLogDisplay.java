package top.linl.qstorycloud.hook;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import top.linl.qstorycloud.R;
import top.linl.qstorycloud.config.LocalModuleData;
import top.linl.qstorycloud.config.UpdateInfoData;
import top.linl.qstorycloud.hook.util.ActivityTools;
import top.linl.qstorycloud.model.LocalModuleInfo;
import top.linl.qstorycloud.model.UpdateInfo;

/**
 * 展示更新日志
 * <p>
 * 这个比较简单 从数据库中查询更新日志和是否已读
 */
public class UpdateLogDisplay {

    public boolean isShow;
    public void hook() {
        try {
            Method onCreateMethod = Activity.class.getDeclaredMethod("onCreate", Bundle.class);
            XposedBridge.hookMethod(onCreateMethod, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    UpdateInfo updateInfo = UpdateInfoData.INSTANCE.getLastUpdateInfo();
                    LocalModuleInfo moduleInfo = LocalModuleData.getLastModuleInfo();
                    if (updateInfo == null || moduleInfo == null) {
                        return;
                    }
                    if (updateInfo.getLatestVersionCode() != moduleInfo.getModuleVersionCode()) {
                        return;
                    }
                    if (updateInfo.isHaveRead()) {
                        return;
                    }
                    showUpdateLog((Activity) param.thisObject, updateInfo);
                }
            });
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void showUpdateLog(Activity activity, UpdateInfo updateInfo) {
        //防止用户选择了左按钮‘喵’ 然后每次打开activity时都会弹更新日志
        if (isShow) {
            return;
        }
        isShow = true;
        ActivityTools.injectResourcesToContext(activity);
        String updateTitle = String.format(activity.getString(R.string.update_log_dialog_title), updateInfo.getLatestVersionName());
        new AlertDialog.Builder(activity,R.style.theme_dialog)
                .setTitle(updateTitle)
                .setCancelable(false)
                .setMessage("更新日志：\n"+updateInfo.getUpdateLog())
                .setPositiveButton(
                        "确定", (dialog, which) -> {
                            updateInfo.setHaveRead(true);
                            UpdateInfoData.INSTANCE.updateLastUpdateInfo(updateInfo);
                            dialog.dismiss();
                        })
                .setNeutralButton("喵", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


}
