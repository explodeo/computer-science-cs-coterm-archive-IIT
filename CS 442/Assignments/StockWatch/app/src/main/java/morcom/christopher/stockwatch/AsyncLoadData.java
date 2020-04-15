package morcom.christopher.stockwatch;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AsyncLoadData extends AsyncTask<String, Void, ArrayList<Stock>> {

    private MainActivity mainActivity;
    /*
    public AsyncLoadData(MainActivity m) {
        mainActivity = m;
    }
    */

    @Override
    protected ArrayList<Stock> doInBackground(String... strings) {
        //Open the JSON file and read the contents to arraylist
        ArrayList<Stock> stockList;
        stockList = loadStocks(strings[0]);
        return stockList;
    }

    @Override
    protected void onPreExecute() { super.onPreExecute(); }

    @Override
    protected void onPostExecute(ArrayList<Stock> stockList) {
        super.onPostExecute(stockList);
    }

    private ArrayList<String> loadStockSymbols(String filename){
        ArrayList<String> jsonContents = new ArrayList<>();
        try {
            InputStream is = mainActivity.getApplicationContext().openFileInput(filename);
            JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            reader.beginArray();
            while (reader.hasNext()) {
                String symbol = "";
                String companyName = "";
                reader.beginObject();
                while(reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equalsIgnoreCase("symbol")) { symbol = reader.nextString(); }
                    else if (name.equalsIgnoreCase("name")) { companyName = reader.nextString(); }
                    else { reader.skipValue(); }
                }
                reader.endObject();
                jsonContents.add(symbol+"  "+companyName);
            }
            return jsonContents;
        }
        catch (FileNotFoundException e) { return jsonContents; }
        catch (Exception e) {
            e.printStackTrace();
            return jsonContents;
        }
    }
    private ArrayList<Stock> loadStocks(String filename){
        ArrayList<Stock> jsonContents = new ArrayList<>();
        try {
            InputStream is = mainActivity.getApplicationContext().openFileInput(filename);
            JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            reader.beginArray();
            while (reader.hasNext()) {
                String symbol = "";
                String companyName = "";
                double latestPrice = 0;
                double change = 0;
                double changePercent = 0;
                reader.beginObject();
                while(reader.hasNext()){
                    String name = reader.nextName();
                    if(name.equalsIgnoreCase("Symbol")){
                        symbol = reader.nextString();
                    }
                    else if (name.equalsIgnoreCase("CompanyName")){
                        companyName = reader.nextString();
                    }
                    else if (name.equalsIgnoreCase("latestPrice")){
                        latestPrice = Double.parseDouble(reader.nextString());
                    }
                    else if (name.equalsIgnoreCase("change")){
                        change = Double.parseDouble(reader.nextString());
                    }
                    else if (name.equalsIgnoreCase("changePercent")){
                        changePercent = Double.parseDouble(reader.nextString());
                    }
                    else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                jsonContents.add(new Stock(symbol, companyName, latestPrice, change, changePercent));
            }
            reader.endArray();
            reader.close();
            is.close();
            return jsonContents;

        }
        catch (FileNotFoundException e) { return jsonContents; }
        catch (Exception e) {
            e.printStackTrace();
            return jsonContents;
        }
    }
}
