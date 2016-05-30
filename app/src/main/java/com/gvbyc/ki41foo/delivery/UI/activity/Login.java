package com.gvbyc.ki41foo.delivery.UI.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.UserAccountManager;
import com.gvbyc.ki41foo.delivery.model.UserInfo;
import com.gvbyc.ki41foo.delivery.protocal.BaseProtocol;
import com.gvbyc.ki41foo.delivery.protocal.Http;
import com.gvbyc.ki41foo.delivery.utils.IntentUtils;
import com.gvbyc.ki41foo.delivery.utils.PackageUtils;
import com.gvbyc.ki41foo.delivery.utils.T;
import com.gvbyc.ki41foo.delivery.utils.Utils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.HashMap;

/**
 * Created by goodview on 31/03/16.
 */
public class Login extends BaseActivity {


    private AppCompatEditText et_phone;
    private AppCompatEditText et_password;
    private Button btn_prefix;
    private String phone;
    private String pwd;
    private String[] prefixes = new String[] {"澳門(+853)", "大陸(+86)", "香港(+852)"};
    private int whichPrefix = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        setUpInput();
    }

    private void setUpInput() {
        et_phone = (AppCompatEditText) findViewById(R.id.phone);
        btn_prefix = (Button) findViewById(R.id.prefix);
        btn_prefix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefixSelectDialog();
            }
        });
        et_password = (AppCompatEditText) findViewById(R.id.password);
        et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    onLoginClicked();
                }
                return false;
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClicked();
            }
        });

        Utils.showSoftInput(et_phone);

        Button tv_signUp = (Button) findViewById(R.id.signup);
        IconicsDrawable icon = new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_navigate_next)
                .color(Color.WHITE)
                .sizeDp(14);
        tv_signUp.setCompoundDrawablesWithIntrinsicBounds(null,null,icon,null);

        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,SignUp.class));
                overridePendingTransition(R.anim.right_in,R.anim.forward_out);
            }
        });

        findViewById(R.id.forgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.startActivity(Login.this,SignUp.class, "mode", SignUp.RESET_PWD);
                overridePendingTransition(R.anim.right_in,R.anim.forward_out);
            }
        });

    }

    private void prefixSelectDialog() {
        new AlertDialog.Builder(Login.this, R.style.AppCompatAlertDialogStyle)
                .setTitle("電話所在地區")
                .setSingleChoiceItems(prefixes, whichPrefix, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btn_prefix.setText(prefixes[which]);
                        whichPrefix = which;
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private boolean checkNull() {
        phone = et_phone.getText().toString().trim();
        pwd = et_password.getText().toString().trim();
        if(TextUtils.isEmpty(phone)) {
            T.showShort("用戶名不能空");
            return false;
        }

        if(TextUtils.isEmpty(pwd)) {
            T.showShort("密碼不能空");
            return false;
        }
        return true;
    }

    private void onLoginClicked() {
        if(checkNull()) {

            HashMap<String, String> map = new HashMap<>();
            map.put("prefix",whichPrefix+ 1 +"");
            map.put("phone",phone);
            map.put("pwd", Utils.encryptPwd(pwd));
            map.put("ver", PackageUtils.getVersionName());

            Http.post("delivery/user/signin", map, new Http.Listener() {
                @Override
                public void onResponse(String result) {
                    String msg = BaseProtocol.getMsg(result);
                    if(msg.equals("msg08")) {
                        T.showShort("登錄成功");
                        UserInfo userInfo = new Gson().fromJson(BaseProtocol.getReturnObject(result).get("object").getAsJsonObject(), UserInfo.class);
                        finish();
                        UserAccountManager.getInstance().logIn(userInfo);
                    }

                    if(msg.equals("msg07")|| msg.equals("msg04")) {
                        T.showShort("帳號密碼不正確");
                    }
                }
            });
        }
    }
}
