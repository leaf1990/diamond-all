package com.taobao.diamond.manager;

public abstract class SkipInitialCallbackListener implements ManagerListener {

    protected String data;

    public SkipInitialCallbackListener(String data) {
        this.data = data;
    }

    public abstract void receiveConfigInfo0(String data);

    @Override
    public void receiveConfigInfo(String configInfo) {
        receiveConfigInfo0(configInfo);
    }
}
