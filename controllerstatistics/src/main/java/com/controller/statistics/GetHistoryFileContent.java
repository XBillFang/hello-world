package com.controller.statistics;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

public class GetHistoryFileContent {
	static Git git;
	public static void main(String[] args) {
		String gitRoot = "D:\\anguo\\.git";
		String branchName = "develop";
		String fileName = "ext/anguo/anguostorefront/web/src/com/tasly/anguo/storefront/controllers";
		String revision = "87fa25af6e2d4c223aa265ad64f173da63e18022";
		try
		{
			//String mobileConfigFacade = getContentWithFile(gitRoot, branchName, fileName, revision);
			//System.out.println(mobileConfigFacade);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		getHistoryInfo(gitRoot);
	}
	
	/**
     * 获取指定分支、指定文件的内容
     * @param gitRoot git仓库目录
     * @param branchName 分支名称
     * @param fileName 文件名称
     * @return
     * @throws Exception
     */
    public static String getContentWithFile(String gitRoot, final String branchName, String fileName, String revision) throws Exception {
   	 
        git = Git.open(new File(gitRoot));
        Repository repository = git.getRepository();
        RevWalk walk = new RevWalk(repository);
        // Ref ref = repository.getRef(branchName);
        // ObjectId objId = ref.getObjectId();
        ObjectId objId = repository.resolve(revision);
        RevCommit revCommit = walk.parseCommit(objId);
        RevTree revTree = revCommit.getTree();
        TreeWalk treeWalk = TreeWalk.forPath(repository, fileName, revTree);
        walk.close();
        //文件名错误
        if (treeWalk == null)
            return null;

        ObjectId blobId = treeWalk.getObjectId(0);
        ObjectLoader loader = repository.open(blobId);
        byte[] bytes = loader.getBytes();
        if (bytes != null)
            return new String(bytes);
        return null;
    }
    
    public static void getHistoryInfo(String gitRoot) {
       File gitDir = new File(gitRoot);
           try {  
               if (git == null) {  
                   git = Git.open(gitDir);  
               }  
               Iterable<RevCommit> gitlog= git.log().call();  
               System.out.println(gitlog.iterator().next().getName());
//               for (RevCommit revCommit : gitlog) {  
//                   String version=revCommit.getName();//版本号  
//                   revCommit.getAuthorIdent().getName();  
//                   revCommit.getAuthorIdent().getEmailAddress();  
//                   revCommit.getAuthorIdent().getWhen();//时间  
//                   System.out.println(version);  
//               }  
           }catch (NoHeadException e) {  
               e.printStackTrace();  
           } catch (GitAPIException e) {  
               e.printStackTrace();  
           } catch (IOException e) {  
               e.printStackTrace();  
           } 
    }
   
}
