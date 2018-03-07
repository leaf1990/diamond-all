package com.leaf.main;

import com.taobao.diamond.manager.DiamondManager;
import com.taobao.diamond.manager.ManagerListener;
import com.taobao.diamond.manager.impl.DefaultDiamondManager;

import java.util.concurrent.Executor;

/**
 * Created by yeyayun on 2018/3/7 0007.
 */
public class AbstractConfigManager {
    public static void main(String[] args) {

//        String content = DiamondEnvRepo.defaultEnv.getConfig(getDataId(), getGroupId(),
//                Constants.GETCONFIG_LOCAL_SERVER_SNAPSHOT, 1000);

        DiamondManager manager = new DefaultDiamondManager(
                "DEFAULT_GROUP",
                "com.taobao.tddl.jdbc.group_V2.4.1_tglearn_group",
                new ManagerListener() {

                    public Executor getExecutor() {
                        return null;
                    }

                    public void receiveConfigInfo(String configInfo) {
                        System.out.println("changed config: " + configInfo);
                    }
                });
        String configureInfomation = manager.getConfigureInfomation(1000);
        System.out.println(configureInfomation);

//        DiamondManager manager = new DefaultDiamondManager("DEFAULT_GROUP", "topicConfig",
//                new ManagerListener() {//填写你服务端后台保存过的group和dataId
//                    public void receiveConfigInfo(String configInfo) {
//                        System.out.println("changed config: " + configInfo);
//                    }
//
//                    public Executor getExecutor() {
//                        return null;
//                    }
//                }, "127.0.0.1,10.126.53.16,10.126.53.17");
    }
}
