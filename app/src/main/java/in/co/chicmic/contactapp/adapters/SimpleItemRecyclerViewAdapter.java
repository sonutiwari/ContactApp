package in.co.chicmic.contactapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.co.chicmic.contactapp.R;
import in.co.chicmic.contactapp.activities.ItemDetailActivity;
import in.co.chicmic.contactapp.activities.ItemListActivity;
import in.co.chicmic.contactapp.dataModels.Person;
import in.co.chicmic.contactapp.fragments.ItemDetailFragment;
import in.co.chicmic.contactapp.listeners.RecycleViewListener;
import in.co.chicmic.contactapp.utilities.AppConstants;

public class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private final ItemListActivity mParentActivity;
    private final List<Person> mValues;
    private final List<Integer> mSelectedItems;
    private final boolean mTwoPane;
    private final RecycleViewListener mListener;
    private final Context mContext;

    public SimpleItemRecyclerViewAdapter(ItemListActivity pParent, List<Person> pItems
            , boolean pTwoPane, RecycleViewListener pListener, Context pContext, List<Integer> mItems) {
        mValues = pItems;
        mParentActivity = pParent;
        mTwoPane = pTwoPane;
        mListener = pListener;
        mContext = pContext;
        mSelectedItems = mItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup pParent, int pViewType) {
        View view = LayoutInflater.from(pParent.getContext())
                .inflate(R.layout.item_list_content, pParent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder pHolder, int pPosition) {
        if (mValues.get(pPosition).getImageUri() != null) {
            pHolder.mPersonImageView.setImageURI(mValues.get(pPosition).getImageUri());
        } else {
            pHolder.mPersonImageView.setImageResource(mValues.get(pPosition).getPersonImageId());
        }

        if (mSelectedItems.contains(pHolder.getAdapterPosition())) {
            pHolder.mItemView.setBackgroundColor(ContextCompat
                    .getColor(mContext, R.color.selected_background));
        } else {
            pHolder.mItemView.setBackgroundColor(ContextCompat
                    .getColor(mContext, R.color.card_background_color));
        }


        pHolder.mPersonNameTextView.setText(mValues.get(pPosition).getPersonName());
        pHolder.mCompanyNameTextView.setText(mValues.get(pPosition).getPersonCompanyName());

        pHolder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Person person = mValues.get(pHolder.getAdapterPosition());
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(AppConstants.sPERSON, person);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(AppConstants.sPERSON, person);
                    context.startActivity(intent);
                }
            }
        });

        pHolder.mItemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mSelectedItems.contains(pHolder.getAdapterPosition())) {
                    pHolder.mItemView.setBackgroundColor(ContextCompat
                            .getColor(mContext, R.color.card_background_color));
                    mSelectedItems.remove(Integer.valueOf(pHolder.getAdapterPosition()));
                } else {
                    pHolder.mItemView.setBackgroundColor(ContextCompat
                            .getColor(mContext, R.color.selected_background));
                    mSelectedItems.add(pHolder.getAdapterPosition());
                }
                mListener.removeObject(mSelectedItems);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView mPersonImageView;
        final TextView mPersonNameTextView;
        final TextView mCompanyNameTextView;
        final View mItemView;

        ViewHolder(View pView) {
            super(pView);
            mItemView = pView;
            mPersonImageView = pView.findViewById(R.id.img_person_pic);
            mPersonNameTextView = pView.findViewById(R.id.person_name);
            mCompanyNameTextView = pView.findViewById(R.id.company_name);
        }
    }
}