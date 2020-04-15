package morcom.christopher.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView symbol;
    public TextView companyName;
    public TextView latestPrice, change, changepercent;

    public MyViewHolder(View view){
        super(view);
        symbol = view.findViewById(R.id.symbol);
        companyName = view.findViewById(R.id.company);
        latestPrice = view.findViewById(R.id.latestPrice);
        change = view.findViewById(R.id.change);
        changepercent = view.findViewById(R.id.changePercent);
    }
}
