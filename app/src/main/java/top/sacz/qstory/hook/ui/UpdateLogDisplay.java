package top.sacz.qstory.hook.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import java.lang.reflect.Method;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import top.linl.qstorycloud.R;
import top.linl.qstorycloud.hook.util.ActivityTools;
import top.sacz.qstory.config.ModuleConfig;
import top.sacz.qstory.net.UpdateInfo;


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
                    if (ModuleConfig.INSTANCE.getModuleInfo().getVersionCode() == 0) {
                        return;
                    }
                    if (ModuleConfig.INSTANCE.isReadUpdateLog()) {
                        return;
                    }
                    List<UpdateInfo> updateInfoList = ModuleConfig.INSTANCE.getUpdateInfoList();
                    if (updateInfoList.isEmpty()) {
                        return;
                    }
                    showUpdateLog((Activity) param.thisObject, updateInfoList.get(0));
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
        String updateTitle = String.format(activity.getString(R.string.update_log_dialog_title), updateInfo.getVersionName());
        new AlertDialog.Builder(activity, R.style.theme_dialog)
                .setTitle(updateTitle)
                .setCancelable(false)
                .setMessage("更新日志：\n" + updateInfo.getUpdateLog())
                .setPositiveButton(
                        "确定", (dialog, which) -> {
                            ModuleConfig.INSTANCE.setReadUpdateLog();
                            dialog.dismiss();
                        })
                .setNeutralButton("喵", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

}
