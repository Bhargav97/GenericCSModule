package com.couchsurf.bhargav.couchsurfing;

public class UtilityClass {

    public static boolean phoneVerifyDone=false;

    public static boolean fromStatus=false;

    public static String getUidFromUrl(String url) {
        int startIndex = url.lastIndexOf('/') + 1;
        int endIndex = url.lastIndexOf('.');
        return url.substring(startIndex, endIndex);
    }

    public static String returnUrlForUid(String Uid){
        return "https://s3.amazonaws.com/couchsurfing-userfiles-mobilehub-151528593/s3Folder/userIcons/"+Uid+".jpg";
    }

}
