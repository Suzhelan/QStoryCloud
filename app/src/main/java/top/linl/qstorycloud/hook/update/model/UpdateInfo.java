package top.linl.qstorycloud.hook.update.model;

import java.io.Serializable;
import java.util.Objects;

public class UpdateInfo implements Serializable {

    private Boolean hasUpdate;
    private String latestVersionName;
    private Integer latestVersionCode;
    private String sender;
    private String updateUrl;
    private String updateLog;
    private Boolean mandatoryUpdate;
    private long updateTime = System.currentTimeMillis();

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getHasUpdate() {
        return hasUpdate;
    }

    public void setHasUpdate(Boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public String getLatestVersionName() {
        return latestVersionName;
    }

    public void setLatestVersionName(String latestVersionName) {
        this.latestVersionName = latestVersionName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Integer getLatestVersionCode() {
        return latestVersionCode;
    }

    public void setLatestVersionCode(Integer latestVersionCode) {
        this.latestVersionCode = latestVersionCode;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }


    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }


    public Boolean getMandatoryUpdate() {
        return mandatoryUpdate;
    }

    public void setMandatoryUpdate(Boolean mandatoryUpdate) {
        this.mandatoryUpdate = mandatoryUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateInfo that = (UpdateInfo) o;
        return hashCode() == that.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasUpdate, latestVersionName, latestVersionCode, sender, updateUrl, updateLog, mandatoryUpdate);
    }

    @Override
    public String toString() {
        return "UpdateInfo{" +
                "hasUpdate=" + hasUpdate +
                ", latestVersionName='" + latestVersionName + '\'' +
                ", latestVersionCode=" + latestVersionCode +
                ", sender='" + sender + '\'' +
                ", updateUrl='" + updateUrl + '\'' +
                ", updateLog='" + updateLog + '\'' +
                ", mandatoryUpdate=" + mandatoryUpdate +
                '}';
    }
}
