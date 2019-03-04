package mg.studio.weatherappdesign;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    weatherTool[] weatherInfomation = null;
    int[] imageview = {0,R.id.imageview1, R.id.imageview2, R.id.imageview3, R.id.imageview4};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setdate();
        new DownloadUpdate().execute();
    }

    public void btnClick(View view) {
        setdate();
        if(isNetworkAvailable(MainActivity.this)) {
            new DownloadUpdate().execute();
            Toast.makeText(getApplicationContext(), "The weather is updated",   Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(MainActivity.this,"There is no Internet. Please check your net",Toast.LENGTH_SHORT).show();

    }

    public void setdate() {
        Date date = new Date();
        java.text.DateFormat format2 = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String s = format2.format(new Date());
        ((TextView) findViewById(R.id.tv_date)).setText(s);

        String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] weekDay = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        ((TextView) findViewById(R.id.TextView1)).setText(weekDays[w]);
        ((TextView) findViewById(R.id.TextView2)).setText(weekDay[w + 1]);
        ((TextView) findViewById(R.id.TextView3)).setText(weekDay[w + 2]);
        ((TextView) findViewById(R.id.TextView4)).setText(weekDay[w + 3]);
        ((TextView) findViewById(R.id.TextView5)).setText(weekDay[w + 4]);
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }

    private class DownloadUpdate extends AsyncTask<String, Void, weatherTool[]> {


        @Override
        protected weatherTool[] doInBackground(String... strings) {
            String stringUrl = "http://wthrcdn.etouch.cn/weather_mini?city=重庆";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    Log.d("TAG", line);
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                String json = buffer.toString();
                weatherTool[] weatherInfos = JSonUtils.getWeatherTool(json);
                weatherInfomation = weatherInfos;
                return weatherInfos;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(weatherTool[] weatherTool) {
            //Update the temperature displayed
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(weatherTool[0].getCurrent_temp());

            String strType = weatherTool[0].getWeather();
            ImageView ivCondition = ((ImageView)findViewById(R.id.img_weather_condition));
            if (strType.contains("雨")) ivCondition.setImageDrawable(getResources().getDrawable(R.drawable.rainy_up));
            else if(strType.contains("晴")) ivCondition.setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));
            else if(strType.contains("阴")) ivCondition.setImageDrawable(getResources().getDrawable(R.drawable.partly_sunny_small));
            else ivCondition.setImageDrawable(getResources().getDrawable(R.drawable.windy_small));

            for(int i = 1; i < 5; i++) {
                strType = weatherTool[i].getWeather();
                ivCondition = ((ImageView) findViewById(imageview[i]));
                if (strType.contains("雨"))
                    ivCondition.setImageDrawable(getResources().getDrawable(R.drawable.rainy_small));
                else if (strType.contains("晴"))
                    ivCondition.setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));
                else if (strType.contains("阴"))
                    ivCondition.setImageDrawable(getResources().getDrawable(R.drawable.partly_sunny_small));
                else
                    ivCondition.setImageDrawable(getResources().getDrawable(R.drawable.windy_small));
            }

        }

    }

}

