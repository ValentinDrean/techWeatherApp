package data;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import model.Place;
import model.Weather;
import util.Utils;

public class JSONWeatherParser
{
    public static Weather getWeather(String data)
    {
        Weather weather = new Weather();

        // create JSONObject from data
        try
        {
            // ALL JSON FILE
            JSONObject rootJSONObject = new JSONObject(data);
            Log.d("rootJSONObject", String.valueOf(rootJSONObject));

            Place place = new Place();

            // for all that data we're gonna parse, refer to this link as an example :
            // api.openweathermap.org/data/2.5/weather?q=Paris,FR&appid=5b58a23d0416e2555408060f71abc77c&units=metric
            JSONObject coordObj = Utils.getObject("coord", rootJSONObject);
            place.setLat(Utils.getFloat("lat", coordObj));
            place.setLon(Utils.getFloat("lon", coordObj));

            // get place data from sys object & json root, refer to json from api
            JSONObject sysObj = Utils.getObject("sys", rootJSONObject);
            place.setCountry(Utils.getString("country", sysObj));
            place.setLastupdate(Utils.getInt("dt", rootJSONObject));
            place.setSunrise(Utils.getInt("sunrise", sysObj));
            place.setSunset(Utils.getInt("sunset", sysObj));
            place.setCity(Utils.getString("name", rootJSONObject));

            // setting weather to this specific configured place
            weather.place = place;

            // its JSONArray here because it's an object into array "weather",refer to json from api
            JSONArray jsonArray = rootJSONObject.getJSONArray("weather");
            // JSONObject isn't named so index 0
            JSONObject weatherObj = jsonArray.getJSONObject(0);
            weather.currentCondition.setWeatherId(Utils.getInt("id", weatherObj));
            weather.currentCondition.setDescription(Utils.getString("description", weatherObj));
            weather.currentCondition.setCondition(Utils.getString("main", weatherObj));
            weather.currentCondition.setIcon(Utils.getString("icon", weatherObj));

            JSONObject mainObj = Utils.getObject("main", rootJSONObject);
            weather.currentCondition.setHumidity(Utils.getInt("humidity", mainObj));
            weather.currentCondition.setPressure(Utils.getInt("pressure", mainObj));
            weather.currentCondition.setMinTemp(Utils.getFloat("temp_min", mainObj));
            weather.currentCondition.setMaxTemp(Utils.getFloat("temp_max", mainObj));
            weather.currentCondition.setTemperature(Utils.getDouble("temp", mainObj));

            JSONObject windObj = Utils.getObject("wind", rootJSONObject);
            weather.wind.setSpeed(Utils.getFloat("speed", windObj));
            weather.wind.setDeg(Utils.getFloat("deg", windObj));

            JSONObject cloudObj = Utils.getObject("clouds", rootJSONObject);
            weather.cloud.setPrecipitation(Utils.getInt("all", cloudObj));

            return weather;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
