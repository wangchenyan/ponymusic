package me.wcy.music.model;

/**
 * Created by wcy on 2016/4/3.
 */
public class UpdateInfo {
    public String name;
    public String version;
    public String changelog;
    public int updated_at;
    public String versionShort;
    public String build;
    public String installUrl;
    public String install_url;
    public String direct_install_url;
    public String update_url;
    public BinaryBean binary;

    public static class BinaryBean {
        public int fsize;
    }
}
