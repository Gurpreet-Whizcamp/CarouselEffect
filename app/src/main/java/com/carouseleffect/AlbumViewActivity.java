package com.carouseleffect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import lib.folderpicker.FolderPicker;

public class AlbumViewActivity extends AppCompatActivity {

    public static final int ADAPTER_TYPE_TOP = 1;
    public static final int ADAPTER_TYPE_BOTTOM = 2;
    int FOLDERPICKER_CODE = 201;
    String folderLocation = "";
    Button btnSelectFolder,btnAuto;
    ArrayList<File> arrayListFile = new ArrayList<>();
    private ViewPager viewpagerTop, viewPagerBackground;
    String TAG = "=AlbumViewActivity=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);

        init();
        //  setupViewPager();
    }

    /**
     * Initialize all required variables
     */
    private void init() {
        viewpagerTop = (ViewPager) findViewById(R.id.viewpagerTop);
        btnSelectFolder = (Button) findViewById(R.id.btnSelectFolder);
        btnAuto = (Button) findViewById(R.id.btnAuto);
        viewPagerBackground = (ViewPager) findViewById(R.id.viewPagerbackground);

        viewpagerTop.setClipChildren(false);
        viewpagerTop.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.pager_margin));
        viewpagerTop.setOffscreenPageLimit(3);
        viewpagerTop.setPageTransformer(false, new CarouselEffectTransformer(this)); // Set transformer

        btnSelectFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlbumViewActivity.this, FolderPicker.class);
                startActivityForResult(intent, FOLDERPICKER_CODE);
            }
        });

        btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayListFile !=null && arrayListFile.size() > 0) {
                    pageSwitcher(1);
                }
            }
        });
    }

    /**
     * Setup viewpager and it's events
     */
    private void setupViewPager() {
        // Set Top ViewPager Adapter
        MyPagerAdapter adapter = new MyPagerAdapter(this, arrayListFile, ADAPTER_TYPE_TOP);
        viewpagerTop.setAdapter(adapter);

        // Set Background ViewPager Adapter
        MyPagerAdapter adapterBackground = new MyPagerAdapter(this, arrayListFile, ADAPTER_TYPE_BOTTOM);
        viewPagerBackground.setAdapter(adapterBackground);


        viewpagerTop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int index = 0;

            @Override
            public void onPageSelected(int position) {
                index = position;

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int width = viewPagerBackground.getWidth();
                viewPagerBackground.scrollTo((int) (width * position + width * positionOffset), 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    viewPagerBackground.setCurrentItem(index);
                }

            }
        });
    }

    /**
     * Handle all click event of activity
     */
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.linMain:
                if (view.getTag() != null) {
                    int poisition = Integer.parseInt(view.getTag().toString());
                    Toast.makeText(getApplicationContext(), "Poistion: " + poisition, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FOLDERPICKER_CODE && resultCode == Activity.RESULT_OK) {

            folderLocation = intent.getExtras().getString("data");
            Log.i("folderLocation", folderLocation);

            if(folderLocation !=null && folderLocation.length() > 0)
            {
                getFromSdcard();
            }

        }
    }



    public void getFromSdcard()
    {
        File[] listFile;
        File file= new File(folderLocation);
        arrayListFile = new ArrayList<>();

        Log.i(TAG,"=====folderLocation=111==="+folderLocation);

        if (file.isDirectory())
        {
            Log.i(TAG,"=====folderLocation=111==="+folderLocation);
            listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++)
            {
                if(listFile[i] !=null &&  ( listFile[i].toString().contains(".jpg") || listFile[i].toString().contains(".png") || listFile[i].toString().contains(".gif)") ) )
                {
                    Log.i(TAG, "=====listFile[i]=111===" + listFile[i]);

                    arrayListFile.add(listFile[i]);
                }
                //f.add(listFile[i].getAbsolutePath());
                if(i == (listFile.length -1))
                {
                    Log.i(TAG, "=====setupViewPager===");
                    setupViewPager();
                }

            }
        }



    }

    public class MyPagerAdapter extends PagerAdapter

    {

        Context context;
        ArrayList<File> listItems;
        int adapterType;

        public MyPagerAdapter(Context context, ArrayList<File> listItems, int adapterType) {
        this.context = context;
        this.listItems = listItems;
        this.adapterType=adapterType;
    }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cover, null);
        try {

            LinearLayout linMain = (LinearLayout) view.findViewById(R.id.linMain);
            ImageView imageCover = (ImageView) view.findViewById(R.id.imageCover);

            linMain.setTag(position);

            switch (adapterType)
            {
                case MainActivity.ADAPTER_TYPE_TOP:
                    linMain.setBackgroundResource(R.drawable.shadow);
                    break;
                case MainActivity.ADAPTER_TYPE_BOTTOM:
                    linMain.setBackgroundResource(0);
                    break;
            }


            File file = listItems.get(position);
            Uri imageUri = Uri.fromFile(file);

            Glide.with(context)
                    .load(imageUri)
                    .into(imageCover);

            container.addView(view);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

        @Override
        public int getCount() {
        return listItems.size();
    }

        @Override
        public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    }


    Timer timer;
    int page = 1;

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        page = viewpagerTop.getCurrentItem();

                timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay
        // in
        // milliseconds
    }

    // this is an inner class...
    class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            runOnUiThread(new Runnable() {
                public void run() {

                    if (page >= (arrayListFile.size()-1) )
                    { // In my case the number of pages are 5
                        timer.cancel();
                        // Showing a toast for just testing purpose
                        Toast.makeText(getApplicationContext(), "No more images",
                                Toast.LENGTH_LONG).show();
                    } else {
                        viewpagerTop.setCurrentItem(page++);
                    }
                }
            });

        }
    }

}
