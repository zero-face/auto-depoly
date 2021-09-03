# auto-deploy

#### 介绍
An automated publishing app dependency for developers can greatly simplify the process of publishing an app. Key configuration, one-click deployment.

#### 软件架构
软件架构说明


#### 安装教程
1.手动下载安装

（1）使用git或者是下载压缩包到本地。
（2）使用maven进行安装到本地仓库中或者是服务器仓库中
（3）正常引入依赖即可

2.maven依赖

maven依赖正在申请中，一旦申请通过，第一时间发布更新......


#### 使用说明

1. 配置参数
AutoPublish autoPublish = new AutoPublish();
//仓库配置
StoreConfig storeConfig = new StoreConfig();
        storeConfig.setBranch_name("master");//分支名
        storeConfig.setGit_username("xxx");//仓库用户名
        storeConfig.setGit_password("xxx");//代码仓库登录密码
        storeConfig.setRemote_repo_uri("xxx");//远程仓库路径
        storeConfig.setLocal_repo_path("xxx"); //本地仓库代码路径（注意：这个仓库可以不存在，存在则必须为空，否则会抛出"Local repository must is empty!"异常）
        storeConfig.setLocal_code_dir("xxx");//本地需要提交提交代码路径
        autoPublish.setStoreConfig(storeConfig);
//服务器配置
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setIp("xxx");//服务器地址
        serverConfig.setUsername("xxx");//服务器登录用户名（默认为root）
        serverConfig.setPassword("xxx");//服务器登录密码
        serverConfig.setPort(xxx);//应用发布的端口（一旦端口被占用会被强行终止）
        serverConfig.setDir("/xxx");//项目发布到服务器上的地址（默认在登录用户名目录下）
        serverConfig.setIsUploadFile(true);//项目发布是否需要上传配置文件，也就是yml文件（由于yml文件中信息敏感，我们不会发布到仓库中，所以需要单独传输，如果项目无配置文件也可以不传）
        autoPublish.setServerConfig(serverConfig);//
2.导入shell脚本文件
  上面仓库中shell目录下的shell文件是用于服务器执行项目启动命令的必要文件，所以需要下载到本地并且放入发布项目的根目录下。

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技


