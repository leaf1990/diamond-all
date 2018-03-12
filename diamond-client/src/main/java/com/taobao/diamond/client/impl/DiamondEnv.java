package com.taobao.diamond.client.impl;

import com.taobao.diamond.client.DiamondConfigure;
import com.taobao.diamond.client.DiamondSubscriber;
import com.taobao.diamond.manager.ManagerListener;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

public class DiamondEnv {

    private static final String DIAMOND_SERVER_HTTP = "DIAMOND_SERVER";
    private static final String DIAMOND_SERVER_PORT = "DIAMOND_PORT";
    private DiamondSubscriber diamondSubscriber = null;

    public DiamondEnv() {
        String diamondServer = System.getenv(DIAMOND_SERVER_HTTP);
        String diamondPort = System.getenv(DIAMOND_SERVER_PORT);

        diamondSubscriber = DiamondClientFactory.getSingletonDiamondSubscriber();
        DiamondConfigure diamondConfigure = new DiamondConfigure();

        if (StringUtils.isNotBlank(diamondServer)) {
            diamondConfigure.setConfigServerAddress(diamondServer);
        }
        if (StringUtils.isNotBlank(diamondPort)) {
            try {
                diamondConfigure.setConfigServerPort(Integer.parseInt(diamondPort));
            } catch (Exception e) {
                // ignore this exception
            }
        }

        diamondSubscriber.setDiamondConfigure(diamondConfigure);
        diamondSubscriber.start();
    }

    public String getConfig(String dataId, String groupId, int fromWhere) {

        return diamondSubscriber.getConfigureInfomation(dataId, groupId, 3000);
    }

    /**
     * Ìí¼ÓÅäÖÃ¼àÌýÆ÷
     *
     * @param dataId
     * @param groupId
     * @param managerListener
     */
    public void addManagerListener(String dataId, String groupId, ManagerListener managerListener) {
        ((DefaultSubscriberListener) diamondSubscriber.getSubscriberListener())
                .addManagerListeners(dataId, groupId, Arrays.asList(managerListener));
    }

    /**
     * ÒÆ³ý¼àÌýÆ÷
     *
     * @param dataId
     * @param groupId
     */
    public void removeManagerListener(String dataId, String groupId) {
        ((DefaultSubscriberListener) diamondSubscriber.getSubscriberListener()).removeManagerListeners(dataId, groupId);
    }
}
