package com.farminfinity.farminfinity.Model;

public class UPIApplicationInfo {
    String packageName;
    String applicationName;
    Long version;

    public UPIApplicationInfo(String packageName, String applicationName, Long version) {
        this.packageName = packageName;
        this.applicationName = applicationName;
        this.version = version;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
