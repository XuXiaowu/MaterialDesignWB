package truecolor.mdwb.ViewHolder;

import android.view.View;
import android.widget.RelativeLayout;

import truecolor.mdwb.R;

/**
 * Created by xiaowu on 15/11/9.
 */
public class LoadingErrorViewHolder extends AbsItemHolder{

    public RelativeLayout mErrorView;

    public LoadingErrorViewHolder(View view){
        super(view);
        mErrorView = (RelativeLayout) view.findViewById(R.id.root_view);
    }
}

