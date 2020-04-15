package morcom.christopher.knowyourgovernment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";

    private String loc;
    private ConstraintLayout bg;
    private Official ofc;
    private TextView office, name, header;
    private ImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        header = (TextView) findViewById(R.id.location);
        office = (TextView) findViewById(R.id.Office);
        name = (TextView) findViewById(R.id.Name);
        pic = (ImageView) findViewById(R.id.Picture);
        bg = (ConstraintLayout) findViewById(R.id.OfcBG);

        Intent intent = getIntent();
        if(intent.hasExtra("header")){
            loc = intent.getStringExtra("header");
            header.setText(loc);
        }
        if(intent.hasExtra("official")) {
            ofc = (Official) intent.getSerializableExtra("official");
            Log.d(TAG, "onCreate: "+ofc.toString());
            if (ofc.getPhoto() != null) {
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        final String changedUrl = ofc.getPhoto().replace("http:", "https:");
                        picasso.load(changedUrl).fit().centerInside()
                                .error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder)
                                .into(pic);
                    }
                }).build();
                picasso.load(ofc.getPhoto()).fit().centerInside()
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(pic);
            } else {
                Picasso.get().load(R.drawable.missingimage)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(pic);
            }

            office.setText(ofc.getOffice());
            name.setText(ofc.getName());
            if(ofc.getParty().contains("Republican")){ bg.setBackgroundColor(Color.RED); }
            else if(ofc.getParty().contains("Democrat")){ bg.setBackgroundColor(Color.BLUE); }
            else { bg.setBackgroundColor(Color.BLACK); } //Party Unknown
        }
    }
}
