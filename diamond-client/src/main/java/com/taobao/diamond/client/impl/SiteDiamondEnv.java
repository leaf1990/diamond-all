package com.taobao.diamond.client.impl;

public class SiteDiamondEnv {

    public static DiamondEnv getDiamondUnitEnv(String diamondServerHttp) {
        return new DiamondEnv(diamondServerHttp, null, null);
    }

    public static DiamondEnv getDiamondUnitEnv(String diamondServerHttp, String diamondServerPort) {
        return new DiamondEnv(diamondServerHttp, diamondServerPort, null);
    }

    public static DiamondEnv getDiamondUnitEnv(String diamondServerHttp, String diamondServerPort, String diamondPort) {
        return new DiamondEnv(diamondServerHttp, diamondServerPort, diamondPort);
    }

}
