package com.libraryteam.update;/**
 * Created by PCPC on 2016/7/13.
 */

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 描述: 更新工具类
 * 名称: UpdateUtil
 * User: csx
 * Date: 07-13
 */
public class UpdateUtil {
    public static String updateUrl = "";
    /**
     * Activity
     */
    private Activity mContext;
    /**
     * 更新实体类
     */
    private Update mUpdate;
    /**
     * 显示下载进度
     */
    private boolean mShowProgressDialog = true;
    /**
     * 显示通知栏进度提醒
     */
    private boolean mShowNotification = true;
    /**
     * 通知id
     */
    private static final int notificationID = 3956;
    /**
     * 通知管理
     */
    private NotificationManager notificationManager = null;
    /**
     * 通知Builder
     */
    private NotificationCompat.Builder notifyBuilder;
    /**
     * 是否显示具体进度
     */
    private boolean indeterminate = false;
    /**
     * 进度对话框
     */
    private MaterialDialog progressDialog;
    /**
     * 是否自动检查更新（不提醒最新版本，自动检查，一般在首页）
     */
    private boolean autoCheck = false;

    /**
     * 检查更新
     *
     * @param context            上下文
     * @param showProgressDialog 是否显示更新对话框
     * @param showNotification   是否在通知栏提醒下载进度
     */
    public void check(Activity context, boolean showProgressDialog, boolean showNotification) {
        mShowProgressDialog = showProgressDialog;
        mShowNotification = showNotification;
        check(context, autoCheck);
    }

    /**
     * 默认自动检查
     *
     * @param context
     */
    public void check(Activity context, boolean autoCheck) {
        this.autoCheck = autoCheck;
        check(context);
    }

    /**
     * 检查更新
     *
     * @param context
     */
    public void check(Activity context) {
        mContext = context;
        OkHttpUtils
                .post()
                .url(updateUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(mContext, "更新信息获取失败，请重试！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mUpdate = new Gson().fromJson(response, Update.class);
                        setAction(mUpdate);
                    }
                });
    }
    /**
     * 检查更新并自定义处理结果
     *
     * @param callback 自定义callBack
     */
    public static void check(Callback<String> callback) {
        OkHttpUtils
                .post()
                .url(updateUrl)
                .addHeader("Cache-Control", "no-cache")
                .build()
                .execute(callback);
    }

    /**
     * 获取更新结果并做出处理
     *
     * @param update
     */
    private void setAction(final Update update) {
        if (!hasUpdate(update.getData().getInnerCode()) || !update.isSuccess()) {
            Toast.makeText(mContext, "当前版本已是最新版本", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * 更新提示
         */
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                .title("有新的版本")
                .content("更新内容")
                .positiveText("立即更新")
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (update.getData().isLimit())
                            mContext.finish();
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (update.getData().isLimit())
                            mContext.finish();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        update(update);
                    }
                });
        //强制更新不可取消
        if (update.getData().isLimit())
            builder.cancelable(false);

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    /**
     * 开始更新
     *
     * @param update
     */
    private void update(Update update) {
        //文件保存路径
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = getPackageName(mContext) + update.getData().getVersion() + "10.apk";

        File file = new File(filePath + "/" + fileName);
        if (file.exists() && checkApkFile(file.getAbsolutePath())) {
            installApk(file);
            return;
        } else {
            file.delete();
        }
        //对话框提醒
        if (mShowProgressDialog)
            getProgressDialog().show();
        //通知栏提醒
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("安装包正在下载...")
                .setContentText("0%");
        notifyBuilder.setProgress(100, 0, indeterminate);
        if (mShowNotification)
            notificationManager.notify(notificationID, notifyBuilder.build());
        Log.e("UpdateUtil", update.getData().getPath());
        /**
         * 开始下载
         */
        OkHttpUtils.get().url(update.getData().getPath()).build()
                .execute(new FileCallBack(filePath, fileName) {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        int percent = (int) (progress * 100);
                        if (mShowProgressDialog)
                            getProgressDialog().setProgress(percent);
                        if (mShowNotification) {
                            if (percent % 5 == 0) {
                                notifyBuilder.setContentText(percent + "%")
                                        .setProgress(100, percent, indeterminate);
                                notificationManager.notify(notificationID, notifyBuilder.build());
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        getProgressDialog().dismiss();
                        if (mShowNotification) {
                            notifyBuilder.setContentTitle("下载失败");
                            notificationManager.notify(notificationID, notifyBuilder.build());
                        }
                        Log.e("UpdateUtil", e.getMessage());
                        Toast.makeText(mContext, "下载失败，请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        if (checkApkFile(response.getAbsolutePath()))
                            installApk(response);
                        else {
                            Toast.makeText(mContext, "安装文件不完整，请重新下载", Toast.LENGTH_SHORT).show();
                        }
                        if (mShowNotification) {
                            notifyBuilder.setContentTitle("下载完成")
                                    .setContentText("100%")
                                    .setProgress(100, 100, indeterminate);
                            notificationManager.notify(notificationID, notifyBuilder.build());
                        }

                    }

                    @Override
                    public void onAfter(int id) {
                        super.onAfter(id);
                        getProgressDialog().dismiss();
                        notificationManager.cancel(notificationID);
                    }

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        if (mShowNotification) {
                            notifyBuilder.setContentTitle("安装包正在下载...")
                                    .setContentText("0%");
                            notifyBuilder.setProgress(100, 0, indeterminate);
                            notificationManager.notify(notificationID, notifyBuilder.build());
                        }
                        if (mShowProgressDialog)
                            getProgressDialog().show();
                    }
                });
    }

    /**
     * 获取进度对话框
     *
     * @return
     */
    private MaterialDialog getProgressDialog() {
        if (progressDialog == null) {
            //进度对话框
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                    .title("努力下载中....")
                    .content("请稍候...")
                    .contentGravity(GravityEnum.CENTER)
                    .progress(false, 100, true);
            progressDialog = builder.build();
        }
        return progressDialog;
    }

    /**
     * 安装下载完成的安装包
     *
     * @param file
     */
    public void installApk(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(Uri.fromFile(file), type);
        mContext.startActivity(intent);
    }

    /**
     * @param apkFilePath
     * @return 检查apk文件是否有效(是正确下载, 没有损坏的)
     */
    public boolean checkApkFile(String apkFilePath) {
        boolean result;
        try {
            PackageManager pManager = mContext.getPackageManager();
            PackageInfo pInfo = pManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
            if (pInfo == null) {
                result = false;
            } else {
                result = true;
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param versionCode
     * @return 根据版本判断是否有更新
     */
    public boolean hasUpdate(int versionCode) {
        if (versionCode <= getVersionCode(mContext)) {
            return false;
        }
        return true;
    }

    /**
     * @param context
     * @return 获得apk版本号
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        PackageInfo packInfo = getPackInfo(context);
        if (packInfo != null) {
            versionCode = packInfo.versionCode;
        }
        return versionCode;
    }


    /**
     * @param context
     * @return 获取apkPackageName
     */
    public static String getPackageName(Context context) {
        String packName = "";
        PackageInfo packInfo = getPackInfo(context);
        if (packInfo != null) {
            packName = packInfo.packageName;
        }
        return packName;
    }

    /**
     * @param context
     * @return 获取得packInfo
     */
    public static PackageInfo getPackInfo(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packInfo;
    }
}
