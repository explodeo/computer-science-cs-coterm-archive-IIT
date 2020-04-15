package morcom.christopher.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;


public class NewsService extends Service {

    private static final String TAG = "NewsService";

    public NewsService newsService = this;
    private ServiceReceiver sReceiver = new ServiceReceiver();
    private boolean isRunning = true;
    private ArrayList<NewsArticle> articleList = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: SERVICE BOUND");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        Log.d(TAG, "onStartCommand: SERVICE BEGIN");
        IntentFilter filter = new IntentFilter(MainActivity.ACTION_MSG_TO_SERVICE);
        registerReceiver(sReceiver, filter);
        Log.d(TAG, "onStartCommand: SERVICE_RECEIVER REGISTERED");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRunning){
                    while(articleList.isEmpty()){
                        try{ Thread.sleep(250); } 
                        catch(Exception e){ e.printStackTrace(); }
                    }
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.ACTION_NEWS_STORY);
                    intent.putExtra(MainActivity.STORY_LIST, articleList);
                    intent.putExtra("Page", 0);
                    sendBroadcast(intent);
                    articleList.clear();
                    Log.d(TAG, "run: ARTICLE LIST UPDATED");
                }
            }
        }).start();
        
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(sReceiver);
        isRunning = false;
        super.onDestroy();
    }

    public void setArticles(ArrayList<NewsArticle> nas){
        articleList.clear();
        for(NewsArticle na: nas){
            articleList.add(na);
        }
    }

    class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case MainActivity.ACTION_MSG_TO_SERVICE:
                    String id = intent.getStringExtra("SourceID");
                    new NewsArticleDownloader(id,newsService).execute();
                    break;
            }
        }
    }

}

