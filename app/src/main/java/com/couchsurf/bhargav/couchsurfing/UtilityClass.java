package com.couchsurf.bhargav.couchsurfing;

public class UtilityClass {

    public static String returnUrlForUid(String Uid){
        return "https://s3.amazonaws.com/couchsurfing-userfiles-mobilehub-151528593/s3Folder/userIcons/"+Uid+".jpg";
    }

}
