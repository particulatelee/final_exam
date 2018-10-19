package com.example.shenglin.easynote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;

public class SearchActivity extends AppCompatActivity {

    LinkedList<MainLine> mainLines = new LinkedList<>();
    SparseArray<String> dirs = new SparseArray<>();
    SparseArray<String> files = new SparseArray<>();

    private SparseBooleanArray checkedMap = new SparseBooleanArray();
    boolean isMultiSelect = false;
    int del_sum = 0;

    LineAdapter adapter = new LineAdapter(SearchActivity.this,mainLines);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        String search_name = intent.getStringExtra("name");
        final String user = intent.getStringExtra("user");

        final ListView listView = (ListView)findViewById(R.id.search_list);

        Search sousuo = new Search(SearchActivity.this,mainLines);
        sousuo.search(search_name,user,dirs,files);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isMultiSelect){
                    String file_name = files.get(position);
                    String table_name = dirs.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("file_name",file_name);
                    intent.putExtra("table",table_name);
                    intent.putExtra("user",user);
                    intent.setClass(SearchActivity.this,EditActivity.class);
                    startActivity(intent);}
                if (isMultiSelect){
                    if (checkedMap.get(position)){checkedMap.put(position,false);
                        del_sum--;}
                    else {checkedMap.put(position,true);
                        del_sum++;}
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                    TextView sum = (TextView)findViewById(R.id.sum);
                    sum.setText("已选"+"("+del_sum+")");
                }
            }
        });


    }
//////////////////----------------------------------------------------------------------------------------------------///////////////////////////////
    private class LineAdapter extends BaseAdapter {
        private Context context;
        private LineAdapter.ViewHolder holder;
        private LinkedList<MainLine> mainLines;

        private LineAdapter(Context context, LinkedList<MainLine> mainLines){
            this.context = context;
            this.mainLines = mainLines;
        }
        @Override
        public int getCount(){
            return mainLines.size();
        }
        @Override
        public Object getItem(int position){
            return position;
        }
        @Override
        public long getItemId(int position){
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if (convertView==null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.main_line, parent, false);
                holder = new LineAdapter.ViewHolder();
                holder.img = convertView.findViewById(R.id.main_line_img);
                holder.text = convertView.findViewById(R.id.main_line_text);
                holder.checkBox = convertView.findViewById(R.id.checkbox);
                convertView.setTag(holder);
            }
            else {
                holder = (LineAdapter.ViewHolder) convertView.getTag();
            }
            holder.text.setText(mainLines.get(position).getContent());
            holder.img.setImageResource(mainLines.get(position).getImgId());
            if (isMultiSelect){
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(checkedMap.get(position));
            }
            else {
                holder.checkBox.setVisibility(View.GONE);
            }
            return convertView;
        }

        private class ViewHolder{
            ImageView img;
            TextView text;
            CheckBox checkBox;
        }
    }
}
