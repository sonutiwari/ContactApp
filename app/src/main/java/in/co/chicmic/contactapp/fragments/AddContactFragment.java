package in.co.chicmic.contactapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import in.co.chicmic.contactapp.R;
import in.co.chicmic.contactapp.dataModels.Person;
import in.co.chicmic.contactapp.listeners.ChooseImageLoadingOptions;
import in.co.chicmic.contactapp.listeners.FragmentAddEditResultListener;
import in.co.chicmic.contactapp.utilities.AppConstants;
import in.co.chicmic.contactapp.utilities.CameraUtils;

public class AddContactFragment extends Fragment
        implements View.OnClickListener, ChooseImageLoadingOptions {
    public static Uri mFileUri = null;
    private DialogFragment mDialog;
    private ImageView mProfileImageView;
    private EditText mPersonName;
    private EditText mCompanyName;
    private TextInputLayout mPersonNameTextInputLayout;
    private TextInputLayout mCompanyNameTextInputLayout;
    private FragmentAddEditResultListener mListener;
    private boolean mIsEdit = false;
    private boolean mIsInputValid = false;


    public AddContactFragment() {
    }

    @Override
    public void onAttach(Context pContext) {
        super.onAttach(pContext);
        mListener = (FragmentAddEditResultListener) pContext;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater pInflater, ViewGroup pContainer,
                             Bundle pSavedInstanceState) {
        View view = pInflater.inflate(R.layout.item_detail, pContainer, false);
        Button doneButton = view.findViewById(R.id.done_button);
        doneButton.setVisibility(View.VISIBLE);
        mPersonName = view.findViewById(R.id.person_name);
        mPersonName.setFocusableInTouchMode(true);
        mCompanyName = view.findViewById(R.id.company_name);
        mCompanyName.setFocusableInTouchMode(true);
        mProfileImageView = view.findViewById(R.id.img_person_pic);
        mPersonNameTextInputLayout = view.findViewById(R.id.person_name_text_input_layout);
        mCompanyNameTextInputLayout = view.findViewById(R.id.company_name_text_input_layout);
        if (getArguments() != null) {
            mIsEdit = true;
            Person person = getArguments().getParcelable(AppConstants.sPERSON);
            assert person != null;
            if (person.getImageUri() != null) {
                mProfileImageView.setImageURI(person.getImageUri());
            } else {
                mProfileImageView.setImageResource(person.getPersonImageId());
            }
            mPersonNameTextInputLayout.setHintAnimationEnabled(false);
            mCompanyNameTextInputLayout.setHintAnimationEnabled(false);
            mPersonName.setText(person.getPersonName());
            mCompanyName.setText(person.getPersonCompanyName());
        }
        mProfileImageView.setOnClickListener(this);
        doneButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View pView) {
        switch (pView.getId()) {
            case R.id.img_person_pic:
                if(android.os.Build.VERSION.SDK_INT  >= Build.VERSION_CODES.JELLY_BEAN)
                    requestRead();
                else
                    selectOrClickPhotoAndSave();
                break;
            case R.id.done_button:
                checkInputs();
                saveAndExit();
                break;
        }
    }

    private void saveAndExit() {
        if (mIsInputValid){
            Person person = new Person(R.drawable.ic_person, mPersonName.getText().toString()
                    , mCompanyName.getText().toString(), mFileUri);
            mListener.setData(person, mIsEdit);
            assert getActivity() != null;
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    private void checkInputs() {
        if (mPersonName.getText().toString().trim().length() < 3){
            mPersonName.setError(getString(R.string.valid_name_warning));
        } else if (mCompanyName.getText().toString().trim().length() < 3){
            mCompanyName.setError(getString(R.string.valid_company_name_warning));
        } else {
            mIsInputValid = true;
        }
    }

    private void selectOrClickPhotoAndSave() {
        mDialog = ChooseOptionsForLoadingImageFragment.newInstance(this);
        assert getActivity() != null;
        mDialog.show(getChildFragmentManager()
                , AppConstants.sIMAGE_SELECT_OR_CLICK);
    }

    @Override
    public void clickImageAndLoad() {
        mDialog.dismiss();
        assert getContext() != null;
        mFileUri = CameraUtils.getOutputMediaFileUri(getContext());
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
        assert getActivity() != null;
        getActivity().startActivityForResult(takePicture, AppConstants.sCLICK_IMAGE);
    }

    @Override
    public void loadImageFromGallery() {
        mDialog.dismiss();
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
        assert getActivity() != null;
        getActivity().startActivityForResult(pickPhoto, AppConstants.sCHOOSE_IMAGE_FROM_GALLERY);
    }

    public void setImageUri() {
        mProfileImageView.setImageURI(mFileUri);
    }

    /**
     * requestPermissions and do something
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void requestRead() {
        assert getActivity() != null;
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    AppConstants.sMY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            selectOrClickPhotoAndSave();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {

        if (requestCode == AppConstants.sMY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectOrClickPhotoAndSave();
            } else {
                // Permission Denied
                Toast.makeText(getContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
