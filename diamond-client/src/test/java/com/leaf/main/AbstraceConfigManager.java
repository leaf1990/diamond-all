package com.leaf.main;

public abstract class AbstraceConfigManager {

    /**
     * ��ȡdataId
     *
     * @return
     */
    public abstract String getDataId();

    /**
     * ��ȡgroupId
     *
     * @return
     */
    public String getGroupId() {
        return "DEFAULT_GROUP";
    }
}
