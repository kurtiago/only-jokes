package com.tiago.onlyjokes;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FullscreenViewpagerAdapter extends PagerAdapter {
    static List<DatabaseModel> dbList;
    static Context context;
    //int[] imageId = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5};

    public FullscreenViewpagerAdapter(Context context, List<DatabaseModel> dbList){
        this.dbList = new ArrayList<DatabaseModel>();
        this.context = context;
        this.dbList = dbList;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Auto-generated method stub
        TextView jokeTV,sourceTV, idTV, categoryTV,addressTV;
        Button favsBT;
        ImageView newIV;
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();

        View viewItem = inflater.inflate(R.layout.fullscreen_item_row, container, false);
        jokeTV = (TextView)viewItem.findViewById(R.id.jokeTV);
        idTV = (TextView)viewItem.findViewById(R.id.branch);
        sourceTV = (TextView)viewItem.findViewById(R.id.sourceTV);
        addressTV = (TextView)viewItem.findViewById(R.id.address);
        categoryTV = (TextView)viewItem.findViewById(R.id.categoryTV);
        categoryTV.setText(MainActivity.chosenCategory);
        categoryTV.setTypeface(MainActivity.myFont2);
        idTV.setText("#"+dbList.get(position).getJoke());
        //MainActivity.currentId = dbList.get(position).getJoke();//test
        //MainActivity.currentJoke = dbList.get(position-1).getEmail();
        jokeTV.setText(dbList.get(position).getEmail());
        jokeTV.setTypeface(MainActivity.jokeFont);
        sourceTV.setText(dbList.get(position).getSource());
        addressTV.setText((position+1)+" / "+dbList.size());
        favsBT = (Button)viewItem.findViewById(R.id.favsBT);

        if(MainActivity.favsList.contains(dbList.get(position).getJoke())){
            System.out.println("aqui instantiateItem contain favs TRUE - position"+position);
            favsBT.setBackground(context.getResources().getDrawable(R.drawable.ic_star_checked));
        }else{
            System.out.println("aqui instantiateItem contain favs FALSE - position"+position);
            favsBT.setBackground(context.getResources().getDrawable(R.drawable.ic_star_normal));
        }

        if(MainActivity.shouldInstantiate){//will call this only once


            //newIV = (ImageView) viewItem.findViewById(R.id.newIV);
            //if(MainActivity.seenList.contains(dbList.get(position).getJoke())){
            //    System.out.println("aqui instantiateItem contain seen TRUE - position"+position);
            //    newIV.setVisibility(View.INVISIBLE);
            //}else{
            //    System.out.println("aqui instantiateItem contain seen FALSE - position"+position);
            //    newIV.setVisibility(View.VISIBLE);
            //}
            MainActivity.shouldInstantiate = false;
        }

        //ImageView imageView = (ImageView) viewItem.findViewById(R.id.imageView);
        //imageView.setImageResource(imageId[position]);
        //TextView textView1 = (TextView) viewItem.findViewById(R.id.textView1);
        //textView1.setText("hi");
        //((ViewPager)container).addView(viewItem);
        container.addView(viewItem);

        return viewItem;
    }

    @Override
    public int getCount() {
        // Auto-generated method stub
        return dbList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // Auto-generated method stub

        //return view == ((View)object);
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Auto-generated method stub
        //((ViewPager) container).removeView((View) object);
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
