package com.gvbyc.ki41foo.delivery.UI.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gvbyc.ki41foo.delivery.AppManager;
import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.TextDialog;
import com.gvbyc.ki41foo.delivery.UI.activity.Login;
import com.gvbyc.ki41foo.delivery.UI.activity.NewsCenter;
import com.gvbyc.ki41foo.delivery.UserAccountManager;
import com.gvbyc.ki41foo.delivery.model.MyAction;
import com.gvbyc.ki41foo.delivery.protocal.BaseProtocol;
import com.gvbyc.ki41foo.delivery.protocal.Http;
import com.gvbyc.ki41foo.delivery.utils.IntentUtils;
import com.gvbyc.ki41foo.delivery.utils.PackageUtils;
import com.gvbyc.ki41foo.delivery.utils.T;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;
import com.gvbyc.ki41foo.delivery.utils.Utils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by goodview on 16/03/16.
 */
public class AccountFragment extends BaseFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.account, null);
        ImageView iv = (ImageView) rootView.findViewById(R.id.avatar);
        IconicsDrawable icon = new IconicsDrawable(this.getContext())
                .icon(GoogleMaterial.Icon.gmd_account_circle)
                .color(UIUtils.getColor(R.color.light_text));
        iv.setImageDrawable(icon);

        RelativeLayout rl_account = (RelativeLayout) rootView.findViewById(R.id.rl_account);
        TextView name = (TextView) rootView.findViewById(R.id.name);
        Button btn = (Button) rootView.findViewById(R.id.btn_login);
        if (UserAccountManager.getInstance().isLogin()) {
            name.setText(UserAccountManager.getInstance().getUserInfo().phone);
            btn.setBackgroundDrawable(UIUtils.getDrawable(R.drawable.rect_grey));
            btn.setText("登 出");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logoutDialog();
                }
            });

            TextView news = (TextView) rootView.findViewById(R.id.item4);
            news.setVisibility(View.VISIBLE);
            news.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(getContext())
                    .icon(GoogleMaterial.Icon.gmd_notifications)
                    .color(Color.BLACK)
                    .sizeDp(16)
                    .paddingDp(0),null,null,null);
            news.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.startActivity(getActivity(), NewsCenter.class);
                }
            });

        } else {
            btn.setBackgroundDrawable(UIUtils.getDrawable(R.drawable.rect_red));
            btn.setText("登 錄");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.startActivity(AppManager.getAppManager().currentActivity(), Login.class);
                }
            });

            rl_account.setVisibility(View.GONE);
            rootView.findViewById(R.id.item4).setVisibility(View.GONE);
        }

        TextView item1 = (TextView) rootView.findViewById(R.id.item1);
        item1.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(getContext())
                .icon(GoogleMaterial.Icon.gmd_comment)
                .color(Color.BLACK)
                .sizeDp(16)
                .paddingDp(0),null,null,null);
        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditCommentDialog();
            }
        });


        TextView item2 = (TextView) rootView.findViewById(R.id.item2);
        item2.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(getContext())
                .icon(GoogleMaterial.Icon.gmd_description)
                .color(Color.BLACK)
                .sizeDp(16)
                .paddingDp(0),null,null,null);
        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TextDialog(getActivity(),TextDialog.PROTOCOL);
            }
        });


        TextView item3 = (TextView) rootView.findViewById(R.id.item3);
        item3.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(getContext())
                .icon(GoogleMaterial.Icon.gmd_share)
                .color(Color.BLACK)
                .sizeDp(16)
                .paddingDp(0),null,null,null);
        item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "9折起，點外賣！<餸上門APP> http://www.gvbyc.com/gvdelivery/delivery/download/user");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "分享餸上門"));
            }
        });

        return rootView;
    }


    private void showEditCommentDialog() {
        final AppCompatEditText editText = new AppCompatEditText(this.getActivity());
        int padding = UIUtils.getDimens(R.dimen.abc_dialog_padding_material);

        final AlertDialog dialog = new AlertDialog.Builder(this.getActivity(), R.style.AppCompatAlertDialogStyle)
                .setTitle("我想同<餸上門>講:")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String trim = editText.getText().toString().trim();
                        if (!TextUtils.isEmpty(trim)) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("type", "2");
                            map.put("ver", PackageUtils.getVersionName());
                            map.put("msg", trim);
                            Http.post("usermessage/create", map, new Http.Listener() {
                                @Override
                                public void onResponse(String result) {
                                    String msg = BaseProtocol.getMsg(result);
                                    if (msg.equals("msg11")) {
                                        T.showShort("多謝");
                                    }
                                }
                            });
                        }

                    }
                }).setNegativeButton("取消", null).setView(editText, padding, padding, padding, 0).create();
        dialog.show();

        Utils.showSoftInput(editText);
    }

    private void logoutDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle)
                .setTitle("我要登出帳戶")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserAccountManager.getInstance().logOut();
                        T.showShort("已經登出");
                    }
                }).setNegativeButton("取消", null).create();
        dialog.show();
    }
}
