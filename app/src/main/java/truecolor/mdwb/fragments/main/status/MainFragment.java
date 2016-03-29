package truecolor.mdwb.fragments.main.status;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import truecolor.mdwb.R;
import truecolor.mdwb.adapter.StatusAdapter;
import truecolor.mdwb.apps.MainActivity;
import truecolor.mdwb.apps.PublishActivity;
import truecolor.mdwb.global.Constant;
import truecolor.mdwb.logics.http.HttpService;
import truecolor.mdwb.model.FriendsTimelineResult;
import truecolor.mdwb.utils.NotificationUtils;
import truecolor.mdwb.utils.PreferenceUtils;
import truecolor.mdwb.utils.Utils;

/**
 * Created by xiaowu on 15/11/2.
 */
public class MainFragment extends Fragment {

    private MainActivity mActivity;
    private View mView;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private PagerAdapter mPagerAdapter;
    public static FloatingActionButton sWhiteFab;
    public static AppBarLayout sAppBarlayout;

    private boolean mIsSetViewPagerLastPosition = true;

    private static final String[] tabTitles = new String[]{"关注微博", "互相关注", "公共微博"};

    public MainFragment(MainActivity activity){
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.main_fragment, container, false);
        initView();
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPagerAdapter == null) {
            setupViewPager();
            mTabLayout.setupWithViewPager(mViewPager);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) mView.findViewById(R.id.toolbar);
        mActivity.setSupportActionBar(toolbar);

        ActionBar actionBar = mActivity.getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.menu_title_home));

        mViewPager = (ViewPager) mView.findViewById(R.id.viewpager);
        sWhiteFab = (FloatingActionButton) mView.findViewById(R.id.fab_white);
        mTabLayout = (TabLayout) mView.findViewById(R.id.tab_view);
        sAppBarlayout = (AppBarLayout) mView.findViewById(R.id.appbar);
        sWhiteFab.setOnClickListener(mWhiteFabClickListener);
        mViewPager.setOffscreenPageLimit(3);
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        public int getItemPosition(Object object){
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    private View.OnClickListener mWhiteFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType(Constant.PUBLISH_STATUESE_NEW);
            intent.setClass(mActivity, PublishActivity.class);
            startActivity(intent);
        }
    };

    private void setupViewPager() {
        mPagerAdapter = new PagerAdapter(getChildFragmentManager());
        mPagerAdapter.addFragment(PageSectionFragment.newInstance(Constant.PAGE_SECETION_FRAGEMENT_TYPE_FRIENDS, mActivity), tabTitles[0]);
        mPagerAdapter.addFragment(PageSectionFragment.newInstance(Constant.PAGE_SECETION_FRAGEMENT_TYPE_BILATERA, mActivity), tabTitles[1]);
        mPagerAdapter.addFragment(PageSectionFragment.newInstance(Constant.PAGE_SECETION_FRAGEMENT_TYPE_PUBILE, mActivity), tabTitles[2]);
        mViewPager.setAdapter(mPagerAdapter);
    }


}
