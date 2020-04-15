package morcom.christopher.stockwatch;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsyncVerifySymbol extends AsyncTask<String,Void,String> {
    private static final String TAG = "AsyncVerifySymbol";

    private MainActivity mainActivity;
    private final String dataURL = "https://api.iextrading.com/1.0/ref-data/symbols";
    private String input;

    public AsyncVerifySymbol(MainActivity m) {
        mainActivity = m;
    }

    @Override
    protected String doInBackground(String... strings) {
        input = strings[0];
        String finalURL = dataURL;
        Uri dataUri = Uri.parse(finalURL);
        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder();
        String stocksStr= "";
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line=reader.readLine()) != null){
                sb.append(line).append('\n');
            }
            stocksStr = sb.toString();
            stocksStr = stocksStr.substring(stocksStr.indexOf("["),stocksStr.indexOf("]")+1);
            return stocksStr;
        } catch (Exception e){
            Log.d(TAG, "doInBackground: !!!!!!!!!!!!!!!!!!!!!! "+e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        //data is being passed correctly...
        final ArrayList<String[]> sList = parseJSON(s);
        final ArrayList <String[]> filteredList = new ArrayList<>();
        Log.d(TAG, "onPostExecute: "+input);
        int x;
        for (String[] stk : sList){
            if (stk[0].startsWith(input)){
                filteredList.add(stk);
                Log.d(TAG, "onPostExecute: "+stk[0]);
            }
        }
        if(filteredList == null || filteredList.size()==0){
            Log.d(TAG, "onPostExecute: no stocks found");
            AlertDialog.Builder adb = new AlertDialog.Builder(mainActivity);
            adb.setMessage("No stocks match the symbol: "+input);
            adb.setTitle("No Stock Found");
            AlertDialog dialog = adb.create();
            dialog.show();

        } else if (filteredList.size()==1){
            Log.d(TAG, "onPostExecute: one stock found");
            mainActivity.parseNewStock(filteredList.get(0)[0]);
        } else {
            Log.d(TAG, "onPostExecute: many stocks found");
            final CharSequence[] stockArr = new CharSequence[filteredList.size()];
            for (int i = 0; i < filteredList.size(); i++)
                stockArr[i] = filteredList.get(i)[0] + '\n' + filteredList.get(i)[1];

            AlertDialog.Builder adb = new AlertDialog.Builder(mainActivity);
            adb.setTitle("Make a selection");

            adb.setItems(stockArr, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String symbol = filteredList.get(which)[0];
                    mainActivity.parseNewStock(symbol);
                }
            });

            adb.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.d(TAG, "onClick: user cancelled dialog");
                }
            });
            AlertDialog dialog = adb.create();
            dialog.show();
        }
    }

    private ArrayList<String[]> parseJSON(String s){
        ArrayList<String[]> stocks = new ArrayList<>();
        try{
            JSONArray jObjMain = new JSONArray(s);
            for(int i=0; i<jObjMain.length(); i++){
                JSONObject jStock = (JSONObject) jObjMain.get(i);
                String symbol = jStock.getString("symbol");
                String companyName = jStock.getString("name");
                //String type = jStock.getString("type");

                if(!symbol.contains(".")){
                    String[] stockKey = {symbol,companyName};
                    stocks.add(stockKey);
                }
            }
            return stocks;
        } catch (Exception e){
            Log.d(TAG, "parseJSON: "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}

