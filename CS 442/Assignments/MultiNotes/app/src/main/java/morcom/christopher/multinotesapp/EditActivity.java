package morcom.christopher.multinotesapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private Note n;
    private EditText title;
    private EditText note;
    private static final int SAVED_NOTE = 3;
    private static final int NO_EDITS_MADE = 4;
    private static final int NOTE_NOT_SAVED = 5;

    private static final String TAG = "EditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityedit);
        Log.d(TAG, "onCreate: ");

        title = (EditText) findViewById(R.id.editTitle);
        note = (EditText) findViewById(R.id.editNote);
        Intent intent = getIntent();
        if (intent.hasExtra("EDIT_NOTE")){
            n = (Note) intent.getSerializableExtra("EDIT_NOTE");
            title.setText(n.getTitle());
            note.setText(n.getInput());
        }
        else {
            n=new Note();
        }

        //Log.d(TAG, "onCreate: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.Save) {
            if (title.getText().toString().matches("")) {
                Toast.makeText(this, "Note must have title", Toast.LENGTH_SHORT).show();
                return false;
            } else if (n.getTitle().equals(title.getText().toString()) && n.getInput().equals(note.getText().toString())) {
                setResult(NO_EDITS_MADE);
                finish();
                return false;
            } else {
                Date now = new Date();
                SimpleDateFormat dateF = new SimpleDateFormat("EEE, MMM d yyy 'at' h:mm a");
                String saveTime = dateF.format(now);
                n.setTitle(title.getText().toString());
                n.setInput(note.getText().toString());
                n.setDate(saveTime);
                Intent returnData = new Intent();
                returnData.putExtra("SAVED_NOTE", n);
                setResult(SAVED_NOTE, returnData);
                finish();
                return true;
            }
        }
        else if (item.getItemId() == android.R.id.home) {   
            onBackPressed();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(title.getText().toString().matches("")){
                    Toast.makeText(EditActivity.this, "Input a Note Title", Toast.LENGTH_SHORT).show();
                }
                else if (n.getTitle().equals(title.getText().toString()) && n.getInput().equals(note.getText().toString())){
                    setResult(NO_EDITS_MADE);
                    finish();
                }
                else {
                    Date now = new Date();
                    SimpleDateFormat dateF = new SimpleDateFormat("EEE, MMM d yyy 'at' h:mm a");
                    String saveTime = dateF.format(now);
                    n.setTitle(title.getText().toString());
                    n.setInput(note.getText().toString());
                    n.setDate(saveTime);
                    Intent returnData = new Intent();
                    returnData.putExtra("SAVED_NOTE", n);
                    setResult(SAVED_NOTE, returnData);
                    finish();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setResult(NOTE_NOT_SAVED);
                finish();
            }
        });

        builder.setMessage("Your note is not saved. Would you like to save it now?");
        builder.setTitle("Note Not Saved!");

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("TITLE", title.getText().toString());
        outState.putString("NOTES", note.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        note.setText(savedInstanceState.getString("NOTES"));
        title.setText(savedInstanceState.getString("TITLE"));
    }
}
