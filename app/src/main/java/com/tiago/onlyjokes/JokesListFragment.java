package com.tiago.onlyjokes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class JokesListFragment extends Fragment {
    RecyclerView myRecyclerView;
    private JokesRecyclerAdapter myJokesRecyclerAdapter;
    EditText filterET;
    public JokesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_jokes, container, false);
        System.out.println("-- cheguei onCreateView3 - screen: "+MainActivity.screen);
        //MainActivity.screen = "jokes";
        System.out.println("-- MainActivityFragment -- onCreateView");
        filterET = (EditText)rootView.findViewById(R.id.filterET);

        ((MainActivity)getActivity()).getSupportActionBar();
        CardView filterCV = (CardView)rootView.findViewById(R.id.cardviewFilter);
        System.out.println("-- cheguei onCreateView4 - screen: "+MainActivity.screen);

        String title = "";
        if("search".equals(MainActivity.screen)){
            MainActivity.chosenCategory="";
            ((MainActivity)getActivity()).initialiseDatabase();
            //filterET.setVisibility(View.VISIBLE);
            filterCV.setVisibility(View.VISIBLE);
            if("".equals(MainActivity.filterText)){
                title = "All jokes ("+MainActivity.dbList.size()+")";
            }else{
                filterET.setText(MainActivity.filterText);
            }
        }else if("favorites".equals(MainActivity.screen)){
            ((MainActivity)getActivity()).initialiseDatabase();

            title = "Favorites ("+MainActivity.favsList.size()+")";
            filterCV.setVisibility(View.GONE);

        }else{

            title = MainActivity.chosenCategory+" ("+MainActivity.dbList.size()+")";
            filterCV.setVisibility(View.GONE);
        }

        //set title according to conditions above
        if (((MainActivity)getActivity()).getSupportActionBar() != null) {
            ((MainActivity)getActivity()).getSupportActionBar().setTitle(title);
        }
        // 1. get a reference to recyclerView
        myRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        filterET.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                MainActivity.filterText = s.toString();
                ((MainActivity)getActivity()).initialiseDatabase();
                initialiseList();

                if (((MainActivity)getActivity()).getSupportActionBar() != null) {
                    ((MainActivity)getActivity()).getSupportActionBar().setTitle("Results: ("+MainActivity.dbList.size()+")");
                }
                //System.out.println("cheguei afterTextChanged" + listView.getAdapter().getCount());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //System.out.println("cheguei beforeTextChanged" + listView.getAdapter().getCount());
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //System.out.println("cheguei onTextChanged" + listView.getAdapter().getCount());
            }
        });
        initialiseList();
        return rootView;
    }

    public void initialiseList(){
        // 2. set layoutManger
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        myRecyclerView.setLayoutManager(llm);
        // 3. create an adapter
        myJokesRecyclerAdapter = new JokesRecyclerAdapter(getActivity(),MainActivity.dbList);
        // 4. set adapter
        myRecyclerView.setAdapter(myJokesRecyclerAdapter);
        // 5. set item animator to DefaultAnimator
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        llm.scrollToPositionWithOffset(MainActivity.listPos, 30);//this will scroll to last joke
    }

    @Override//had to add this override so the one below works properly
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        System.out.println("-- onPrepareOptionsMenu -xegay");
        MenuItem item3 = menu.findItem(R.id.action_search);
        item3.setVisible(false);
    }

}
