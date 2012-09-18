
package com.robotfactorial.mangroves.test;

import com.robotfactorial.mangroves.About;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class AboutTest extends ActivityInstrumentationTestCase2<About> {

    private About aboutActivity;

    private TextView aboutVersionView;

    private String aboutString;

    public AboutTest() {
        super("com.robotfactorial.mangroves", About.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        
        setActivityInitialTouchMode(false);
        setActivityIntent(new Intent(Intent.ACTION_VIEW));
        aboutActivity = this.getActivity();
        aboutVersionView = (TextView)aboutActivity
                .findViewById(com.robotfactorial.mangroves.R.id.version);
        aboutString = aboutActivity.getPackageManager().getPackageInfo(
                aboutActivity.getPackageName(), 0).versionName;
    }

    public void testPreconditions() {
        assertNotNull(aboutVersionView);
    }

    public void testText() {
        assertEquals(aboutString, (String)aboutVersionView.getText());
    }
}
