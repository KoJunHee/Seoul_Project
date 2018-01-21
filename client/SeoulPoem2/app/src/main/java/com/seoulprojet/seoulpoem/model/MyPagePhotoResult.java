package com.seoulprojet.seoulpoem.model;

import java.util.ArrayList;

/**
 * Created by lynn on 2017-10-18.
 */

public class MyPagePhotoResult {

    public String status;
    public PhotoMessage msg;

    public class PhotoMessage{
        public int counts;
        public ArrayList<MyPagePhotoListData> photos;
    }
}
