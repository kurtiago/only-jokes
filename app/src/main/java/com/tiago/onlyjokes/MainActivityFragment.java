package com.tiago.onlyjokes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements CategoriesRecyclerAdapter.OnItemClickListener{
    private CategoriesRecyclerAdapter myCategoriesRecyclerAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("-- MainActivityFragment -- onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ((MainActivity)getActivity()).getSupportActionBar();
        if (((MainActivity)getActivity()).getSupportActionBar() != null) {
            ((MainActivity)getActivity()).getSupportActionBar().setTitle("Only Jokes");
        }
        //MainActivity.screen = "home";
        MainActivity.filterText = "";
        MainActivity.listPos=0;
        // 1. get a reference to recyclerView
        RecyclerView myRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleview);

        // 2. set layoutManger
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        myRecyclerView.setLayoutManager(llm);


        // 3. create an adapter
        myCategoriesRecyclerAdapter = new CategoriesRecyclerAdapter(getActivity());
        myCategoriesRecyclerAdapter.setOnItemClickListener(this);
        myRecyclerView.setAdapter(myCategoriesRecyclerAdapter);
        //myRecyclerView.setLayoutManager(linearLayoutManager);        // 4. set adapter
        //recyclerView.setAdapter(mAdapter);
        // 5. set item animator to DefaultAnimator
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //myCategoriesRecyclerAdapter.add(0, "Long Jokes", R.drawable.pic_long);
        //myCategoriesRecyclerAdapter.add(0, "Dirty Jokes");
        //myCategoriesRecyclerAdapter.add(0, "Insults");
        //myCategoriesRecyclerAdapter.add(0, "Pick-up lines", R.drawable.pic_momma);
        //if(MainActivity.isPro){
        //    myCategoriesRecyclerAdapter.add(0, "Adult", R.drawable.pic_momma);
        //}else{
        //    myCategoriesRecyclerAdapter.add(0, "Adult (locked)", R.drawable.pic_momma);
        //}
        myCategoriesRecyclerAdapter.add(0, "Adult", R.drawable.pic_adult);
        myCategoriesRecyclerAdapter.add(0, "Yo Momma", R.drawable.pic_momma);
        myCategoriesRecyclerAdapter.add(0, "Intellectual", R.drawable.pic_brain);//v1.2
        myCategoriesRecyclerAdapter.add(0, "Bumper Stickers", R.drawable.pic_car);
        myCategoriesRecyclerAdapter.add(0, "Quotes", R.drawable.pic_quote);
        myCategoriesRecyclerAdapter.add(0, "Chuck Norris", R.drawable.pic_boot);
        myCategoriesRecyclerAdapter.add(0, "Sports", R.drawable.pic_sport);
        myCategoriesRecyclerAdapter.add(0, "Daily life", R.drawable.pic_daily);
        myCategoriesRecyclerAdapter.add(0, "One-liners", R.drawable.pic_oneliner);
        myCategoriesRecyclerAdapter.add(0, "Politics", R.drawable.pic_politics);
        myCategoriesRecyclerAdapter.add(0, "Bar", R.drawable.pic_bar);
        myCategoriesRecyclerAdapter.add(0, "Animals", R.drawable.pic_animal);
        myCategoriesRecyclerAdapter.add(0, "Kids", R.drawable.pic_toy);
        myCategoriesRecyclerAdapter.add(0, "Doctor", R.drawable.pic_doctor);
        myCategoriesRecyclerAdapter.add(0, "Marriage", R.drawable.pic_wedding);
        myCategoriesRecyclerAdapter.add(0, "School", R.drawable.pic_school);
        myCategoriesRecyclerAdapter.add(0, "Favorites", R.drawable.ic_star_checked);//v1.2
        //myCategoriesRecyclerAdapter.add(0, "Dark Humor");
        //myCategoriesRecyclerAdapter.add(0, "Blonde");
        //myCategoriesRecyclerAdapter.add(0, "Corny Jokes");
        //myCategoriesRecyclerAdapter.add(0, "Riddles");
        //myCategoriesRecyclerAdapter.add(0, "All", R.drawable.ic_about);
        //myCategoriesRecyclerAdapter.notifyDataSetChanged();
        //test();
        return rootView;
    }


    //public void test(){
    //    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    //    alertDialog.setTitle("Alert");
    //    alertDialog.setMessage("Alert message to be shown");
    //    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
    //            new DialogInterface.OnClickListener() {
    //                public void onClick(DialogInterface dialog, int which) {
    //                    dialog.dismiss();
    //                    myCategoriesRecyclerAdapter.add(0, "Favorites", R.drawable.ic_star_checked);
    //                    myCategoriesRecyclerAdapter.notifyDataSetChanged();
    //                }
    //            });
    //    alertDialog.show();
    //}

    @Override
    public void onItemClick(CategoriesRecyclerAdapter.ItemHolder item, int position) {
        MainActivity.chosenCategory = item.getItemName().toString();
        ((MainActivity)getActivity()).initialiseDatabase();
        MainActivity.listPos = 0;
        MainActivity.shouldShuffleList=true;
        /**if(MainActivity.chosenCategory.equals("Search")){
            ((MainActivity)getActivity()).inflateFragment("search");
        }else */
        switch (MainActivity.chosenCategory) {
            case "Favorites":
                if (MainActivity.favsList.size() > 0) {
                    ((MainActivity) getActivity()).inflateFragment("favorites");
                } else {
                    ((MainActivity) getActivity()).showToastMessage("Add some favorites first!");
                }
                break;
            case "Adult":
                if (MainActivity.isPro) {
                    ((MainActivity) getActivity()).inflateFragment("fullscreen");
                } else {
                    ((MainActivity) getActivity()).showToastMessage("Buy pro version to unlock adult jokes.");
                }
                break;
            default:
                ((MainActivity) getActivity()).inflateFragment("fullscreen");
                break;
        }

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        //Bundle payload = new Bundle();
        //payload.putString(FirebaseAnalytics.Param.VALUE, MainActivity.chosenCategory);
        //mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, payload);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "category");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, MainActivity.chosenCategory);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        //System.out.println("-- MainActivityFragment -onItemClick - chosenCategory: "+MainActivity.chosenCategory);
    }

}
