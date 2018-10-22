package com.example.shenglin.easynote;

import cn.bmob.v3.BmobObject;

/**
 * Created by Lee on 2018/10/21.
 */

public class MyBmobFile extends BmobObject {
    private Integer version;
    private MyBmobUser user;
    private String name;

    public MyBmobFile() {
        this.setTableName("File");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MyBmobUser getUser() {
        return user;
    }

    public void setUser(MyBmobUser user) {
        this.user = user;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
