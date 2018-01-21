package com.seoulprojet.seoulpoem.model;

import java.util.ArrayList;

/**
 * Created by lynn on 2017-10-15.
 */

public class TodayResult {
    public ArrayList<SubwayList> subway_list;

    public class SubwayList{
        public int idnotices;
        public String title;
    }
}
