package morcom.christopher.stockwatch;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private List<Stock> stockList;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper; // The SwipeRefreshLayout
    private StockAdapter stockAdapter;
    private DatabaseHandler dbHandler;

    //private static final int ADD_CODE = 1;
    //private static final int UPDATE_CODE = 2;
    //private static final int FIND_CODE = 3;

    private static final String TAG = "MainActivity";

    private void doRefresh() {
        if (!hasNetworkAccess()){
            badNetwork();
        }
        refresh();
        stockAdapter.notifyDataSetChanged();
        swiper.setRefreshing(false);
        Log.d(TAG, "doRefresh: stocks refreshed");
    }

    public boolean hasNetworkAccess() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null) {
            if (ni.isConnected()) {
                Log.d(TAG, "hasNetworkAccess: NETWORK GOOD");
                return true;
            }
        }
        return false;
    }

    public void badNetwork(){
        Log.d(TAG, "hasNetworkAccess: NETWORK BAD...Alerting");
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("No Network Connection");
        adb.setMessage("Stocks Cannot Be Added Without A Network Connection");
        adb.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        stockList = new ArrayList<>();
        dbHandler = new DatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.Recycler);
        stockAdapter = new StockAdapter(stockList, this);
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        if (hasNetworkAccess()){
            refresh();//???
        } else {
            badNetwork();
        }
    }

    public void refresh(){
        if (!hasNetworkAccess()){ badNetwork(); return;}
        ArrayList<String[]> tmp = dbHandler.loadStocks();
        stockList.clear();
        for(int i=0; i<tmp.size();i++){
            new AsyncDownloadData(this).execute(tmp.get(i)[0]);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateSymbols()


    }

    @Override
    protected void onDestroy() {
        dbHandler.shutDown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        String stock = stockList.get(pos).getSymbol().toUpperCase();
        Log.d(TAG, "onClick: "+stock);
        String intentURL = "http://www.marketwatch.com/investing/stock/"+stock;
        try {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentURL)));
        } catch (ActivityNotFoundException e) {
            Log.d(TAG, "onClick: "+e);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dbHandler.deleteStock(stockList.get(pos).getSymbol());
                stockList.remove(pos);
                stockAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Delete stock " + stockList.get(pos).getSymbol().toUpperCase() + "?");
        builder.setTitle("Delete Stock");
        builder.setIcon(R.drawable.deletestockicon);
        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    public void addNewStock(Stock s){
        if (s != null){
            stockList.add(s);
            Collections.sort(stockList, new Comparator<Stock>() {
                public int compare(Stock s1, Stock s2) {
                    return s1.getSymbol().compareTo(s2.getSymbol());
                }
            });
            dbHandler.addStock(s);
            stockAdapter.notifyDataSetChanged();
        }
    }

    public void parseNewStock(String symbol){
        boolean duplicate = false;
        for(int i=0; i<stockList.size();i++){
            if(stockList.get(i).getSymbol().equals(symbol)){
                duplicate = true;
            }
        }
        if(duplicate){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Duplicate Stock");
            adb.setMessage("Stock Symbol "+ symbol +" is already displayed");
            adb.setIcon(R.drawable.warningicon);
            AlertDialog d = adb.create();
            d.show();
        }
        else {
            new AsyncDownloadData(this).execute(symbol); //??
        }
    }

    //@Override
    public boolean onAddStockSelected(MenuItem m){
        if (m.getItemId() == R.id.addStockButton){
            if (hasNetworkAccess()){
                final MainActivity main = this;
                final EditText field = new EditText(this);
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                field.setInputType(InputType.TYPE_CLASS_TEXT);
                field.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                field.setGravity(Gravity.CENTER_HORIZONTAL);
                adb.setView(field);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new AsyncVerifySymbol(main).execute(field.getText().toString());
                    }
                });
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled add stock
                    }
                });
                adb.setTitle("Stock Selection");
                adb.setMessage("Please enter a Stock Symbol:");
                AlertDialog dialog = adb.create();
                dialog.show();

            } else {
                badNetwork();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(m);
        }
    }
}
