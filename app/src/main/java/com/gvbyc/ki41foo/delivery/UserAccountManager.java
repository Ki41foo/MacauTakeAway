package com.gvbyc.ki41foo.delivery;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gvbyc.ki41foo.delivery.model.MyAction;
import com.gvbyc.ki41foo.delivery.model.UserInfo;
import com.gvbyc.ki41foo.delivery.utils.PackageUtils;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;

import java.util.HashMap;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;


/**
 * Created by goodview on 01/02/16.
 */
public class UserAccountManager {
    private UserInfo userInfo;

    private static UserAccountManager ourInstance;

    public static UserAccountManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new UserAccountManager();
        }

        return ourInstance;
    }

    private UserAccountManager() {

    }

    public boolean isLogin() {
        return getUserInfo() != null;
    }

    public UserInfo getUserInfo() {
        if (userInfo == null) {
            String s = BaseApplication.getSpString("user_info");
            if (s != null) {
                userInfo = new Gson().fromJson(s, UserInfo.class);
            }
        }
        return userInfo;
    }


    public void logIn(UserInfo info) {
        updateUserInfo(info);
        setJpushAlias(info.code);
        EventBus.getDefault().post(new MyAction(MyAction.HOME_REFRESH));
    }

    public void logOut() {
        updateUserInfo(null);
        JPushInterface.stopPush(UIUtils.getContext());
        EventBus.getDefault().post(new MyAction(MyAction.HOME_REFRESH));
    }


    public void updateUserInfo(UserInfo info) {
        userInfo = info;
        if (info == null) {
            BaseApplication.putSp("user_info", null);
        } else {
            BaseApplication.putSp("user_info", new Gson().toJson(userInfo));
        }
    }

    private void setJpushAlias(final String uCode) {

        if (JPushInterface.isPushStopped(UIUtils.getContext())) {
            JPushInterface.resumePush(UIUtils.getContext());
        }
        JPushInterface.setAlias(UIUtils.getContext(), uCode, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if (i != 0) {
                    setJpushAlias(uCode);
                }
            }
        });
    }
}
