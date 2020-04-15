package morcom.christopher.multinotesapp;

import android.os.AsyncTask;
import android.util.JsonReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;



public class AsyncLoadData extends AsyncTask<String, Void, ArrayList<Note>>{
    private MainActivity mainActivity;

    public AsyncLoadData(MainActivity m) {
        mainActivity = m;
    }

    @Override
    protected ArrayList<Note> doInBackground(String... strings) {
        //Open the JSON file and read the contents to arraylist
        ArrayList<Note> notelist;
        notelist = loadNotes(strings[0]);
        return notelist;
    }

    @Override
    protected void onPreExecute() { super.onPreExecute(); }

    @Override
    protected void onPostExecute(ArrayList<Note> notes) {
        super.onPostExecute(notes);
        mainActivity.whenAsyncIsDone(notes);
    }

    private ArrayList<Note> loadNotes(String filename){
        ArrayList<Note> jsonContents = new ArrayList<Note>();
        try {
            InputStream is = mainActivity.getApplicationContext().openFileInput(filename);
            JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            reader.beginArray();
            while (reader.hasNext()) {
                String title = "";
                String date = "";
                String input = "";
                reader.beginObject();
                while(reader.hasNext()){
                    String name = reader.nextName();
                    if(name.equals("Title")){
                        title = reader.nextString();
                    }
                    else if (name.equals("Date")){
                        date = reader.nextString();
                    }
                    else if (name.equals("Input")){
                        input = reader.nextString();
                    }
                    else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                jsonContents.add(new Note(title, date, input));
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
