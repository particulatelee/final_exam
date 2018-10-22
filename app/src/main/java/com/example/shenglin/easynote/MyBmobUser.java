package com.example.shenglin.easynote;

import cn.bmob.v3.BmobUser;

public class MyBmobUser extends BmobUser {
    private Integer age;

    public MyBmobUser() {
        this.setTableName("_User");
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
