package com.brcorner.imagepager;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doClick(View view)
    {
        ArrayList<String> urls = new ArrayList<String>();
        urls.add(String.valueOf(R.mipmap.p1));
        urls.add(String.valueOf(R.mipmap.p2));
        urls.add(String.valueOf(R.mipmap.p3));
        urls.add(String.valueOf(R.mipmap.p4));
        Intent intent = new Intent(this, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 1);
        this.startActivity(intent);
    }


}
