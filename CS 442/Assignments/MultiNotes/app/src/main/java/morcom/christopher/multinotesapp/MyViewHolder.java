package morcom.christopher.multinotesapp;

import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView updated;
    public TextView preview;

    public MyViewHolder(View view){
        super(view);
        title = view.findViewById(R.id.Title);
        updated = view.findViewById(R.id.Updated);
        preview = view.findViewById(R.id.Preview);
    }
}
