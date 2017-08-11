package me.wcy.music.utils.id3;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.io.TextEncoding;
import org.blinkenlights.jid3.v2.APICID3V2Frame;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by hzwangchenyan on 2017/8/11.
 */
public class ID3Tags {
    private static final String FRONT_COVER_DESC = "front_cover";
    private static final String MIME_TYPE_JPEG = "image/jpeg";

    // 标题
    private String title;
    // 艺术家
    private String artist;
    // 专辑
    private String album;
    // 流派
    private String genre;
    // 年份
    private Integer year;
    // 注释
    private String comment;
    // 封面图片
    private File coverFile;

    public void fillID3Tag(ID3V2_3_0Tag id3V2_3_0Tag) throws ID3Exception {
        TextEncoding.setDefaultTextEncoding(TextEncoding.UNICODE);
        if (title != null) {
            id3V2_3_0Tag.setTitle(title);
        }
        if (artist != null) {
            id3V2_3_0Tag.setArtist(artist);
        }
        if (album != null) {
            id3V2_3_0Tag.setAlbum(album);
        }
        if (genre != null) {
            id3V2_3_0Tag.setGenre(genre);
        }
        if (year != null && year >= 0 && year <= 9999) {
            id3V2_3_0Tag.setYear(year);
        }
        if (comment != null) {
            id3V2_3_0Tag.setComment(comment);
        }

        TextEncoding.setDefaultTextEncoding(TextEncoding.ISO_8859_1);
        if (coverFile != null && coverFile.exists()) {
            byte[] data = toByteArray(coverFile);
            if (data != null) {
                id3V2_3_0Tag.removeAPICFrame(FRONT_COVER_DESC);
                APICID3V2Frame apicid3V2Frame = new APICID3V2Frame(MIME_TYPE_JPEG, APICID3V2Frame.PictureType.FrontCover, FRONT_COVER_DESC, data);
                id3V2_3_0Tag.addAPICFrame(apicid3V2Frame);
            }
        }
    }

    public static class Builder {
        private ID3Tags id3Tags;

        public Builder() {
            id3Tags = new ID3Tags();
        }

        public ID3Tags build() {
            return id3Tags;
        }

        public Builder setTitle(String title) {
            id3Tags.title = title;
            return this;
        }

        public Builder setArtist(String artist) {
            id3Tags.artist = artist;
            return this;
        }

        public Builder setAlbum(String album) {
            id3Tags.album = album;
            return this;
        }

        public Builder setGenre(String genre) {
            id3Tags.genre = genre;
            return this;
        }

        public Builder setYear(Integer year) {
            id3Tags.year = year;
            return this;
        }

        public Builder setComment(String comment) {
            id3Tags.comment = comment;
            return this;
        }

        public Builder setCoverFile(File coverFile) {
            id3Tags.coverFile = coverFile;
            return this;
        }
    }

    private byte[] toByteArray(File file) {
        ByteArrayOutputStream bos = null;
        BufferedInputStream in = null;
        try {
            bos = new ByteArrayOutputStream((int) file.length());
            in = new BufferedInputStream(new FileInputStream(file));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len;
            while ((len = in.read(buffer, 0, buf_size)) > 0) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
