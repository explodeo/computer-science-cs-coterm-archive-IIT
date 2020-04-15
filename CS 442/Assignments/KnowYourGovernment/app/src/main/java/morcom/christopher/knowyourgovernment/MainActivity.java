package morcom.christopher.knowyourgovernment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.xml.sax.Locator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerview;
    private List<Official> officialList;
    private OfficialAdapter officialAdapter;
    private TextView location;
    private String cityZip;
    private GeoLocator geolocator;

    private boolean hasNetworkAccess(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null) {
            if (ni.isConnected()) {
                Log.d(TAG, "hasNetworkAccess: NETWORK GOOD");
                return true;
            }
        }
        //noConnectionDialog();
        return false;
    }
    private void noConnectionDialog(){
        location.setText(R.string.no_loc);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage("Data cannot be loaded without a valid network connection.");
        adb.setTitle("No Network Connection");
        AlertDialog ad = adb.create();
        ad.show();
    }

    @Override
    public boolean onLongClick(View view) {
        onClick(view);
        return true;
    }

    @Override
    public void onClick(View v) {
        Official ofc = officialList.get(recyclerview.getChildLayoutPosition(v));
        Intent ofcAct = new Intent(this, OfficialActivity.class);
        ofcAct.putExtra("heading", location.getText());
        ofcAct.putExtra("official", ofc);
        startActivity(ofcAct);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //incomplete
        switch (item.getItemId()){
            case R.id.about:
                Intent aboutActivity = new Intent(this, About.class);
                startActivity(aboutActivity);
                return true;
            case R.id.search:
                final MainActivity m = this;
                final EditText et = new EditText(this);
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setGravity(Gravity.CENTER);
                //et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                adb.setView(et);
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cityZip = et.getText().toString();
                        new CivicInfoDownloader(m).execute(cityZip);
                    }
                });

                adb.setTitle("Enter a City, State or Zip Code:");
                adb.create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 5) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        geolocator.onCreateLocationManager();
                        geolocator.determineLocation();
                        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    } else {
                        Toast.makeText(this, "Location permissions are required.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: CREATE ACTIVITY");
        location = (TextView) findViewById(R.id.location);
        geolocator = new GeoLocator(this);
        geolocator.locationManager.removeUpdates(geolocator.locationListener); //suspend GPS updates
        if (!hasNetworkAccess()){
            Log.d(TAG, "onCreate: NO NETWORK ACCESS");
            noConnectionDialog(); 
        }
        Log.d(TAG, "onCreate: BUILDING OFFICIAL LIST");
        officialList = new ArrayList<>();
        recyclerview = (RecyclerView) findViewById(R.id.Recycler);
        officialAdapter = new OfficialAdapter(officialList, this);
        recyclerview.setAdapter(officialAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        //officialAdapter.notifyDataSetChanged();
    }

    public void getLocationData(double lat, double lon) {
        List<Address> locations;
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        try {
            locations = gc.getFromLocation(lat, lon, 1);
            cityZip = locations.get(0).getPostalCode();
            Log.d(TAG, "getLocationData: GET LOCATION OFFICIAL DATA\n Address="+cityZip);
            new CivicInfoDownloader(this).execute(cityZip);
        } catch (IOException e) {
            Log.d(TAG, "getLocationData: INVALID ADDRESS");
            //Toast.makeText(this, "Invalid Address", Toast.LENGTH_SHORT).show();
        }
    }

    public void setOfficialList(Object[] locOfficial){
        if (locOfficial != null){
            officialList.clear();
            cityZip = (String) locOfficial[0]; //this is the location
            Log.d(TAG, "setOfficialList: LOCATION SET");
            ArrayList<Official> officials = (ArrayList<Official>) locOfficial[1]; //this is the list data
            Log.d(TAG, "setOfficialList: OFFICIAL LIST SET");
            officialList.addAll(officials);
            location.setText(cityZip);
            officialAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Address Data Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        geolocator.shutdown();
        super.onDestroy();
    }

}
