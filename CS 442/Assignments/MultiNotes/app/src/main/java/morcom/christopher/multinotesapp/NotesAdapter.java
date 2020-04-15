package morcom.christopher.multinotesapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<MyViewHolder>{
    private List<Note> noteList;
    private MainActivity mainAct;

    public NotesAdapter(List<Note> notelst, MainActivity mainactivity){
        this.noteList = notelst;
        mainAct = mainactivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.individualnote, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.title.setText(note.getTitle());
        holder.updated.setText(note.getDate());
        holder.preview.setText(note.getPreview());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
