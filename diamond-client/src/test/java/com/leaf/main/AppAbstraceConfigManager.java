package com.leaf.main;

import com.taobao.diamond.app.AbstractConfigManager;

public class AppAbstraceConfigManager extends AbstractConfigManager {
    private final static String DATA_ID = "com.taobao.tddl.jdbc.group_V2.4.1_tglearn_group";
    public static AppAbstraceConfigManager config = new AppAbstraceConfigManager();
    private String paramA;
    private String paramB;

    @Override
    public void parseContent(String content) {
        System.out.println(":::::::::::::::::::::" + content);
    }

    /**
     * ªÒ»°dataId
     *
     * @return
     */
    public String getDataId() {
        return DATA_ID;
    }

    public String getParamA() {
        return paramA;
    }

    public AppAbstraceConfigManager setParamA(String paramA) {
        this.paramA = paramA;
        return this;
    }

    public String getParamB() {
        return paramB;
    }

    public AppAbstraceConfigManager setParamB(String paramB) {
        this.paramB = paramB;
        return this;
    }

}
