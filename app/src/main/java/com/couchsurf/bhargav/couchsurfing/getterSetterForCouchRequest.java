package com.couchsurf.bhargav.couchsurfing;

import java.util.ArrayList;
import java.util.Map;

public class getterSetterForCouchRequest {


    public static String GRid;
    public static ArrayList<Map> mapOfRequests;
    public static String guestUID;
    public static String guestName;
    public static String acc;
    public static String fromDate;
    public static String toDate;

    public static void setFromDate(String fromDate) {
        getterSetterForCouchRequest.fromDate = fromDate;
    }

    public static void setToDate(String toDate) {
        getterSetterForCouchRequest.toDate = toDate;
    }

    public static String getFromDate() {
        return fromDate;
    }

    public static String getToDate() {
        return toDate;
    }

    public static void setAcc(String acc) {
        getterSetterForCouchRequest.acc = acc;
    }

    public static String getAcc() {
        return acc;
    }

    public static void setGuestName(String guestName) {
        getterSetterForCouchRequest.guestName = guestName;
    }

    public static String getGuestName() {
        return guestName;
    }

    public static void setGrid(String c){
        GRid = c;
    }

    public static String getGrid() {
        return GRid;
    }

    public static void setGuestUID(String c){
        guestUID = c;
    }

    public static String getGuestUID() {
        return guestUID;
    }

    public static void setMap(ArrayList m){
        mapOfRequests = m;
    }

    public static ArrayList<Map> getMapForMatchedCouch(){
        return mapOfRequests;
    }

}
