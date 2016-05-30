package com.gvbyc.ki41foo.delivery.model;

import java.util.ArrayList;

/**
 * Created by goodview on 24/07/15.
 */
public class UserInfo {
    public String code;
    public String age;
    public String email;
    public String gender;
    public String name;
    public String phone;
    public String postn;
    public String prefix;
    public String workplace;
    public String recaddnum;
    public ArrayList<ContactList> contactlist;

    public String mPassword;

    public class ContactList {
        public String address;
        public String cid;
        public String isd;
        public String name;
        public String phone;
        public String prefix;
    }

}
