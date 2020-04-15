package morcom.christopher.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NewsSourceDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "NewsSourceDownloader";
    private String baseURL = "https://newsapi.org/v1/sources?language=en&country=us&category=";
    private static final String KEY = "&apiKey=c3471c1defdc4b4e949106896c1cd7ec";

    private MainActivity mainActivity;
    private String category;

    public NewsSourceDownloader(MainActivity mainActivity, String category) {
        this.mainActivity = mainActivity;
        this.category = (category.equals("All")) ? "" : category;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: PROCESS NEWS_SOURCE (JSON) LIST");
        ArrayList<NewsSource> sourceList = new ArrayList<>();
        ArrayList<String> categoryList = new ArrayList<>();
        try {
            JSONArray nsArr = (new JSONObject(s)).getJSONArray("sources");
            for(int i=0; i<nsArr.length(); i++){
                JSONObject newsSource = nsArr.getJSONObject(i);
                String ID = newsSource.getString("id");
                String name = newsSource.getString("name");
                String url = newsSource.getString("url");
                String category = newsSource.getString("category");
                NewsSource source = new NewsSource(ID, name, url, category);
                sourceList.add(source);
                if(!categoryList.contains(category)){
                    categoryList.add(category);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "onPostExecute: SET NEWS_SOURCE LIST IN MAIN_ACTIVITY");
        mainActivity.setSources(sourceList, categoryList);
    }

    @Override
    protected String doInBackground(String... strings) {
        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL(Uri.parse(baseURL+category+KEY).toString());
            Log.d(TAG, "doInBackground: ARTICLE SOURCE CONNECTING...");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while((line=reader.readLine()) != null){ sb.append(line); }
            Log.d(TAG, "doInBackground: FETCHED ARTICLE DATA");
            return sb.toString();
        } catch (Exception e){
            Log.d(TAG, "doInBackground: "+e.getMessage());
            return null;
        }
    }
}
