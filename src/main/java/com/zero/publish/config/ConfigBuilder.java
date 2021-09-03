package com.zero.publish.config;

import com.zero.publish.service.RemoteService;
import lombok.Data;

/**
 * @Author Zero
 * @Date 2021/9/1 21:33
 * @Since 1.8
 * @Description
 **/
@Data
public class ConfigBuilder {

    private StoreConfig storeConfig;

    private ServerConfig serverConfig;

    /**
     * 本地仓库配置路径（.git）
     */
    private String local_repogit_config;

    private RemoteService remoteService;



    ConfigBuilder() {

    }
    public ConfigBuilder(StoreConfig storeConfig,ServerConfig serverConfig) {
        if(null == storeConfig) {
            throw new RuntimeException("must config the store info");
        } else {
            this.storeConfig = storeConfig;
            setLocalRepogitConfig();
        }
        if(null == serverConfig) {
            throw new RuntimeException("must config the server info");
        } else {
            this.serverConfig = serverConfig;

        }
    }

    public void setLocalRepogitConfig() {
        this.local_repogit_config = storeConfig.getLocal_repo_path() + "\\.git";

    }





}
