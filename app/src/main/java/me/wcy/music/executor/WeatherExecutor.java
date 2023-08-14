package me.wcy.music.executor;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import me.wcy.music.R;
import me.wcy.music.utils.binding.Bind;
import me.wcy.music.utils.binding.ViewBinder;

/**
 * 更新天气
 * Created by wcy on 2016/1/17.
 * <p>
 * 天气现象表
 * <p>
 * 晴
 * 多云
 * 阴
 * 阵雨
 * 雷阵雨
 * 雷阵雨并伴有冰雹
 * 雨夹雪
 * 小雨
 * 中雨
 * 大雨
 * 暴雨
 * 大暴雨
 * 特大暴雨
 * 阵雪
 * 小雪
 * 中雪
 * 大雪
 * 暴雪
 * 雾
 * 冻雨
 * 沙尘暴
 * 小雨-中雨
 * 中雨-大雨
 * 大雨-暴雨
 * 暴雨-大暴雨
 * 大暴雨-特大暴雨
 * 小雪-中雪
 * 中雪-大雪
 * 大雪-暴雪
 * 浮尘
 * 扬沙
 * 强沙尘暴
 * 飑
 * 龙卷风
 * 弱高吹雪
 * 轻霾
 * 霾
 */
public class WeatherExecutor implements IExecutor {
    private static final String TAG = "WeatherExecutor";
    private Context mContext;
    @Bind(R.id.ll_weather)
    private LinearLayout llWeather;
    @Bind(R.id.iv_weather_icon)
    private ImageView ivIcon;
    @Bind(R.id.tv_weather_temp)
    private TextView tvTemp;
    @Bind(R.id.tv_weather_city)
    private TextView tvCity;
    @Bind(R.id.tv_weather_wind)
    private TextView tvWind;

    public WeatherExecutor(Context context, View navigationHeader) {
        mContext = context.getApplicationContext();
        ViewBinder.bind(this, navigationHeader);
    }

    @Override
    public void execute() {
    }

    private int getWeatherIcon(String weather) {
        if (TextUtils.isEmpty(weather)) {
            return R.drawable.ic_weather_sunny;
        }

        if (weather.contains("-")) {
            weather = weather.substring(0, weather.indexOf("-"));
        }
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int resId;
        if (weather.contains("晴")) {
            if (hour >= 7 && hour < 19) {
                resId = R.drawable.ic_weather_sunny;
            } else {
                resId = R.drawable.ic_weather_sunny_night;
            }
        } else if (weather.contains("多云")) {
            if (hour >= 7 && hour < 19) {
                resId = R.drawable.ic_weather_cloudy;
            } else {
                resId = R.drawable.ic_weather_cloudy_night;
            }
        } else if (weather.contains("阴")) {
            resId = R.drawable.ic_weather_overcast;
        } else if (weather.contains("雷阵雨")) {
            resId = R.drawable.ic_weather_thunderstorm;
        } else if (weather.contains("雨夹雪")) {
            resId = R.drawable.ic_weather_sleet;
        } else if (weather.contains("雨")) {
            resId = R.drawable.ic_weather_rain;
        } else if (weather.contains("雪")) {
            resId = R.drawable.ic_weather_snow;
        } else if (weather.contains("雾") || weather.contains("霾")) {
            resId = R.drawable.ic_weather_foggy;
        } else if (weather.contains("风") || weather.contains("飑")) {
            resId = R.drawable.ic_weather_typhoon;
        } else if (weather.contains("沙") || weather.contains("尘")) {
            resId = R.drawable.ic_weather_sandstorm;
        } else {
            resId = R.drawable.ic_weather_cloudy;
        }
        return resId;
    }

    private void release() {
        mContext = null;
        llWeather = null;
        ivIcon = null;
        tvTemp = null;
        tvCity = null;
        tvWind = null;
    }

    @Override
    public void onPrepare() {
    }

    @Override
    public void onExecuteSuccess(Object o) {
    }

    @Override
    public void onExecuteFail(Exception e) {
    }
}
