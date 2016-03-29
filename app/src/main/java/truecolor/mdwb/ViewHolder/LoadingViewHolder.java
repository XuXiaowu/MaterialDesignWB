package truecolor.mdwb.ViewHolder;

import android.view.View;
import android.widget.RelativeLayout;

import truecolor.mdwb.R;

/**
 * Created by xiaowu on 15/11/9.
 */
public class LoadingViewHolder extends AbsItemHolder{

    public RelativeLayout mRootView;

    public LoadingViewHolder(View view){
        super(view);

        mRootView = (RelativeLayout) view.findViewById(R.id.root_view);
    }
}

