package com.zero.publish.po;

import lombok.Data;

/**
 * @Author Zero
 * @Date 2021/9/1 11:54
 * @Since 1.8
 * @Description 仓库配置
 **/
@Data
public class StoreConfig {
    /**
     * 本地仓库路径
     */
    private String local_repo_path;
    /**
     * 本地仓库配置路径（.git）
     */
    private String local_repogit_config;
    /**
     * 远程仓库地址
     */
    private String remote_repo_uri;
    /**
     * 本地代码路径
     */
    private String init_local_code_dir;
    private String local_code_ct_sql_dir;
    /**
     * 分支名
     */
    private String branch_name;
    /**
     * 用户名
     */
    private String git_username;
    /**
     * 密码
     */
    private String git_password;
}
