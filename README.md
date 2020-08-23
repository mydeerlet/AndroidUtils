# AndroidUtils

#### 介绍

1.在项目目录的 build.gradle  中添加   maven { url 'https://jitpack.io' }

2.使用 JitPack 生成依赖 打开 https://jitpack.io/


3.初始化仓库 git init

4.关联远程仓库 git remote add origin xxxxx

5.添加 git add *

6.提交 git commit —m"初次提交"

7.拉取 git pull origin master

8.上传 git push -u -f origin master

WebView 启动app
   <activity android:name=".guide.WelComeActivity">
     <intent-filter>
         <action android:name="android.intent.action.MAIN" />

         <category android:name="android.intent.category.LAUNCHER" />
         <category android:name="android.intent.category.BROWSABLE"/>
         <category android:name="android.intent.category.DEFAULT"/>
         <action android:name="android.intent.action.VIEW"/>
         <data android:scheme="xl" android:host="goods" android:path="/goodsDetail" android:port="8888"/>
     </intent-filter>
 </activity>
 
10.<? a href="xl://goods:8888/goodsDetail?goodsId=10011002">Clicsdf asdfsadfasd asdfasdfasdfk
