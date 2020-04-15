package morcom.christopher.stockwatch;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class StockDownloader extends AsyncTask<String, String, String> {

    private static final String filename = "StockNames.json";

    private MainActivity mainActivity;
    public StockDownloader(MainActivity m) {
        mainActivity = m;
    }

    @Override
    protected String doInBackground(String... fileURL) {
        int count;
        try {
            URL url = new URL(fileURL[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    999999);
            // Output stream
            OutputStream output = new FileOutputStream(filename);

            byte data[] = new byte[1024];

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return filename;
    }
}
