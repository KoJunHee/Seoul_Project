package com.seoulprojet.seoulpoem.model;

import java.util.ArrayList;

/**
 * Created by lynn on 2017-10-16.
 */

public class NoticeDetailResult {

    public NoticeDetailList notice;

    public class NoticeDetailList{
        public int idnotices;
        public String title;
        public String content;
        public String date;
        public String photo;
    }
}
