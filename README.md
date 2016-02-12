# hello-world
first repository
test push to github

迈出第一步
****************************************************
1.注册github (最好用gmail)
2.创建新repository前要邮箱认证
3.安装git (http://git-scm.com/download/win)
4.创建文件夹TestGit
5.进入TestGit 右键选git Bash Here
6.进入目录$ cd ~/.ssh
7.创建sshkey ssh-keygen -t rsa -C "b******i@gmail.com"
8./.ssh目录在cygwinhome文件夹内 打开生成的公共密钥id_rsa.pub
9.拷贝id_rsa.pub内所有内容 复制到github->setting->SSH Key里
10.测试是否连接成功 git git@github.com
11.设置用户信息 git config --global user.name "xbill"
                git config --global user.email "b******i@gmail.com"
12.初始化 git init
13.添加远程主机 git remote add helloworld git@github.com:XBillFang/hello-world.git
14.复制到本地 git clone git@github.com:XBillFang/hello-world.git
15.测试更新远程分支内容 修改README.md
16.添加更新文件 git add README.md
17.提交更新并加入注释到本地仓库 git commit -m "first commit"
18.更新本地项目到github git push -u origin master
19.创建分支 git branch Develop
20.切换分支 git checkout Develop
21.将新建分支更新到github git push origin Develop
********************************************************

其他命令
1.git status 查看状态信息
2.git remote -v 查看远程连接原信息
3.git branch 查看全部分支
