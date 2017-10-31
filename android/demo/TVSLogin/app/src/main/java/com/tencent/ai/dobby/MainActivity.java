package com.tencent.ai.dobby;

import android.content.Intent;
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
import com.tencent.ai.tvs.info.LocManager;
import com.tencent.ai.tvs.info.LocationInfo;
import com.tencent.ai.tvs.info.QQOpenInfoManager;
import com.tencent.ai.tvs.info.UserInfoManager;
import com.tencent.ai.tvs.info.WxInfoManager;
import com.tencent.connect.common.Constants;

public class MainActivity extends AppCompatActivity implements AuthorizeListener, BindingListener {


    private static final String appidWx = "wxd077c3460b51e427";
    private static final String appidQQOpen = "222222";

    private LoginProxy proxy;

    private WxInfoManager wxInfoManager;
    private QQOpenInfoManager qqOpenInfoManager;

    private RadioGroup netEnvRG;
    private RadioButton testEnvRB, formalEnvRB;

    private Button wxLoginBtn, wxLogoutBtn, wxUserCenterBtn;
    private Button qqOpenLoginBtn, qqOpenLogoutBtn, qqOpenUserCenterBtn;

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

    private LinearLayout wxTokenLayout, qqopenTokenLayout;
    private TextView wxATTextView, wxRTTextView, qqopenATTextView;

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

        wxUserCenterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxy.toUserCenter(ELoginPlatform.WX);
            }
        });

        qqOpenUserCenterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxy.toUserCenter(ELoginPlatform.QQOpen);
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
                LocationInfo companyLocationInfo = locManager.getLocation(ELocationType.COMPANY);
                companyLocationInfo.addr = "c_addr";
                companyLocationInfo.name = "c_name";
                companyLocationInfo.longitube = "c_longitube";
                companyLocationInfo.latitube = "c_latitube";
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            if (resultCode == -1) {
                proxy.handleQQOpenIntent(requestCode, resultCode, data);
            }
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
            case AuthorizeListener.TOKENVERIFY_TYPE:
                qqOpenLoginBtn.setEnabled(false);
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
                        +"\nCompanyInfo:"+companyInfo.addr+"|"+companyInfo.name+"|"+companyInfo.latitube+"|"+companyInfo.longitube);
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
            case AuthorizeListener.TOKENVERIFY_TYPE:
                qqOpenLoginBtn.setEnabled(true);
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

        wxTokenLayout = (LinearLayout) findViewById(R.id.wxtokenlayout);
        wxATTextView = (TextView)findViewById(R.id.accesstokenid);
        wxRTTextView = (TextView)findViewById(R.id.refreshtokenid);

        qqopenTokenLayout = (LinearLayout) findViewById(R.id.qqopentokenlayout);
        qqopenATTextView = (TextView)findViewById(R.id.accesstokenidqqopen);
    }

    private void registerProxy() {
        proxy = LoginProxy.getInstance(appidWx, appidQQOpen);

        wxInfoManager = (WxInfoManager) proxy.getInfoManager(ELoginPlatform.WX);
        qqOpenInfoManager = (QQOpenInfoManager) proxy.getInfoManager(ELoginPlatform.QQOpen);

        proxy.setOwnActivity(this);
        proxy.setAuthorizeListener(this);
        proxy.setBindingListener(this);
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
    }
}