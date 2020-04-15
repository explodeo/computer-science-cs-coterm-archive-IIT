package morcom.christopher.multinotesapp;

import android.view.MenuItem;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.util.JsonWriter;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private List<Note> nList;
    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    private static final int NEW_NOTE = 1;
    private static final int EDIT_NOTE = 2;
    private static final int SAVED_NOTE = 3;
    private static final int NOEDIT = 4;
    private Note editedNote;
    private int editedNotePos;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);
        Log.d(TAG, "onCreate: ");
        /*LOAD JSON NOTES*/
        String jsonFile = getString(R.string.file_name);
        new AsyncLoadData(this).execute(jsonFile);
    }

    @Override
    protected void onPause() { /*SAVE TO JSON*/
        saveNoteList();
        super.onPause();
    }

    public void saveNoteList() {
        try {
            FileOutputStream out = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            JsonWriter noteWriter = new JsonWriter(new OutputStreamWriter(out, getString(R.string.encoding)));
            noteWriter.setIndent("\t");
            noteWriter.beginArray();
            for (Note n: nList){
                noteWriter.beginObject();
                noteWriter.name("Title").value(n.getTitle());
                noteWriter.name("Date").value(n.getDate());
                noteWriter.name("Input").value(n.getInput());
                noteWriter.endObject();
            }
            noteWriter.endArray();
            noteWriter.close();

            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();

        } catch (Exception e) { e.getStackTrace(); }
    }



    @Override
    public void onClick(View view) {
        editedNotePos = recyclerView.getChildLayoutPosition(view);
        editedNote = nList.get(editedNotePos);

        Intent edit = new Intent(MainActivity.this, EditActivity.class);
        edit.putExtra("EDIT_NOTE", editedNote);
        startActivityForResult(edit,EDIT_NOTE);
    }

    @Override
    public boolean onLongClick(View view) {
        final int POS = recyclerView.getChildLayoutPosition(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                nList.remove(POS);
                notesAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "Note Kept", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setMessage("Are you sure you want to delete this note?");
        builder.setTitle("Delete Note");
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.Add:
                Log.d(TAG, "onOptionsItemSelected: in Add");
                Intent add = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(add,NEW_NOTE);
                return true;
            case R.id.Info:
                Log.d(TAG, "onOptionsItemSelected: in Info");
                Intent info = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(info);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == NEW_NOTE) {
            if (resultCode == SAVED_NOTE){
                Note newNote = (Note) data.getSerializableExtra("SAVED_NOTE");
                nList.add(0,newNote);
                notesAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == EDIT_NOTE) {
            if (resultCode == SAVED_NOTE){
                editedNote = (Note) data.getSerializableExtra("SAVED_NOTE");
                nList.remove(editedNotePos);
                nList.add(0,editedNote);
                notesAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == NOEDIT){
                Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show();
            }
        }
        notesAdapter.notifyDataSetChanged();
        Log.d(TAG, "onActivityResult: completed task CODE "+requestCode);
    }

    public void whenAsyncIsDone(List<Note> nl) {
        nList = nl;
        if (nList != null) {
            recyclerView = findViewById(R.id.Recycler);
            notesAdapter = new NotesAdapter(nList, this);
            recyclerView.setAdapter(notesAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            notesAdapter.notifyDataSetChanged();
        }
    }
}
