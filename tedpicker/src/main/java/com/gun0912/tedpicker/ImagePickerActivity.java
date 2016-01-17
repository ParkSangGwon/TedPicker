/*
 * Copyright (c) 2016. Ted Park. All Rights Reserved
 */

package com.gun0912.tedpicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;

import java.util.ArrayList;


public class ImagePickerActivity extends AppCompatActivity implements CameraHostProvider {

    /**
     * Returns the parcelled image uris in the intent with this extra.
     */
    public static final String EXTRA_IMAGE_URIS = "image_uris";
    public static CwacCameraFragment.MyCameraHost mMyCameraHost;
    /**
     * Key to persist the list when saving the state of the activity.
     */

    public static ArrayList<Uri> mSelectedImages;
    // initialize with default config.
    private static Config mConfig = new Config();

    View view_root;
    LinearLayout mSelectedImagesContainer;
    TextView mSelectedImageEmptyMessage;
    View view_selected_photos_container;
    TextView tv_selected_title;
    ViewPager mViewPager;
    TabLayout tabLayout;

    PagerAdapter_Picker adapter;

    public static Config getConfig() {
        return mConfig;
    }

    public static void setConfig(Config config) {

        if (config == null) {
            throw new NullPointerException("Config cannot be passed null. Not setting config will use default values.");
        }

        mConfig = config;
    }

    @Override
    public CameraHost getCameraHost() {
        return mMyCameraHost;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_activity_main_pp);
        initView();

        setTitle(mConfig.getToolbarTitleRes());
        mSelectedImages = new ArrayList<Uri>();
        setupFromSavedInstanceState(savedInstanceState);

        setupTabs();


    }


    private void initView() {

        view_root = findViewById(R.id.view_root);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);


        tv_selected_title = (TextView) findViewById(R.id.tv_selected_title);

        mSelectedImagesContainer = (LinearLayout) findViewById(R.id.selected_photos_container);
        mSelectedImageEmptyMessage = (TextView) findViewById(R.id.selected_photos_empty);

        view_selected_photos_container = findViewById(R.id.view_selected_photos_container);
        view_selected_photos_container.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view_selected_photos_container.getViewTreeObserver().removeOnPreDrawListener(this);

                int selected_bottom_size = (int) getResources().getDimension(mConfig.getSelectedBottomHeight());

                ViewGroup.LayoutParams params=view_selected_photos_container.getLayoutParams();
                params.height= selected_bottom_size;
                view_selected_photos_container.setLayoutParams(params);


                return true;
            }
        });




        if(mConfig.getSelectedBottomColor()>0){
            tv_selected_title.setBackgroundColor(mConfig.getSelectedBottomColor());
            mSelectedImageEmptyMessage.setTextColor(mConfig.getSelectedBottomColor());
        }






    }


    private void setupFromSavedInstanceState(Bundle savedInstanceState) {

        ArrayList<Uri> list;
        if (savedInstanceState != null) {
            list = savedInstanceState.getParcelableArrayList(EXTRA_IMAGE_URIS);
        } else {
            list = getIntent().getParcelableArrayListExtra(EXTRA_IMAGE_URIS);
        }

        if (list == null)
            return;


        for (Uri uri : list) {
            addImage(uri);
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putParcelableArrayList(EXTRA_IMAGE_URIS, mSelectedImages);
    }

    private void setupTabs() {
        adapter = new PagerAdapter_Picker(this, getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mViewPager);


        if(mConfig.getTabBackgroundColor()>0)
        tabLayout.setBackgroundColor(mConfig.getTabBackgroundColor());

        if(mConfig.getTabSelectionIndicatorColor()>0)
        tabLayout.setSelectedTabIndicatorColor(mConfig.getTabSelectionIndicatorColor());

    }

    public GalleryFragment getGalleryFragment() {

        if (adapter == null || adapter.getCount() < 2)
            return null;

        return (GalleryFragment) adapter.getItem(1);

    }

    public boolean addImage(final Uri uri) {


        if (mSelectedImages.size() == mConfig.getSelectionLimit()) {
            String text = String.format(getResources().getString(R.string.max_count_msg),mConfig.getSelectionLimit());
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            return false;
        }


        if (mSelectedImages.add(uri)) {
            View rootView = LayoutInflater.from(this).inflate(R.layout.picker_list_item_selected_thumbnail, null);
            ImageView thumbnail = (ImageView) rootView.findViewById(R.id.selected_photo);
            ImageView iv_close = (ImageView) rootView.findViewById(R.id.iv_close);
            iv_close.setImageResource(mConfig.getSelectedCloseImage());


            rootView.setTag(uri);


            //  mImageFetcher.loadImage(image.mUri, thumbnail);
            mSelectedImagesContainer.addView(rootView, 0);

            int selected_bottom_size = (int) getResources().getDimension(mConfig.getSelectedBottomHeight());

            Glide.with(getApplicationContext())
                    .load(uri.toString())
                    .override(selected_bottom_size, selected_bottom_size)
                    .dontAnimate()
                    .centerCrop()
                    .error(R.drawable.no_image)
                    .into(thumbnail);

            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeImage(uri);

                }
            });



            if (mSelectedImages.size() >= 1) {
                mSelectedImagesContainer.setVisibility(View.VISIBLE);
                mSelectedImageEmptyMessage.setVisibility(View.GONE);
            }
            return true;
        }


        return false;
    }

    public boolean removeImage(Uri uri) {


        boolean result = mSelectedImages.remove(uri);


        if (result) {

            if (GalleryFragment.mGalleryAdapter != null) {
                GalleryFragment.mGalleryAdapter.notifyDataSetChanged();
            }

            for (int i = 0; i < mSelectedImagesContainer.getChildCount(); i++) {
                View childView = mSelectedImagesContainer.getChildAt(i);
                if (childView.getTag().equals(uri)) {
                    mSelectedImagesContainer.removeViewAt(i);
                    break;
                }
            }

            if (mSelectedImages.size() == 0) {
                mSelectedImagesContainer.setVisibility(View.GONE);
                mSelectedImageEmptyMessage.setVisibility(View.VISIBLE);
            }


        }
        return result;
    }

    public boolean containsImage(Uri uri) {
        return mSelectedImages.contains(uri);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_done) {
            updatePicture();
            return true;
        }

        return super.onOptionsItemSelected(item);


    }

    private void updatePicture() {

        if (mSelectedImages.size() < mConfig.getSelectionMin()) {
            String text = String.format(getResources().getString(R.string.min_count_msg),mConfig.getSelectionMin());
            Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_IMAGE_URIS, mSelectedImages);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }


}