package top.linl.qstorycloud.hook;

import android.content.Context;

public class PathTool {

    public static String getDataPath() {
        //路径需要是data/data/包名/app_qstory_cloud_data的才能被DexClassLoader释放资源
        return HookEnv.getHostAppContext().getDir("qstory_cloud_data", Context.MODE_PRIVATE).getAbsolutePath();
    }
}
