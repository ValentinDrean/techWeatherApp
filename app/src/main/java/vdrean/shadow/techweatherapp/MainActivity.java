package vdrean.shadow.techweatherapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import data.CityPreference;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;
import util.Utils;

public class MainActivity extends AppCompatActivity
{
    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;

    private Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView) findViewById(R.id.cityText);
        iconView = (ImageView) findViewById(R.id.thumbnailIcon);
        temp = (TextView) findViewById(R.id.tempText);
        description = (TextView) findViewById(R.id.cloudText);
        humidity = (TextView) findViewById(R.id.humidText);
        pressure = (TextView) findViewById(R.id.pressureText);
        wind = (TextView) findViewById((R.id.windText));
        sunrise = (TextView) findViewById(R.id.riseText);
        sunset = (TextView) findViewById(R.id.setText);
        updated = (TextView) findViewById(R.id.updateText);

        CityPreference cityPreference = new CityPreference(MainActivity.this);

        renderWeatherData(cityPreference.getCity());
    }

    public void renderWeatherData(String city)
    {
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city + "&APPID=" + Utils.API_ID + "&units=metric"});
    }

    // AsyncTask class for weather gather
    private class WeatherTask extends AsyncTask<String, Integer, Weather>
    {
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute() {
            this.dialog.setMessage("Getting weather");
            this.dialog.show();
        }

        @Override
        protected Weather doInBackground(String... params)
        {
            try {
                URL url = new URL(Utils.BASE_URL + params[0]);

                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();

                final int httpCodeResponse = connection.getResponseCode();

                if (httpCodeResponse != HttpURLConnection.HTTP_OK)
                {
                    Log.d("Data : ", String.valueOf(httpCodeResponse));
                    return null;
                }
                else
                {
                    String data = ( (new WeatherHttpClient()).getWeatherData(params[0]));
                    weather = JSONWeatherParser.getWeather(data);

                    new DownloadImageAsyncTask().execute(weather.currentCondition.getIcon());
                    return weather;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Weather weather)
        {
            if (dialog.isShowing())
            {
                dialog.dismiss();
            }

            if (weather != null)
            {
                Calendar cal = Calendar.getInstance();
                TimeZone tz = cal.getTimeZone();

                // is it local time ?
                Log.d("Time zone: ", tz.getDisplayName());

                // date formatter in local timezone
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                sdf.setTimeZone(tz);

                String updateDate = sdf.format(new Date(weather.place.getLastupdate() * 1000));
                String sunriseDate = sdf.format(new Date(weather.place.getSunrise() * 1000));
                String sunsetDate = sdf.format(new Date(weather.place.getSunset() * 1000));
                Log.d("Time: ", updateDate);

                super.onPostExecute(weather);

                // Converting m/s to km/h
                Double windKmh = weather.wind.getSpeed() * 3.6;

                //getting nice & formated values
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature());
                String windFormat = decimalFormat.format(windKmh);

                cityName.setText(weather.place.getCity() + "," + weather.place.getCountry());
                temp.setText("" + tempFormat + " °C");
                humidity.setText("Humidity: " + weather.currentCondition.getHumidity() + " %");
                pressure.setText("Pressure: " + weather.currentCondition.getPressure() + " hPa");
                wind.setText("Wind: " + windFormat.toString() + " km/h");
                sunrise.setText("Sunrise: " + sunriseDate);
                sunset.setText("Sunset: " + sunsetDate);
                updated.setText("Last Updated: " + updateDate);
                description.setText("Condition: " + weather.currentCondition.getCondition() + " (" +
                        weather.currentCondition.getDescription() + ")");

            }
            else
            {
                Toast.makeText(MainActivity.this, "No city named that way!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... params)
        {
            return downloadImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            iconView.setImageBitmap(bitmap);
        }

        private Bitmap downloadImage(String code)
        {
            try {
                URL url = new URL(Utils.ICON_URL + code + ".png");

                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();

                final int httpCodeResponse = connection.getResponseCode();

                if (httpCodeResponse != HttpURLConnection.HTTP_OK)
                {
                    Log.d("Data : ", String.valueOf(httpCodeResponse));
                    return null;
                }
                else
                {
                    InputStream input = connection.getInputStream();
                    Bitmap currentBitmap = BitmapFactory.decodeStream(input);
                    return currentBitmap;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }

    private void showInputDialogChangeCity()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Change City");

        final EditText cityInput = new EditText(MainActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Paris, FR");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity(cityInput.getText().toString());

                String newCity = cityPreference.getCity();

                renderWeatherData(newCity);
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action if it's present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //deals with action bar item clicks here
        int id = item.getItemId();

        if (id == R.id.change_cityId)
        {
            showInputDialogChangeCity();
        }
        return super.onOptionsItemSelected(item);
    }
}
