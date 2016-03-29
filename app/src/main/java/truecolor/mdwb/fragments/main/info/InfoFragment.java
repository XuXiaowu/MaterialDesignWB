package truecolor.mdwb.fragments.main.info;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import truecolor.mdwb.R;
import truecolor.mdwb.logics.http.HttpService;
import truecolor.mdwb.utils.Utils;

/**
 * Created by xiaowu on 16/2/13.
 */
public class InfoFragment extends Fragment{

    private View mView;
    private TextView mVersionNumberView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.info_fragment, container, false);
        initView();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            PackageInfo mPackageInfo = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getApplication().getPackageName(), 0);
            mVersionNumberView.setText(getString(R.string.info_version_number, mPackageInfo.versionName));
        } catch (PackageManager.NameNotFoundException ignore) {
        }
    }

    private void initView(){
        mVersionNumberView = (TextView) mView.findViewById(R.id.info_version_number_view);
    }


}
