package com.linbin.myscrollbar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SweetPanelListview.OnPositionChangedChangedListener {

    private SweetPanelListview mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (SweetPanelListview) findViewById(R.id.list);
        mListView.setAdapter(new DummyAdapter());
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setPositionChangedListener(this);
    }

    @Override
    public void positionChangedChanged(SweetPanelListview listview, int position, View view) {
        ((TextView) view).setText(position + "");
    }

    private class DummyAdapter extends BaseAdapter{

        private int mNum = 100;
        @Override
        public int getCount() {
            return mNum;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null){
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item,parent,false);
            }
            TextView textView = (TextView)convertView;
            ((TextView) convertView).setText("" + position);
            return convertView;
        }
    }


}
