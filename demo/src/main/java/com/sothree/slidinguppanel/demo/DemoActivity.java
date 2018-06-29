package com.sothree.slidinguppanel.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

public class DemoActivity extends AppCompatActivity implements MapWrapperLayout.OnDragListener {
    private static final String TAG = "DemoActivity";

    private SlidingUpPanelLayout mLayout;
    private RelativeLayout mLinearSlide;
    private GoogleMap mGoogleMap;
    private CoordinatorLayout mCoordinatorLayout;
    private LinearLayout mLinearHelp;
    private boolean isShowHelp = false;
    private int mLinearHelpHeight;
    private int imageParentWidth = -1;
    private int imageParentHeight = -1;
    private int imageHeight = -1;
    private int centerX = -1;
    private int centerY = -1;
    private View mMarkerParentView;
    private ImageView mMarkerImageView;
    private FrameLayout mFrameLayout;
    private int heightScreen, defaultVisibleFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            w.setStatusBarColor(ContextCompat.getColor(DemoActivity.this, R.color.colorPrimary));
        }

        // mLinearSlide = findViewById(R.id.ll_slide);

        CustomMapFragment mapFragment
                = (CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.setOnDragListener(this);
        mMarkerParentView = findViewById(R.id.marker_view_incl);
        mMarkerImageView = (ImageView) findViewById(R.id.marker_icon_view);

        // Sét đặt sự kiện thời điểm GoogleMap đã sẵn sàng.
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(DemoActivity.this, R.raw.mapstyle_sliver));
                //  mGoogleMap.setMyLocationEnabled(true);

                mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
//                        mGoogleMap.clear();
//
//                        mGoogleMap.addMarker(new MarkerOptions()
//                                .position(mGoogleMap.getCameraPosition().target)
//                                .title(mGoogleMap.getCameraPosition().target.toString())
//                        );

                    }
                });
            }
        });


        mLinearHelp = findViewById(R.id.ll_help);
        mFrameLayout = findViewById(R.id.content);


        BottomAppBar mBottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(mBottomAppBar);
        mCoordinatorLayout = findViewById(R.id.cl_data);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightScreen = displayMetrics.heightPixels;

        mCoordinatorLayout.getLayoutParams().height = heightScreen / 7;


        mLayout = findViewById(R.id.sliding_layout);
        mLayout.setPanelHeight(heightScreen / 4);

        defaultVisibleFrameLayout = mLayout.getPanelHeight() - mCoordinatorLayout.getLayoutParams().height;

        mLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                if (slideOffset >= 0.5f) {
                    mCoordinatorLayout.setVisibility(View.GONE);
//                    // Animate the hidden linear layout as visible and set
//                    mCoordinatorLayout
//                            .animate()
//                            .setDuration(100)
//                            .alpha(0.0f)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    Log.v(TAG, "Animation ended. Set the view as gone");
//                                    super.onAnimationEnd(animation);
//                                    mCoordinatorLayout.setVisibility(View.GONE);
//
//                                }
//                            })
//                    ;
                    // slideToTop(mCoordinatorLayout);

                } else {
                    mCoordinatorLayout.setVisibility(View.VISIBLE);
                }

                mCoordinatorLayout.setAlpha(1 - slideOffset);
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

        mCoordinatorLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.action_favorites: {
                        mLayout.setPanelState(PanelState.COLLAPSED);
                        break;
                    }
                }
                return true;
            }
        });

    }

    public void slideToTop(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
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
                    int newPanelHeight = defaultPanelHeight - mLinearHelpHeight + defaultVisibleFrameLayout;
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
                            int newPanelHeight = defaultPanelHeight + mLinearHelpHeight - defaultVisibleFrameLayout;
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        imageParentWidth = mMarkerParentView.getWidth();
        imageParentHeight = mMarkerParentView.getHeight();
        imageHeight = mMarkerImageView.getHeight();

        centerX = imageParentWidth / 2;
        centerY = (imageParentHeight / 2) + (imageHeight / 2);
    }


    @Override
    public void onDrag(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Projection projection = (mGoogleMap != null && mGoogleMap
                    .getProjection() != null) ? mGoogleMap.getProjection()
                    : null;
            //
            if (projection != null) {
                LatLng centerLatLng = projection.fromScreenLocation(new Point(
                        centerX, centerY));

                //  updateLocation(centerLatLng);
            }
        }
    }

    private void updateLocation(LatLng centerLatLng) {
        if (centerLatLng != null) {
            Geocoder geocoder = new Geocoder(DemoActivity.this,
                    Locale.getDefault());

            List<Address> addresses = new ArrayList<Address>();
            try {
                addresses = geocoder.getFromLocation(centerLatLng.latitude,
                        centerLatLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {

                String addressIndex0 = (addresses.get(0).getAddressLine(0) != null) ? addresses
                        .get(0).getAddressLine(0) : null;
                String addressIndex1 = (addresses.get(0).getAddressLine(1) != null) ? addresses
                        .get(0).getAddressLine(1) : null;
                String addressIndex2 = (addresses.get(0).getAddressLine(2) != null) ? addresses
                        .get(0).getAddressLine(2) : null;
                String addressIndex3 = (addresses.get(0).getAddressLine(3) != null) ? addresses
                        .get(0).getAddressLine(3) : null;

                String completeAddress = addressIndex0 + "," + addressIndex1;

                if (addressIndex2 != null) {
                    completeAddress += "," + addressIndex2;
                }
                if (addressIndex3 != null) {
                    completeAddress += "," + addressIndex3;
                }
                if (completeAddress != null) {
//                    mLocationTextView.setText(completeAddress);
                    Log.d("ADDRESS", completeAddress);
                }
            }
        }
    }
}
