package com.zero.publish.service;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import com.zero.publish.config.ConfigBuilder;
import com.zero.publish.config.ServerConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Zero
 * @Date 2021/9/3 22:48
 * @Since 1.8
 * @Description
 **/
@Data
@Slf4j
public class ServerService {
    private ServerConfig serverConfig;

    private ConfigBuilder configBuilder;

    public static final String JAR_META_INF="META-INF";

    public ServerService(ConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
        this.serverConfig = configBuilder.getServerConfig();
    }
    public void invoke(String targetDir,String remoteUrl) throws FileNotFoundException {
        //开始服务器运行
        invokeShell(serverConfig.getIp(),serverConfig.getPort(),serverConfig.getUsername(),
                serverConfig.getPassword(), targetDir,remoteUrl);
    }

    private void invokeShell(String ip,Integer port,String name,String pwd,String targetDir,String remoteUrl) throws FileNotFoundException {

        Connection conn = connectServer(ip, name, pwd);
        if(conn == null) {
            throw new RuntimeException("登录远程机器失败");
        }
        boolean upload= true;
        //提交配置文件
        if(serverConfig.getIsUploadFile()) {
           /* if(null == fileName || fileName.length ==0) {
                throw new RuntimeException("缺少配置文件的地址");
            }*/
            //创建应用目录
            final String mergeDir = mergeDir(serverConfig.getDir(), targetDir);
            log.info("开始创建远程服务器文件目录: {}",mergeDir);
            RemoteInvokeShell(conn, "mkdir " + mergeDir);

            //将脚本文件、配置文件提交到服务器
            final File shellFile = getShellFile();
            final List<File> ymlFile = getYMLFile();
            if(null != shellFile) {
                ymlFile.add(shellFile);
            }
            if( null== ymlFile || ymlFile.size() == 0) {
                throw new RuntimeException("没有文件");
            }
            final List<String> list = ymlFile.stream().map(file -> {
                final String path = file.getAbsolutePath();
                return path;
            }).collect(Collectors.toList());
            final Connection connection = connectServer(ip, name, pwd);
            if(null == connection) {
                throw new RuntimeException("远程登录失败");
            }
            upload = commitConfigFile(connection, mergeDir,list.toArray(new String[list.size()]));
        }
        if(upload) {
            log.info("提交文件成功");
            //配置文件真实路径
            final String mergeDir = mergeDir(serverConfig.getDir(), targetDir);
            //生成远程命令
            final String mergeCmd = mergeCmd(remoteUrl, port, mergeDir);
            final Connection connection = connectServer(ip, name, pwd);
            if(null == connection) {
                throw new RuntimeException("远程登录失败");
            }
            RemoteInvokeShell(connection, mergeCmd);
        }

    }
    /**
     * 生成发布命令
     * @param remoteUrl
     * @param port
     * @param mergeDir
     * @return
     * @throws FileNotFoundException
     */
    private String mergeCmd( String remoteUrl, Integer port, String mergeDir) throws FileNotFoundException {
        String cmd="sh " + mergeDir + getShellFile().getName() + " " + remoteUrl + " " + port + " "+ serverConfig.getIsUploadFile().toString();
        return cmd;
    }

    /**
     * 提交文件
     * @param connection
     * @param targetDir
     * @param fileName
     * @return
     */
    private boolean commitConfigFile(Connection connection,String targetDir,String...fileName) {
        try {
            SCPClient scpClient = new SCPClient(connection);
            scpClient.put(fileName, targetDir);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            this.close(connection);
        }
        return  true;
    }

    /**
     * 连接服务器
     * @param ip
     * @param name
     * @param pwd
     * @return
     */
    private Connection connectServer(String ip,String name,String pwd) {
        Connection conn=null;
        try {
            conn = new Connection(ip,22);
            conn.connect();
            if (conn.authenticateWithPassword(name, pwd)) {
                log.info("登录远程机器成功" + ip);
                return conn;
            } else {
                log.info("登录远程机器失败" + ip);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放服务器连接
     * @param connection
     */
    private void close(Connection connection) {
        if(connection != null) {
            connection.close();
        }
    }

    /**
     * 获取本地shell脚本文件
     * @return
     * @throws FileNotFoundException
     */
    private File getShellFile() throws FileNotFoundException {

        String path = ResourceUtils.getURL("classpath:").getPath();
        System.out.println(path);
        for (File file : new File(path).listFiles()) {
            if(file.getName().contains(".sh")) {
                return file;
            }
        }
        return null;
    }

    /**
     * 获取配置文件列表
     * @return
     * @throws FileNotFoundException
     */
    private List<File> getYMLFile() throws FileNotFoundException {
        String path = ResourceUtils.getURL("classpath:").getPath();
        List<File> listYmlFile = new ArrayList<>();
        for (File file:new File(path).listFiles()) {
            if((file.getName().contains(".yml") || file.getName().contains(".properties")) && !file.getName().equals("application.yml")) {
                listYmlFile.add(file);
            }
        }
        return listYmlFile;
    }

    /**
     * 配置发布目录
     * @param root
     * @param appDir
     * @return
     */
    private String mergeDir(String root,String appDir) {
        final StringBuffer buffer = new StringBuffer("/");
        final String[] splitDir = root.trim().split("\\/");
        for(String dir: splitDir) {
            if(!dir.equals("") ) {
                buffer.append(dir + "/");
            }

        }
        buffer.append(appDir + "/");
        return buffer.toString();
    }
    /**
     * 执行远程服务器上的shell脚本
     * @param cmds shell命令
     */
    public void RemoteInvokeShell(Connection conn,String cmds) {
        try {
            final Session session = conn.openSession();
            // Execute a command on the remote machine.
            log.info("开始执行 {}",cmds);
            session.execCommand(cmds);
            BufferedReader br = new BufferedReader(new InputStreamReader(session.getStdout()));
            BufferedReader brErr = new BufferedReader(new InputStreamReader(session.getStderr()));
            String line;
            while ((line = br.readLine()) != null) {
                log.info("br={}", line);
            }
            while ((line = brErr.readLine()) != null) {
                log.info("brErr={}", line);
            }
            if (null != br) {
                br.close();
            }
            if(null != brErr){
                brErr.close();
            }
            session.waitForCondition(ChannelCondition.EXIT_STATUS, 0);
            int ret = session.getExitStatus();
            log.info("getExitStatus:"+ ret);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            close(conn);
        }
    }
}
