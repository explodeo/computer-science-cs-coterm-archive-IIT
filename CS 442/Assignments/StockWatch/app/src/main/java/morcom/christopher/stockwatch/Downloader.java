package morcom.christopher.stockwatch;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Downloader extends AsyncTask<String, String, ArrayList<Stock>> {

    private static final String TAG = "Downloader";

    private MainActivity mainActivity;

    public Downloader(MainActivity m) {
        mainActivity = m;
    }

    @Override
    protected ArrayList<Stock> doInBackground(String... fileURL) {
        Log.d(TAG, "doInBackground: started asynctask");
        JSONArray nameSymbol;
        JsonObject tmp;
        try{
            URL url = new URL(fileURL[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            // download the file
            InputStream input = new BufferedInputStream(url.openStream(), 999999);
            //convert input into a json array
            String j = new JsonParser().parse(new InputStreamReader((InputStream) connection.getContent())).toString();
            JSONObject jo = new JSONObject(j);
            nameSymbol = jo.getJSONArray("symbol");
            Log.d(TAG, "doInBackground: created jsonarray ");

        } catch (Exception e) { Log.e("Error: ", e.getMessage()); }
        return null;
    }

    @Override
    protected void onPreExecute() { super.onPreExecute(); }

    @Override
    protected void onPostExecute(ArrayList<Stock> s) {
        super.onPostExecute(s);
        //mainActivity.whenAsyncIsDone(s);
    }

}
