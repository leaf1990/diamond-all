package com.leaf.main;

import com.taobao.diamond.client.impl.DefaultDiamondEnv;
import com.taobao.diamond.manager.ManagerListenerAdapter;
import org.junit.Test;

public class DiamondEnvTest {

    @Test
    public void test_getConfig() {
        String config = DefaultDiamondEnv.diamondEnv.getConfig("aa.bb.cc", "DEFAULT_GROUP", 1, 1000);
        DefaultDiamondEnv.diamondEnv.addManagerListener("aa.bb.cc", "DEFAULT_GROUP", new ManagerListenerAdapter() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println(configInfo);
            }
        });
        System.out.println(config);
    }

    @Test
    public void test_appGetConfig() {
        AppAbstraceConfigManager.config.getParamA();
    }
}
