package top.linl.qstorycloud.hook;

import top.linl.qstorycloud.hook.moduleloader.ModuleLoader;
import top.linl.qstorycloud.hook.update.DetectUpdates;

public class HookInit {

    public void init() {
        ModuleLoader moduleLoader = new ModuleLoader();
        moduleLoader.readyToLoad();
        DetectUpdates detectUpdates = new DetectUpdates();
        detectUpdates.startObservingUpdates();
    }

}
