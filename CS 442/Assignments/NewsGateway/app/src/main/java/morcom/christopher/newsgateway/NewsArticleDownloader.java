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

public class NewsArticleDownloader extends AsyncTask<String, Void, String> {
    private String id;
    private NewsService newsService;
    private static final String TAG = "NewsArticleDownloader";
    private static final String KEY = "&apiKey=c3471c1defdc4b4e949106896c1cd7ec";
    private final String baseURL = "https://newsapi.org/v1/articles?source=";

    public NewsArticleDownloader(String id, NewsService newsService) {
        this.id = id;
        this.newsService = newsService;
    }

    @Override
    protected void onPostExecute(String s) {
        ArrayList<NewsArticle> aList = new ArrayList<>();
        try{
            JSONArray articles = (new JSONObject(s)).getJSONArray("articles");
            for(int x=0; x<articles.length(); x++){
                JSONObject jsonArticle = articles.getJSONObject(x);
                String author=null, title=null, description=null, url=null, urlToImage=null, publishedAt=null;
                if(jsonArticle.has("author")){
                    author = jsonArticle.getString("author");
                }
                if(jsonArticle.has("title")){
                    title = jsonArticle.getString("title");
                }
                if(jsonArticle.has("description")){
                    description = jsonArticle.getString("description");
                }
                if(jsonArticle.has("url")){
                    url = jsonArticle.getString("url");
                }
                if(jsonArticle.has("urlToImage")){
                    urlToImage = jsonArticle.getString("urlToImage");
                }
                if(jsonArticle.has("publishedAt")){
                    publishedAt = jsonArticle.getString("publishedAt");
                }
                NewsArticle a = new NewsArticle(author, title, description, url, urlToImage, publishedAt);
                aList.add(a);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        newsService.setArticles(aList);
    }

    @Override
    protected String doInBackground(String... strings) {
        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL(Uri.parse(baseURL+id+KEY).toString());
            Log.d(TAG, "doInBackground: "+url.toString());
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