package com.airmoll.muzik;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.airmoll.muzik.databinding.ActivityMainBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    String[] songItems;
    String[] songPath;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        runtimePermission();


    }

    public void runtimePermission() {
        /*ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1
        );*/


        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] files = file.listFiles();


        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(findSong(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }


    void displaySongs() {

        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCurosr = getContentResolver().query(songUri, null, null, null, null);
        int i = 0;
        int titleIndex, pathIndex;
        if (songCurosr != null && songCurosr.moveToFirst()) {
            songItems = new String[songCurosr.getCount()];
            songPath = new String[songCurosr.getCount()];
            do {
                titleIndex = songCurosr.getColumnIndex(MediaStore.Audio.Media.TITLE);
                pathIndex = songCurosr.getColumnIndex(MediaStore.Audio.Media.DATA);
                songItems[i] = songCurosr.getString(titleIndex);
                songPath[i] = songCurosr.getString(pathIndex);
                ++i;
            } while (songCurosr.moveToNext());
            songCurosr.close();
            /*ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songItems);
            binding.songsListView.setAdapter(myAdapter);*/
            CustomAdapter customAdapter = new CustomAdapter();
            binding.songsListView.setAdapter(customAdapter);

            binding.songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //String songName = (String) binding.songsListView.getItemAtPosition(position);
                    startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                    .putExtra("title", songItems)
                    .putExtra("path",songPath)
                    .putExtra("position", position));
                }
            });
        }
        /*ArrayList<File> mySongs = new ArrayList<>();
        mySongs = findSong(Environment.getExternalStorageDirectory());
        int size = mySongs.size();
        if (size==0)
            Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
        else
        {
            songItems = new String[size];

            for (int i = 0; i < size; i++) {
                songItems[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
            }
            ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songItems);
            binding.songsListView.setAdapter(myAdapter);
        }*/

    }

 /*   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //displaySongs();
                Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
            } else runtimePermission();
        }
    }*/


    class CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return songItems.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View myView = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textsong = myView.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(songItems[position]);
            return myView;
        }
    }


}