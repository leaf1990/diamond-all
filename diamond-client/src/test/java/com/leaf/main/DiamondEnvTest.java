package com.leaf.main;

import com.taobao.diamond.client.BatchHttpResult;
import com.taobao.diamond.client.impl.DefaultDiamondEnv;
import com.taobao.diamond.client.impl.DiamondEnv;
import com.taobao.diamond.manager.ManagerListenerAdapter;
import com.taobao.diamond.utils.JSONUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DiamondEnvTest {

    @Test
    public void test_getConfig() {
        String config = DefaultDiamondEnv.diamondEnv.getConfig("com.taobao.tddl.jdbc.group_V2.4.1_yunamall_group", "DEFAULT_GROUP", 1, 1000);
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

    @Test
    public void test_batchGetConfig() throws Exception {
        List<String> dataIds = new ArrayList<String>();
        dataIds.add("aaa");
        dataIds.add("bbb");
        BatchHttpResult batchHttpResult = new DiamondEnv("127.0.0.1", "8080", "8080")
                .batchQuery(dataIds, "DEFAULT_GROUP", 1000);
        System.out.println(JSONUtils.serializeObject(batchHttpResult));
    }
}
