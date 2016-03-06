/*
 * Copyright (c) 2016. Ted Park. All Rights Reserved
 */

package com.gun0912.tedpicker;

import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Created by jay on 3/5/15.
 */
public class Config {

    private int toolbarTitleRes = R.string.toolbar_title;

    private int tabBackgroundColor;
    private int tabSelectionIndicatorColor;

    private int selectedBottomColor;

    private int selectionLimit = Integer.MAX_VALUE;
    private int selectionMin = 0;

    private int cameraHeight = R.dimen.ted_picker_camera_height;

    private int cameraBtnImage = R.drawable.ic_camera;
    private int cameraBtnBackground = R.drawable.btn_bg;

    private int selectedCloseImage = R.drawable.ic_clear;
    private int selectedBottomHeight = R.dimen.ted_picker_selected_image_height;

    private int savedDirectoryName = R.string.default_directory;


    private boolean flashOn = false;

    public int getCameraHeight() {
        return cameraHeight;
    }

    public void setCameraHeight(@DimenRes int dimenRes) {
        if (dimenRes <= 0)
            throw new IllegalArgumentException("Invalid value for cameraHeight");

        this.cameraHeight = dimenRes;
    }

    public int getSavedDirectoryName() {
        return savedDirectoryName;
    }

    public void setSavedDirectoryName(@StringRes int stringRes) {
        if (stringRes <= 0)
            throw new IllegalArgumentException("Invalid value for savedDirectoryName");

        this.savedDirectoryName = stringRes;
    }

    public int getSelectedBottomHeight() {
        return selectedBottomHeight;
    }

    public void setSelectedBottomHeight(@DimenRes int dimenRes) {
        if (dimenRes <= 0)
            throw new IllegalArgumentException("Invalid value for selectedBottomHeight");

        this.selectedBottomHeight = dimenRes;
    }

    public int getSelectedBottomColor() {
        return selectedBottomColor;
    }

    public void setSelectedBottomColor(@ColorRes int colorRes) {
        if (colorRes <= 0)
            throw new IllegalArgumentException("Invalid value for selectedBottomColor");

        this.selectedBottomColor = colorRes;
    }

    public int getToolbarTitleRes() {
        return toolbarTitleRes;
    }

    public void setToolbarTitleRes(@StringRes int toolbarTitleRes) {
        if (toolbarTitleRes <= 0)
            throw new IllegalArgumentException("Invalid value for toolbarTitleRes");

        this.toolbarTitleRes = toolbarTitleRes;
    }


    public int getTabBackgroundColor() {
        return tabBackgroundColor;
    }

    public void setTabBackgroundColor(@ColorRes int colorRes) {
        if (colorRes <= 0)
            throw new IllegalArgumentException("Invalid value for tabBackgroundColor");


        this.tabBackgroundColor = colorRes;

    }


    public int getTabSelectionIndicatorColor() {
        return tabSelectionIndicatorColor;
    }

    /**
     * Sets selected tab indicator color.
     */
    public void setTabSelectionIndicatorColor(@ColorRes int colorRes) {
        if (colorRes <= 0) throw new IllegalArgumentException("Invalid value for tabSelectionIndicatorColor");


        this.tabSelectionIndicatorColor = colorRes;

    }

    public int getSelectionLimit() {
        return selectionLimit;
    }

    /**
     * Limit the number of images that can be selected. By default the user can
     * select infinite number of images.
     *
     * @param selectionLimit
     */
    public void setSelectionLimit(int selectionLimit) {
        this.selectionLimit = selectionLimit;

    }

    public int getSelectionMin() {
        return selectionMin;
    }

    public void setSelectionMin(int selectionmin) {
        selectionMin = selectionmin;

    }


    public int getCameraBtnImage() {
        return cameraBtnImage;
    }

    public void setCameraBtnImage(@DrawableRes int drawableRes) {
        if (drawableRes <= 0) throw new IllegalArgumentException("Invalid value for cameraBtnImage");
        this.cameraBtnImage = drawableRes;
    }

    public int getCameraBtnBackground() {
        return cameraBtnBackground;
    }

    public void setCameraBtnBackground(@DrawableRes int drawableRes) {
        if (drawableRes <= 0) throw new IllegalArgumentException("Invalid value for cameraBtnBackground");
        this.cameraBtnBackground = drawableRes;
    }


    public int getSelectedCloseImage() {
        return selectedCloseImage;
    }

    public void setSelectedCloseImage(@DrawableRes int drawableRes) {
        if (drawableRes <= 0) throw new IllegalArgumentException("Invalid value for selectedCloseImage");

        this.selectedCloseImage = drawableRes;
    }


    public boolean isFlashOn(){
        return flashOn;
    }

    public void setFlashOn(boolean flashOn){
        this.flashOn = flashOn;
    }
}
