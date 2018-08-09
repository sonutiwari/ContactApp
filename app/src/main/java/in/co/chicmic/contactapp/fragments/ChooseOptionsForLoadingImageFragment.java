package in.co.chicmic.contactapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import in.co.chicmic.contactapp.R;
import in.co.chicmic.contactapp.listeners.ChooseImageLoadingOptions;

public class ChooseOptionsForLoadingImageFragment extends DialogFragment {

    private static ChooseImageLoadingOptions mListener;

    public ChooseOptionsForLoadingImageFragment() {
        // Required empty public constructor
    }

    public static ChooseOptionsForLoadingImageFragment newInstance(
            ChooseImageLoadingOptions pListener) {
        mListener = pListener;
        return new ChooseOptionsForLoadingImageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater pInflater, ViewGroup pContainer,
                             Bundle pSavedInstanceState) {
        View view = pInflater.inflate(R.layout.choose_options_and_load_image
                , pContainer, false);
        LinearLayout cameraOptionSelected = view.findViewById(R.id.camera_option);

        LinearLayout galleryOptionSelected = view.findViewById(R.id.gallery_option);
        cameraOptionSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.clickImageAndLoad();
            }
        });

        galleryOptionSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.loadImageFromGallery();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
