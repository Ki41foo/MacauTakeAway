package com.gvbyc.ki41foo.delivery.plivo;

import com.gvbyc.ki41foo.delivery.ThreadManager;

import java.util.LinkedHashMap;


/**
 * Created by goodview on 07/01/16.
 */
public class PlivoHelper {
    private static String CODE = null;


    private String AUTH_ID = "MANJIWZJI0NGM2NZAYZD";
    private String AUTH_TOKEN = "NmJkMTZiODY0OTVkMjg5ZjczZWU0YTA2MDQ2MDlm";
    private String PLIVO_VERSION = "v1";


    public PlivoHelper () {


    }

    public void send(final String dest) {
        final RestAPI api = new RestAPI(AUTH_ID, AUTH_TOKEN, PLIVO_VERSION);
        ThreadManager.getShortPool().execute(new Runnable() {
            @Override
            public void run() {
                LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
                parameters.put("src", "+85328501062"); // Sender's phone number with country code
                parameters.put("dst", dest); // Receiver's phone number with country code
                parameters.put("text", "你的驗證碼為" + getRandomCode() + "  <餸上門>，如非本人操作，不必理會。"); // Your SMS text message
                // Send Unicode text
                //parameters.put("text", "こんにちは、元気ですか？"); // Your SMS text message - Japanese
                //parameters.put("text", "Ce est texte généré aléatoirement"); // Your SMS text message - French
//                parameters.put("url", "http://example.com/report/"); // The URL to which with the status of the message is sent
//                parameters.put("method", "GET"); // The method used to call the url


                try {
                    api.sendMessage(parameters);
                } catch (PlivoException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private String getRandomCode() {
        CODE = String.valueOf((int) ((Math.random()*9000)+1000));
        return CODE;
    }

    public boolean verifyCode(String num) {
        if(CODE == null || num == null) return false;
        if(num.equals(CODE)) {
            CODE = null;
            return true;
        }
        return false;
    }
}
