package com.seoulprojet.seoulpoem.model;

/**
 * Created by junhee on 2017. 10. 5..
 */

public class HashtagListData {
    public int imgResourceID;
    public String text;

    public HashtagListData(int imgResourceID, String text) {
        this.imgResourceID = imgResourceID;
        this.text = text; //제목 대체할 수 있는 거

    }
}
