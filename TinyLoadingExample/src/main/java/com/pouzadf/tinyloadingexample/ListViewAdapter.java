package com.pouzadf.tinyloadingexample;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.pouzadf.tinyloading.TinyLoading;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<Pair<String, String>> sourceList;
    private Context c;

    static private class ViewHolder {
        private ImageView l_picture;
        private ImageView r_picture;
    }

    public ListViewAdapter(Context c, ArrayList<Pair<String, String>> sourceList){
        this.sourceList = sourceList;
        this.c = c;

    }

    public View getView(final int position, View convert_view, ViewGroup parent) {
        ViewHolder holder;
        if (convert_view == null) {
            holder = new ViewHolder();
            // get access to the layout infaltor service
            LayoutInflater inflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convert_view = inflator.inflate(R.layout.list_item, parent, false);

            // pull all the items from the XML so we can modify them
            holder.l_picture =  convert_view.findViewById(R.id.left_image);
            holder.r_picture =  convert_view.findViewById(R.id.right_image);
            convert_view.setTag(holder);

        } else {
            holder = (ListViewAdapter.ViewHolder) convert_view.getTag();
        }
        TinyLoading.get().cancel(holder.l_picture);
        TinyLoading.get().cancel(holder.r_picture);
            TinyLoading.get()
                    .with(this.c)
                    .load(sourceList.get(position).second)
                    .fallback(ContextCompat.getDrawable(c,R.drawable.ic_launcher_background))
                    .into(holder.l_picture);
            TinyLoading.get()
                    .with(this.c)
                    .load(sourceList.get(position).first)
                    .fallback(ContextCompat.getDrawable(c,R.drawable.ic_launcher_background))
                    .into(holder.r_picture);

        return convert_view;
    }

    @Override
    public int getCount() {
        return sourceList.size();
    }

    @Override
    public Object getItem(int i) {
        return sourceList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
