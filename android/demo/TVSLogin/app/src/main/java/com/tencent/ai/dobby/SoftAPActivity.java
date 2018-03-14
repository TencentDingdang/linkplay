package com.tencent.ai.dobby;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.ai.tvs.info.ProductManager;
import com.tencent.ai.tvsdevice.softap.ApNetworkConfigClient;
import com.tencent.ai.tvsdevice.softap.ApNetworkConfigListener;
import com.tencent.ai.tvsdevice.softap.ProductInfoListener;
import com.tencent.ai.tvsdevice.softap.SoftAPConnListener;
import com.tencent.ai.tvsdevice.softap.WifiConnManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SoftAPActivity extends AppCompatActivity {

    private static final int WIFI_PERMISSION_REQUEST_CODE = 101;

    private static final int MSG_WAIT_FOR_AP_CONNECTED = 0;
    private static final int MSG_CONFIG_NETWORK_FOR_REMOTE_DEVICE = 1;
    private static final int MSG_CONFIG_NETWORK_COMPLETED = 2;

    private static final int MSG_SEND_ING_START_TYPE = 1000;
    private static final int MSG_RECV_PLATFORM_TYPE = 1001;
    private static final int MSG_SEND_APINFO_TYPE = 1002;
    private static final int MSG_RECV_PRODUCTINFO_TYPE = 1003;
    private static final int MSG_SEND_CLIENTID_TYPE = 1004;
    private static final int MSG_RECV_ING_TYPE = 1005;
    private static final int MSG_SEND_ING_END_TYPE = 1006;

    private static final int SOFTAP_RESULT = 2000;
    private static final int SOFTAP_RESULT_SUCCESS = 2001;
    private static final int SOFTAP_RESULT_ERROR = 2002;

    private WifiConnManager mConnManager;
    private ApNetworkConfigClient mApNetworkConfigClient;

    private ListView mWifiListView;
    private EditText mWifiSsidView, mWifiPskView;
    private SwipeRefreshLayout mRefreshView;
    private ProgressBar mAPConnProgress;

    private ArrayList<String> mWifiList;
    private ArrayAdapter<String> mAdapter;

    private NetworkConfigHandler mHandler;
    private String mCurrentNetworkSSID, mTargetApNetworkSSID, mConfigNetworkSSID, mConfigNetworkPSK;
    private boolean mIsApConnected;

    private int mWaitCount = 10;

    private ELoginPlatform platform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.softap_main);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            int loginPlatform = bundle.getInt("loginplatform");
            if (ELoginPlatform.WX.ordinal() == loginPlatform) {
                platform = ELoginPlatform.WX;
            }
            else if (ELoginPlatform.QQOpen.ordinal() == loginPlatform) {
                platform = ELoginPlatform.QQOpen;
            }
        }

        mWifiList = new ArrayList<String>();

        mWifiListView = (ListView) findViewById(R.id.wifi_list);
        mWifiSsidView = (EditText) findViewById(R.id.ssid);
        mWifiPskView  = (EditText) findViewById(R.id.psk);
        mAPConnProgress = (ProgressBar) findViewById(R.id.apConnProgress);
        mRefreshView = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateWifiList();
            }
        });

        mHandler = new NetworkConfigHandler();
        mConnManager = new WifiConnManager(this);
        mApNetworkConfigClient = new ApNetworkConfigClient();

        mWifiListView.setOnItemClickListener(mItemClickListener);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mWifiList);
        mWifiListView.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.INTERNET}, WIFI_PERMISSION_REQUEST_CODE);
        } else {
            initWifiList();
        }

        updateCurrentNetworkSSID();
        mWifiSsidView.setText(mCurrentNetworkSSID);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
    }

    private void updateCurrentNetworkSSID() {
        WifiInfo info = mConnManager.getWifiInfo();
        String curSsid = "";
        if (info != null) {
            curSsid = info.getSSID().replace("\"", "");
            if (curSsid.equals("<unknown ssid>")) {
                curSsid = "";
            }
        }
        mCurrentNetworkSSID = curSsid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void initWifiList() {
        if (mConnManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            mConnManager.openWifi();
        } else {
            updateWifiList();
        }
    }

    private void updateWifiList() {
        new AsyncTask<Void, Void, List<ScanResult>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!mRefreshView.isRefreshing())
                    mRefreshView.setRefreshing(true);
            }

            @Override
            protected List<ScanResult> doInBackground(Void... voids) {
                mConnManager.startScan();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return mConnManager.getWifiList();
            }

            @Override
            protected void onPostExecute(List<ScanResult> scanResults) {
                super.onPostExecute(scanResults);
                if (scanResults == null)
                    return;

                mWifiList.clear();
                for (ScanResult scanRst : scanResults) {
                    if (scanRst.SSID != null && !scanRst.SSID.equals("")) {
                        mWifiList.add(scanRst.SSID);
                    }
                }
                mAdapter.notifyDataSetChanged();

                if (mRefreshView.isRefreshing())
                    mRefreshView.setRefreshing(false);
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WIFI_PERMISSION_REQUEST_CODE) {
            initWifiList();
        }
    }

    private class NetworkConfigHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WAIT_FOR_AP_CONNECTED:
                    mAPConnProgress.setVisibility(View.VISIBLE);
                    if (!mIsApConnected) {
                        if (mWaitCount-- > 0) {
                            sendEmptyMessageDelayed(MSG_WAIT_FOR_AP_CONNECTED, 1000);
                        } else {
                            // 配网超时
                            obtainMessage(MSG_CONFIG_NETWORK_COMPLETED, ApNetworkConfigClient.
                                    RESULT_CODE_FAILED_BY_CONNECT_TO_AP_TIMEOUT, 0).sendToTarget();
                        }
                        return;
                    }
                    // 开始配网
                    sendEmptyMessage(MSG_CONFIG_NETWORK_FOR_REMOTE_DEVICE);
                    break;
                case MSG_CONFIG_NETWORK_FOR_REMOTE_DEVICE:
                    mApNetworkConfigClient.configNetworkForRemoteDevice(mConfigNetworkSSID, mConfigNetworkPSK, mApNetworkConfigListener, mSoftAPConnListener, mProductInfoListener);
                    break;
                case MSG_CONFIG_NETWORK_COMPLETED:
                    mAPConnProgress.setVisibility(View.GONE);
                    if (msg.arg1 == ApNetworkConfigClient.RESULT_CODE_SUCCESS) {
                        HashMap<String, String> data = (HashMap<String, String>) msg.obj;
                        String pid = data.get("PID");
                        String dsn = data.get("DSN");
                        Toast.makeText(SoftAPActivity.this, "Config AP Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SoftAPActivity.this, "Config AP Fail: " + msg.arg1, Toast.LENGTH_SHORT).show();
                    }
                    if (!("").equals(mCurrentNetworkSSID)) {
                        boolean ret = mConnManager.connectConfiguration("\"" + mCurrentNetworkSSID + "\"");
                    }
                    break;
                case MSG_SEND_ING_START_TYPE:
                    if (msg.arg1 == SOFTAP_RESULT) {
                        if (msg.arg2 == SOFTAP_RESULT_SUCCESS) {
                            Toast.makeText(SoftAPActivity.this, "MSG_SEND_ING_START_TYPE SUCCESS", Toast.LENGTH_SHORT).show();
                        }
                        else if (msg.arg2 == SOFTAP_RESULT_ERROR){
                            Toast.makeText(SoftAPActivity.this, "MSG_SEND_ING_START_TYPE ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case MSG_RECV_PLATFORM_TYPE:
                    if (msg.arg1 == SOFTAP_RESULT) {
                        if (msg.arg2 == SOFTAP_RESULT_SUCCESS) {
                            Toast.makeText(SoftAPActivity.this, "MSG_RECV_PLATFORM_TYPE SUCCESS", Toast.LENGTH_SHORT).show();
                        }
                        else if (msg.arg2 == SOFTAP_RESULT_ERROR){
                            Toast.makeText(SoftAPActivity.this, "MSG_RECV_PLATFORM_TYPE ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case MSG_SEND_APINFO_TYPE:
                    if (msg.arg1 == SOFTAP_RESULT) {
                        if (msg.arg2 == SOFTAP_RESULT_SUCCESS) {
                            Toast.makeText(SoftAPActivity.this, "MSG_SEND_APINFO_TYPE SUCCESS", Toast.LENGTH_SHORT).show();
                        }
                        else if (msg.arg2 == SOFTAP_RESULT_ERROR){
                            Toast.makeText(SoftAPActivity.this, "MSG_SEND_APINFO_TYPE ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case MSG_RECV_PRODUCTINFO_TYPE:
                    if (msg.arg1 == SOFTAP_RESULT) {
                        if (msg.arg2 == SOFTAP_RESULT_SUCCESS) {
                            Toast.makeText(SoftAPActivity.this, "MSG_RECV_PRODUCTINFO_TYPE SUCCESS", Toast.LENGTH_SHORT).show();
                        }
                        else if (msg.arg2 == SOFTAP_RESULT_ERROR){
                            Toast.makeText(SoftAPActivity.this, "MSG_RECV_PRODUCTINFO_TYPE ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case MSG_SEND_CLIENTID_TYPE:
                    if (msg.arg1 == SOFTAP_RESULT) {
                        if (msg.arg2 == SOFTAP_RESULT_SUCCESS) {
                            Toast.makeText(SoftAPActivity.this, "MSG_SEND_CLIENTID_TYPE SUCCESS", Toast.LENGTH_SHORT).show();
                        }
                        else if (msg.arg2 == SOFTAP_RESULT_ERROR){
                            Toast.makeText(SoftAPActivity.this, "MSG_SEND_CLIENTID_TYPE ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case MSG_RECV_ING_TYPE:
                    if (msg.arg1 == SOFTAP_RESULT) {
                        if (msg.arg2 == SOFTAP_RESULT_SUCCESS) {
                            Toast.makeText(SoftAPActivity.this, "MSG_RECV_ING_TYPE SUCCESS", Toast.LENGTH_SHORT).show();
                        }
                        else if (msg.arg2 == SOFTAP_RESULT_ERROR){
                            Toast.makeText(SoftAPActivity.this, "MSG_RECV_ING_TYPE ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case MSG_SEND_ING_END_TYPE:
                    if (msg.arg1 == SOFTAP_RESULT) {
                        if (msg.arg2 == SOFTAP_RESULT_SUCCESS) {
                            Toast.makeText(SoftAPActivity.this, "MSG_SEND_ING_END_TYPE SUCCESS", Toast.LENGTH_SHORT).show();
                        }
                        else if (msg.arg2 == SOFTAP_RESULT_ERROR){
                            Toast.makeText(SoftAPActivity.this, "MSG_SEND_ING_END_TYPE ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }

    private ProductInfoListener mProductInfoListener = new ProductInfoListener() {
        @Override
        public String getProductInfo(String productId, String dsn) {
            ProductManager.getInstance().productId = productId;
            ProductManager.getInstance().dsn = dsn;
            String clientId = LoginProxy.getInstance("", "", SoftAPActivity.this).getClientId(platform);
            return clientId;
        }
    };

    private ApNetworkConfigListener mApNetworkConfigListener = new ApNetworkConfigListener() {
        @Override
        public void onApNetworkConfigResult(int resultCode, HashMap<String, String> data) {
            mHandler.obtainMessage(MSG_CONFIG_NETWORK_COMPLETED, resultCode, 0, data).sendToTarget();
        }
    };

    private SoftAPConnListener mSoftAPConnListener = new SoftAPConnListener() {
        @Override
        public void onSuccess(int type) {
            switch (type) {
                case SoftAPConnListener.SEND_ING_START_TYPE:
                    mHandler.obtainMessage(MSG_SEND_ING_START_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_SUCCESS).sendToTarget();
                    break;
                case SoftAPConnListener.RECV_PLATFORM_TYPE:
                    mHandler.obtainMessage(MSG_RECV_PLATFORM_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_SUCCESS).sendToTarget();
                    break;
                case SoftAPConnListener.SEND_APINFO_TYPE:
                    mHandler.obtainMessage(MSG_SEND_APINFO_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_SUCCESS).sendToTarget();
                    break;
                case SoftAPConnListener.RECV_PRODUCTINFO_TYPE:
                    mHandler.obtainMessage(MSG_RECV_PRODUCTINFO_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_SUCCESS).sendToTarget();
                    break;
                case SoftAPConnListener.SEND_CLIENTID_TYPE:
                    mHandler.obtainMessage(MSG_SEND_CLIENTID_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_SUCCESS).sendToTarget();
                    break;
                case SoftAPConnListener.RECV_ING_TYPE:
                    mHandler.obtainMessage(MSG_RECV_ING_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_SUCCESS).sendToTarget();
                    break;
                case SoftAPConnListener.SEND_ING_END_TYPE:
                    mHandler.obtainMessage(MSG_SEND_ING_END_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_SUCCESS).sendToTarget();
                    break;
            }
        }

        @Override
        public void onError(int type) {
            switch (type) {
                case SoftAPConnListener.SEND_ING_START_TYPE:
                    mHandler.obtainMessage(MSG_SEND_ING_START_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_ERROR).sendToTarget();
                    break;
                case SoftAPConnListener.RECV_PLATFORM_TYPE:
                    mHandler.obtainMessage(MSG_RECV_PLATFORM_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_ERROR).sendToTarget();
                    break;
                case SoftAPConnListener.SEND_APINFO_TYPE:
                    mHandler.obtainMessage(MSG_SEND_APINFO_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_ERROR).sendToTarget();
                    break;
                case SoftAPConnListener.RECV_PRODUCTINFO_TYPE:
                    mHandler.obtainMessage(MSG_RECV_PRODUCTINFO_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_ERROR).sendToTarget();
                    break;
                case SoftAPConnListener.SEND_CLIENTID_TYPE:
                    mHandler.obtainMessage(MSG_SEND_CLIENTID_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_ERROR).sendToTarget();
                    break;
                case SoftAPConnListener.RECV_ING_TYPE:
                    mHandler.obtainMessage(MSG_RECV_ING_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_ERROR).sendToTarget();
                    break;
                case SoftAPConnListener.SEND_ING_END_TYPE:
                    mHandler.obtainMessage(MSG_SEND_ING_END_TYPE, SOFTAP_RESULT, SOFTAP_RESULT_ERROR).sendToTarget();
                    break;
            }
        }
    };

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position >= mWifiList.size())
                return;

            String apSsid = mWifiList.get(position);
            String ssid = mWifiSsidView.getText().toString();
            String psk = mWifiPskView.getText().toString();

            updateCurrentNetworkSSID();
            mIsApConnected = false;

            WifiConfiguration configuration = mConnManager.createWifiInfo(apSsid, "", 1);
            boolean ret = mConnManager.addNetwork(configuration);
            if (!ret) {
                mHandler.obtainMessage(MSG_CONFIG_NETWORK_COMPLETED, ApNetworkConfigClient.RESULT_CODE_FAILED_BY_CAN_NOT_CONNECT_TO_AP, 0).sendToTarget();
                return;
            }

            mTargetApNetworkSSID = apSsid;
            mConfigNetworkSSID = ssid;
            mConfigNetworkPSK = psk;
            mWaitCount = 10;
            mHandler.sendEmptyMessage(MSG_WAIT_FOR_AP_CONNECTED);
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                if (state == WifiManager.WIFI_STATE_ENABLED) {
                    updateWifiList();
                }
            }
            else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (!mIsApConnected && networkInfo != null && networkInfo.isConnected()
                        && ("\"" + mTargetApNetworkSSID + "\"").equals(networkInfo.getExtraInfo())) {
                    mIsApConnected = true;
                }
            }
        }
    };
}
