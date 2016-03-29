package truecolor.mdwb.ViewHolder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import truecolor.mdwb.R;

/**
 * Created by xiaowu on 15/11/9.
 */
public class EmptyViewHolder extends AbsItemHolder {

    public RelativeLayout mRootView;
    public TextView mTextView;

    public EmptyViewHolder(View itemView) {
        super(itemView);

        mRootView = (RelativeLayout) itemView.findViewById(R.id.root_view);
        mTextView = (TextView) itemView.findViewById(R.id.empyt_text_view);
    }
}
