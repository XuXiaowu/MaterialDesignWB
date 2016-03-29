package truecolor.mdwb.fragments.main;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import truecolor.mdwb.R;

/**
 * Created by xiaowu on 15/9/9.
 */
public abstract class ABaseFragment extends Fragment{

    public View rootView;
    public int rootViewHeight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(initContentViewRes(), container, false);
        initContentView();
        initView();
        measurRootViewHeight();
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public abstract int initContentViewRes();

    public abstract void initContentView();

    public abstract void initView();

    private void measurRootViewHeight(){
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootViewHeight = rootView.getMeasuredHeight();
            }
        });
    }
}
