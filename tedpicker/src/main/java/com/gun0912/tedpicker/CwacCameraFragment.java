/*
 * Copyright (c) 2016. Ted Park. All Rights Reserved
 */

package com.gun0912.tedpicker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;

import com.commonsware.cwac.camera.CameraUtils;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.gun0912.tedpicker.util.BitmapUtil;
import com.gun0912.tedpicker.util.Util;
import com.gun0912.tedpicker.view.DrawingView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CwacCameraFragment extends Fragment implements View.OnClickListener {

    static final int FOCUS_AREA_WEIGHT = 1000;
    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    public static float camera_width;
    public static float camera_height;

    private static Config mConfig;
    View view;
    ImageButton btn_take_picture;
    View vShutter;
    CameraView cameraView;
    DrawingView drawingView;
    List<Camera.Area> focusList;

    private ProgressDialog mProgressDialog;

    /**
     * @param config
     */
    public static void setConfig(@Nullable Config config) {
        mConfig = config;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImagePickerActivity.mMyCameraHost = new MyCameraHost(getActivity());


        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_title));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.picker_fragment_camera_cwac, null, false);

        initView();


        ViewGroup.LayoutParams params=cameraView.getLayoutParams();
         params.height= (int) getResources().getDimension(mConfig.getCameraHeight());

        Log.d("ted","params.height: "+params.height);
        cameraView.setLayoutParams(params);


        // 카메라뷰의 크기와 위치를 가져온다
        // get CameraView's widht/height
        cameraView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                cameraView.getViewTreeObserver().removeOnPreDrawListener(this);

                camera_width = cameraView.getWidth();
                camera_height = cameraView.getHeight();

                return true;
            }
        });


        // 카메라뷰를 터치하는경우 해당 위치로 포커스를 잡는다
        // when cameraView touch, show focus
        cameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        focusOnTouch(event);
                    }
                }
                return true;
            }
        });


        addSensorListener();


        return view;
    }

    int device_orientation;

    private void addSensorListener() {

        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(new SensorEventListener() {


            @Override
            public void onSensorChanged(SensorEvent event) {


/*

                if (event.values[1]<6.5 && event.values[1]>-6.5) {
                    if (device_orientation!=1) {
                        Log.d("Sensor", "Landscape");
                    }
                    device_orientation=1;
                } else {
                    if (device_orientation!=0) {
                        Log.d("Sensor", "Portrait");
                    }
                    device_orientation=0;
                }


*/

                float x = event.values[0];
                float y = event.values[1];




                if (x<5 && x>-5 && y > 5)
                    device_orientation = 0;
                else if (x<-5 && y<5 && y>-5)
                    device_orientation = 90;
                else if (x<5 && x>-5 && y<-5)
                    device_orientation = 180;
                else if (x>5 && y<5 && y>-5)
                    device_orientation = 270;




            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
        }, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);


    }

    private void initView() {
        cameraView = (CameraView) view.findViewById(R.id.cameraView);

        btn_take_picture = (ImageButton) view.findViewById(R.id.btn_take_picture);
        btn_take_picture.setOnClickListener(this);
        btn_take_picture.setImageResource(mConfig.getCameraBtnImage());
        btn_take_picture.setBackgroundResource(mConfig.getCameraBtnBackground());


        vShutter = view.findViewById(R.id.vShutter);


        drawingView = (DrawingView) view.findViewById(R.id.drawingView);

    }

    private void focusOnTouch(MotionEvent event) {


        float x = event.getX();
        float y = event.getY();


        Rect touchRect = new Rect(
                (int) (x - 100),
                (int) (y - 100),
                (int) (x + 100),
                (int) (y + 100));

        Rect cameraViewRect = new Rect();
        cameraView.getLocalVisibleRect(cameraViewRect);


        // 사각형 범위가 카메라뷰 위치를 벗어나는경우 카메라뷰의 최대 위치로 보정한다
        // calculate right range

        if (touchRect.left < cameraViewRect.left) {
            touchRect.left = cameraViewRect.left;
        }


        if (touchRect.right > cameraViewRect.right) {
            touchRect.right = cameraViewRect.right;
        }

        if (touchRect.top < cameraViewRect.top) {
            touchRect.top = cameraViewRect.top;
        }

        if (touchRect.bottom > cameraViewRect.bottom) {
            touchRect.bottom = cameraViewRect.bottom;
        }


        final Rect targetFocusRect = new Rect(
                touchRect.left * 2000 / cameraView.getWidth() - FOCUS_AREA_WEIGHT,
                touchRect.top * 2000 / cameraView.getHeight() - FOCUS_AREA_WEIGHT,
                touchRect.right * 2000 / cameraView.getWidth() - FOCUS_AREA_WEIGHT,
                touchRect.bottom * 2000 / cameraView.getHeight() - FOCUS_AREA_WEIGHT);


        doTouchFocus(targetFocusRect);


        // 사각형의 화면 뷰를 1초간 보여주었다가 없앤다
        // Remove the square indicator after 1000 msec
        drawingView.setHaveTouch(true, touchRect);
        drawingView.invalidate();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                drawingView.setHaveTouch(false, new Rect(0, 0, 0, 0));
                drawingView.invalidate();
            }
        }, 1000);


    }

    public void doTouchFocus(Rect tfocusRect) {
        try {

            // Area 리스트에 넣고 포커스를 잡는다
            focusList = new ArrayList<Camera.Area>();
            Camera.Area focusArea = new Camera.Area(tfocusRect, FOCUS_AREA_WEIGHT);
            focusList.add(focusArea);


            cameraView.autoFocus();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_take_picture) {
            onTakePicture(v);
        }

    }

    private void takePicture() {
        Log.d("gun0912","takePicture()");

        try {
            cameraView.takePicture(false, true);
            btn_take_picture.setEnabled(false);
            animateShutter();
        } catch (IllegalStateException ex) {

            Util.toast(this, getResources().getString(R.string.focusing));

        }


    }



    public void onTakePicture(View view) {
        Log.d("gun0912","onTakePicture()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && focusList == null
                ) {
            cameraView.autoFocus();
        } else {
            takePicture();
        }

    }

    private void animateShutter() {
        vShutter.setVisibility(View.VISIBLE);
        vShutter.setAlpha(0.f);

        ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0f, 0.8f);
        alphaInAnim.setDuration(100);
        alphaInAnim.setStartDelay(100);
        alphaInAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0.8f, 0f);
        alphaOutAnim.setDuration(200);
        alphaOutAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                vShutter.setVisibility(View.GONE);
                mProgressDialog.show();

            }
        });
        animatorSet.start();
    }

    public void showTakenPicture(Uri uri) {

        ImagePickerActivity mImagePickerActivity = ((ImagePickerActivity) getActivity());

        mImagePickerActivity.addImage(uri);

        GalleryFragment mGalleryFragment = mImagePickerActivity.getGalleryFragment();

        if (mGalleryFragment != null) {
            mGalleryFragment.refreshGallery(mImagePickerActivity);
        }


        focusList = null;
        btn_take_picture.setEnabled(true);

        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();


    }

    @Override
    public void onStop() {
        super.onStop();

        // if activity is closed suddenly,
        // dismiss the progress dialog.
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    class MyCameraHost extends SimpleCameraHost {

        final double SIZE_MULTIPLE = 1.5;
        Activity activity;
        Camera.Size bestPictureSize;

        public MyCameraHost(Activity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        protected File getPhotoDirectory() {



            return new File(Environment.getExternalStorageDirectory() + "/"+getResources().getString(mConfig.getSavedDirectoryName())+"/");
        }

        @Override
        public boolean useFullBleedPreview() {
            return true;
        }





        // 미리보기 화면에서 사진 크기 해상도를 미리 가져온다
        @Override
        public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
        Log.d("gun0912","adjustPreviewParameters()");

            bestPictureSize = getBestPictureSize(parameters);
            return super.adjustPreviewParameters(parameters);
        }

        @Override
        public Camera.Size getPictureSize(PictureTransaction xact, Camera.Parameters parameters) {


            //   return super.getPictureSize(xact, parameters);
            if (bestPictureSize == null) {
                bestPictureSize = getBestPictureSize(parameters);
            }

            return bestPictureSize;

        }

        private Camera.Size getBestPictureSize(Camera.Parameters parameters) {
            Log.d("gun0912","getBestPictureSize()");

            Camera.Size result;
            Log.d("gun0912","camera_width: "+camera_width);

            if (camera_width == 0) {
                return CameraUtils.getLargestPictureSize(this, parameters, false);
            }



            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();


            Collections.sort(sizes,
                    Collections.reverseOrder(new SizeComparator()));
            result = sizes.get(sizes.size() - 1);

            for (Camera.Size entry : sizes) {

                if (entry.height >= camera_width * SIZE_MULTIPLE && entry.width >= camera_height * SIZE_MULTIPLE) {
                    result = entry;
                } else {
                    break;
                }


            }
            Log.i("gun0912","result: "+result.height+"x"+result.width);

            return result;

        }




        private Bitmap getCorrectOrientImage(Bitmap bitmap) {



                    bitmap = Util.rotate(bitmap, device_orientation);



            return bitmap;


        }
        private Bitmap getCorrectOrientImage(Bitmap bitmap, String path) {


            ExifInterface exif = null;
            try {

                exif = new ExifInterface(path);


                if (exif != null) {
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    Log.d("gun0912","exifOrientation: "+exifOrientation);
                    int exifDegree = Util.exifOrientationToDegrees(exifOrientation);
                    Log.d("gun0912","exifDegree: "+exifDegree);
                    bitmap = Util.rotate(bitmap, exifDegree);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;


        }


        @Override
        public Camera.Parameters adjustPictureParameters(PictureTransaction xact, Camera.Parameters parameters) {

            if(mConfig.isFlashOn()){
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            }

            return super.adjustPictureParameters(xact, parameters);
        }



        @Override
        public void saveImage(PictureTransaction xact, byte[] image) {


            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

            try {

                final File photo = getPhotoPath();

                if (photo.exists()) {
                    photo.delete();
                }


                // 회전값을 보정한다
                bitmap = Util.rotate(bitmap, device_orientation);
                //bitmap = getCorrectOrientImage(bitmap, photo.toString());
               // bitmap = getCorrectOrientImage(bitmap, photo.toString());

                float ratio = camera_height / camera_width;

                Bitmap crop_bitmap = BitmapUtil.cropCenterBitmap(bitmap, bitmap.getWidth(), (int) (bitmap.getWidth() * ratio));
                FileOutputStream fos;

                fos = new FileOutputStream(photo);

                crop_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();
                fos.close();


                // 사진을 저장한뒤 미디어를 스캔해서 저장한 파일을 읽어온다
                MediaScannerConnection.scanFile(activity, new String[]{photo.getPath()}, new String[]{"image/jpeg"}, new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {

                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showTakenPicture(Uri.parse(photo.toString()));
                            }
                        });

                    }
                });


            } catch (Exception e) {
                handleException(e);

                e.printStackTrace();
            }

        }



        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.d("gun0912","onAutoFocus()");





            try {

                // 터치해서 포커스 잡는 경우
                if (focusList != null) {
                    Log.d("gun0912","터치해서 포커스 잡는 경우");
                    Camera.Parameters param = camera.getParameters();

                    int maxNumFocusAreas = param.getMaxNumFocusAreas();
                    int maxNumMeteringAreas = param.getMaxNumMeteringAreas();

                    param.setFocusAreas(focusList);
                    param.setMeteringAreas(focusList);

                    camera.setParameters(param);
                    super.onAutoFocus(success, camera);
                }
                // 아무터치하지않고 그냥 바로 촬영버튼 누른경우
                else {
                    Log.d("gun0912","아무터치하지않고 그냥 바로 촬영버튼 누른경우");
                    //  super.onAutoFocus(success, camera);
                    takePicture();
                }


            } catch (Exception e) {
                e.printStackTrace();

            }



        }

        private class SizeComparator implements
                Comparator<Camera.Size> {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                int left = lhs.width * lhs.height;
                int right = rhs.width * rhs.height;

                if (left < right) {
                    return (-1);
                } else if (left > right) {
                    return (1);
                }

                return (0);
            }


        }

    }



}