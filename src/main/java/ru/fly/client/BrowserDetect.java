package ru.fly.client;

import com.google.gwt.user.client.Window;

/**
 * User: fil
 * Date: 05.12.15
 */
public class BrowserDetect {

    private static String ua;
    private static Boolean isChrome;
    private static Boolean isSafari;

    private static String getUa(){
        if(ua == null){
            ua = Window.Navigator.getUserAgent().toLowerCase();
        }
        return ua;
    }

    public static boolean isChrome() {
        if(isChrome == null){
            isChrome = getUa().contains("chrome");
        }
        return isChrome;
    }

    public static boolean isSafari() {
        if(isSafari == null) {
            isSafari = getUa().contains("safari");
        }
        return isSafari;
    }

}
