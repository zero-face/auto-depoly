package com.zero.publish.service;

import com.zero.publish.config.ConfigBuilder;
import com.zero.publish.config.StoreConfig;
import com.zero.publish.util.JGitUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @Author Zero
 * @Date 2021/9/3 21:50
 * @Since 1.8
 * @Description
 **/
@Slf4j
public class RemoteService {

    private ConfigBuilder configBuilder;

    private StoreConfig storeConfig;

    public  RemoteService(ConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
        this.storeConfig = configBuilder.getStoreConfig();
    }
    public String remoteOperation() {
       checkLocal();
       boolean remote = getRemote();
       if(!remote) {
           log.error("Associated with the repository fail");
           throw new RuntimeException("Associated with the repository fail");
       }
       log.info("Associated with the repository success");
        final String remoteUrl = submit();
        if(null == remoteUrl) {
            log.info("code submit fail");
            throw new RuntimeException("code submit fail");
        }
        return remoteUrl;
    }

    public void checkLocal() {
        final File file = new File(storeConfig.getLocal_repo_path());
        if(file.exists()) {
            if(file.listFiles().length>0) {
                throw new RuntimeException("Local repository must is empty!");
            }
        }
    }

    /**
     * 提交代码
     * @return
     */
    private String submit() {
        return JGitUtil.commitAndPush(configBuilder.getLocal_repogit_config(), //需要提交代码地址
                storeConfig.getLocal_repo_path(), //仓库地址
                configBuilder.getLocal_repogit_config(),  //仓库配置文件地址
                "第一次提交", //提交描述
                false, //不强制
                storeConfig.getGit_username(),//账户
                storeConfig.getGit_password()); //密码
    }

    /**
     * 本地和远程仓库建立联系
     * @return
     */
    private boolean getRemote() {
        return JGitUtil.setupRepository(storeConfig.getGit_username(),
                storeConfig.getGit_password(),
                storeConfig.getRemote_repo_uri(),
                storeConfig.getLocal_repo_path());
    }

}
