package com.tencent.ai.dobby;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tencent.ai.tvs.AuthorizeListener;
import com.tencent.ai.tvs.BindingListener;
import com.tencent.ai.tvs.LoginProxy;
import com.tencent.ai.tvs.env.ELocationType;
import com.tencent.ai.tvs.env.ELoginEnv;
import com.tencent.ai.tvs.env.ELoginPlatform;
import com.tencent.ai.tvs.info.BindManager;
import com.tencent.ai.tvs.info.DeviceManager;
import com.tencent.ai.tvs.info.LocManager;
import com.tencent.ai.tvs.info.LocationInfo;
import com.tencent.ai.tvs.info.PushInfoManager;
import com.tencent.ai.tvs.info.QQInfoManager;
import com.tencent.ai.tvs.info.QQOpenInfoManager;
import com.tencent.ai.tvs.info.UserInfoManager;
import com.tencent.ai.tvs.info.WxInfoManager;
import com.tencent.aisdk.udp.UDPClient;
import com.tencent.connect.common.Constants;

import oicq.wlogin_sdk.request.WtloginHelper;

public class MainActivity extends AppCompatActivity implements AuthorizeListener, BindingListener, UDPClient.Callback {


    private static final String appidWx = "wxd077c3460b51e427";
    private static final String appidQQOpen = "222222";
    private static final long appidQQ = 1600001268L;

    private LoginProxy proxy;

    // 内部使用
    private LoginProxy innerProxy;

    private WxInfoManager wxInfoManager;
    private QQOpenInfoManager qqOpenInfoManager;
    private QQInfoManager qqInfoManager;

    private RadioGroup netEnvRG;
    private RadioButton testEnvRB, formalEnvRB;

    private Button wxLoginBtn, wxLogoutBtn, wxUserCenterBtn;
    private Button qqOpenLoginBtn, qqOpenLogoutBtn, qqOpenUserCenterBtn;
    private Button qqLoginBtn, qqLogoutBtn, qqUserCenterBtn;

    private EditText getCaptchaEditText;
    private Button getCaptchaButton;
    private TextView getCaptchaTextView;

    private EditText bindPhoneNumberEditText;
    private Button bindPhoneNumberButton;
    private TextView bindPhoneNumberTextView;

    private Button bindLocationButton, queryLocationButton;
    private TextView bindLocationTextView;

    private LinearLayout queryLocationLayout;
    private TextView queryLocationTextView;

    private Button devicebindButton;
    private TextView devicebindTextView;

    private Button devicescanButton;
    private TextView devicescanTextView;

    private Button getMemberStatusButton;
    private TextView getMemberStatusTextView;

    private LinearLayout wxTokenLayout, qqopenTokenLayout, qqTokenLayout;
    private TextView wxATTextView, wxRTTextView, qqopenATTextView, qqATTextView;

    private DeviceManager deviceManager;

    private static final ELoginPlatform TEST_PLATFORM = ELoginPlatform.WX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewsById();

        registerProxy();

        requestProxyOp();

        showTokenInfo();

        netEnvRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (testEnvRB.getId() == checkedId) {
                    proxy.setLoginEnv(ELoginEnv.TEST);
                }
                else if (formalEnvRB.getId() == checkedId) {
                    proxy.setLoginEnv(ELoginEnv.FORMAL);
                }
            }
        });

        wxLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxy.requestLogin(ELoginPlatform.WX, "productId", "dsn", MainActivity.this);
            }
        });

        qqOpenLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxy.requestLogin(ELoginPlatform.QQOpen, "productId", "dsn", MainActivity.this);
            }
        });

        qqLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                innerProxy.requestLogin(ELoginPlatform.QQ, "productId", "dsn", MainActivity.this);
            }
        });

        wxLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxy.clearToken(ELoginPlatform.WX, MainActivity.this);
            }
        });

        qqOpenLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxy.clearToken(ELoginPlatform.QQOpen, MainActivity.this);
            }
        });

        qqLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                innerProxy.clearToken(ELoginPlatform.QQ, MainActivity.this);
            }
        });

        wxUserCenterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxy.toUserCenter(ELoginPlatform.WX, deviceManager);
            }
        });

        qqOpenUserCenterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxy.toUserCenter(ELoginPlatform.QQOpen, deviceManager);
            }
        });

        qqUserCenterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                innerProxy.toUserCenter(ELoginPlatform.QQ, deviceManager);
            }
        });

        getCaptchaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxy.requestGetCaptcha(TEST_PLATFORM, getCaptchaEditText.getText().toString());
            }
        });

        bindPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxy.requestBindPhoneNumber(TEST_PLATFORM, getCaptchaEditText.getText().toString(), bindPhoneNumberEditText.getText().toString());
            }
        });

        bindLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocManager locManager = LocManager.getInstance();
                LocationInfo homeLocationInfo = locManager.getLocation(ELocationType.HOME);
                homeLocationInfo.addr = "h_addr";
                homeLocationInfo.name = "h_name";
                homeLocationInfo.longitube = "h_longitube";
                homeLocationInfo.latitube = "h_latitube";
                homeLocationInfo.cabAddr = "h_caddr";
                homeLocationInfo.cabName = "h_cname";
                homeLocationInfo.cabLongitube = "h_clongitube";
                homeLocationInfo.cabLatitube = "h_clatitube";
                LocationInfo companyLocationInfo = locManager.getLocation(ELocationType.COMPANY);
                companyLocationInfo.addr = "c_addr";
                companyLocationInfo.name = "c_name";
                companyLocationInfo.longitube = "c_longitube";
                companyLocationInfo.latitube = "c_latitube";
                companyLocationInfo.cabAddr = "c_caddr";
                companyLocationInfo.cabName = "c_cname";
                companyLocationInfo.cabLongitube = "c_clongitube";
                companyLocationInfo.cabLatitube = "c_clatitube";
                if (proxy.isLocationOpValid(TEST_PLATFORM)) {
                    proxy.requestBindLocation(TEST_PLATFORM, homeLocationInfo, companyLocationInfo);
                }
            }
        });

        queryLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (proxy.isLocationOpValid(TEST_PLATFORM)) {
                    proxy.requestQueryLocation(TEST_PLATFORM);
                }
            }
        });

        devicebindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushInfoManager pushInfoManager = PushInfoManager.getInstance();
                DeviceManager testDevManager = new DeviceManager();
                pushInfoManager.id = "pushid";
                pushInfoManager.idExtra = "pushidextra";
                pushInfoManager.idType = 0;
                testDevManager.qua = "devqua";
                testDevManager.bindTime = 10L;
                testDevManager.guid = "devguid";
                testDevManager.imei = "devimei";
                testDevManager.license = "devlc";
                testDevManager.mac = "devmac";
                testDevManager.qimei = "devqimei";
                testDevManager.enrollTime = 20L;
                proxy.requestSetPushMapInfoEx(TEST_PLATFORM, pushInfoManager, testDevManager);
            }
        });

        devicescanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UDPClient.start(MainActivity.this);
            }
        });

        getMemberStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxy.getMemberStatus(TEST_PLATFORM, deviceManager);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            if (RESULT_OK == resultCode) {
                proxy.handleQQOpenIntent(requestCode, resultCode, data);
            }
            else if (RESULT_CANCELED == resultCode){
            }
        }
        else if (requestCode == WtloginHelper.QuickLoginRequestCode.REQUEST_QQ_LOGIN
                || requestCode == WtloginHelper.QuickLoginRequestCode.REQUEST_PT_LOGIN) {
            if (RESULT_OK == resultCode) {
                innerProxy.handleQQIntent(data);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSuccess(int type) {
        switch (type)
        {
            case AuthorizeListener.AUTH_TYPE:
                break;
            case AuthorizeListener.REFRESH_TYPE:
                wxLoginBtn.setEnabled(false);
                break;
            case AuthorizeListener.WX_TVSIDRECV_TYPE:
                wxLoginBtn.setEnabled(false);
                break;
            case AuthorizeListener.QQOPEN_TVSIDRECV_TYPE:
                qqOpenLoginBtn.setEnabled(false);
                break;
            case AuthorizeListener.QQ_TVSIDRECV_TYPE:
                qqLoginBtn.setEnabled(false);
                break;
            case AuthorizeListener.TOKENVERIFY_TYPE:
                qqOpenLoginBtn.setEnabled(false);
                qqLoginBtn.setEnabled(false);
                break;
            case AuthorizeListener.USERINFORECV_TYPE:
                UserInfoManager mgr = UserInfoManager.getInstance();
                break;
            case BindingListener.GET_CAPTCHA_TYPE:
                getCaptchaTextView.setText("Captcha Send Success");
                break;
            case BindingListener.BIND_PHONENUMBER_TYPE:
                bindPhoneNumberTextView.setText("Bind Success");
                break;
            case BindingListener.BIND_LOCATION_TYPE:
                bindLocationTextView.setText("Bind Success");
                break;
            case BindingListener.QUERY_LOCATION_TYPE:
                LocationInfo homeInfo = LocManager.getInstance().getLocation(ELocationType.HOME);
                LocationInfo companyInfo = LocManager.getInstance().getLocation(ELocationType.COMPANY);
                queryLocationLayout.setVisibility(View.VISIBLE);
                queryLocationTextView.setText("HomeInfo:"+homeInfo.addr+"|"+homeInfo.name+"|"+homeInfo.latitube+"|"+homeInfo.longitube
                                                            + "|" +homeInfo.cabAddr+"|"+homeInfo.cabName+"|"+homeInfo.cabLatitube+"|"+homeInfo.cabLongitube
                                                                +"\nCompanyInfo:"+companyInfo.addr+"|"+companyInfo.name+"|"+companyInfo.latitube+"|"+companyInfo.longitube
                                                                    + "|" +companyInfo.cabAddr+"|"+companyInfo.cabName+"|"+companyInfo.cabLatitube+"|"+companyInfo.cabLongitube);
                break;
            case BindingListener.BIND_DEVICE_TYPE:
                devicebindTextView.setText("Bind Success");
                break;
            case BindingListener.BIND_GET_MEMBER_STATUS_TYPE:
                getMemberStatusTextView.setText("Get Member Status Success");
                break;
        }
    }

    @Override
    public void onError(int type) {
        switch (type)
        {
            case AuthorizeListener.AUTH_TYPE:
                break;
            case AuthorizeListener.REFRESH_TYPE:
                wxLoginBtn.setEnabled(true);
                break;
            case AuthorizeListener.WX_TVSIDRECV_TYPE:
                wxLoginBtn.setEnabled(true);
                break;
            case AuthorizeListener.QQOPEN_TVSIDRECV_TYPE:
                qqOpenLoginBtn.setEnabled(true);
                break;
            case AuthorizeListener.QQ_TVSIDRECV_TYPE:
                qqLoginBtn.setEnabled(true);
                break;
            case AuthorizeListener.TOKENVERIFY_TYPE:
                qqOpenLoginBtn.setEnabled(true);
                qqLoginBtn.setEnabled(true);
                break;
            case BindingListener.GET_CAPTCHA_TYPE:
                getCaptchaTextView.setText("Captcha Send Error");
                break;
            case BindingListener.BIND_PHONENUMBER_TYPE:
                bindPhoneNumberTextView.setText("Bind Error: wrong captcha");
                break;
            case BindingListener.BIND_LOCATION_TYPE:
                bindLocationTextView.setText("Bind Error");
                break;
            case BindingListener.QUERY_LOCATION_TYPE:
                queryLocationLayout.setVisibility(View.VISIBLE);
                queryLocationTextView.setText("Query Error");
                break;
            case BindingListener.BIND_DEVICE_TYPE:
                devicebindTextView.setText("Bind Error");
                break;
            case BindingListener.BIND_GET_MEMBER_STATUS_TYPE:
                getMemberStatusTextView.setText("Get Member Status Error");
                break;
        }
    }

    @Override
    public void onCancel(int type) {
        switch (type) {
            case AuthorizeListener.AUTH_TYPE:
                break;
        }
    }

    @Override
    public void onUDPResponse(String ip, int req, String data) {
        if (req == UDPClient.UDP_CLIENT_STARTED) {
            UDPClient.requestAll(UDPClient.REQ_DEVICE_INFO);
        }
        else if (req == UDPClient.REQ_DEVICE_INFO) {
            deviceManager = new DeviceManager(ip, data);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    devicescanTextView.setText(deviceManager.toSimpleString());
                }
            });
        }
        else if (req == UDPClient.SET_ACCOUNT_INFO) {
            BindManager.getInstance().bind(ip, "", data, new BindManager.Callback() {
                @Override
                public void onSuccess(String msg) {

                }

                @Override
                public void onFail(String msg) {

                }
            });
        }
    }

    private void findViewsById() {
        netEnvRG = (RadioGroup) findViewById(R.id.netenvrg);
        testEnvRB = (RadioButton) findViewById(R.id.testenvrb);
        formalEnvRB = (RadioButton) findViewById(R.id.formalenvrb);

        wxLoginBtn = (Button)findViewById(R.id.wxlogin);
        wxLogoutBtn = (Button)findViewById(R.id.wxlogout);
        wxUserCenterBtn = (Button) findViewById(R.id.wxjumpusercenterbtn);

        qqOpenLoginBtn = (Button)findViewById(R.id.qqopenlogin);
        qqOpenLogoutBtn = (Button)findViewById(R.id.qqopenlogout);
        qqOpenUserCenterBtn = (Button) findViewById(R.id.qqopenjumpusercenterbtn);

        qqLoginBtn = (Button)findViewById(R.id.qqlogin);
        qqLogoutBtn = (Button)findViewById(R.id.qqlogout);
        qqUserCenterBtn = (Button)findViewById(R.id.qqumpusercenterbtn);

        getCaptchaEditText = (EditText) findViewById(R.id.getcaptchaedittext);
        getCaptchaButton = (Button) findViewById(R.id.getcaptchabutton);
        getCaptchaTextView = (TextView) findViewById(R.id.getcaptchatextview);

        bindPhoneNumberEditText = (EditText) findViewById(R.id.bindnumberedittext);
        bindPhoneNumberButton = (Button) findViewById(R.id.bindnumberbutton);
        bindPhoneNumberTextView = (TextView) findViewById(R.id.bindnumbertextview);

        bindLocationButton = (Button) findViewById(R.id.bindlocation);
        queryLocationButton = (Button) findViewById(R.id.querylocation);
        bindLocationTextView = (TextView) findViewById(R.id.bindlocationtextview);

        queryLocationLayout = (LinearLayout) findViewById(R.id.querylocationlayout);
        queryLocationTextView = (TextView) findViewById(R.id.querylocationtextview);

        devicebindButton = (Button) findViewById(R.id.devicebindbtn);
        devicebindTextView = (TextView) findViewById(R.id.devicebindbtntextview);

        devicescanButton = (Button) findViewById(R.id.devicescanbtn);
        devicescanTextView = (TextView) findViewById(R.id.devicescantext);

        getMemberStatusButton = (Button) findViewById(R.id.getmemberstatusbtn);
        getMemberStatusTextView = (TextView) findViewById(R.id.getmemberstatustext);

        wxTokenLayout = (LinearLayout) findViewById(R.id.wxtokenlayout);
        wxATTextView = (TextView)findViewById(R.id.accesstokenid);
        wxRTTextView = (TextView)findViewById(R.id.refreshtokenid);

        qqopenTokenLayout = (LinearLayout) findViewById(R.id.qqopentokenlayout);
        qqopenATTextView = (TextView)findViewById(R.id.accesstokenidqqopen);

        qqTokenLayout = (LinearLayout) findViewById(R.id.qqtokenlayout);
        qqATTextView = (TextView) findViewById(R.id.accesstokenidqq);
    }

    private void registerProxy() {
        proxy = LoginProxy.getInstance(appidWx, appidQQOpen);
        innerProxy = LoginProxy.getInstance(appidWx, appidQQ);

        /*-------------------------------------Use CustomApplication Context Start-------------------------------------*/
//      proxy = LoginProxy.getInstance(appidWx, appidQQOpen, getApplicationContext());
//      innerProxy = LoginProxy.getInstance(appidWx, appidQQ, getApplicationContext());
        /*-------------------------------------Use CustomApplication Context End-------------------------------------*/

        wxInfoManager = (WxInfoManager) proxy.getInfoManager(ELoginPlatform.WX);
        qqOpenInfoManager = (QQOpenInfoManager) proxy.getInfoManager(ELoginPlatform.QQOpen);
        qqInfoManager = (QQInfoManager) innerProxy.getInfoManager(ELoginPlatform.QQ);

        proxy.setOwnActivity(this);
        proxy.setAuthorizeListener(this);
        proxy.setBindingListener(this);

        innerProxy.setOwnActivity(this);
        innerProxy.setAuthorizeListener(this);
        innerProxy.setBindingListener(this);
    }

    private void requestProxyOp() {
        if (proxy.isTokenExist(ELoginPlatform.WX, this)) {
            proxy.requestTokenVerify(ELoginPlatform.WX, "productId", "dsn");
        }
        else {
            wxLoginBtn.setEnabled(true);
        }

        if (proxy.isTokenExist(ELoginPlatform.QQOpen, this)) {
            proxy.requestTokenVerify(ELoginPlatform.QQOpen, "productId", "dsn");
        }
        else {
            qqOpenLoginBtn.setEnabled(true);
        }

        if (innerProxy.isTokenExist(ELoginPlatform.QQ, this)) {
            innerProxy.requestTokenVerify(ELoginPlatform.QQ, "productId", "dsn");
        }
        else {
            qqLoginBtn.setEnabled(true);
        }
    }

    private void showTokenInfo() {
        if (!"".equals(wxInfoManager.accessToken)) {
            wxTokenLayout.setVisibility(View.VISIBLE);
            wxATTextView.setText("AccessToken:" + wxInfoManager.accessToken);
        }

        if (!"".equals(wxInfoManager.refreshToken)) {
            wxTokenLayout.setVisibility(View.VISIBLE);
            wxRTTextView.setText("RefreshToken:" + wxInfoManager.refreshToken);
        }

        if (!"".equals(qqOpenInfoManager.accessToken)) {
            qqopenTokenLayout.setVisibility(View.VISIBLE);
            qqopenATTextView.setText("AccessToken:" + qqOpenInfoManager.accessToken);
        }

        if (!"".equals(qqInfoManager.accessToken)) {
            qqTokenLayout.setVisibility(View.VISIBLE);
            qqATTextView.setText("AccessToken: " + qqInfoManager.accessToken);
        }
    }
}
