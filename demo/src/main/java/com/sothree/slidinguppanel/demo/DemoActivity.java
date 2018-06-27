package com.sothree.slidinguppanel.demo;

import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class DemoActivity extends AppCompatActivity {
    private static final String TAG = "DemoActivity";

    private SlidingUpPanelLayout mLayout;
    private RelativeLayout mLinearSlide;
    private GoogleMap mGoogleMap;
    private CoordinatorLayout mCoordinatorLayout;
    private LinearLayout mLinearHelp;
    private boolean isShowHelp = false;
    private int mLinearHelpHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        // mLinearSlide = findViewById(R.id.ll_slide);

        SupportMapFragment mapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        // Sét đặt sự kiện thời điểm GoogleMap đã sẵn sàng.
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
            }
        });


        mLinearHelp = findViewById(R.id.ll_help);


        BottomAppBar mBottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(mBottomAppBar);
        mCoordinatorLayout = findViewById(R.id.cl_data);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        mCoordinatorLayout.getLayoutParams().height = height / 7;

        mLayout = findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                if (slideOffset >= 0.5f) {
                    mCoordinatorLayout.setVisibility(View.GONE);
                } else {
                    mCoordinatorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
                if (newState == PanelState.COLLAPSED) {
                    mCoordinatorLayout.setVisibility(View.VISIBLE);
                } else if (newState == PanelState.EXPANDED) {
                    mCoordinatorLayout.setVisibility(View.GONE);
                }
            }
        });
        mLayout.setFadeOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //mLayout.setPanelState(PanelState.COLLAPSED);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.demo, menu);
        MenuItem item = menu.findItem(R.id.action_toggle);
        if (mLayout != null) {
            if (mLayout.getPanelState() == PanelState.HIDDEN) {
                item.setTitle(R.string.action_show);
            } else {
                item.setTitle(R.string.action_hide);
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle: {

                return true;
            }
            case R.id.action_anchor: {
                if (isShowHelp) {
                    mLinearHelp.setVisibility(View.GONE);
                    int defaultPanelHeight = mLayout.getPanelHeight();
                    int newPanelHeight = defaultPanelHeight - mLinearHelpHeight;
                    mLayout.setPanelHeight(newPanelHeight);
                } else {
                    mLinearHelp.setVisibility(View.VISIBLE);
                    mLinearHelp.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            mLinearHelp.getViewTreeObserver().removeOnPreDrawListener(this);

                            mLinearHelpHeight = mLinearHelp.getHeight();
                            Log.d("HEIGHT", "" + mLinearHelpHeight);
                            //calculate panel height
                            int defaultPanelHeight = mLayout.getPanelHeight();
                            int newPanelHeight = defaultPanelHeight + mLinearHelpHeight;
                            mLayout.setPanelHeight(newPanelHeight);
                            return false;
                        }
                    });


                }
                isShowHelp = !isShowHelp;
                return true;
            }
            case android.R.id.home: {
                mLayout.setPanelState(PanelState.EXPANDED);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == PanelState.EXPANDED || mLayout.getPanelState() == PanelState.ANCHORED)) {
            mLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
