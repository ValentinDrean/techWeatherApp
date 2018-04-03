package data;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference
{
    SharedPreferences prefs;

    public CityPreference(Activity activity)
    {
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    // if no chosen city here's default :
    public String getCity()
    {
        return prefs.getString("city", "Montreuil, FR");
    }

    public void setCity(String city)
    {
        prefs.edit().putString("city", city).commit();
    }
}
