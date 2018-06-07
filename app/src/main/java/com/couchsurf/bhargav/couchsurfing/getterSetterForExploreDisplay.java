package com.couchsurf.bhargav.couchsurfing;

import java.util.ArrayList;
import java.util.Map;

public class getterSetterForExploreDisplay {

    public static String city;
    public static String gcid;
    public static ArrayList<Map> mapForMatchedCouch;
    public static String UID;
    public static void setCity(String c){
        city = c;
    }

    public static String getCity() {
        return city;
    }

    public static void setGcid(String c){
        gcid = c;
    }

    public static String getGcid() {
        return gcid;
    }


    public static void setMap(ArrayList m){
        mapForMatchedCouch = m;
    }

    public static ArrayList<Map> getMapForMatchedCouch(){
        return mapForMatchedCouch;
    }

    public static void setUid(String u){
       UID = u;
    }

    public static String getUid(){
        return UID;
    }
}
