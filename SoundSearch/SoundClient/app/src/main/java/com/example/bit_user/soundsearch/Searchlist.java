package com.example.bit_user.soundsearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

class Mdata{
    int num;
    String title;
    String singer;
    String index;
    public Mdata(int num, String title, String singer, String index) { this.num = num; this.title = title; this.singer = singer; this.index = index; }
    public int getNum() { return num; }
    public void setNum(int num) { this.num = num; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSinger() { return singer; }
    public void setSinger(String singer) { this.singer = singer; }
    public String getIndex() { return index; }
    public void setIndex(String index) { this.index = index; }
}

class MyViewAdapter extends ArrayAdapter<Mdata> { // 수정확인    <안에 타입 변경>
    Context context;
    int resourceId;
    public MyViewAdapter(Context context, int resourceId, List<Mdata> li) { // 수정확인  <안에 타입 변경>
        super(context, resourceId, li);
        this.context = context;
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(resourceId, null);

        // 코드수정부분 시작
        ((TextView) convertView.findViewById(R.id.titlet)).setText( getItem(position).getTitle() );  //Image추가 단
        ((TextView) convertView.findViewById(R.id.singert)).setText( getItem(position).getSinger() );
        ((TextView) convertView.findViewById(R.id.indext)).setText( getItem(position).getIndex() );
        // 코드수정부분 끝

        return convertView;
    }

}

public class Searchlist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int ct=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlist);
        Intent intent=getIntent();
        String data =intent.getStringExtra("data");
        String result =intent.getStringExtra("result");
        if(data==null){
            ((TextView) findViewById(R.id.result)).setText("\""+result+"\" 에 대한 검색결과가 없습니다.");
        }else{
            ((TextView) findViewById(R.id.result)).setText("\""+result+"\" 에 대한 검색결과입니다.");
            String [] datasplit=data.split(" ");
            LinkedList<Mdata> mlistdata=new LinkedList<>();
            while(true){
                int num = Integer.parseInt(datasplit[(ct*4)]);
                String title = datasplit[(ct*4)+1];
                String singer = datasplit[(ct*4)+2];
                String index = datasplit[(ct*4)+3];
                mlistdata.add(new Mdata(num,title,singer,index));
                ct++;
                if(datasplit.length/4==ct){
                    break;
                }
            }

            MyViewAdapter adapter=new MyViewAdapter(this, R.layout.list, mlistdata);
            ListView listview =(ListView) findViewById(R.id.lv);
            listview.setAdapter(adapter);

        }

    }
}
