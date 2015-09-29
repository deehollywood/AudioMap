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

import java.util.Collection;

public class TrackingService extends Service implements BeaconConsumer {

    private static final String BEACON_MONITORING_ID = "pitlab";
    private static final String BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    //private static final String BEACON_LAYOUT = "1E 02 01 1A 1A FF 4C 00 02 15 6f 2A d7 1e b7 50 11 e4 89 f3 68 f7 13";
    private static final String BEACONCHEX = "49 54 55 20 34 41 30 35 20 20 20 20 20 20 20 20";
    private static final String BEACONCODE = BEACONCHEX.replace(" ", "");
    protected static final String TAG = "RangingActivity";
    public static final String LOGTAG = "MyLog";
    String distance;
    String location;
    BeaconManager beaconManager;
    long time;
    long timeDetected;
    String macAdd = "";
    String currentMacAdd;

    public TrackingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        Log.i(LOGTAG, "service starting");

        //WAS USED TO TEST WITHOUT BEACON
        //dBMeter meter = new dBMeter();
        //meter.startTread();

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
                if(elapsedtime > 10000){
                    macAdd = "";
                }

               // Log.i(LOGTAG, "scaning for beacons");

                if (beacons.size() > 0) {
                    Log.i(LOGTAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away. It is located at:  " + hexToASCII2(BEACONCODE) + " at" + time);

                    currentMacAdd = beacons.iterator().next().getBluetoothAddress();
                    Log.i(LOGTAG, "CURRENT MAC address: " + currentMacAdd);
                    Log.i(LOGTAG, "MAC address: " + macAdd);
                    double dist = (double) beacons.iterator().next().getDistance();
                    distance = Double.toString(dist);
                    location = hexToASCII2(BEACONCODE);

                    if (beacons.iterator().next().getDistance() < 10.0 && !currentMacAdd.equals(macAdd)) {
                        timeDetected = System.currentTimeMillis();
                        macAdd = currentMacAdd;
                        Log.i(LOGTAG, "A beacon was detected within 10m. - Distance: " + beacons.iterator().next().getDistance());

                        //startTread();
                        dBMeter meter = new dBMeter();
                        meter.startTread();
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

    public String getLocation() {
        return location.toString();
    }

    public String getDistance() {
        return distance;
    }

    public long getTime() {
        return time;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
