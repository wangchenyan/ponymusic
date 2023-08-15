package me.wcy.music.loader;

import android.content.Context;
import android.content.CursorLoader;
import android.provider.MediaStore;

public class MusicCursorLoader extends CursorLoader {
    private final String[] proj;

    public MusicCursorLoader(Context context) {
        super(context);
        this.proj = new String[]{"_id", "is_music", "title", "artist", "album", "album_id", "_data", "_display_name", "_size", "duration"};
        this.setProjection(this.proj);
        this.setUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        this.setSortOrder("date_modified desc");
        this.setSelection("mime_type= ?");
        this.setSelectionArgs(new String[]{"audio/mpeg"});
    }
}
