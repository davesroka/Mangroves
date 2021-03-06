
package org.qfi.mangroves.ui.tablet;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.qfi.mangroves.R;
import org.qfi.mangroves.util.Util;
import org.qfi.mangroves.views.AboutView;

public class AboutFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (Util.isTablet(getActivity()))
            getDialog().setTitle(getString(R.string.about));
        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.about_view, container, false);
        if (v != null) {
            new AboutView(v, getActivity());
        }
        return v;
    }

    /**
     * Create a new instance of MyDialogFragment, providing "num" as an
     * argument.
     */
    static AboutFragment newInstance() {
        AboutFragment f = new AboutFragment();
        return f;
    }
}
