package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import util.Utils;

public class WeatherHttpClient // get & parse json object from API
{
    public String getWeatherData(String place)
    {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) (new URL(Utils.BASE_URL + place)).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true); // ?
            connection.connect();

            //read dat response
            StringBuffer stringBuffer = new StringBuffer( ); // bucket where data arrives
            inputStream = connection.getInputStream(); // streams of bits, just bits of data incoming
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            // Dat bufferedreader can read the stream of bits, hold everything in the bucket
            String line = null;

            while((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line + "\r\n"); // Just to get nice & organized data
            }

            inputStream.close();
            connection.disconnect();

            return stringBuffer.toString(); // get data all as string
        }
        catch (IOException e)
        {
            e.printStackTrace();
//            return null;
        }
        return null;
    }
}
