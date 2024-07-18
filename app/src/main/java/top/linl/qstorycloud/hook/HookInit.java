package top.linl.qstorycloud.hook;

import top.linl.qstorycloud.hook.moduleloader.ModuleLoader;
import top.linl.qstorycloud.hook.update.UpdateChecker;

public class HookInit {

    public void init() {
        ModuleLoader moduleLoader = new ModuleLoader();
        moduleLoader.readyToLoad();
        UpdateChecker updateChecker = new UpdateChecker();
        updateChecker.startObservingUpdates();
    }

}
