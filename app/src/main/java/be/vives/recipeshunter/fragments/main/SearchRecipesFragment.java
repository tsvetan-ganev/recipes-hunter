package be.vives.recipeshunter.fragments.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import be.vives.recipeshunter.R;
import be.vives.recipeshunter.activities.FavouritesActivity;

/**
 * Fragment containing a search box.
 */
public class SearchRecipesFragment extends Fragment {

    // widgets
    private EditText mSearchEditText;
    private Button mSearchSubmitButton;
    private Button mGoToFavouritesButton;

    // interaction listener for MainActivity
    private OnSearchSubmitFragmentListener mListener;

    public SearchRecipesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search_recipes, container, false);

        mSearchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        mSearchSubmitButton = (Button) view.findViewById(R.id.search_submit_button);
        mGoToFavouritesButton = (Button) view.findViewById(R.id.search_recipe_favourites_button);


        mSearchSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                if (deviceHasInternetAccess()) {
                    mListener.setSearchQuery(mSearchEditText.getText().toString());
                    mListener.navigateFromSearchSubmitFragment();
                } else  {
                    Snackbar snackbar = Snackbar.make(view, "You don't have internet access.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });

        mGoToFavouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent favouritesIntent = new Intent(getContext(), FavouritesActivity.class);
                startActivity(favouritesIntent);
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

    private boolean deviceHasInternetAccess() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public interface OnSearchSubmitFragmentListener {
        void setSearchQuery(String query);

        void navigateFromSearchSubmitFragment();
    }
}
