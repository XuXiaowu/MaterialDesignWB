package truecolor.mdwb.ViewHolder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import truecolor.mdwb.R;

/**
 * Created by xiaowu on 15/11/9.
 */
public class SearchUsersViewHolder extends AbsItemHolder {

    public RelativeLayout mRootView;
    public TextView mUserNameView;
    public TextView mFollowersCountView;

    public SearchUsersViewHolder(View itemView) {
        super(itemView);

        mRootView = (RelativeLayout) itemView.findViewById(R.id.root_view);
        mUserNameView = (TextView) itemView.findViewById(R.id.user_name_view);
        mFollowersCountView = (TextView) itemView.findViewById(R.id.followers_count_view);
    }
}
