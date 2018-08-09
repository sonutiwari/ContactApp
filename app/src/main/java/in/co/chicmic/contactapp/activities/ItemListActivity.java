package in.co.chicmic.contactapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.co.chicmic.contactapp.R;
import in.co.chicmic.contactapp.adapters.SimpleItemRecyclerViewAdapter;
import in.co.chicmic.contactapp.dataModels.Person;
import in.co.chicmic.contactapp.fragments.AddContactFragment;
import in.co.chicmic.contactapp.listeners.FragmentAddEditResultListener;
import in.co.chicmic.contactapp.listeners.RecycleViewListener;
import in.co.chicmic.contactapp.utilities.AppConstants;

public class ItemListActivity extends AppCompatActivity
        implements RecycleViewListener, View.OnClickListener, FragmentAddEditResultListener {

    private final ArrayList<Person> mPersonArrayList = new ArrayList<>();
    private boolean mTwoPane;
    private SimpleItemRecyclerViewAdapter mAdapter;
    private TextView mEditOptionTextView;
    private TextView mDeleteOptionTextView;
    private ImageButton mCloseImageButton;
    private List<Integer> mItems = new ArrayList<>();
    private AddContactFragment mFragment;
    private boolean mIsDataSortedInAscending = false;
    private int mIndex = 0;
    private boolean mIsEdit = false;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar mToolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(mToolbar);
        mEditOptionTextView = mToolbar.findViewById(R.id.edit);
        mDeleteOptionTextView = mToolbar.findViewById(R.id.delete);
        mCloseImageButton = mToolbar.findViewById(R.id.btn_close);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = findViewById(R.id.fab);

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }
        fab.setOnClickListener(this);
        RecyclerView mRecyclerView = findViewById(R.id.item_list);
        setupRecyclerView(mRecyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView pRecyclerView) {
        setDummyData();
        mAdapter = new SimpleItemRecyclerViewAdapter(this
                , mPersonArrayList, mTwoPane, this, this, mItems);
        pRecyclerView.setAdapter(mAdapter);
    }

    private void setDummyData() {
        for (int i = 0; i < 30; i++) {
            mPersonArrayList
                    .add(new Person(R.drawable.ic_person
                            , getString(R.string.person) + i
                            , getString(R.string.person_company_name), null));
        }
    }

    @Override
    public void removeObject(List<Integer> pItems) {
        mItems = pItems;
        showEditOptions();
        if (mItems.size() > 1) {
            mEditOptionTextView.setVisibility(View.GONE);
        } else {
            mEditOptionTextView.setVisibility(View.VISIBLE);
        }
        if (pItems.size() == 0) {
            hideEditOptions();
        }
        mEditOptionTextView.setOnClickListener(this);
        mDeleteOptionTextView.setOnClickListener(this);
        mCloseImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View pView) {
        switch (pView.getId()) {
            case R.id.fab:
                mIsEdit = false;
                mItems.clear();
                removeObject(mItems);
                mAdapter.notifyDataSetChanged();
                if (mTwoPane) {
                    mFragment = new AddContactFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, mFragment)
                            .commit();
                } else {
                    startActivityForResult(new Intent(ItemListActivity.this
                            , AddNewContactActivity.class), AppConstants.sADD_CONTACTS);
                }
                break;
            case R.id.delete:
                mIsEdit = false;
                showAlertAndDelete();
                break;
            case R.id.edit:
                mIndex = mItems.get(0);
                mIsEdit = true;
                if (mTwoPane) {
                    mFragment = new AddContactFragment();
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(AppConstants.sPERSON
                            , mPersonArrayList.get(mItems.get(0)));
                    mFragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, mFragment)
                            .commit();
                    hideEditOptions();
                } else {
                    Intent intent = new Intent(ItemListActivity.this
                            , AddNewContactActivity.class);
                    intent.putExtra(AppConstants.sPERSON, mPersonArrayList.get(mItems.get(0)));
                    startActivityForResult(intent, AppConstants.sEDIT_CONTACT);
                }
                break;
            case R.id.btn_close:
                mIsEdit = false;
                hideEditOptions();
                clearListAndNotifyAdapter();
        }

    }

    private void showAlertAndDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.danger_dialog);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Collections.sort(mItems);
                for (int index = 0; index < mItems.size(); index++) {
                    int indexToBeDeleted = mItems.get(index);
                    mPersonArrayList.remove(indexToBeDeleted - index);
                }
                clearListAndNotifyAdapter();
                hideEditOptions();
                if (getSupportFragmentManager().findFragmentById(R.id.item_detail_container) != null) {
                    getSupportFragmentManager().beginTransaction().
                            remove(getSupportFragmentManager()
                                    .findFragmentById(R.id.item_detail_container)).commit();
                }
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hideEditOptions();
                clearListAndNotifyAdapter();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int pRequestCode, int pResultCode, Intent pData) {
        if (pResultCode == RESULT_OK && pRequestCode == AppConstants.sEDIT_CONTACT) {
            hideEditOptions();
            if (pData.hasExtra(AppConstants.sPERSON)) {
                mPersonArrayList.set(mItems.get(0)
                        , (Person) pData.getParcelableExtra(AppConstants.sPERSON));
                sortData(mIsDataSortedInAscending);
                clearListAndNotifyAdapter();
            }
        }
        if (pResultCode == RESULT_OK && pRequestCode == AppConstants.sADD_CONTACTS) {
            if (pData.hasExtra(AppConstants.sPERSON)) {
                mPersonArrayList.add((Person) pData.getParcelableExtra(AppConstants.sPERSON));
                sortData(mIsDataSortedInAscending);
                mAdapter.notifyDataSetChanged();
            }
        }
        if (pResultCode == RESULT_OK && pRequestCode == AppConstants.sCLICK_IMAGE) {
            mFragment.setImageUri();
        }
        if (pResultCode == RESULT_OK && pRequestCode == AppConstants.sCHOOSE_IMAGE_FROM_GALLERY) {
            AddContactFragment.mFileUri = pData.getData();
            AddContactFragment.mFileUri = Uri.parse(getImagePath(AddContactFragment.mFileUri));
            mFragment.setImageUri();
        }
    }

    private void showEditOptions() {
        mEditOptionTextView.setVisibility(View.VISIBLE);
        mDeleteOptionTextView.setVisibility(View.VISIBLE);
        mCloseImageButton.setVisibility(View.VISIBLE);
    }

    private void hideEditOptions() {
        mEditOptionTextView.setVisibility(View.GONE);
        mDeleteOptionTextView.setVisibility(View.GONE);
        mCloseImageButton.setVisibility(View.GONE);
    }

    @Override
    public void setData(Person pPerson, boolean pIsEdit) {
        if (!mIsEdit) {
            mPersonArrayList.add(pPerson);
            sortData(mIsDataSortedInAscending);
            mAdapter.notifyDataSetChanged();
        } else {
            mPersonArrayList.remove(mIndex);
            mPersonArrayList.add(pPerson);
            sortData(mIsDataSortedInAscending);
            mAdapter.notifyDataSetChanged();
        }
        clearListAndNotifyAdapter();
    }

    private void sortData(final boolean pSortedAscending) {
        Collections.sort(mPersonArrayList, new Comparator<Person>() {
            @Override
            public int compare(Person person, Person person2) {
                if (pSortedAscending) {
                    return person.getPersonName().compareToIgnoreCase(person2.getPersonName());
                } else {
                    return (-1 * person.getPersonName()
                            .compareToIgnoreCase(person2.getPersonName()));
                }
            }
        });
    }

    public String getImagePath(Uri pUri){
        Cursor cursor = this.getContentResolver().query(pUri, null, null
                , null, null);
        assert cursor != null;
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();
        cursor = this.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? "
                , new String[]{document_id}, null);
        assert cursor != null;
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }
    private void clearListAndNotifyAdapter() {
        mItems.clear();
        mAdapter.notifyDataSetChanged();
    }
}
