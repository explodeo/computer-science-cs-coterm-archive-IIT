package morcom.christopher.temperatureconverter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText tempInput;
    private TextView tempOutput;
    private TextView hist;

    private Integer cToF;
    private DecimalFormat df = new DecimalFormat("#.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempInput = findViewById(R.id.tempIN);
        tempOutput = findViewById(R.id.tempOUT);
        hist = findViewById(R.id.history);
        //make history scrollable
        hist.setMovementMethod(new ScrollingMovementMethod());


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        /*outState.putString("OUTPUT1", output1.getText().toString());
        outState.putString("OUTPUT2", output2.getText().toString()); */

        outState.putString("TEMPINPUT", tempInput.getText().toString());
        outState.putString("TEMPOUTPUT", tempOutput.getText().toString());
        outState.putString("HISTORY", hist.getText().toString());
        outState.putString("CONVSELECT", cToF.toString());
        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);
        tempInput.setText(savedInstanceState.getString("TEMPINPUT"));
        tempOutput.setText(savedInstanceState.getString("TEMPOUTPUT"));
        hist.setText(savedInstanceState.getString("HISTORY"));
        if(!hist.getText().toString().trim().isEmpty()){ hist.setTextColor(Color.BLACK); }
        cToF = Integer.valueOf(savedInstanceState.getString("CONVSELECT"));
    }

    public void convertTemp(View view){
        Log.d(TAG, "convertTemp: ");
        String inText = tempInput.getText().toString();
        String current;
        double input, conTemp;
        if (!inText.trim().isEmpty() && cToF != null){ //check if there is input
            input = Double.parseDouble(inText);
            input = Double.parseDouble(df.format(input));
            tempInput.setText(String.format("%s", input));
            //check which radiobutton got clicked and convert and add create history line
            if(cToF == 1){
                conTemp = (input*1.8)+32;
                conTemp = Double.parseDouble(df.format(conTemp));
                current = "C to F: "+input+" → "+conTemp+"\n";
            }
            else{
                conTemp = (input-32)/1.8;
                conTemp = Double.parseDouble(df.format(conTemp));
                current = "F to C: "+input+" → "+conTemp+"\n";
            }

            String ou = ""+(conTemp);
            if (!ou.trim().isEmpty()){
                hist.setTextColor(Color.BLACK);
                //append to history here
                if(hist.getText().toString().isEmpty()){ hist.setText(current); }
                else { hist.append(current); }
                //output text as string
                tempOutput.setText(ou);
            }
        }
        else {
            Toast.makeText(this, "You must enter a value and select an option for conversion.", Toast.LENGTH_SHORT).show();
        }
    }

    public void c_to_F_Clicked(View v){
        cToF = 1;
    }
    public void f_to_C_Clicked(View v){
        cToF = 0;
    }
}
