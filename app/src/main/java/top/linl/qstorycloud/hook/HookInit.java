package top.linl.qstorycloud.hook;


import top.linl.qstorycloud.hook.moduleloader.ModuleLoader;
import top.linl.qstorycloud.hook.update.UpdateObserver;
import top.sacz.qstory.hook.UpdateTask;
import top.sacz.qstory.hook.ui.UpdateLogDisplay;

public class HookInit {

    public void init() {
        //模块加载器，加载模块
        ModuleLoader moduleLoader = new ModuleLoader();
        moduleLoader.readyToLoad();

        if (HookEnv.isMainProcess()) {
            //展示更新日志
            UpdateLogDisplay updateLogDisplay = new UpdateLogDisplay();
            updateLogDisplay.hook();
            //监听更新
            UpdateTask updateTask = new UpdateTask();
            updateTask.init();
        }
    }

}
