package truecolor.mdwb.apps;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import truecolor.mdwb.R;
import truecolor.mdwb.fragments.main.publish.PublishStatusCommentFragment;
import truecolor.mdwb.fragments.main.publish.PublishStatusFragment;
import truecolor.mdwb.fragments.main.publish.PublishStatusRepostFragment;
import truecolor.mdwb.global.Constant;

public class PublishActivity extends ActionBarActivity {

    private Toolbar mToobar;
    private View mActionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_publish);

        mToobar = (Toolbar) findViewById(R.id.publish_toolbar);
        setSupportActionBar(mToobar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        actionBar.setDisplayHomeAsUpEnabled(true);

        initActionBarView();
        actionBar.setCustomView(mActionBarView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        String type = intent.getType();
        String statueseId = intent.getStringExtra(Constant.EXTRA_STATUESE_ID);

        switch (type){
            case Constant.PUBLISH_STATUESE_NEW:
                PublishStatusFragment publishStatusFragment = PublishStatusFragment.newInstance(mActionBarView);
                getFragmentManager().beginTransaction().add(R.id.publish_fragment_container, publishStatusFragment, "PublishFragment").commit();
                break;
            case Constant.PUBLISH_COMMENTS_CREATE:
                PublishStatusCommentFragment publishStatusCommentFragment = PublishStatusCommentFragment.newInstance(mActionBarView, statueseId, null);
                getFragmentManager().beginTransaction().add(R.id.publish_fragment_container, publishStatusCommentFragment, "PublishCommentFragment").commit();
                break;
            case Constant.PUBLISH_STATUESE_REPOST:
                String statusComment = intent.getStringExtra(Constant.EXTRA_COMMENT);
                PublishStatusRepostFragment statusRepostFragment = PublishStatusRepostFragment.newInstance(mActionBarView, statueseId, statusComment);
                getFragmentManager().beginTransaction().add(R.id.publish_fragment_container, statusRepostFragment, "PublishRepostFragment").commit();
                break;
            case Constant.PUBLISH_COMMENTS_REPLY:
                String commendId = intent.getStringExtra(Constant.EXTRA_COMMENT_ID);
                PublishStatusCommentFragment publishStatusCommentReplyFragment = PublishStatusCommentFragment.newInstance(mActionBarView, statueseId, commendId);
                getFragmentManager().beginTransaction().add(R.id.publish_fragment_container, publishStatusCommentReplyFragment, "PublishCommentFragment").commit();
                break;
            case Constant.PUBLISH_COMMENTS_REPOST:
                String comment = intent.getStringExtra(Constant.EXTRA_COMMENT);
                PublishStatusRepostFragment commentRepostFragment = PublishStatusRepostFragment.newInstance(mActionBarView, statueseId, comment);
                getFragmentManager().beginTransaction().add(R.id.publish_fragment_container, commentRepostFragment, "PublishRepostFragment").commit();
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_publish_statues, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initActionBarView(){
        mActionBarView = LayoutInflater.from(this).inflate(R.layout.action_bar_loading_view, null);
    }
}
