
package com.robotfactorial.mangroves.ui.phone;

import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentMapActivity;
import android.text.TextUtils;

import com.crittercism.app.Crittercism;
import com.flurry.android.FlurryAgent;
import com.robotfactorial.mangroves.Preferences;
import com.robotfactorial.mangroves.R;
import com.robotfactorial.mangroves.helpers.ReportViewPager;
import com.robotfactorial.mangroves.helpers.TabsAdapter;
import com.robotfactorial.mangroves.ui.tablet.ListReportFragment;
import com.robotfactorial.mangroves.ui.tablet.MapFragment;

public class ReportTabActivity extends FragmentMapActivity {

    private ReportViewPager mViewPager;

    private TabsAdapter mTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.report_tab);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle();
        ActionBar.Tab reportsTab = getSupportActionBar().newTab().setText(
                getString(R.string.reports));
        ActionBar.Tab mapTab = getSupportActionBar().newTab().setText(getString(R.string.map));


		mViewPager = (ReportViewPager)findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, getSupportActionBar(), mViewPager);

        mTabsAdapter.addTab(reportsTab, ListReportFragment.class);
        mTabsAdapter.addTab(mapTab, MapFragment.class);

        if (savedInstanceState != null) {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("index"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("index", getSupportActionBar().getSelectedNavigationIndex());
    }
    
    public void setTitle() {
		Preferences.loadSettings(this);
//		if ((Preferences.activeMapName != null)
//				&& (!TextUtils.isEmpty(Preferences.activeMapName))) {
//
//			getSupportActionBar().setTitle(Preferences.activeMapName);
//		}
		getSupportActionBar().setTitle(R.string.app_title);
	}

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

	@Override
	protected void onStart() {
		super.onStart();    //To change body of overridden methods use File | Settings | File Templates.
		FlurryAgent.onStartSession(this, "KK7J627DVMWCF7J6WB5B");
		Crittercism.init(getApplicationContext(), "504f6d47c8f97411ee000002");
	}

	@Override
	protected void onStop() {
		super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
		FlurryAgent.onEndSession(this);
	}

}
