##Android 应用自动更新(AppUpdate)

之前一直使用友盟的自动更新，用的挺好的，但是10月份就会下架，现在处于维护可用状态。。有些机型已经不能使用了，，各种下载失败，无更新提示。
so 自己动手吧

* > 1、版本检查
* > 2、文件下载
* > 3、软件安装(首先检查文件是否完整)
* > 4、是否强制更新（强制更新时，取消更新会退出）


####1、导入library项目
在Application中配置应用检查地址
    `UpdateUtil.updateUrl = url;
可配置通知栏方式可对话框方式

* > 默认方式(同时使用通知栏与对话框，无更新时会提示，有更新时弹出对话框)
   
    `new UpdateUtil.check(activity)`
    
* > 无更新时是否提示（同时使用通知栏与对话框，有更新时弹出对话框，一般用在首页检查更新）

    `new UpdateUtil.check(activity,true)`
    
* > 可配置无更新是否提示，下载时的提示样式（通知栏，对话框）
    
    `new UpdateUtil.check(activity,true,true,true)`
    
* > 自定义回调
    
    `new UpdateUtil.check(CallBack<String> callBack)`


####2、添加权限

    `<uses-permission android:name="android.permission.INTERNET" />`
    `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`

####3、使用截图
![screenshot](https://raw.github.com/hqucsx/AppUpdate/master/screenshots/1.png)
![screenshot](https://raw.github.com/hqucsx/AppUpdate/master/screenshots/2.png)
![screenshot](https://raw.github.com/hqucsx/AppUpdate/master/screenshots/3.png)
