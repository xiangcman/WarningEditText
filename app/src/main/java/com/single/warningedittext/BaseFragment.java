package com.single.warningedittext;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import warningedittext.single.com.library.WarningEditText;

/**
 * Created by xiangcheng on 16/11/14.
 */
public abstract class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), null);
        final WarningEditText we = (WarningEditText) view.findViewById(R.id.warning_et);
        final EditText et = (EditText) view.findViewById(R.id.test_et);
        et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //if editText status is WarningStart and the editText has translated
                if (we.isHasWarning() && !we.getWarning() && TextUtils.isEmpty(we.getText())) {
                    we.setWarning(true);
                    we.startWarning();
                }
                return false;
            }
        });

        return view;
    }

    public abstract int getLayout();
}
