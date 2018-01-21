package com.seoulprojet.seoulpoem.model;

/**
 * Created by lynn on 2017-10-14.
 */

public class MyPageResult {
    public String status;
    public MyPageMessage msg;

    public class MyPageMessage{
        public String profile;
        public String background;
        public String inform;
        public String pen_name;
    }
}
