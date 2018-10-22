package com.example.shenglin.easynote;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Lee on 2018/10/20.
 */

public class MyBmobNote extends BmobObject {
    private String name;
    private Integer Version;
    private MyBmobUser user;
    private String fileId;
    private String context;

    public MyBmobNote() {
        this.setTableName("Note");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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

    public Integer getVersion() {
        return Version;
    }

    public void setVersion(Integer Version) {
        this.Version = Version;
    }
}
