package morcom.christopher.knowyourgovernment;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CivicInfoDownloader extends AsyncTask<String, Void, String> {

    private MainActivity mainActivity;
    private static final String key = "AIzaSyAd-x5CFO7uqkrWfAaKZcqWIeOimfXsMFY"; //this API key is !!ANDROID-ONLY RESTRICTED!!
    private static final String TAG = "CivicInfoDownloader";

    private String civicDataURL = "https://www.googleapis.com/civicinfo/v2/representatives?key="+key+"&address=";
    private String address;

    public CivicInfoDownloader(MainActivity m){ mainActivity = m; }

    @Override
    protected String doInBackground(String... strings){
        address = strings[0];
        String builtURL = (Uri.parse(civicDataURL+address)).toString(); //makes it easier to do this in one line lol
        StringBuilder sb = new StringBuilder();
        
        //the below was optimized from last project //
        
        try{
            URL url = new URL(builtURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if(connection.getResponseCode() == 400){
                Log.d(TAG, "doInBackground: BAD REQUEST CODE 400");
                return sb.toString();
            } else {
                Log.d(TAG, "doInBackground: START CONNECTION: "+url.toString());
                connection.setRequestMethod("GET");
                InputStream inStream = connection.getInputStream();
                Log.d(TAG, "doInBackground: CONNECTED TO CIVIC_DATA_URL. Getting Data...");
                BufferedReader reader = new BufferedReader((new InputStreamReader(inStream)));
                String buf;
                while((buf=reader.readLine()) != null){ sb.append(buf); }
                return sb.toString();
            }
        }
        catch(Exception e){
            Log.d(TAG, "doInBackground: --- ERROR ---\n".toUpperCase()+e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s){
        if(s==null||s.equals("")){
            mainActivity.setOfficialList(null);
        } else {
            Object[] data = parseJSON(s);
            mainActivity.setOfficialList(data);
        }
    }

    //@NonNull
    private Object[] parseJSON(String json){
        ArrayList<Official> officialList = new ArrayList<>();
        String location="";
        try{
            JSONObject data = new JSONObject(json);
            JSONObject normalizedInput = data.getJSONObject("normalizedInput");
            JSONArray offices = data.getJSONArray("offices");
            JSONArray officials = data.getJSONArray("officials");
            location = normalizedInput.getString("city")+", "
                    +normalizedInput.getString("state")+" "
                    +normalizedInput.getString("zip");

            for (int i = 0; i<offices.length()-1;i++){
                JSONObject office = offices.getJSONObject(i);
                String ofcTitle = office.getString("name");
                JSONArray officialIndices = office.getJSONArray("officialIndices");
                for(int y = 0; y<officialIndices.length();y++){
                    JSONObject ofc = officials.getJSONObject(officialIndices.getInt(y));

                    Log.d(TAG, "parseJSON: BEGIN BUILDING NEW OFFICIAL");

                    String name = ofc.getString("name");
                    String addr = null;
                    if(ofc.has("address")) {
                        JSONObject addressData = ofc.getJSONArray("address").getJSONObject(0);
                        StringBuilder address = new StringBuilder();
                        if (addressData.has("line1")) { address.append(addressData.getString("line1")); }
                        if (addressData.has("line2")) { address.append(",\n" + addressData.getString("line2")); }
                        if (addressData.has("line3")) { address.append(",\n" + addressData.getString("line3")); }
                        if (addressData.has("city")) { address.append( ",\n" + addressData.getString("city")); }
                        if (addressData.has("state")) { address.append(", " + addressData.getString("state")); }
                        if (addressData.has("zip")) { address.append( ", " + addressData.getString("zip")); }
                        addr = (address.length() != 0) ? address.toString() : null;
                    }
                    //Log.d(TAG, "parseJSON: ADDRESS BUILT (OR NULL) -> "+addr);
                    String party = (ofc.has("party")) ? ofc.getString("party"): "Unknown";
                    //Log.d(TAG, "parseJSON: PARTY BUILT (OR UNKNOWN) -> "+party);
                    String phone = (ofc.has("phones")) ? ofc.getJSONArray("phones").getString(0) : null;
                    //Log.d(TAG, "parseJSON: PHONE BUILT (OR NULL) -> "+phone);
                    String website = (ofc.has("urls")) ? ofc.getJSONArray("urls").getString(0) : null;
                    //Log.d(TAG, "parseJSON: EMAIL BUILT (OR NULL) -> "+website);
                    String email = (ofc.has("emails")) ? ofc.getJSONArray("emails").getString(0) : null;
                    //Log.d(TAG, "parseJSON: EMAIL BUILT (OR NULL) -> "+email);
                    String photo = (ofc.has("photoUrl")) ? ofc.getString("photoUrl") : null;
                    //Log.d(TAG, "parseJSON: PHOTO_URL BUILT (OR NULL) -> "+photo);
                    String fb=null, tw=null, gplus=null, yt=null;
                    if(ofc.has("channels")) {
                        JSONArray sm = ofc.getJSONArray("channels");
                        for (int xx = 0; xx < sm.length(); xx++) {
                            JSONObject socialMedia = sm.getJSONObject(xx);
                            if(socialMedia.getString("type").equals("Facebook")){
                                fb = socialMedia.getString("id");
                            } else if(socialMedia.getString("type").equals("Twitter")){
                                tw = socialMedia.getString("id");
                            } else if(socialMedia.getString("type").equals("GooglePlus")){
                                gplus = socialMedia.getString("id");
                            } else if(socialMedia.getString("type").equals("YouTube")){
                                yt = socialMedia.getString("id");
                            }
                        }
                    }
                    //Log.d(TAG, "parseJSON: SOCIAL MEDIA BUILT (OR NULL)");

                    Official o = new Official(name, party, ofcTitle, addr, phone, website, email, photo, gplus, fb, tw, yt);
                    officialList.add(o);
                    Log.d(TAG, "parseJSON: OFFICIAL ADDED TO OFFICIAL LIST: "+o.toString());
                }
            }

        } catch (Exception e){
            Log.d(TAG, "parseJSON: "+e.getMessage());
        }
        return new Object[]{location, officialList};
    }
}
