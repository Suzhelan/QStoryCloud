package top.linl.qstorycloud.config;

import com.alibaba.fastjson2.TypeReference;

import java.util.ArrayList;
import java.util.List;

import top.linl.qstorycloud.model.LocalModuleInfo;
import top.linl.qstorycloud.util.SpHelper;

public class LocalModuleData {


    private static final SpHelper spHelper =new SpHelper("ModuleData");

    private LocalModuleData() {
    }

    public static LocalModuleInfo getLastModuleInfo() {
        //获取尾部数据
        List<LocalModuleInfo> dataList = getDataList();
        if (dataList.isEmpty()) {
            return null;
        }
        return dataList.get(dataList.size() - 1);
    }

    public static void addModuleInfo(LocalModuleInfo localModuleInfo) {
        List<LocalModuleInfo> dataList = getDataList();
        dataList.add(localModuleInfo);
        spHelper.put("dataList", dataList);
    }

    public static void clear() {
        spHelper.put("dataList", new ArrayList<LocalModuleInfo>());
    }

    private static List<LocalModuleInfo> getDataList() {
        List<LocalModuleInfo> result = spHelper.getType("dataList", new TypeReference<ArrayList<LocalModuleInfo>>() {
        });
        if (result == null) {
            result = new ArrayList<>();
        }
        return result;
    }

}
