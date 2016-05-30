package com.gvbyc.ki41foo.delivery.UI.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gvbyc.ki41foo.delivery.AppManager;
import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.TextDialog;
import com.gvbyc.ki41foo.delivery.UserAccountManager;
import com.gvbyc.ki41foo.delivery.model.UserInfo;
import com.gvbyc.ki41foo.delivery.plivo.PlivoHelper;
import com.gvbyc.ki41foo.delivery.protocal.BaseProtocol;
import com.gvbyc.ki41foo.delivery.protocal.Http;
import com.gvbyc.ki41foo.delivery.utils.PackageUtils;
import com.gvbyc.ki41foo.delivery.utils.T;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;
import com.gvbyc.ki41foo.delivery.utils.Utils;

import java.util.HashMap;

/**
 * Created by goodview on 31/03/16.
 */
public class SignUp extends BaseActivity {

    public static final int SIGN_UP = 0;
    public static final int RESET_PWD = 1;

    private int mode = SIGN_UP;

    private AppCompatEditText et_phone;
    private AppCompatEditText et_code;
    private Button btn_prefix;
    private Button btn_verify;
    private String[] prefixes = new String[] {"澳門(+853)", "大陸(+86)", "香港(+852)"};
    private int whichPrefix = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        mode = getIntent().getIntExtra("mode", SIGN_UP);
        if (this.mode == RESET_PWD) {
            ((TextView) findViewById(R.id.title)).setText("重置密碼");
        }

        initView();
    }

    private void initView() {
        et_phone = (AppCompatEditText) findViewById(R.id.phone);

        btn_prefix = (Button) findViewById(R.id.prefix);
        btn_prefix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefixSelectDialog();
            }
        });
        et_code = (AppCompatEditText) findViewById(R.id.et_code);
        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String trim = s.toString().trim();
                if (trim.length() == 4) {
                    boolean b = new PlivoHelper().verifyCode(trim);
                    if(b) {
                        showPwdDialog();

                    } else {
                        T.showShort("驗證碼錯誤");
                    }

                }
            }
        });



        btn_verify = (Button) findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String trim = et_phone.getText().toString().trim();
                if(trim.length() < 8) {
                    T.showShort("電話號碼太短");
                    return;
                }
                new PlivoHelper().send(UIUtils.getStringArray(R.array.prefixes)[whichPrefix] + trim);
                btn_verify.setEnabled(false);
                et_phone.setEnabled(false);
                btn_prefix.setEnabled(false);
                btn_verify.setText("60秒後可重新發送");
                btn_verify.setTextColor(UIUtils.getColor(R.color.secondary_text));
                Utils.showSoftInput(et_code);
                btn_verify.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_verify.setEnabled(true);
                        btn_verify.setText("發送短信驗證碼");
                        btn_verify.setTextColor(Color.WHITE);
                        et_phone.setEnabled(true);
                        btn_prefix.setEnabled(true);
                    }
                },60000);
            }
        });

        findViewById(R.id.protocol).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TextDialog(SignUp.this,TextDialog.PROTOCOL);
            }
        });
    }

    private void showPwdDialog() {
        View inflate = View.inflate(this, R.layout.set_pwd, null);
        final AppCompatEditText et_1 = (AppCompatEditText)inflate.findViewById(R.id.et_1);
        final AppCompatEditText et_2 = (AppCompatEditText)inflate.findViewById(R.id.et_2);
        int padding = UIUtils.getDimens(R.dimen.abc_dialog_padding_material);
        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                .setTitle("驗證成功，請設置密碼")
                .setPositiveButton("確定", null).setNegativeButton("取消註冊", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                }).setView(inflate, padding, padding, padding, 0)
                .setCancelable(false)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface d) {
                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String p1 = et_1.getText().toString().trim();
                        String p2 = et_2.getText().toString().trim();
                        if (TextUtils.isEmpty(p1) || TextUtils.isEmpty(p2)) {
                            T.showShort("密碼不能空");
                            return;
                        }

                        if(p1.length() < 8 || p2.length() < 8) {
                            T.showShort("密碼至少要8位");
                            return;
                        }

                        if (!p1.equals(p2)) {
                            T.showShort("兩次輸入的密碼不相同");
                            return;
                        }

                        String phone = et_phone.getText().toString().trim();
                        final HashMap<String, String> map = new HashMap<>();
                        map.put("prefix",whichPrefix+1+"");
                        map.put("phone",phone);
                        map.put("pwd",Utils.encryptPwd(p1));
                        map.put("ver",PackageUtils.getVersionName());

                        Http.post(mode == SIGN_UP ? "delivery/user/signup" : "delivery/user/reset", map, new Http.Listener() {
                            @Override
                            public void onResponse(String result) {
                                String msg = BaseProtocol.getMsg(result);
                                if (msg.equals("msg01")) {
                                    T.showShort("註冊成功！");
                                    d.dismiss();
                                    UserInfo userInfo = new Gson().fromJson(BaseProtocol.getReturnObject(result).get("object").getAsJsonObject(), UserInfo.class);
                                    finish();
                                    AppManager.getAppManager().finishActivity(Login.class);
                                    UserAccountManager.getInstance().logIn(userInfo);
                                } else if (msg.equals("msg03")) {
                                    T.showLong("這個號碼已經註冊過了，請直接登錄！");
                                    d.dismiss();
                                    onBackPressed();
                                } else if (msg.equals("msg09")) {
                                    T.showLong("重置密碼失敗");
                                    d.dismiss();
                                    onBackPressed();
                                } else if (msg.equals("msg10")) {
                                    T.showLong("重置密碼成功，請登錄！");
                                    d.dismiss();
                                    onBackPressed();
                                }

                                else if (msg.equals("msg04")) {
                                    T.showLong("這個號碼還沒有註冊，請先註冊！");
                                    d.dismiss();
                                    onBackPressed();
                                }
                            }
                        });

                    }
                });
            }
        });
        dialog.show();

        Utils.showSoftInput(et_1);
    }


    private void prefixSelectDialog() {
        new AlertDialog.Builder(SignUp.this, R.style.AppCompatAlertDialogStyle)
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_in,R.anim.right_out);
    }
}
