package com.tiago.onlyjokes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class FullscreenFragment extends Fragment {
    static ViewPager viewPager;
    FloatingActionButton fab;
    Button favsBT;
    public FullscreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fullscreen, container, false);
        //MainActivity.screen = "fullscreen";
        ((MainActivity)getActivity()).getSupportActionBar();
        if("All".equals(MainActivity.chosenCategory)){
            MainActivity.chosenCategory="";
        }
        ((MainActivity)getActivity()).getSupportActionBar();
        if (((MainActivity)getActivity()).getSupportActionBar() != null) {
            ((MainActivity)getActivity()).getSupportActionBar().setTitle("Only Jokes");
        }

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).shareJoke();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                //mlayoutmanager.scrollToPositionWithOffset(0,0);
            }
        });

        // 1. get a reference
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);

        PagerAdapter adapter = new FullscreenViewpagerAdapter(getActivity(),MainActivity.dbList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(MainActivity.listPos);
        //viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setPageTransformer(true, (ViewPager.PageTransformer) new DepthPageTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //setButtons();
                MainActivity.listPos = position;
                MainActivity.currentJoke = FullscreenViewpagerAdapter.dbList.get(position).getEmail();
                MainActivity.currentId = FullscreenViewpagerAdapter.dbList.get(position).getJoke();
                ((MainActivity)getActivity()).showInterstitial();

                System.out.println("onPageSelected - position: "+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setHasOptionsMenu(true);
        MainActivity.currentJoke = FullscreenViewpagerAdapter.dbList.get(MainActivity.listPos).getEmail();
        MainActivity.currentId = FullscreenViewpagerAdapter.dbList.get(MainActivity.listPos).getJoke();

        favsBT = (Button)rootView.findViewById(R.id.favsBT);
        //favsBT.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View view) {
        //

        //    }
        //});


        return rootView;
    }




    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        System.out.println("-- onPrepareOptionsMenu -xegay");
        MenuItem item = menu.findItem(R.id.action_list);
        MenuItem item2 = menu.findItem(R.id.action_speak);
        MenuItem item3 = menu.findItem(R.id.action_search);
        item.setVisible(true);
        item2.setVisible(true);
        item3.setVisible(false);
    }
}
