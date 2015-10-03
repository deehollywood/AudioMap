package dk.itu.projectdee.audiomap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class TrackingService extends Service implements BeaconConsumer {

    public static final String LOGTAG = "MyLog";
    private static final String BEACON_MONITORING_ID = "pitlab";
    private static final String BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    //private static final String BEACON_LAYOUT = "1E 02 01 1A 1A FF 4C 00 02 15 6f 2A d7 1e b7 50 11 e4 89 f3 68 f7 13";
    private static final String BEACONCHEX = "49 54 55 20 34 41 30 35 20 20 20 20 20 20 20 20";
    private static final String BEACONCODE = BEACONCHEX.replace(" ", "");
    BeaconManager beaconManager;
    private static String macAdd = "";
    String currentMacAdd;
    String location;
    String distance;
    double distanceMax = 10.0;
    long time;
    long timeDetected;
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-mm-dd");
    SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm:ss");
    String date = dateformat.format(new Date());
    String currentTime = timeformat.format(new Date());
    boolean record = true;


    public TrackingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        Log.i(LOGTAG, "service started");

        //USED TO TEST WITHOUT BEACON
       /* dBMeter meter = new dBMeter();
        meter.startrecording();
        double soundlevel = meter.returnSoundLevel();
        String sound = Double.toString(soundlevel);

        Log.d(ServerConnect.TAG, "just before sc.sendData");
        Log.d(ServerConnect.TAG, "the Volume from trackingService: :  " + sound);*/

        beaconManager = BeaconManager.getInstanceForApplication(this);
        BeaconParser parser = new BeaconParser();
        parser.setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");
        //  parser.setBeac onLayout("1E 02 01 1A 1A FF 4C 00 02 15 6f 2A d7 1e b7 50 11 e4 89 f3 68 f7 13");
        //  parser.setBeaconLayout("49 54 55 20 34 41 30 35 20 20 20 20 20 20 20 20");
        beaconManager.getBeaconParsers().add(parser);
        beaconManager.bind(this);
        beaconManager.setBackgroundScanPeriod(1100l);
        beaconManager.setBackgroundBetweenScanPeriod(30000l);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    public void onBeaconServiceConnect() {

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                time = System.currentTimeMillis();
                long  elapsedtime = time - timeDetected;
                /*if(elapsedtime > 20005){
                    macAdd = "";
                }*/

                //Log.i(LOGTAG, "scaning for beacons");

                if (beacons.size() > 0) {
                    //Log.i(LOGTAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away. It is located at:  " + hexToASCII2(BEACONCODE) + " at" + time);

                    if (beacons.iterator().next().getDistance() < distanceMax){
                        currentMacAdd = beacons.iterator().next().getBluetoothAddress();
                        Log.i(LOGTAG, "MAC address detected: " + currentMacAdd);
                    }

                    //Log.i(LOGTAG, "CURRENT MAC address: " + currentMacAdd + " -- MAC address: " + macAdd);

                    double dist = beacons.iterator().next().getDistance();
                    distance = Double.toString(dist);
                    location = hexToASCII2(BEACONCODE);


                    if (beacons.iterator().next().getDistance() < distanceMax && macAdd.equals("")) {
                        Log.i(LOGTAG, "MACADD: "+ macAdd+"  --  CURADD: "+ currentMacAdd);
                        timeDetected = System.currentTimeMillis();
                        macAdd = currentMacAdd;
                        //Log.i(LOGTAG, "A beacon was detected within 10m. - Distance: " + beacons.iterator().next().getDistance());

                        //dBMeter meter = new dBMeter();
                        //meter.startrecording();
                        if (record) {

                            dBMeter meter = new dBMeter();
                            meter.startrecording();
                           /* double soundlevel = meter.returnSoundLevel();
                            String sound = Double.toString(soundlevel);

                            Log.d(ServerConnect.TAG, "the Volume from trackingService: :  " + sound);

                            ServerConnect sc = new ServerConnect(date, currentTime, currentMacAdd, sound);
                            sc.sendData();*/
                        }

                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new org.altbeacon.beacon.Region("BEACONCODE", null, null, null));
        } catch (RemoteException e) {
        }

    }

    public static String hexToASCII2(String BEACONCode) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BEACONCODE.length() - 1; i += 2) {
            String output = BEACONCODE.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
        }
        return sb.toString();
    }


    public static void returnDbResult(double db){
        Log.i(LOGTAG, "result from dbmeter: "+db);
        macAdd = "";
        //TODO send til database(reading, tid, date, currentMacAdd)
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
