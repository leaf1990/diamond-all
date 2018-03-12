package com.leaf.main;

public abstract class AbstraceConfigManager {

    /**
     * 获取dataId
     *
     * @return
     */
    public abstract String getDataId();

    /**
     * 获取groupId
     *
     * @return
     */
    public String getGroupId() {
        return "DEFAULT_GROUP";
    }
}
