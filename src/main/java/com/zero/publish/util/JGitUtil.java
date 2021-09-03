package com.zero.publish.util;

/**
 * @Author Zero
 * @Date 2021/9/1 13:15
 * @Since 1.8
 * @Description
 **/

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Git操作工具类
 */
@Slf4j
public class JGitUtil {



    public enum SqlTypeEnum {
        SQL_CALC, EMAIL, MYSQL_TO_HIVE, HIVE_TO_MYSQL
    }


    final static Logger LOG = LoggerFactory.getLogger(JGitUtil.class);


    /**
     * 检查是否存在分支
     * @param local_repo
     * @param branchName
     * @return
     * @throws IOException
     */
    public static boolean isExistBranch(String local_repo,String branchName) throws IOException {
        String newBranchIndex = "refs/heads/" + branchName;
        Git git = Git.open(new File(local_repo));
        try {
            //检查新建的分支是否已经存在，如果存在则将已存在的分支强制删除并新建一个分支
            List<Ref> refs = git.branchList().call();
            for (Ref ref : refs) {
                if (ref.getName().equals(newBranchIndex)) {
                    System.out.println("exist branch:" + branchName);
                    return true;
                }
            }
            System.out.println("not exist branch named" + branchName);
            return false;
        } catch (GitAPIException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除指定的分支
     * @param local_repo
     * @param branchName
     * @return
     * @throws IOException
     */
    public static boolean deleteBranch(String local_repo,String branchName) throws IOException, GitAPIException {
        Git git = Git.open(new File(local_repo));
        String newBranchIndex = "refs/heads/" + branchName;
        if(isExistBranch(local_repo, branchName)) {
            git.branchDelete().setBranchNames(branchName).setForce(true).call(); //强制删除该分支
            System.out.println("deleted " + branchName);
            return true;
        }
        System.out.println("not exist branch named" + branchName);
        return false;
    }
    /**
     * sql脚本文件同步到git仓库
     *
     * @param qte         SQl类型
     * @param loginName   系统登录名
     * @param fileName    文件名
     * @param
     * @param comment     提交说明
     * @return
     */
//    public static boolean writeFileToGit(SqlTypeEnum qte, String loginName, String sqlConent, String fileName, String comment,boolean force) {
//
//       /* JGitUtil.pull();
//        String dest = LOCAL_CODE_CT_SQL_DIR + qte.name().toLowerCase();
//        String path = LOCAL_REPO_PATH + "/" + dest;
//        File f = new File(path);
//        if (!f.exists()) {
//            f.mkdirs();
//        }
//        dest = dest + "/" + fileName;
//        path = path + "/" + fileName;
//        comment = loginName + " option of " + comment;
//        return true == JGitUtil.createFile(sqlConent, path) == JGitUtil.commitAndPush(dest, comment,force);*/
//    }

    /**
     * 根据主干master新建分支并同步到远程仓库
     *
     * @param branchName 分支名
     * @throws IOException
     * @throws GitAPIException
     */
    public static boolean newBranch(String local_repo,String branchName) throws IOException {
        String newBranchIndex = "refs/heads/"+branchName;
        String gitPathURI = "";
        Git git =Git.open(new File(local_repo));
        try {

            //检查新建的分支是否已经存在，如果存在则将已存在的分支强制删除并新建一个分支
            List<Ref> refs = git.branchList().call();
            for (Ref ref : refs) {
                if (ref.getName().equals(newBranchIndex)) {
                    System.out.println("Removing branch before");
                    git.branchDelete().setBranchNames(branchName).setForce(true).call(); //强制删除该分支
                    break;
                }
            }
            //新建分支
            Ref ref = git.branchCreate().setName(branchName).call();
            //推送到远程
            git.push().add(ref).call();
//            gitPathURI = REMOTE_REPO_URI + " " + "/" + branchName;
            return true;
        } catch (GitAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 添加文件
     * @param
     * @return
     */
    public static boolean addFile(String localRpoConfig,String gitDir) {

        boolean addFileFlag = true;
        try (
                Git git = Git.open(new File(localRpoConfig));
        ) {

            //add file to git
            git.add().addFilepattern(gitDir).call();
            System.out.println("Added file to repository at " + git.getRepository().getDirectory());
        } catch (Exception e) {
            e.printStackTrace();
            addFileFlag = false;
        }
        return addFileFlag;
    }

    /**
     * 提交代码到本地仓库
     *
     * @param \\filePath 文件位置(相对仓库位置:a/b/file)
     * @param comment  提交git内容描述
     * @return
     */
    public static boolean commitFile(String comment,String localRepoConfig) {

        boolean commitFileFlag = true;
        try (Git git = Git.open(new File(localRepoConfig));) {
            //提交代码到本地仓库
            git.commit().setMessage(comment).call();
            LOG.info("Committed to repository at " + git.getRepository().getDirectory());
        } catch (Exception e) {
            e.printStackTrace();
            commitFileFlag = false;
            LOG.error("commitFile error! \n" + e.getMessage());
        }
        return commitFileFlag;
    }

    public static boolean push(String localRepoConfig,String username,String password) {

        boolean pushFlag = true;
        try (Git git = Git.open(new File(localRepoConfig));) {
            //提交代码到本地仓库
            UsernamePasswordCredentialsProvider provider;
            provider = new UsernamePasswordCredentialsProvider(username, password);
            git.push().setCredentialsProvider(provider).call();
            LOG.info("push " + git.getRepository() + File.separator + git.getRepository().getBranch());
        } catch (Exception e) {
            e.printStackTrace();
            pushFlag = false;
            LOG.error("push error! \n" + e.getMessage());
        }
        return pushFlag;
    }

    /**
     * 提交并推送代码至远程服务器
     *
     * @param \\ 提交文件路径(相对路径)
     * @param desc     提交描述
     * @return
     */
    public static String commitAndPush(String gitDir, //提交代码地址
                                        String localRep, //仓库地址
                                        String localRepoConfig, //仓库配置文件地址
                                        String desc, //描述
                                        boolean force,
                                        String username,
                                        String password) {
        String url = null;
        try(Git git = Git.open(new File(localRepoConfig))) {
            UsernamePasswordCredentialsProvider provider;
            provider = new UsernamePasswordCredentialsProvider(username, password);
            //添加
            git.add().addFilepattern(".").call();
            //提交
            git.commit().setMessage(desc).call();
            //推送到远程
            if (!StringUtils.isBlank(username) && !StringUtils.isBlank(password)) { //账户密码不为空
                final Iterable<PushResult> call = git.push().setCredentialsProvider(provider).setForce(force).call();
                url=call.iterator().next().getURI().toString();
            } else {
                LOG.error("账户名或密码错误");
                return null;
            }
            LOG.info("Commit And Push file " + gitDir + " to repository at " + git.getRepository().getDirectory());
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Commit And Push error! \n" + e.getMessage());
        }
        return url;
    }

    /**
     * 拉取远程代码
     * @param\\ remoteBranchName
     * @return 远程分支名
     */
    public static boolean pull(String remoteBranchName,String localRepoConfig) {
        boolean pullFlag = true;
        try (Git git = Git.open(new File(localRepoConfig));) {
            git.pull()
                    .setRemoteBranchName(remoteBranchName).call();
        } catch (Exception e) {
            e.printStackTrace();
            pullFlag = false;
        }
        return pullFlag;
    }

    /**
     * 切换分支
     * @param branchName
     * @return
     */
    public static boolean checkout(String branchName,String localRepoConfig) {

        boolean checkoutFlag = true;
        try (Git git = Git.open(new File(localRepoConfig))) {
            git.checkout().setName("refs/heads/" + branchName).setForce(true).call();
        } catch (Exception e) {
            e.printStackTrace();
            checkoutFlag = false;
        }
        return checkoutFlag;
    }


    /**
     * 从远程获取最新版本到本地   不会自动合并 merge
     * @param \\branchName
     * @return
     */
    public static boolean fetch(String localRepoConfig,String username,String password) {
        boolean fetchFlag = true;
        UsernamePasswordCredentialsProvider provider =new UsernamePasswordCredentialsProvider(username,password);
        try (Git git = Git.open(new File(localRepoConfig));) {
            git.fetch().setCheckFetchedObjects(true).call();
        } catch (Exception e) {
            e.printStackTrace();
            fetchFlag = false;
        }
        return fetchFlag;
    }

    /**
     * 删除文件夹
     * @param file
     */
    private static void deleteFolder(File file) {
        if (file.isFile() || file.list().length == 0) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFolder(files[i]);
                files[i].delete();
            }
        }
    }

    /**
     * 生成文件写内容
     *
     * @param content  文件内容
     * @param filePath 文件名称
     */
    @SuppressWarnings("unused")
    private static boolean createFile(String content, String filePath) {

        //删除前一天临时目录
//	  File af = new File(filePath+File.separator+DateUtil.getAgoBackDate(-1));
//	  if (af.()) {
//		  deleexiststeFolder(af);
//	  }
//	  //创建临时存储目录
//	  File f = new File(filePath+File.separator+DateUtil.getAgoBackDate(0));
//	  if (!f.exists()) {
//		f.mkdirs();
//	  }
//	  if (!fileName.endsWith(".sql")) {
//		  fileName+=".sql";
//	  }
        boolean createFileFlag = true;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                createFileFlag = false;
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8));) {
            bw.write(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            createFileFlag = false;
        } catch (IOException e) {
            e.printStackTrace();
            createFileFlag = false;
        }
        return createFileFlag;
    }
    /**
     * 创建本地新仓库
     * @param repoPath 仓库地址 D:/workspace/TestGitRepository
     * @return
     * @throws IOException
     */
    public static Repository createNewRepository(String repoPath) throws IOException {
        File localPath = new File(repoPath);
        if(!localPath.exists()) {
            localPath.mkdirs();
        }
        // create the directory
        Repository repository = FileRepositoryBuilder.create(new File(localPath + "/.git"));
        repository.create();
        return repository;
    }
    public static CredentialsProvider createCredential(String userName, String password) {
        return new UsernamePasswordCredentialsProvider(userName, password);
    }
    /**
     * 创建仓库，只执行一次
     */
    public static boolean setupRepository(String username ,String password,String remoteUrl,String localRepo) {
        boolean setupRepositoryFlag = true;
        try {
            //设置远程服务器上的用户名和密码
            final CredentialsProvider provider = createCredential(username, password);
            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                Git git = Git.cloneRepository().setURI(remoteUrl) //设置远程URI
                        .setBranch("master")   //设置clone下来的分支,默认master
                        .setDirectory(new File(localRepo))  //设置下载存放路径
                        .call();
            } else {
                Git git = Git.cloneRepository().setURI(remoteUrl) //设置远程URI
                        .setBranch("master")   //设置clone下来的分支,默认master
                        .setDirectory(new File(localRepo))  //设置下载存放路径
                        .setCredentialsProvider(provider) //设置权限验证
                        .call();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setupRepositoryFlag = false;
        }
        return setupRepositoryFlag;
    }

}