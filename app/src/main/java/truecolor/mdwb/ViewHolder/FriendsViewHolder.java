package truecolor.mdwb.ViewHolder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import truecolor.mdwb.R;
import truecolor.mdwb.view.CircleImageView;

/**
 * Created by xiaowu on 15/11/9.
 */
public class FriendsViewHolder extends AbsItemHolder {

    public LinearLayout mRootView;
    public TextView mUserNameView;
    public CircleImageView mHeadView;

    public FriendsViewHolder(View itemView) {
        super(itemView);

        mRootView = (LinearLayout) itemView.findViewById(R.id.root_view);
        mUserNameView = (TextView) itemView.findViewById(R.id.user_name_view);
        mHeadView = (CircleImageView) itemView.findViewById(R.id.head_view);
    }
}
