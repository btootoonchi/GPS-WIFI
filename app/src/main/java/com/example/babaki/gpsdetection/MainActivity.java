package com.example.babaki.gpsdetection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends Activity /*ActionBarActivity*/ {

    Button btnShowLocation;

    // GPSTracker class
    GPSTracker gps;

    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    ListView list;
    String wifis[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isNetworkAvailable();
        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

//        list = (ListView)findViewById(R.id.listView1);
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        mainWifiObj.startScan();
//isNetworkAvailable();
        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // create class object
                gps = new GPSTracker(MainActivity.this);

                gps.stopUsingGPS();

                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                    TextView txtLatitud = (TextView) findViewById(R.id.txtLatitud);
                    TextView txtLongitud = (TextView) findViewById(R.id.txtLongitud);
                    String latitud = Double.toString(latitude);
                    String longitud = Double.toString(longitude);
                    txtLatitud.setText(latitud);
                    txtLongitud.setText(longitud);
                    WebView webView = (WebView) findViewById(R.id.webView);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setBuiltInZoomControls(true);
                    webView.getSettings().setSupportZoom(true);
                    webView.setWebViewClient(new WebViewClient());
                    //	webView.loadUrl("http://www.openstreetmap.org/?mlat="+latitud+"&mlon="+longitud+"#map=14/"+latitud+"/"+longitud+"&layers=N");
                    webView.loadUrl("https://www.google.com/maps/search/" + latitud + ",+" + longitud);
                    //webView.loadUrl("http://www.openstreetmap.org/#map=5/"+latitud+"/"+longitud);

                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
                gps.stopUsingGPS();
//                isNetworkAvailable();
            }
        });
//        isNetworkAvailable();
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

    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
            wifis = new String[wifiScanList.size()+1];
            for(int i = 0; i < wifiScanList.size(); i++){
                wifis[i] = ((wifiScanList.get(i)).toString());
            }

            int networkInfo = mainWifiObj.getWifiState();

            if (networkInfo == WifiManager.WIFI_STATE_ENABLED || networkInfo == WifiManager.WIFI_STATE_ENABLING) {
                WifiInfo info = mainWifiObj.getConnectionInfo();
                wifis[wifiScanList.size()] = info.getSSID().toString();
            }

            TextView txtLatitud = (TextView) findViewById(R.id.txtLatitud);
            txtLatitud.setText(wifis[wifiScanList.size()]);
//            list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
//                    android.R.layout.simple_list_item_1,wifis));
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();
        return activeNetworkInfo.isConnected();
    }

}
