package com.brcorner.imagepager.fragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.brcorner.imagepager.R;
import com.brcorner.imagepager.view.CircleProgressBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 单张图片显示Fragment
 * @author dong
 */
@SuppressLint("ValidFragment")
public class ImageDetailFragment extends Fragment {
    private String mImageUrl;
    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;
    private CircleProgressBar progressDialog;


    private static final int DIALOG_DISMISS = 0;
    private static final int DIALOG_SHOW = 1;


    public static ImageDetailFragment newInstance(String imageUrl) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url")
                : null;

    }

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case DIALOG_SHOW:
                    if (progressDialog != null) {
                        progressDialog.setVisibility(View.VISIBLE);
                    }
                    break;
                case DIALOG_DISMISS:
                    if (progressDialog != null) {
                        progressDialog.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    private Runnable runnable_collect = new Runnable() {
        @Override
        public void run() {
            myHandler.sendEmptyMessage(DIALOG_SHOW);
            mImageView.setDrawingCacheEnabled(true);
            Bitmap mBitmap = mImageView.getDrawingCache();
            ImageDetailFragment.addBitmapToAlbum(ImageDetailFragment.this.getActivity()
                    .getBaseContext(), mBitmap);// 实际起作用的方法
            myHandler.sendEmptyMessage(DIALOG_DISMISS);
        }
    };


    public static boolean addBitmapToAlbum(Context context, Bitmap bm) {
        String uriStr = MediaStore.Images.Media.insertImage(context.getContentResolver(), bm, "", "");

        if(uriStr == null){
            return false;
        }

        String picPath  = getFilePathByContentResolver(context, Uri.parse(uriStr));
        if(picPath == null) {
            return false;
        }

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.ORIENTATION, 0);
        values.put(MediaStore.Images.Media.DATA, picPath);

        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return true;
    }

    private static String getFilePathByContentResolver(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath  = null;
        if (null == c) {
            throw new IllegalArgumentException(
                    "Query on " + uri + " returns null result.");
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
            } else {
                filePath = c.getString(
                        c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_image_detail,
                container, false);
        mImageView = (ImageView) v.findViewById(R.id.image);
        mImageView.setClickable(true);

        mAttacher = new PhotoViewAttacher(mImageView);

        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }
        });


        progressDialog = (CircleProgressBar) v.findViewById(R.id.loading);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mImageView.setImageResource(Integer.parseInt(mImageUrl));
        //网络用下面注释代码
//        ImageLoader.getInstance().displayImage(mImageUrl, mImageView,
//				new SimpleImageLoadingListener() {
//					@Override
//					public void onLoadingStarted(String imageUri, View view) {
//						if (progressDialog != null) {
//							progressDialog.setVisibility(View.VISIBLE);
//						}
//					}
//
//					@Override
//					public void onLoadingFailed(String imageUri, View view,
//							FailReason failReason) {
//						String message = null;
//						switch (failReason.getType()) {
//						case IO_ERROR:
//							message = "下载错误";
//							break;
//						case DECODING_ERROR:
//							message = "图片无法显示";
//							break;
//						case NETWORK_DENIED:
//							message = "网络有问题，无法下载";
//							break;
//						case OUT_OF_MEMORY:
//							message = "图片太大无法显示";
//							break;
//						case UNKNOWN:
//							message = "未知的错误";
//							break;
//						}
//						Toast.makeText(getActivity(), message,
//								Toast.LENGTH_SHORT).show();
//						if (progressDialog != null) {
//							progressDialog.setVisibility(View.GONE);
//						}
//					}
//
//					@Override
//					public void onLoadingComplete(String imageUri, View view,
//							Bitmap loadedImage) {
//						if (progressDialog != null) {
//							progressDialog.setVisibility(View.VISIBLE);
//						}
//						mAttacher.update();
//					}
//				});
    }

}
