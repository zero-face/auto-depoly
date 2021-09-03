package com.zero.publish.po;

/**
 * @Author Zero
 * @Date 2021/9/1 21:33
 * @Since 1.8
 * @Description
 **/

public class ConfigBuilder {
    private StoreConfig storeConfig;
    private ServerConfig serverConfig;
    public ConfigBuilder(StoreConfig storeConfig,ServerConfig serverConfig) {
        if(null == storeConfig) {
            throw new RuntimeException("must config the store info");
        } else {
            this.storeConfig = storeConfig;
        }
        if(null == serverConfig) {
            throw new RuntimeException("must config the server info");
        } else {
            this.serverConfig = serverConfig;
        }
    }

}
