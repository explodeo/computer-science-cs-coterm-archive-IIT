
package morcom.christopher.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ////--------------------------------MAINACTIVITY--------------------------------////
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String STORY_LIST = "STORY_LIST";
    private ArrayList<NewsSource> newsSources = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<NewsArticle> articles = new ArrayList<>();
    private HashMap<String, ArrayList<NewsSource>> hashMap = new HashMap<>();
    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager viewPager;
    private Menu menu;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NewsReceiver newsReceiver = new NewsReceiver();
    private String categorySelection = "All";
    private int sourceSelected = -1;
    private int page;

    //--------------------------------SUBCLASSES--------------------------------//
    class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(@NonNull Context context, Intent intent) {
            switch(intent.getAction()){
                case ACTION_NEWS_STORY:
                    articles = (ArrayList<NewsArticle>) intent.getSerializableExtra(STORY_LIST);
                    page = intent.getIntExtra("Page", 0);
                    resetFragments(articles, page);
                    break;
            }
        }

        public void resetFragments(ArrayList<NewsArticle> a, int p){
            for (int i = 0; i < pageAdapter.getCount(); i++)
                pageAdapter.notifyChangeInPosition(i);
            fragments.clear();
            int count = a.size();
            for (int x = 0; x < count; x++) {
                fragments.add(NewsFragment.newInstance(a.get(x),(x+1)+" of "+count));
            }
            pageAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(p);
        }
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;

        public MyPageAdapter(FragmentManager fm) { super(fm); }

        @Override
        public int getItemPosition(@NonNull Object object) { return POSITION_NONE; }

        @Override
        public Fragment getItem(int position) { return fragments.get(position); }

        @Override
        public int getCount() { return fragments.size(); }

        @Override
        public long getItemId(int position) { return baseId + position; }

        public void notifyChangeInPosition(int n) { baseId += getCount() + n; }
    }

    //--------------------------------METHODS--------------------------------//

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, NewsService.class));
        registerReceiver(newsReceiver, new IntentFilter(ACTION_NEWS_STORY));
        Log.d(TAG, "onCreate: SERVICE AND RECEIVER SET");

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerList = findViewById(R.id.drawer);
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list, newsSources));
        drawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        sourceSelected = position;
                        Log.d(TAG, "onItemClick: NEWSSOURCE SELECTED\n" +
                                "GETTING ARTICLE FRAGS...");
                        viewPager.setBackground(null);
                        setTitle(newsSources.get(sourceSelected).getName());
                        Intent intent = new Intent();
                        intent.setAction(ACTION_MSG_TO_SERVICE);
                        intent.putExtra("SourceID", newsSources.get(sourceSelected).getId());
                        sendBroadcast(intent);
                        drawerLayout.closeDrawer(drawerList);
                    }
                }
        );
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.Open_Nav_Drawer,
                R.string.Close_Nav_Drawer
        );
        Log.d(TAG, "onCreate: DRAWER SET");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragments = new ArrayList<>();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(pageAdapter);

        if(newsSources.isEmpty()){
            new NewsSourceDownloader(this,categorySelection).execute();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        if(!articles.isEmpty()){
            viewPager.setBackground(null);
            Intent intent = new Intent();
            intent.setAction(ACTION_NEWS_STORY);
            intent.putExtra(STORY_LIST, articles);
            intent.putExtra("Page", page);
            sendBroadcast(intent);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, NewsService.class));
        unregisterReceiver(newsReceiver);
        super.onDestroy();
    }

    //menu methods
    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.menu, m);
        menu = m;
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        categorySelection = item.toString();
        new NewsSourceDownloader(this,categorySelection).execute();
        return super.onOptionsItemSelected(item);
    }

    public void setSources(ArrayList<NewsSource> sourceList, ArrayList<String> categoryList) {
        hashMap.clear();
        newsSources.clear();
        for (NewsSource s : sourceList) {
            if (!hashMap.containsKey(s.getCategory())) {
                hashMap.put(s.getCategory(), new ArrayList<NewsSource>());
            }
            hashMap.get(s.getCategory()).add(s);
        }

        hashMap.put("All", sourceList);
        newsSources.addAll(sourceList);

        Log.d(TAG, "setSources: "+newsSources.toString());

        ((ArrayAdapter) drawerList.getAdapter()).notifyDataSetChanged();


        if(categories.isEmpty()){
            categories = categoryList;
            categories.add(0,"All");

            if(menu == null){
                invalidateOptionsMenu();
                if(menu == null){
                    return;
                }
            }
            for(String c: categories){ menu.add(c); }
        }
    }
}
