package me.wcy.music.utils.permission;

/**
 * Created by hzwangchenyan on 2016/11/24.
 */
public interface PermissionResult {
    void onGranted();

    void onDenied();
}
