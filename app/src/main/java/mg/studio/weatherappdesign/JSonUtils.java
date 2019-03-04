package mg.studio.weatherappdesign;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSonUtils {
    public static weatherTool[] getWeatherTool(String jsonText){
        weatherTool[] weatherInfo = new weatherTool[5];
        try{
            JSONObject jsonObject = new JSONObject(jsonText);
            JSONObject dataInfo = jsonObject.getJSONObject("data");
            String city = dataInfo.getString("city");
            JSONArray forecast = (JSONArray) dataInfo.get("forecast");

            for(int i = 0;i < 5;i++) {
                JSONObject weather = (JSONObject) forecast.get(i);
                weatherInfo[i] = new weatherTool();
                weatherInfo[i].setCity(city);
                weatherInfo[i].setWeather(weather.getString("type"));

                String strTemp = null;
                strTemp = dataInfo.getString("wendu");
                weatherInfo[i].setCurrent_temp(strTemp);
            }

        }catch (Exception e){ e.printStackTrace();}

        return weatherInfo;
    }
}