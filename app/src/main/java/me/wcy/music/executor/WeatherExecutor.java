package me.wcy.music.executor;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;

/**
 * 更新天气
 * Created by wcy on 2016/1/17.
 */
public class WeatherExecutor implements AMapLocalWeatherListener {
    private static final String TAG = WeatherExecutor.class.getSimpleName();
    private Context mContext;
    private View vNavigationHeader;

    public WeatherExecutor(Context context, View navigationHeader) {
        mContext = context;
        vNavigationHeader = navigationHeader;
    }

    public void execute() {
        LocationManagerProxy mLocationManagerProxy = LocationManagerProxy.getInstance(mContext);
        mLocationManagerProxy.requestWeatherUpdates(LocationManagerProxy.WEATHER_TYPE_LIVE, this);
    }

    @Override
    public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
        if (aMapLocalWeatherLive != null && aMapLocalWeatherLive.getAMapException().getErrorCode() == 0) {
            String weather = aMapLocalWeatherLive.getWeather();
            String temperature = aMapLocalWeatherLive.getTemperature();
            String city = aMapLocalWeatherLive.getCity();
            String wind_dir = aMapLocalWeatherLive.getWindDir();
            String wind_power = aMapLocalWeatherLive.getWindPower();
            String humidity = aMapLocalWeatherLive.getHumidity();// 空气湿度
            Log.d(TAG, weather + "," + temperature + "," + city + "," + wind_dir + "," + wind_power + "," + humidity);
        } else {
            Log.e(TAG, "获取天气预报失败");
        }
    }

    @Override
    public void onWeatherForecaseSearched(AMapLocalWeatherForecast aMapLocalWeatherForecast) {
    }
}
