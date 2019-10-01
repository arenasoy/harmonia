package br.edu.ifpr.tcc.localizacao;

import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Enzo on 15/08/2016.
 */
public class Localizacao {

    private static String urlBase = "https://maps.googleapis.com/maps/api/geocode/json?";
    private static String key = "&key=AIzaSyBTrblLbU4vIL3bXcDZDrDDR6PWEWcebZI";

    public static String getEndereco(double lat, double lng) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String url = urlBase + "latlng=" + lat + "," + lng + key;

        try {
            JSONObject jsonObject = getJSONObjectFromURL(url);
            JSONObject jsonResult = jsonObject.getJSONArray("results").getJSONObject(0);
            return jsonResult.getString("formatted_address");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /* Imprimir todos os resultados:
            JSONArray array = jsonObject.getJSONArray("results");
            for (int i =0; i < array.length(); i++) {
                JSONObject jsonResult = jsonObject.getJSONArray("results").getJSONObject(i);
                System.out.println(jsonResult.getString("formatted_address"));
            } */

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {

        HttpURLConnection urlConnection = null;

        URL url = new URL(urlString);

        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);

        urlConnection.setDoOutput(true);

        urlConnection.connect();

        BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));

        char[] buffer = new char[1024];

        String jsonString = new String();

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();

        jsonString = sb.toString();

        //System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }

}
