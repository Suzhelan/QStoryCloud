package top.linl.qstorycloud.model;

import java.io.Serializable;

public class LocalModuleInfo implements Serializable {
    private String moduleName;
    private String moduleVersionName;
    private int moduleVersionCode;
    private String moduleApkPath;

    private long updateTime = System.currentTimeMillis();

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleVersionName() {
        return moduleVersionName;
    }

    public void setModuleVersionName(String moduleVersionName) {
        this.moduleVersionName = moduleVersionName;
    }

    public int getModuleVersionCode() {
        return moduleVersionCode;
    }

    public void setModuleVersionCode(int moduleVersionCode) {
        this.moduleVersionCode = moduleVersionCode;
    }

    public String getModuleApkPath() {
        return moduleApkPath;
    }

    public void setModuleApkPath(String moduleApkPath) {
        this.moduleApkPath = moduleApkPath;
    }



}
