package com.seoulprojet.seoulpoem.model;

/**
 * Created by lynn on 2017-10-25.
 */

public class UserPageResult {
    public String status;
    public UserPageMessage msg;

    public class UserPageMessage{
        public UserInform user;
        public int owner;
    }
}
