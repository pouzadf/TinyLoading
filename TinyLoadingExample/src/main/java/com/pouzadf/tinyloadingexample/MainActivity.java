package com.pouzadf.tinyloadingexample;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.pouzadf.tinyloading.TinyLoading;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] arr = {"http://www.michelweiland.com/koken/storage/cache/images/001/730/Paysage-poetique-carre-NB-MG-4243,medium_large.1449440854.jpg",
                "http://www.michelweiland.on-web.fr/koken/storage/cache/images/001/732/Paysage-poetique-carre-NB-MG-4256,medium_large.1449440912.jpg",
                "http://cdn.designfather.com/wp-content/uploads/2015/02/michael_kenna_supports_de_jetee_france_2560x1440.jpg",
        "http://www.michelweiland.com/koken/storage/cache/images/001/727/Paysage-poetique-carre-NB-IMG-4214,medium_large.1449440771.jpg",
        "https://www.2tout2rien.fr/wp-content/uploads/2017/03/Les-somptueuses-minimalistes-photos-noir-et-blanc-de-nature-de-George-Digalakis-12.jpg",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTv4JBjGpnYZK11LUzCGNicTPVzxf_grvhM0D0YKXAWjYPovTZ4",
        "http://photography-now.com/images/Bilder/gross/33221.jpg",
        "http://photography-now.com/images/Hauptbilder/gross/109104.jpg",
        "https://a.1stdibscdn.com/archivesE/upload/a_1342/1469470459866/Punta_Brava_Study_7_Ensenada_Mexico_2008_l.jpg"};

        Random rand = new Random();
        ArrayList<Pair<String, String>> list = new ArrayList<>();
        ListView lv = findViewById(R.id.lv);
        ListViewAdapter adapter = new ListViewAdapter(this, list);
        for (int i = 0; i < 100; i++)
        {
            int random = rand.nextInt(arr.length - 1);
            int anotherRandom = rand.nextInt(arr.length - 1);
            list.add(new Pair<String, String>(arr[anotherRandom],arr[random]));
            random = rand.nextInt(arr.length - 1);
            anotherRandom = rand.nextInt(arr.length - 1);
            list.add(new Pair<String, String>(arr[anotherRandom],arr[random]));
        }

        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();





    }
}