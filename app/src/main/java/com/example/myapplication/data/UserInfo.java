package com.example.myapplication.data;

import java.io.Serializable;

public class UserInfo implements Serializable {
   public String userId;
    public String nickname;
    public String imagePath;

    public UserInfo(){

    }
    public UserInfo(String userId, String nickname, String imagePath){
        this.userId=userId;
        this.nickname=nickname;
        this.imagePath=imagePath;
    }
}
