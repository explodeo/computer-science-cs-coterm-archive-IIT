package morcom.christopher.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private Official official;
    private String location;
    private TextView office, name, party, address, phone, email, website, header;
    private ImageButton picture, fb, tw, yt, gplus;
    private ConstraintLayout bg;

    private static final String TAG = "OfficialActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        header = (TextView) findViewById(R.id.location);
        office = (TextView) findViewById(R.id.Office);
        name = (TextView) findViewById(R.id.Name);
        party = (TextView) findViewById(R.id.OfcActParty);
        address = (TextView) findViewById(R.id.Address);
        phone = (TextView) findViewById(R.id.Phone);
        email = (TextView) findViewById(R.id.Email);
        website = (TextView) findViewById(R.id.Website);
        picture = (ImageButton) findViewById(R.id.Picture);
        fb = (ImageButton) findViewById(R.id.Facebook);
        tw = (ImageButton) findViewById(R.id.Twitter);
        yt = (ImageButton) findViewById(R.id.Youtube);
        gplus = (ImageButton) findViewById(R.id.GooglePlus);
        bg = (ConstraintLayout) findViewById(R.id.OfcBG);

        Intent intent = getIntent();
        if(intent.hasExtra("heading")){
            location = intent.getStringExtra("heading");
            header.setText(location);
        }
        if(intent.hasExtra("official")){
            official = (Official) intent.getSerializableExtra("official");
            Log.d(TAG, "onCreate: Setting Official Image");
            if (official.getPhoto() != null) {
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        final String changedUrl = official.getPhoto().replace("http:", "https:");
                        picasso.load(changedUrl).fit().centerInside()
                                .error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder)
                                .into(picture);
                        Log.d(TAG, "onImageLoadFailed: load image error");
                    }
                }).build();
                picasso.load(official.getPhoto()).fit().centerInside()
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(picture);
                Log.d(TAG, "onCreate: IMAGE LOADED");
            } else {
                Log.d(TAG, "onCreate: Broken or null image.");
                Picasso.get().load(R.drawable.missingimage)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(picture);
            }

            setOfficialActivityData(official);
        }
    }

    public void setOfficialActivityData(Official o) {

        Log.d(TAG, "setOfficialData: SETTING OFFICIAL DATA \n "+o.toString());

        office.setText(o.getOffice());
        name.setText(o.getName());
        String ofcParty ='('+o.getParty()+')';
        party.setText(ofcParty);
        if(o.getAddress()==null){
            address.setText("No Data Provided");
            phone.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        } else {
            address.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            address.setText(o.getAddress());
            Linkify.addLinks(address, Linkify.ALL);
            address.setLinkTextColor(Color.WHITE);
        }
        if(o.getPhone()==null){
            phone.setText("No Data Provided");
            phone.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        } else {
            phone.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            phone.setText(o.getPhone());
            Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
            phone.setLinkTextColor(Color.WHITE);
        }
        if(o.getEmail()==null){
            email.setText("No Data Provided");
            phone.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        } else {
            email.setText(o.getEmail());
            Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
            email.setLinkTextColor(Color.WHITE);
        }
        if(o.getWebsite()==null){
            website.setText("No Data Provided");
            phone.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        } else {
            website.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            website.setText(o.getWebsite());
            Linkify.addLinks(website, Linkify.WEB_URLS);
            website.setLinkTextColor(Color.WHITE);
        }

        if(o.getFacebook()==null){
            fb.setVisibility(View.INVISIBLE);
            fb.setClickable(false);
        }
        if(o.getTwitter()==null){
            tw.setVisibility(View.INVISIBLE);
            tw.setClickable(false);
        }
        if(o.getGooglePlus()==null){
            gplus.setVisibility(View.INVISIBLE);
            gplus.setClickable(false);
        }
        if(o.getYoutube()==null){
            yt.setVisibility(View.INVISIBLE);
            yt.setClickable(false);
        }
        if(o.getParty().contains("Republican")){ bg.setBackgroundColor(Color.RED); }
        else if(o.getParty().contains("Democrat")){ bg.setBackgroundColor(Color.BLUE); }
        else { bg.setBackgroundColor(Color.BLACK); } //Party Unknown
    }
    
    /*OnClick Methods Below*/
    public void onPhotoClicked(View v){
        Log.d(TAG, "onPhotoClicked: PHOTO CLICKED");
        if (official.getPhoto()!= null){
            Intent photoActivity = new Intent(this, PhotoActivity.class);
            photoActivity.putExtra("official",official);
            photoActivity.putExtra("header",header.getText().toString());
            startActivity(photoActivity);
        }
    }
    public void onFacebookClicked(View v){
        Log.d(TAG, "onFacebookClicked: FACEBOOK CLICKED");
        String FACEBOOK_URL = "https://www.facebook.com/" + official.getFacebook();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + official.getFacebook();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);

    }
    public void onTwitterClicked(View v){
        Log.d(TAG, "onTwitterClicked: TWITTER CLICKED");
        Intent intent = null;
        String name = official.getTwitter();
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }
    public void onYoutubeClicked(View v){
        Log.d(TAG, "onYoutubeClicked: YOUTUBE CLICKED");
        String name = official.getYoutube();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }
    public void onGooglePlusClicked(View v){
        Log.d(TAG, "onGooglePlusClicked: GOOGLEPLUS CLICKED");
        String name = official.getGooglePlus();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }
}
