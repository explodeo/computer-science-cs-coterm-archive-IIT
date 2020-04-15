package morcom.christopher.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView officialOffice;
    public TextView officialNameParty;

    public ViewHolder(View v){
        super(v);
        officialOffice = v.findViewById(R.id.Title);
        officialNameParty = v.findViewById(R.id.NameParty);
    }
}