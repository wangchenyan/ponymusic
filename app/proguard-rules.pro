# okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.** { *; }

-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn org.blinkenlights.jid3.**

-keep class android.support.** { *; }

# glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# amap
-dontwarn com.amap.api.**
-keep class com.amap.api.** { *; }

# greenDAO
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties
# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use Rx:
-dontwarn rx.**
# greenDAO end

# RxBus
-keep class com.hwangjr.rxbus.** { *; }
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.hwangjr.rxbus.annotation.Subscribe public *;
    @com.hwangjr.rxbus.annotation.Produce public *;
}
# RxBus end