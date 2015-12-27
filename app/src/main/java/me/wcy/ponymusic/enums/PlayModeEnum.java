package me.wcy.ponymusic.enums;

/**
 * 播放模式
 * Created by wcy on 2015/12/26.
 */
public enum PlayModeEnum {
    LOOP(1),
    SHUFFLE(2),
    ONE(3);

    private int value;

    private PlayModeEnum(int value) {
        this.value = value;
    }

    public static PlayModeEnum valueOf(int value) {
        switch (value) {
            case 1:
                return LOOP;
            case 2:
                return SHUFFLE;
            case 3:
                return ONE;
            default:
                return LOOP;
        }
    }

    public int value() {
        return value;
    }
}
