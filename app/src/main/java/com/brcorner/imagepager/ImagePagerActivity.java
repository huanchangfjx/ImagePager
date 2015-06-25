package com.brcorner.imagepager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.brcorner.imagepager.fragment.ImageDetailFragment;
import com.brcorner.imagepager.view.HackyViewPager;

import java.util.ArrayList;

/**
 * 图片查看器
 * @author dong
 */
public class ImagePagerActivity extends FragmentActivity{
    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";

    private HackyViewPager mPager;
    private int pagerPosition;
    private LinearLayout ll_indicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepager);


        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        ArrayList<String> urls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);

        mPager = (HackyViewPager) findViewById(R.id.pager);
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
        mPager.setAdapter(mAdapter);
        ll_indicator = (LinearLayout) findViewById(R.id.ll_indicator);
        if (urls.size() > 1) {
            addIndicator(urls, ll_indicator);
        }
        // 更新下标
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                setImageBackground(arg0);
            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        mPager.setCurrentItem(pagerPosition);
    }

    ArrayList<ImageView> indicatorList = new ArrayList<ImageView>();


    // 添加指示器
    private void addIndicator(ArrayList<String> urls, LinearLayout ll_indicator) {

        int paddingDp = dip2px(this, 4);
        int pointsize = dip2px(this, 10);
        for (int i = 0; i < urls.size(); i++) {
            ImageView child = new ImageView(this);
            child.setImageResource(R.mipmap.point_grey);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pointsize,pointsize);
            child.setLayoutParams(params);
            child.setPadding(paddingDp, 0, paddingDp, 0);
            indicatorList.add(child);
            ll_indicator.addView(child);
        }
        setPointLight(0);

    };

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    private void setPointLight(int position) {

        if (indicatorList.size() < 2) {
            return;
        }

        for (ImageView imageView : indicatorList) {
            imageView.setImageResource(R.mipmap.point_grey);
        }

        ImageView imageView = indicatorList.get(position);
        imageView.setImageResource(R.mipmap.point_white);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }


    /**
     * 设置选中的tip的背景
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < indicatorList.size(); i++) {
            if (i == selectItems) {
                indicatorList.get(i).setImageResource(R.mipmap.point_white);
            } else {
                indicatorList.get(i).setImageResource(R.mipmap.point_grey);
            }
        }
    }


    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ArrayList<String> fileList;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList.get(position);
            return ImageDetailFragment.newInstance(url);
        }

    }
}
