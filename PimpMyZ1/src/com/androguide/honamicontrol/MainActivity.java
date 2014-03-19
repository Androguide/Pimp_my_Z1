/**   Copyright (C) 2013  Louis Teboul (a.k.a Androguide)
 *
 *    admin@pimpmyrom.org  || louisteboul@gmail.com
 *    http://pimpmyrom.org || http://androguide.fr
 *    71 quai Clémenceau, 69300 Caluire-et-Cuire, FRANCE.
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License along
 *      with this program; if not, write to the Free Software Foundation, Inc.,
 *      51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 **/

package com.androguide.honamicontrol;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androguide.honamicontrol.fragments.CardsFragment;
import com.androguide.honamicontrol.fragments.HelpFragment;
import com.androguide.honamicontrol.fragments.KernelFragment;
import com.androguide.honamicontrol.fragments.SoundControlFragment;
import com.androguide.honamicontrol.fragments.SoundFragment;
import com.androguide.honamicontrol.fragments.TouchScreenFragment;
import com.androguide.honamicontrol.fragments.WelcomeFragment;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements
        OnPageChangeListener {

    private final Handler handler = new Handler();
    private ArrayList<String> headers = new ArrayList<String>();
    private PagerSlidingTabStrip tabs;
    private Drawable oldBackground = null;
    private int currentColor = 0xFF3F9FE0;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mDrawerHeaders[] = {"Website", "XDA Thread",
            "Follow Me on Twitter", "Follow Me on Google+", "Become a Fan"};
    private String mAppColor = "#6f8c8d";
    private String[] flatColors = {"#7f8c8d", "#16a085", "#e67e22", "#2980b9", "#34495e"};
    private static ViewPager pager;
    private Drawable.Callback drawableCallback = new Drawable.Callback() {

        @Override
        public void invalidateDrawable(Drawable who) {
            try {
                getSupportActionBar().setBackgroundDrawable(who);
            } catch (NullPointerException ignored) {
            }
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        headers.add("Welcome");
        headers.add("Kernel Control");
        headers.add("Sound Control");
        headers.add("Touch Screen");
        headers.add("Help Center");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        ArrayAdapter<String> pimpAdapter = new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerHeaders);
        mDrawerList.setAdapter(pimpAdapter);

        // TODO: Delete in RC/Stable builds
        Log.e("FIRST POS", mDrawerList.getFirstVisiblePosition() + "");
        Log.e("LAST POS", mDrawerList.getLastVisiblePosition() + "");

        View child = mDrawerList.getChildAt(mDrawerList
                .getFirstVisiblePosition());

        if (child != null
                && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            child.setBackground(getColouredTouchFeedback());
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        /**
         * ActionBarDrawerToggle ties together the proper interactions between
         * the sliding drawer and the action bar app icon
         */
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.open_drawer, R.string.close_drawer) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        /** Tabs adapter using the PagerSlidingStrip library */
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        MyPagerAdapter adapter = new MyPagerAdapter(
                this.getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        final int pageMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(this);

        changeColor(Color.parseColor(mAppColor));
        pager.setOffscreenPageLimit(5);
    }

    public static ViewPager getViewPager() {
        return pager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
        }
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /**
     * Method to set the color scheme according to the color defined in
     * config.xml
     *
     * @param newColor : the color retrieved from config.xml
     */
    public void changeColor(int newColor) {
        tabs.setIndicatorColor(newColor);
        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = getResources().getDrawable(
                R.drawable.actionbar_bottom);
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable,
                bottomDrawable});

        if (oldBackground == null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
                ld.setCallback(drawableCallback);
            else
                getSupportActionBar().setBackgroundDrawable(ld);

        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                    oldBackground, ld});
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
                td.setCallback(drawableCallback);
            else
                getSupportActionBar().setBackgroundDrawable(td);
            td.startTransition(200);
        }
        oldBackground = ld;
        currentColor = newColor;

        /**
         * The following is a work-around to avoid NPE, see the following
         * thread:
         *
         * @see http://stackoverflow.com/questions/11002691/actionbar-
         *      setbackgrounddrawable-nulling-background-from-thread-handler
         */
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } catch (NullPointerException e) {
            Log.e("NPE", e.getMessage());
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /* Save current color scheme value to the #Bundle */
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int pos) {
        //We could set a different color for each tab here if needed
        changeColor(Color.parseColor(flatColors[pos]));
        switch (pos) {
            case 2:
                getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_tools_sound_control));
                break;
            case 3:
                getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_tools_touchscreen));
                break;
            default:
                getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_launcher));
                break;
        }
    }

    private void selectItem(int position) {
        SharedPreferences prefs = getSharedPreferences("CONFIG", 0);
        switch (position) {
            case 0:
                goToUrl(prefs.getString("WEBSITE", "http://androguide.github.io/Pimp_my_Z1/"));
                break;
            case 1:
                goToUrl(prefs.getString("XDA",
                        "http://forum.xda-developers.com/showthread.php?p=50930265"));
                break;
            case 2:
                goToUrl(prefs.getString("TWITTER",
                        "https://twitter.com/androguidefr"));
                break;
            case 3:
                goToUrl(prefs.getString("GOOGLE+",
                        "https://plus.google.com/u/0/116104837766524942436/posts"));
                break;
            case 4:
                goToUrl(prefs.getString("FACEBOOK",
                        "https://www.facebook.com/andro.guidefr"));
                break;
            default:
                return;
        }
        /* Update the selected item and automatically close the drawer */
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /**
     * Convenience method for triggering an #Intent.ACTION_VIEW event to an url
     * passed as a parameter
     *
     * @param url : the url to launch the intent with
     */
    private void goToUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    @Override
    public void setTitle(CharSequence title) {
        try {
            getSupportActionBar().setTitle(title);
        } catch (NullPointerException e) {
            Log.e("NPE", e.getMessage());
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * #onPostCreate() and #onConfigurationChanged()
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        /* Sync the toggle state after onRestoreInstanceState has occurred. */
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /* Pass any configuration change to the drawer toggle */
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private StateListDrawable getColouredTouchFeedback() {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(Color.parseColor(mAppColor)));
        states.addState(new int[]{android.R.attr.state_focused},
                new ColorDrawable(Color.parseColor(mAppColor)));
        states.addState(new int[]{},
                getResources().getDrawable(android.R.color.transparent));
        return states;
    }

    /**
     * Adapter for the ViewPager
     */
    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return headers.get(position);
        }

        @Override
        public int getCount() {
            return headers.size();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new WelcomeFragment();
                case 1:
                    return new KernelFragment();
                case 2:
                    return new SoundControlFragment();
                case 3:
                    return new TouchScreenFragment();
                case 4:
                    return new HelpFragment();
                default:
                    return new CardsFragment();
            }
        }
    }

    /**
     * Handle the drawer items click
     */
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            for (int i = 0; i < parent.getCount(); i++)
                view.setBackground(getColouredTouchFeedback());
            selectItem(position);
        }
    }
}
