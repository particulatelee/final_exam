package com.example.shenglin.easynote;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Lee on 2018/10/23.
 */

public class FileHelper {
    private Context context;

    public FileHelper(){
    }

    public FileHelper(Context context){
        super();
        this.context = context;
    }
//读取SD卡，并写出数据
    public void save(String filename, String filecontent, String user)throws Exception {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//            filename = Environment.getExternalStorageDirectory().getCanonicalPath()+"/"+filename;
//            filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Lee/"+filename;
            filename = Environment.getExternalStorageDirectory()+"/轻松记/"+user+"/"+filename;
            FileOutputStream out = new FileOutputStream(filename);
            out.write(filecontent.getBytes());
            out.close();
        }else {
            Toast.makeText(context,"存wrong!", Toast.LENGTH_SHORT).show();
        }
    }
    //读取SD卡，并写入数据
    public String read(String filename, String user)throws IOException {
        StringBuilder sb = new StringBuilder("");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            filename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + filename;
//            filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Lee/"+filename;
              filename = Environment.getExternalStorageDirectory()+"/轻松记/"+user+"/"+filename;
            FileInputStream in = new FileInputStream(filename);
            byte[] temp = new byte[1024];
            int l = 0;
            while ((l = in.read(temp)) > 0) {
                sb.append(new String(temp, 0, l));
            }
            in.close();
        }
        return sb.toString();
    }
}
