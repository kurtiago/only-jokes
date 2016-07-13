package com.tiago.onlyjokes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<CategoriesRecyclerAdapter.ItemHolder> {

    private List<String> itemsName;
    private List<Integer> itemsImages;
    private OnItemClickListener onItemClickListener;
    private LayoutInflater layoutInflater;

    public CategoriesRecyclerAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
        itemsName = new ArrayList<String>();
        itemsImages = new ArrayList<Integer>();
    }

    @Override
    public CategoriesRecyclerAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.category_item_row, parent, false);
        return new ItemHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(CategoriesRecyclerAdapter.ItemHolder holder, int position) {
        holder.setItemName(itemsName.get(position),itemsImages.get(position));
    }

    @Override
    public int getItemCount() {
        return itemsName.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener(){
        return onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(ItemHolder item, int position);
    }

    public void add(int location, String iName, int drawable){
        itemsName.add(location, iName);
        itemsImages.add(location, drawable);
        notifyItemInserted(location);
    }

    //public void remove(int location){
    //    if(location >= itemsName.size())
    //        return;
//
    //    itemsName.remove(location);
    //    notifyItemRemoved(location);
    //}

    public static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private CategoriesRecyclerAdapter parent;
        TextView textItemName;
        ImageView imageView;

        public ItemHolder(View itemView, CategoriesRecyclerAdapter parent) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.parent = parent;
            textItemName = (TextView) itemView.findViewById(R.id.categoryTV);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textItemName.setTypeface(MainActivity.myFont2);
        }

        public void setItemName(CharSequence name, int drawable){
            textItemName.setText(name);
            imageView.setImageResource(drawable);
        }

        public CharSequence getItemName(){
            return textItemName.getText();
        }

        @Override
        public void onClick(View v) {
            final OnItemClickListener listener = parent.getOnItemClickListener();
            if(listener != null){
                listener.onItemClick(this, getPosition());
            }
        }
    }
}
