package Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;

import Util.HttpCallbackListener;
import Util.HttpUtil;
import Util.Utility;

public class WeatherActivity extends Activity {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishTime;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout =findViewById(R.id.weather_info_layout);
        cityNameText=findViewById(R.id.city_name);
        weatherDespText=findViewById(R.id.weather_desp);
        publishTime=findViewById(R.id.publish_text);
        temp1Text=findViewById(R.id.temp1);
        temp2Text=findViewById(R.id.temp2);
        currentDateText=findViewById(R.id.current_date);
        String countyCode=getIntent().getStringExtra("county_name");
        if(!TextUtils.isEmpty(countyCode)){
            publishTime.setText("同步中。。");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else{
            showWeather();
        }
    }



    private void queryWeatherInfo(String weatherCode) {
        String address="http://www.weather.com.cn/data/cityinfo"+weatherCode+".html";
        queryFromServer(address,"weatherCode");
    }
    private void queryWeatherCode(String countyCode) {
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address,"countyCode");
    }

    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if("countyCode".equals(type)){
                    if(!TextUtils.isEmpty(type)){
                        String[] array=response.split("\\|");
                        if(array!=null&&array.length==2){
                            String weatherCode=array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if("weatherCode".equals(type)){
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishTime.setText("同步失败");
                    }
                });
            }
        });
    }
    private void showWeather() {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        publishTime.setText("Today"+prefs.getString("publish_time","")+"发布");
        currentDateText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }

}
