package me.wcy.music.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wcy on 2016/4/3.
 */
public class UpdateInfo {
    @SerializedName("name")
    public String name;
    @SerializedName("version")
    public String version;
    @SerializedName("changelog")
    public String changelog;
    @SerializedName("updated_at")
    public int updated_at;
    @SerializedName("versionShort")
    public String versionShort;
    @SerializedName("build")
    public String build;
    @SerializedName("installUrl")
    public String installUrl;
    @SerializedName("install_url")
    public String install_url;
    @SerializedName("direct_install_url")
    public String direct_install_url;
    @SerializedName("update_url")
    public String update_url;
    @SerializedName("binary")
    public BinaryBean binary;

    public static class BinaryBean {
        @SerializedName("fsize")
        public int fsize;
    }
}
