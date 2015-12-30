package me.wcy.ponymusic.model;

/**
 * JavaBean
 * Created by wcy on 2015/12/27.
 */
public class JDownloadInfo {
    JBitrate bitrate;

    public JBitrate getBitrate() {
        return bitrate;
    }

    public void setBitrate(JBitrate bitrate) {
        this.bitrate = bitrate;
    }

    public class JBitrate {
        int file_duration;
        String file_link;

        public int getFile_duration() {
            return file_duration;
        }

        public void setFile_duration(int file_duration) {
            this.file_duration = file_duration;
        }

        public String getFile_link() {
            return file_link;
        }

        public void setFile_link(String file_link) {
            this.file_link = file_link;
        }
    }
}
