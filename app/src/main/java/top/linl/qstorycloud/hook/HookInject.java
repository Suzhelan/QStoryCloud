package top.linl.qstorycloud.hook;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;


import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import top.linl.qstorycloud.hook.util.ActivityTools;
import top.linl.qstorycloud.util.SpHelper;

public class HookInject implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private static final AtomicBoolean IS_INJECT = new AtomicBoolean();

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        HookEnv.setModuleApkPath(startupParam.modulePath);
    }

    private void initAppContext(Context applicationContext) {
        //获取和设置全局上下文和类加载器
        HookEnv.setHostAppContext(applicationContext);//context
        HookEnv.setHostApkPath(applicationContext.getApplicationInfo().sourceDir);//apk path
        ActivityTools.injectResourcesToContext(applicationContext);
        //初始化mmkv和自定义路径
        String dataDir = applicationContext.getDataDir().getAbsolutePath() + "/qstory_cloud_config";
        SpHelper.Companion.initialize(dataDir);
        //使用这个类加载器 因为框架可能会提供不正确的类加载器，我没有反射工具包装类 所以就不写了
        ClassLoader hostClassLoader = applicationContext.getClassLoader();
        HookInit hookInit = new HookInit();
        hookInit.init();
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        String packageName = loadPackageParam.packageName;
        if (!loadPackageParam.isFirstApplication) return;
        if (!packageName.matches(HookEnv.getTargetPackageName())) return;
        HookEnv.setLoadPackageParam(loadPackageParam);
        //设置当前应用包名
        HookEnv.setCurrentHostAppPackageName(packageName);
        //设置进程名 方便在判断当前运行的是否主进程
        HookEnv.setProcessName(loadPackageParam.processName);

        XposedBridge.hookMethod(getAppContextInitMethod(loadPackageParam), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                if (IS_INJECT.getAndSet(true)) return;
                ContextWrapper wrapper = (ContextWrapper) param.thisObject;
                initAppContext(wrapper.getBaseContext());
            }
        });
    }

    /**
     * 获取application的onCreate方法
     */
    private Method getAppContextInitMethod(XC_LoadPackage.LoadPackageParam loadParam) {
        try {
            if (loadParam.appInfo.name != null) {
                Class<?> clz = loadParam.classLoader.loadClass(loadParam.appInfo.name);
                try {
                    return clz.getDeclaredMethod("attachBaseContext", Context.class);
                } catch (Throwable i) {
                    try {
                        return clz.getDeclaredMethod("onCreate");
                    } catch (Throwable e) {
                        try {
                            return clz.getSuperclass().getDeclaredMethod("attachBaseContext", Context.class);
                        } catch (Throwable m) {
                            return clz.getSuperclass().getDeclaredMethod("onCreate");
                        }
                    }
                }

            }
        } catch (Throwable o) {
            XposedBridge.log("[error]" + Log.getStackTraceString(o));
        }
        try {
            return ContextWrapper.class.getDeclaredMethod("attachBaseContext", Context.class);
        } catch (Throwable u) {
            XposedBridge.log("[error]" + Log.getStackTraceString(u));
            return null;
        }
    }
}