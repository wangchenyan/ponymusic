package me.wcy.music.utils.permission;

public interface PermissionResult {
    void onGranted();

    void onDenied();
}
