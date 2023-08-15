package me.wcy.music.utils.id3

import java.io.File

/**
 * ID3 Tag 工具类<br></br>
 * 基于[JID3](https://blinkenlights.org/jid3/)编写<br></br>
 * Created by hzwangchenyan on 2017/8/11.
 */
object ID3TagUtils {
    private const val TAG = "ID3TagUtils"

    /**
     * @param clearOriginal 是否清除原始标签
     */
    fun setID3Tags(sourceFile: File?, id3Tags: ID3Tags?, clearOriginal: Boolean): Boolean {
        return false
    }
}