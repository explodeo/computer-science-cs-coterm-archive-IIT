package morcom.christopher.stockwatch;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.Locale;

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private static final String TAG = "StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainActivity;

    public StockAdapter(List<Stock> stocklst, MainActivity m){
        this.stockList = stocklst;
        mainActivity = m;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_list_entry, parent, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Stock stock = stockList.get(position);

        holder.symbol.setText(stock.getSymbol());
        holder.companyName.setText(stock.getCompanyName());
        holder.change.setText(Double.toString(stock.getChange()));
        holder.changepercent.setText("("+String.format( "%.2f", stock.getChangepercent())+"%)");
        holder.latestPrice.setText(Double.toString(stock.getLatestPrice()));

        Log.d(TAG, "onBindViewHolder: setting stock fields (& colors) for individual stocks");
        if(stock.getChange()<0){
            holder.symbol.setTextColor(Color.RED);
            holder.companyName.setTextColor(Color.RED);
            holder.change.setTextColor(Color.RED);
            holder.changepercent.setTextColor(Color.RED);
            holder.latestPrice.setTextColor(Color.RED);

            String changeWithSymbol = "\u25BC "+Double.toString(stock.getChange());
            holder.change.setText(changeWithSymbol);
        }
        else {
            holder.symbol.setTextColor(Color.GREEN);
            holder.companyName.setTextColor(Color.GREEN);
            holder.change.setTextColor(Color.GREEN);
            holder.changepercent.setTextColor(Color.GREEN);
            holder.latestPrice.setTextColor(Color.GREEN);

            String changeWithSymbol = "\u25B2 " + Double.toString(stock.getChange());
            holder.change.setText(changeWithSymbol);
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

}
