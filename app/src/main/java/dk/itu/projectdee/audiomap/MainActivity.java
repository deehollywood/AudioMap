package dk.itu.projectdee.audiomap;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner floor, room;
    private Button showTable,showAudiomap;
    private EditText dateFrom, dateTo, timeStart,timeEnd;
    List<String> floorList = new ArrayList<>();
    List<String> roomList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyBluetooth();

        floor = (Spinner)findViewById(R.id.spinner1);
        room = (Spinner)findViewById(R.id.spinner2);
        showTable = (Button)findViewById(R.id.button1);
        showAudiomap=(Button)findViewById(R.id.button2);
        dateFrom =(EditText)findViewById(R.id.edit_text1);
        dateTo=(EditText)findViewById(R.id.edit_text2);
        timeStart=(EditText)findViewById(R.id.edit_text3);
        timeEnd=(EditText)findViewById(R.id.edit_text4);

        floorList.add("AllFloors");floorList.add("floor");floorList.add("1");floorList.add("2");floorList.add("3");floorList.add("4");floorList.add("5");

        roomList.add("8");roomList.add("12");roomList.add("14");roomList.add("18");roomList.add("28");roomList.add("30");
        roomList.add("40");roomList.add("42");roomList.add("50");roomList.add("52");roomList.add("LAB 5");

        //button when user wants to see the table
        showTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        //button when user wants to see the heatmap
        showAudiomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, floorList);
        floor.setAdapter(adapter);
        floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Your choise is: " + floor.getItemAtPosition(position) + " ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roomList);
        room.setAdapter(adapter1);
        room.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "and your room is: " + room.getItemAtPosition(position) + "  ", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //Verify bluetooth capabilieties
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void verifyBluetooth() {
        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }else{
                Intent intent = new Intent(this, TrackingService.class);
                startService(intent);
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }
            });
            builder.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
