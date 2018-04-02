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
        // Here is our weather object with all json data we're gonna parse
        // Lets go make it a JSONObject for your AsyncTaks ?
        Weather weather = new Weather();

        // create JSONObject from data
        try {
            // ALL JSON
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
            // this can be done thanks to our weather hub class here
            weather.place = place;

            // get weather data related to our city
            //its JSONArray here because it's an object into array "weather", refer to json from api
            JSONArray jsonArray = rootJSONObject.getJSONArray("weather");
            // and JSONObject isn't named and is at index 0
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

            // getting wind object from json, again, to json from api
            JSONObject windObj = Utils.getObject("wind", rootJSONObject);
            // again, this can be done thanks to our weather hub class here
            weather.wind.setSpeed(Utils.getFloat("speed", windObj));
            weather.wind.setDeg(Utils.getFloat("deg", windObj));

            // getting cloud object from json, again, refer to json from api
            JSONObject cloudObj = Utils.getObject("clouds", rootJSONObject);
            // again, this can be done thanks to our weather hub class here
            weather.clouds.setPrecipitation(Utils.getInt("all", cloudObj));

            return weather;
        } catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
        // wtf can we do in case all something go wron?g
//        return null;
    }
}
