package be.vives.recipeshunter.fragments;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.activities.MainActivity;

/**
 * Fragment containing a search box.
 */
public class SearchRecipesFragment extends Fragment {
    private OnSearchSubmitFragmentListener mListener;
    private EditText mSearchEditText;
    private Button mSearchSubmitButton;

    public SearchRecipesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_recipes, container, false);

        mSearchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        mSearchSubmitButton = (Button) view.findViewById(R.id.search_submit_button);

        mSearchSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                mListener.setSearchQuery(mSearchEditText.getText().toString());
                mListener.navigateFromSearchSubmitFragment();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSearchSubmitFragmentListener) activity;
        } catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + OnSearchSubmitFragmentListener.class);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }

    public interface OnSearchSubmitFragmentListener {
        void setSearchQuery(String query);
        void navigateFromSearchSubmitFragment();
    }
}
