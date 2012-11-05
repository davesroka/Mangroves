package org.qfi.mangroves.ui.phone;

import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentMapActivity;
import android.text.TextUtils;

import org.qfi.mangroves.Preferences;
import org.qfi.mangroves.R;
import org.qfi.mangroves.helpers.ReportViewPager;
import org.qfi.mangroves.helpers.TabsAdapter;
import org.qfi.mangroves.ui.tablet.ListCheckinFragment;
import org.qfi.mangroves.ui.tablet.MapCheckinFragment;

public class CheckinTabActivity extends FragmentMapActivity {

	private ReportViewPager mViewPager;

	private TabsAdapter mTabsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.checkin_tab);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle();
		ActionBar.Tab reportsTab = getSupportActionBar().newTab().setText(
				getString(R.string.checkins));
		ActionBar.Tab mapTab = getSupportActionBar().newTab().setText(
				getString(R.string.map));

		mViewPager = (ReportViewPager) findViewById(R.id.pager);

		mTabsAdapter = new TabsAdapter(this, getSupportActionBar(), mViewPager);

		mTabsAdapter.addTab(reportsTab, ListCheckinFragment.class);
		mTabsAdapter.addTab(mapTab, MapCheckinFragment.class);

		if (savedInstanceState != null) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt("index"));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("index", getSupportActionBar()
				.getSelectedNavigationIndex());
	}

	public void setTitle() {
		Preferences.loadSettings(this);
		if ((Preferences.activeMapName != null)
				&& (!TextUtils.isEmpty(Preferences.activeMapName))) {

			getSupportActionBar().setTitle(Preferences.activeMapName);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
