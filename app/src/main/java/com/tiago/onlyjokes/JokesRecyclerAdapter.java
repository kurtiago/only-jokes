package com.tiago.onlyjokes;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class JokesRecyclerAdapter extends RecyclerView.Adapter<JokesRecyclerAdapter.ViewHolder> {
    CardView cv;
    static   List<DatabaseModel> dbList;
    static  Context context;
    JokesRecyclerAdapter(Context context, List<DatabaseModel> dbList ){
        this.dbList = new ArrayList<DatabaseModel>();
        this.context = context;
        this.dbList = dbList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.jokes_item_row, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //holder.name.setText(dbList.get(position).getJoke());
        //holder.email.setText(dbList.get(position).getJoke());
        holder.email.setText(dbList.get(position).getEmail());
        holder.email.setTypeface(MainActivity.jokeFont);

    }

    @Override
    public int getItemCount() {
        return dbList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        public TextView name,email;
        private final Context c;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            //name = (TextView) itemLayoutView
            //        .findViewById(R.id.rvname);
            cv = (CardView) itemView.findViewById(R.id.cardviewList);
            email = (TextView)itemLayoutView.findViewById(R.id.rvemail);
            itemLayoutView.setOnClickListener(this);
            c = itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            MainActivity.listPos = getAdapterPosition();
            ((MainActivity)context).inflateFragment("fullscreen");

        }
    }
    
    public void setFilter(List<DatabaseModel> countryModels) {
        dbList = new ArrayList<>();
        dbList.addAll(countryModels);
        notifyDataSetChanged();
    }

}
