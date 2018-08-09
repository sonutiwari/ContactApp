package in.co.chicmic.contactapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import in.co.chicmic.contactapp.R;
import in.co.chicmic.contactapp.dataModels.Person;
import in.co.chicmic.contactapp.utilities.AppConstants;

public class ItemDetailFragment extends Fragment {

    private TextInputLayout mPersonNameTextInputLayout;
    private TextInputLayout mCompanyNameTextInputLayout;
    public ItemDetailFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater pInflater, ViewGroup pContainer,
                             Bundle pSavedInstanceState) {
        View view = pInflater.inflate(R.layout.item_detail, pContainer, false);

        mPersonNameTextInputLayout = view.findViewById(R.id.person_name_text_input_layout);
        mPersonNameTextInputLayout.setHintAnimationEnabled(false);
        mCompanyNameTextInputLayout = view.findViewById(R.id.company_name_text_input_layout);
        mCompanyNameTextInputLayout.setHintAnimationEnabled(false);

        assert getArguments() != null;
        Person person = getArguments().getParcelable(AppConstants.sPERSON);
        EditText personName = view.findViewById(R.id.person_name);
        assert person != null;
        personName.setText(person.getPersonName());
        EditText companyName = view.findViewById(R.id.company_name);
        companyName.setText(person.getPersonCompanyName());
        ImageView profileImageView = view.findViewById(R.id.img_person_pic);
        if (person.getImageUri() != null)
            profileImageView.setImageURI(person.getImageUri());
        else
            profileImageView.setImageResource(person.getPersonImageId());
        personName.setEnabled(false);
        companyName.setEnabled(false);
        return view;
    }
}
