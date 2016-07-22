package com.libraryteam.update;/**
 * Created by PCPC on 2016/7/13.
 */

/**
 * 描述:
 * 名称: Update
 * User: csx
 * Date: 07-13
 */
public class Update {

    /**
     * success : true
     * message : 版本信息
     * data :
     */

    private boolean success;
    private String message;
    /**
     * limit : false 是否强制更新
     * time : 1467947414000
     * desc : update desc
     * path : download path
     * channel : channel(类似友盟的渠道更新)
     * innerCode : 4610 versionCode
     * version : 4.6.1 versionName
     */

    private DataEntity data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public static class DataEntity {
        private boolean limit;
        private long time;
        private String desc;
        private String path;
        private String channel;
        private int innerCode;
        private String version;

        public boolean isLimit() {
            return limit;
        }

        public void setLimit(boolean limit) {
            this.limit = limit;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public int getInnerCode() {
            return innerCode;
        }

        public void setInnerCode(int innerCode) {
            this.innerCode = innerCode;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
