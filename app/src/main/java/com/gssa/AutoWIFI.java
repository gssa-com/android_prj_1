package com.gssa;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Comparator;
import java.util.List;


public class AutoWIFI extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "WIFIScanner";

    // WifiManager variable
    WifiManager wifimanager;

    // UI variable
    TextView textStatus;
    Button btnScanStart;
    Button btnScanStop;

    private static final int REQUEST_CODE_LOCATION = 2;
    private int scanCount = 0;
    String text = "";
    String result = "";
    String except_keyword = "T Free, T wifi, U+Net, VANA, iptime";

    Context context;
    boolean isAuthIng = false;

    private List<ScanResult> mScanResult; // ScanResult List

    @RequiresApi(api = Build.VERSION_CODES.N)
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) && isAuthIng == false && isConnected() == false) {

                getWIFIScanResult(context); // get WIFISCanResult
                wifimanager.startScan(); // for refresh

            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };

    void getPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // ACCESS_COARSE_LOCATION
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
            } else {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION);
            }
            // ACCESS_WIFI_STATE
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_WIFI_STATE)) {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, REQUEST_CODE_LOCATION);
            } else {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, REQUEST_CODE_LOCATION);
            }
            // ACCESS_NETWORK_STATE
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)) {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, REQUEST_CODE_LOCATION);
            } else {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, REQUEST_CODE_LOCATION);
            }
            // CHANGE_WIFI_STATE
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CHANGE_WIFI_STATE)) {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.CHANGE_WIFI_STATE}, REQUEST_CODE_LOCATION);
            } else {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.CHANGE_WIFI_STATE}, REQUEST_CODE_LOCATION);
            }
        }
    }

    boolean isConnected(){
        ConnectivityManager manager;
        WifiManager wifiManager;
        NetworkInfo wifi;

        wifiManager = (WifiManager)context.getSystemService(WIFI_SERVICE); //활성화 한지 와이파이 체크 하기 위함
        manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE); //와이파이 연결 체크
        wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);//와이파이 연결 체크

        // wifi 또는 모바일 네트워크 어느 하나라도 연결이 되어있다면,
        if (wifi.isConnected()) {
            Log.i("연결됨" , "와이파이 연결되어있음"); //와이파이 연결 되있을 때 구분 구문
            textStatus.append("연결 체크 : 연결 O \n");
            return true;
        }
        else
        {
            textStatus.append("연결 체크 : 연결 X \n");
            return false;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getWIFIScanResult(Context context) {

        String except_wifi = "X";

        mScanResult = wifimanager.getScanResults(); // ScanResult

        // level 오름차순 sort
        mScanResult.sort(new Comparator<ScanResult>()
        {
            @Override
            public int compare(ScanResult arg0, ScanResult arg1)
            {
                return arg1.level - arg0.level;  // level 이 - 값이므로 0에서 가까운게 신호가 좋음.
            }
        });

        textStatus.setText("Scan count is \t" + ++scanCount + " times \n");

        textStatus.append("=======================================\n");
        for (int i = 0; i < mScanResult.size(); i++) {
            ScanResult result = mScanResult.get(i);

            if(except_keyword.contains((result.SSID+"    ".toString()).substring(0,2)) == true)
            {
                except_wifi = "X";   // 제외할 와이파이
                continue;
            }
            else except_wifi = "O";

            Log.d(TAG, "except_keyword : " + except_keyword+" substr : " + (result.SSID+"    ").substring(0,2));

            textStatus.append((i + 1)
                    + ".\t SSID : "
                    + result.SSID.toString()
                    + "\t RSSI : " + result.level + " dBm ["+ except_wifi + "]\n");

            // wifi 또는 모바일 네트워크 어느 하나라도 연결이 되어있다면,
            if (isConnected() == true) {
               // 연결됨
                textStatus.append("이미 연결되어 있음 " + result.SSID.toString() + "\n");
            }
            else
            {
                if("O".equals(except_wifi))
                {
                    textStatus.append("연결 시도합니다. " + result.SSID.toString() + "\n");
                    setNewConnection(context, result.SSID.toString());
                }
            }
            break;
        }
        textStatus.append("=======================================\n");
    }

    public void setNewConnection(Context context, String ssid)
    {
        WifiManager wifiManager=(WifiManager)context.getSystemService(WIFI_SERVICE); //활성화 한지 와이파이 체크 하기 위함

        Toast.makeText(getApplicationContext(),"WIFI 활성화 시 이용하실 수 있습니다.", Toast.LENGTH_LONG).show();
        wifiManager.setWifiEnabled(true); //와이파이 활성화

        try {
            Thread.sleep(5000);
        }catch(Exception e) {
            e.printStackTrace();
        }

        /* 재연결 합니다. */
        WifiConfiguration wifiConfig = new WifiConfiguration(); // 와이파이 연결하기
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        if("dksoft".equals(ssid)) {
            wifiConfig.preSharedKey = String.format("\"%s\"", "dksoft0603");
        }
        else if("sksoon".equals(ssid)) {
            wifiConfig.preSharedKey = String.format("\"%s\"", "teat1324");
        }
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();

        try {
            Thread.sleep(3000);
        }catch(Exception e) {
            e.printStackTrace();
        }

        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
        Log.i("연결됨" , "재연결 완료 SSID : " + ssid); //와이파이 연결 되있을 때 구분 구문
        textStatus.append("재연결 완료 SSID. " + ssid + "\n");
        isAuthIng = true;

        try {
            Thread.sleep(10000);
        }catch(Exception e) {
            e.printStackTrace();
        }

        isAuthIng = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initWIFIScan() {
        // init WIFISCAN
        scanCount = 0;
        text = "";
        final IntentFilter filter = new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
        wifimanager.startScan();
        Log.d(TAG, "initWIFIScan()");
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;

        // Setup UI
        textStatus = (TextView) findViewById(R.id.textStatus);
        btnScanStart = (Button) findViewById(R.id.btnScanStart);
        btnScanStop = (Button) findViewById(R.id.btnScanStop);

        // Setup OnClickListener
        btnScanStart.setOnClickListener(this);
        btnScanStop.setOnClickListener(this);

        // 권한 요청
        getPermission();

        // Setup WIFI
        wifimanager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        Log.d(TAG, "Setup WIfiManager getSystemService");

        // if WIFIEnabled
        if (wifimanager.isWifiEnabled() == false)
            wifimanager.setWifiEnabled(true);

    }

    public void printToast(String messageToast) {
        Toast.makeText(this, messageToast, Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnScanStart) {
            Log.d(TAG, "OnClick() btnScanStart()");
            printToast("WIFI SCAN !!!");
            initWIFIScan(); // start WIFIScan
        }
        if (v.getId() == R.id.btnScanStop) {
            Log.d(TAG, "OnClick() btnScanStop()");
            printToast("WIFI STOP !!!");
            unregisterReceiver(mReceiver); // stop WIFISCan
        }
    }
}
