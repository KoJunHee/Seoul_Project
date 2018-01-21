package com.seoulprojet.seoulpoem.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by lynn on 2017-10-14.
 */

public class MyPagePoemResult {

    public String status;
    public PoemMessage msg;

    public class PoemMessage{
        public int counts;
        public ArrayList<MyPagePoemListData> poems;
    }
}
