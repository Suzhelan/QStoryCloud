package top.linl.qstorycloud.hook.moduleloader;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import top.linl.qstorycloud.config.LocalModuleData;
import top.linl.qstorycloud.hook.HookEnv;
import top.linl.qstorycloud.hook.PathTool;
import top.linl.qstorycloud.log.QSLog;
import top.linl.qstorycloud.model.LocalModuleInfo;

/**
 * 模块加载器，真正加载模块apk的地方
 */
public class ModuleLoader {

    /**
     * 判断是否有条件加载本地的模块
     */
    private boolean hasConditionLoading() {
        //模块信息为空
        LocalModuleInfo localModuleInfo = LocalModuleData.getLastModuleInfo();
        if (localModuleInfo == null) {
            return false;
        }
        //模块文件为空
        File moduleApkFile = new File(localModuleInfo.getModuleApkPath());
        return moduleApkFile.exists();
    }

    /**
     * 判断是否开启了安全模式
     */
    private boolean isOpenSafeMode() {
        //这里用到了跨进程通讯
        //获取内容提供者
        ContentResolver contentResolver = HookEnv.getHostAppContext().getContentResolver();
        //查询是否开启了安全模式
        Uri uri = Uri.parse("content://qstorycloud.linl.top/common/query");
        Cursor cursor = contentResolver.query(uri, new String[]{"value"}, "name=?", new String[]{"safe_mode"}, null);
        String openState = "false";
        if (cursor != null && cursor.moveToNext()) {
            openState = cursor.getString(0);
            cursor.close();
        }
        return Boolean.parseBoolean(openState);
    }

    /**
     * 尝试加载模块
     */
    public void readyToLoad() {
        //打开了安全模式不加载模块
        if (isOpenSafeMode()) return;
        if (hasConditionLoading()) {
            LocalModuleInfo localModuleInfo = LocalModuleData.getLastModuleInfo();
            //加载模块
            loadModuleAPKAndHook(localModuleInfo.getModuleApkPath());
        }
    }

    /**
     * 加载模块并执行模块的Hook
     *
     * @param pluginApkPath 模块apk路径
     */
    public void loadModuleAPKAndHook(String pluginApkPath) {
        File optimizedDirectoryFile = new File(PathTool.getDataPath());
        // 构建插件的DexClassLoader类加载器，参数：
        // 1、包含dex的apk文件或jar文件的路径，
        // 2、apk、jar解压缩生成dex存储的目录需要是data/data/包名/app_xxx的路径 一般通过context.getDir("dirName", Context.MODE_PRIVATE)获取
        // 3、本地library库目录，一般为null，
        // 4、父ClassLoader， 如果是模块要用XposedBridge.class.getClassLoader(),不能用其他的
        try {
            DexClassLoader dexClassLoader = new DexClassLoader(pluginApkPath, optimizedDirectoryFile.getPath(),
                    null, XposedBridge.class.getClassLoader());
            //获取插件的入口类 也就是实现了IXposedHookLoadPackage, IXposedHookZygoteInit的类
            Class<?> entryHookClass = dexClassLoader.loadClass("lin.xposed.hook.InitInject");
            Object hookInjectInstance = entryHookClass.newInstance();
            //初始化zygote 一定是比handleLoadPackage先调用的
            Method initZygoteMethod = entryHookClass.getMethod("initZygote", IXposedHookZygoteInit.StartupParam.class);
            //反射new实例
            Constructor<IXposedHookZygoteInit.StartupParam> constructor = IXposedHookZygoteInit.StartupParam.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            IXposedHookZygoteInit.StartupParam startupParam = constructor.newInstance();
            startupParam.modulePath = pluginApkPath;
            startupParam.startsSystemServer = false;
            initZygoteMethod.invoke(hookInjectInstance, startupParam);
            //正常的hook初始化流程
            Method entryHookMethod = entryHookClass.getMethod("handleLoadPackage", XC_LoadPackage.LoadPackageParam.class);
            entryHookMethod.invoke(hookInjectInstance, HookEnv.getLoadPackageParam());
        } catch (Exception e) {
            QSLog.e("ModuleLoader", e);
        }
    }

}
