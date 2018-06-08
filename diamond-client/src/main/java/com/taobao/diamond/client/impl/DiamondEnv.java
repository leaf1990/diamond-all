package com.taobao.diamond.client.impl;

import com.taobao.diamond.client.BatchHttpResult;
import com.taobao.diamond.client.DiamondConfigure;
import com.taobao.diamond.client.DiamondSubscriber;
import com.taobao.diamond.common.Constants;
import com.taobao.diamond.domain.ConfigInfoEx;
import com.taobao.diamond.manager.ManagerListener;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiamondEnv {
    private static final Log log = LogFactory.getLog(DiamondEnv.class);
    private static final String DIAMOND_CONF_SERVER_HTTP = "DIAMOND_CONF_SERVER_HTTP";
    private static final String DIAMOND_CONF_SERVER_PORT = "DIAMOND_CONF_SERVER_PORT";
    private static final String DIAMOND_CONF_PORT = "DIAMOND_CONF_PORT";
    private DiamondSubscriber diamondSubscriber = null;

    public DiamondEnv() {
        this(
                System.getenv(DIAMOND_CONF_SERVER_HTTP),
                System.getenv(DIAMOND_CONF_SERVER_PORT),
                System.getenv(DIAMOND_CONF_PORT)
        );
    }

    public DiamondEnv(String diamondServerHttp, String diamondServerPort, String diamondPort) {
        DiamondConfigure diamondConfigure = new DiamondConfigure();

        if (StringUtils.isNotBlank(diamondServerHttp)) {
            diamondConfigure.setConfigServerAddress(diamondServerHttp);
        }

        if (StringUtils.isNumeric(diamondServerPort)) {
            Integer port = Integer.parseInt(diamondServerPort);
            diamondConfigure.setConfigServerPort(port);
        } else if (diamondServerPort != null) {
            throw new RuntimeException("Env " + DIAMOND_CONF_SERVER_PORT + " format error: " + diamondServerPort);
        }

        if (StringUtils.isNumeric(diamondPort)) {
            Integer port = Integer.parseInt(diamondPort);
            diamondConfigure.setPort(port);
        } else if (diamondPort != null) {
            throw new RuntimeException("Env " + DIAMOND_CONF_PORT + " format error: " + diamondPort);
        }

        diamondSubscriber = DiamondClientFactory.getSingletonDiamondSubscriber();
        diamondSubscriber.setDiamondConfigure(diamondConfigure);
        diamondSubscriber.start();
    }

    public String getConfig(String dataId, String groupId, int fromWhere, long timeout) {
        switch (fromWhere) {
            case Constants.GET_FROM_LOCAL_SERVER_SNAPSHOT:
                return diamondSubscriber.getConfigureInfomation(dataId, groupId, timeout);
            case Constants.GET_FROM_SNAPSHOT_LOCAL_SERVER:
                return diamondSubscriber.getAvailableConfigureInfomationFromSnapshot(dataId, groupId, timeout);
            default:
                throw new IllegalArgumentException("invalid fromWhere args");
        }
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

    public BatchHttpResult batchQuery(List<String> dataIds, String group, int timeout) {
        BatchHttpResult result = diamondSubscriber.getConfigureInformationBatch(dataIds, group, timeout);
        if (result.isSuccess()) {
            return result;
        }

        return batchQueryFromLocal(dataIds, group, timeout);
    }

    private BatchHttpResult batchQueryFromLocal(List<String> dataIds, String group, int timeout) {
        List<ConfigInfoEx> configInfoExList = new ArrayList<ConfigInfoEx>(dataIds.size());

        for (String dataId : dataIds) {
            ConfigInfoEx configInfoEx = new ConfigInfoEx(dataId, group, null);
            String content = null;
            try {
                content = diamondSubscriber.getFromLocalAndSnapshot(dataId, group, timeout);
            } catch (Exception e) {
                log.warn("batchQuery error: dataIds=" + dataIds.toString() + " group=" + group, e);
            }

            if (StringUtils.isBlank(content)) {
                log.info("batchQueryFromLocal content is blank. dataId=" + dataId + " group=" + group);

                return new BatchHttpResult(Constants.BATCH_OP_ERROR);
            }

            configInfoEx.setContent(content);
            configInfoEx.setStatus(Constants.BATCH_QUERY_EXISTS);

            configInfoExList.add(configInfoEx);
        }

        return new BatchHttpResult(configInfoExList);
    }

    public List<ManagerListener> getListeners(String dataId, String group) {
        if (group == null) {
            group = Constants.DEFAULT_GROUP;
        }

        return ((DefaultSubscriberListener) diamondSubscriber.getSubscriberListener())
                .getManagerListenerList(dataId, group);
    }
}
