package com.seoulprojet.seoulpoem.model;

import java.util.ArrayList;

/**
 * Created by lynn on 2017-10-15.
 */

public class NoticeResult {
    public ArrayList<NoticeList> notice_list;

    public class NoticeList{
        public int idnotices;
        public String title;
        public String date;
    }
}
