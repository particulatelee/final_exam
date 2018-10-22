package com.example.shenglin.easynote;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Lee on 2018/10/21.
 */

public class MyBmobDefault extends BmobObject{
    private String name;
    private Integer version;
    private String context;
    private MyBmobUser user;

    public MyBmobDefault() {
        this.setTableName("Default");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public MyBmobUser getUser() {
        return user;
    }

    public void setUser(MyBmobUser user) {
        this.user = user;
    }
}
