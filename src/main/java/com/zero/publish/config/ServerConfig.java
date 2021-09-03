package com.zero.publish.config;

import lombok.Data;

/**
 * @Author Zero
 * @Date 2021/9/1 11:56
 * @Since 1.8
 * @Description
 **/
@Data
public class ServerConfig {
    /**
     * 服务器ip
     */
    private String ip;
    /**
     * 应用发布的端口号
     */
    private Integer port;
    /**
     * 服务器用户名(默认root)
     */
    private String username="root";
    /**
     * 服务器密码
     */
    private String password;
    /**
     * 执行命令
     */
    private String cmd;
    /**
     * 代码，应用地址(默认在登录用户目录下)
     */
    private String dir="/" + username;
    /**
     * 是否传递文件（默认不传递）
     */
    private Boolean isUploadFile=true;


}
