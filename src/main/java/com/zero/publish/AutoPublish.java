package com.zero.publish;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import com.zero.publish.config.ConfigBuilder;
import com.zero.publish.service.RemoteService;
import com.zero.publish.config.ServerConfig;
import com.zero.publish.config.StoreConfig;
import com.zero.publish.service.ServerService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Zero
 * @Date 2021/9/1 11:48
 * @Since 1.8
 * @Description
 **/
@Data
@Slf4j
public class AutoPublish {

    private StoreConfig storeConfig;

    private ServerConfig serverConfig;

    private ConfigBuilder configBuilder;

    private RemoteService remoteService;

    private ServerService serverService;

    public void execute() throws FileNotFoundException {
        log.info("=======开始发布项目======");
        if(null == configBuilder) {
            final ConfigBuilder config = new ConfigBuilder(storeConfig, serverConfig);
            if(null == remoteService) {
                remoteService = new RemoteService(config);
            }
            if(null == serverService) {
                serverService = new ServerService(config);
            }
        }
        String remoteUrl = remoteService.remoteOperation();
        String targetDir = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1,remoteUrl.lastIndexOf("."));
        serverService.invoke(targetDir, remoteUrl);
        log.info("=======项目已经发布======");
    }
}
