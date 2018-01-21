package com.seoulprojet.seoulpoem.model;

/**
 * Created by junhee on 2017. 10. 6..
 */

public class DetailResult {
    public String status;
    public Data data;

    public class Data {
        public String photo;
        public String tags;
        public String inform;
        public int bookmark;
        public int modifiable;
        public Writer writer;
    }

    public class Writer{
        public String profile;
        public String pen_name;
        public String email;
        public int type;
    }

}
