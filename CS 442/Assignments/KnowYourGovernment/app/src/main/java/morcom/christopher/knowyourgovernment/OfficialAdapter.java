package morcom.christopher.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "OfficialAdapter";

    private List<Official> officialList;
    private MainActivity mainActivity;

    public OfficialAdapter (List<Official> oList, MainActivity m){
        this.officialList = oList;
        mainActivity = m;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.official_layout, parent, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int pos) {
        Official ofc = officialList.get(pos);
        vh.officialOffice.setText(ofc.getOffice());
        String nameParty = ofc.getName()+" ("+ofc.getParty()+")";
        vh.officialNameParty.setText(nameParty);
    }

    @Override
    public int getItemCount() { return officialList.size(); }

}
