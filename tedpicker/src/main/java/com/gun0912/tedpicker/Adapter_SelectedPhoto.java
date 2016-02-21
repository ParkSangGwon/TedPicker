package com.gun0912.tedpicker;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gun0912.tedpicker.custom.adapter.BaseRecyclerViewAdapter;


/**
 * Created by TedPark on 16. 2. 20..
 */
public class Adapter_SelectedPhoto extends BaseRecyclerViewAdapter<Uri, Adapter_SelectedPhoto.SelectedPhotoHolder> {



    int closeImageRes;

    ImagePickerActivity imagePickerActivity;

    public Adapter_SelectedPhoto(ImagePickerActivity imagePickerActivity, int closeImageRes) {
        super(imagePickerActivity);
        this.imagePickerActivity = imagePickerActivity;
        this.closeImageRes = closeImageRes;


    }

    @Override
    public void onBindView(SelectedPhotoHolder holder, int position) {

        Uri uri = getItem(position);



        Glide.with(imagePickerActivity)
                .load(uri.toString())
             //   .override(selected_bottom_size, selected_bottom_size)
                .dontAnimate()
                .centerCrop()
                .error(R.drawable.no_image)
                .into(holder.selected_photo);




        holder.iv_close.setTag(uri);



    }

    @Override
    public SelectedPhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(getContext());
        View view = mInflater.inflate(R.layout.picker_list_item_selected_thumbnail, parent, false);
        return new SelectedPhotoHolder(view);
    }





    class SelectedPhotoHolder extends RecyclerView.ViewHolder {


        ImageView selected_photo;
        ImageView iv_close;


        public SelectedPhotoHolder(View itemView) {
            super(itemView);
            selected_photo = (ImageView) itemView.findViewById(R.id.selected_photo);
            iv_close = (ImageView) itemView.findViewById(R.id.iv_close);
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = (Uri) view.getTag();
                    imagePickerActivity.removeImage(uri);
                }
            });

        }





    }
}
