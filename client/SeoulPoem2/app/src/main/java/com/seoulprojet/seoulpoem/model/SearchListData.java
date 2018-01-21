package com.seoulprojet.seoulpoem.model;

/**
 * Created by junhee on 2017. 10. 6..
 */

public class SearchListData {
    public int imgResourceId;
    public String writerName;
    public int photoNum;
    public int poemNum;

    public String title;
    public String contents;


    public SearchListData(int imgResourceId, String writerName, int photoNum, int poemNum, String title, String contents) {
        this.imgResourceId = imgResourceId;
        this.writerName = writerName;
        this.photoNum = photoNum;
        this.poemNum = poemNum;
        this.title = title;
        this.contents = contents;
    }
}
