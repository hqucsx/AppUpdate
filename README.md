##Android 应用自动更新(AppUpdate)

之前一直使用友盟的自动更新，用的挺好的，但是10月份就会下架，现在处于维护可用状态。。有些机型已经不能使用了，，各种下载失败，无更新提示。
so 自己动手吧

* > 版本检查
* > 文件下载
* > 软件安装(首先检查文件是否完整)
* > 是否强制更新（强制更新时，取消更新会退出）


####1、导入library项目
在Application中配置应用检查地址

    `UpdateUtil.updateUrl = url;`
    
可配置通知栏方式可对话框方式

* > 默认方式(使用对话框，无更新时会提示，有更新时弹出对话框)
   
    `new UpdateUtil.check(activity)`
    
* > 无更新时是否提示（使用对话框，有更新时弹出对话框，一般用在首页检查更新）

    `new UpdateUtil.check(activity,true)`
    
* > 可配置无更新是否提示，下载时的提示样式（通知栏，对话框）
    
    `new UpdateUtil.check(activity,true,true,true)`
    
* > 自定义回调
    
    `new UpdateUtil.check(CallBack<String> callBack)`

在非强制更新的时候，只使用对话框时，如果没有下载完成，对话框被取消，则在通知栏显示下载进度
在强制更新时对话框不可取消

####2、添加权限

    `<uses-permission android:name="android.permission.INTERNET" />`
    `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`

####3、使用截图
![screenshot](https://raw.github.com/hqucsx/AppUpdate/master/screenshots/1.png)
![screenshot](https://raw.github.com/hqucsx/AppUpdate/master/screenshots/2.png)
![screenshot](https://raw.github.com/hqucsx/AppUpdate/master/screenshots/3.png)


###changelog
* > 修改安装文件的保存位置，如果sd卡存在并有写入的权限时，保存在sd卡，否则保存在缓存区（6.0权限默认不授予）
* > 修改通知策略，默认只使用对话框通知下载进度
