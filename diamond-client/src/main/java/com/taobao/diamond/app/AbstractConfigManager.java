package com.taobao.diamond.app;

import com.taobao.diamond.client.impl.DefaultDiamondEnv;
import com.taobao.diamond.common.Constants;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.ManagerListenerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * Created by yeyayun on 2018/3/14 0014.
 */
public abstract class AbstractConfigManager {
    private final static Log logger = LogFactory.getLog(AbstractConfigManager.class);
    private final static String DEFAULT_GROUP = "DEFAULT_GROUP";

    public AbstractConfigManager() {
        String content = DefaultDiamondEnv.diamondEnv.getConfig(getDataId(), getGroupId(), Constants.GET_FROM_LOCAL_SERVER_SNAPSHOT, 1000);
        try {
            parseContent(content);
        } catch (IOException e) {
            logger.error("load " + getDataId() + " fail. content=" + content, e);
        }

        if (needRefreshConfig()) {
            ManagerListener listener = new ManagerListenerAdapter() {
                @Override
                public void receiveConfigInfo(String content) {
                    try {
                        parseContent(content);
                    } catch (IOException e) {
                        logger.error("refresh " + getDataId() + " fail. content=" + content, e);
                    }
                }
            };
            DefaultDiamondEnv.diamondEnv.addManagerListener(getDataId(), getGroupId(), listener);
        }
    }

    public abstract void parseContent(String content) throws IOException;

    /**
     * String -> properties
     *
     * @param content
     * @return
     * @throws IOException
     */
    public Properties parseAsProperties(String content) throws IOException {
        Properties properties = new Properties();
        properties.load(new StringReader(content));
        return properties;
    }

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
        return DEFAULT_GROUP;
    }

    /**
     * 是否需要刷新配置信息，默认刷新配置
     *
     * @return
     */
    public boolean needRefreshConfig() {
        return true;
    }
}
