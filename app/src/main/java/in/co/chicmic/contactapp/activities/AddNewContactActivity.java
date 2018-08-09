package in.co.chicmic.contactapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.co.chicmic.contactapp.R;
import in.co.chicmic.contactapp.dataModels.Person;
import in.co.chicmic.contactapp.fragments.ChooseOptionsForLoadingImageFragment;
import in.co.chicmic.contactapp.listeners.ChooseImageLoadingOptions;
import in.co.chicmic.contactapp.utilities.AppConstants;

public class AddNewContactActivity extends AppCompatActivity
        implements ChooseImageLoadingOptions, View.OnClickListener {
    private ImageView mProfileImageView;
    private DialogFragment mDialog;
    private EditText mPersonName;
    private EditText mCompanyName;
    private Uri mFileUri = null;
    private boolean isInputValid = false;
    private Person mPerson;


    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        setContentView(R.layout.item_detail);

        findViewById(R.id.tool_bar).setVisibility(View.VISIBLE);
        Toolbar toolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mProfileImageView = findViewById(R.id.img_person_pic);
        Button mDoneButton = findViewById(R.id.done_button);
        mPersonName = findViewById(R.id.person_name);
        mCompanyName = findViewById(R.id.company_name);

        mPersonName.setFocusableInTouchMode(true);
        mCompanyName.setFocusableInTouchMode(true);
        if (getIntent().getExtras() != null) {
            Person person = getIntent().getExtras().getParcelable(AppConstants.sPERSON);
            assert person != null;
            mPerson = person;
            if (person.getImageUri() != null) {
                mFileUri = person.getImageUri();
                mProfileImageView.setImageURI(person.getImageUri());
            } else {
                mProfileImageView.setImageResource(person.getPersonImageId());
            }
            mPersonName.setText(person.getPersonName());
            mCompanyName.setText(person.getPersonCompanyName());
        }
        mDoneButton.setVisibility(View.VISIBLE);
        mDoneButton.setOnClickListener(this);
        mProfileImageView.setOnClickListener(this);
    }

    private void loadFragmentToChooseOptions() {
        mDialog = ChooseOptionsForLoadingImageFragment.newInstance(this);
        mDialog.show(getSupportFragmentManager(), AppConstants.sIMAGE_SELECT_OR_CLICK);
    }

    @Override
    public void clickImageAndLoad() {
        mDialog.dismiss();
        try {
            mFileUri = FileProvider.getUriForFile(this,
                    getString(R.string.provider_id), createImageFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);//Send fileUri with intent
        startActivityForResult(takePicture, AppConstants.sCLICK_IMAGE);
    }

    @Override
    public void loadImageFromGallery() {
        mDialog.dismiss();
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
        startActivityForResult(pickPhoto, AppConstants.sCHOOSE_IMAGE_FROM_GALLERY);
    }

    protected void onActivityResult(int pRequestCode
            , int pResultCode, Intent pImageReturnedIntent) {
        super.onActivityResult(pRequestCode, pResultCode, pImageReturnedIntent);
        switch (pRequestCode) {
            case AppConstants.sCLICK_IMAGE:
                if (pResultCode == RESULT_OK) {
                    if (mFileUri != null)
                        mProfileImageView.setImageURI(mFileUri);
                } else {
                    mFileUri = mPerson.getImageUri();
                }
                break;
            case AppConstants.sCHOOSE_IMAGE_FROM_GALLERY:
                if (pResultCode == RESULT_OK) {
                    mFileUri = pImageReturnedIntent.getData();
                    mFileUri = Uri.parse(getImagePath(mFileUri));
                    mProfileImageView.setImageURI(mFileUri);
                } else{
                    mFileUri = mPerson.getImageUri();
                }
                break;
        }
    }

    @Override
    public void onClick(View pView) {
        switch (pView.getId()) {
            case R.id.img_person_pic:
                if(android.os.Build.VERSION.SDK_INT  >= Build.VERSION_CODES.JELLY_BEAN)
                    requestRead();
                else
                    loadFragmentToChooseOptions();
                break;
            case R.id.done_button:
                checkInputs();
                saveAndExit();
                break;
        }
    }

    private void checkInputs() {
        if (mPersonName.getText().toString().trim().length() < 3) {
            mPersonName.setError(getString(R.string.valid_name_warning));
        } else if (mCompanyName.getText().toString().trim().length() < 3) {
            mCompanyName.setError(getString(R.string.valid_company_name_warning));
        } else {
            isInputValid = true;
        }
    }

    private void saveAndExit() {
        if (isInputValid) {
            Person person = new Person(R.drawable.ic_person, mPersonName.getText().toString().trim()
                    , mCompanyName.getText().toString().trim(), mFileUri); //Uri.parse(imageFilePath)
            Intent data = new Intent();
            data.putExtra(AppConstants.sPERSON, person);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem pItem) {
        if (pItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(pItem);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable(AppConstants.sFILE_URI, mFileUri);
    }

    /*
     * Here we restore the mFileUri again
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url.
        mFileUri = savedInstanceState.getParcelable(AppConstants.sFILE_URI);
    }

    String imageFilePath;
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        imageFilePath = "file://" + image.getAbsolutePath();
        return image;
    }

    public String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null
                , null, null);
        assert cursor != null;
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? "
                , new String[]{document_id}, null);
        assert cursor != null;
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    AppConstants.sMY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            loadFragmentToChooseOptions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {

        if (requestCode == AppConstants.sMY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadFragmentToChooseOptions();
            } else {
                // Permission Denied
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
