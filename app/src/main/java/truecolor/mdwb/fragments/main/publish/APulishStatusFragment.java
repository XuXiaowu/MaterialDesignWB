package truecolor.mdwb.fragments.main.publish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.annotation.Check;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import truecolor.mdwb.R;
import truecolor.mdwb.apps.FriendsActivity;
import truecolor.mdwb.fragments.main.ABaseFragment;
import truecolor.mdwb.global.Constant;
import truecolor.mdwb.model.EmotionsResult;
import truecolor.mdwb.utils.Utils;

/**
 * Created by xiaowu on 15/9/14.
 */
public class APulishStatusFragment extends ABaseFragment{

    public LinearLayout sendBtn;
    public LinearLayout emotionBtn;
    public LinearLayout photoBtn;
    public LinearLayout trendsBtn;
    public LinearLayout mentionBtn;
    public LinearLayout photoContainer;
    public LinearLayout publishBbottomBtnView;
    public EditText editContent;
    public CheckBox checkBox;
    public GridView mEmotionsGridView;
    private List<EmotionsResult> emotionsList;
    private DbUtils mDbUtils;

    private static final int REQUEST_CODE = 10001;

    @Override
    public void initContentView() {
        sendBtn = (LinearLayout) rootView.findViewById(R.id.publish_btn_send);
        emotionBtn = (LinearLayout) rootView.findViewById(R.id.publish_btn_emotion);
        photoBtn = (LinearLayout) rootView.findViewById(R.id.publish_btn_photo);
        trendsBtn = (LinearLayout) rootView.findViewById(R.id.publish_btn_trends);
        mentionBtn = (LinearLayout) rootView.findViewById(R.id.publish_btn_mention);
        photoContainer = (LinearLayout) rootView.findViewById(R.id.publish_photo_container);
        editContent = (EditText) rootView.findViewById(R.id.edit_content);
        checkBox = (CheckBox) rootView.findViewById(R.id.checkbox_view);
        mEmotionsGridView = (GridView) rootView.findViewById(R.id.emotions_gridview);
        publishBbottomBtnView = (LinearLayout) rootView.findViewById(R.id.include_publish_bottom_btn_view);
        mEmotionsGridView.setVisibility(View.GONE);

        emotionBtn.setOnClickListener(emotionBtnClickListener);
        trendsBtn.setOnClickListener(trendsBtnClickListener);
        mentionBtn.setOnClickListener(mentionBtnClickListener);

        setmEmotionsGridView();
    }

    @Override
    public void initView() {

    }

    @Override
    public int initContentViewRes() {
        return R.layout.publish_fragment_status_view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE){
            if (resultCode == getActivity().RESULT_OK){
                String userName = data.getStringExtra(Constant.EXTRA_FRIENDS_USER_NAME);
                if (userName != null){
                    Editable editable = editContent.getText();
                    int start = editContent.getSelectionStart();
                    String mention = "@" + userName + " ";
                    editable.insert(start, mention);
                    editContent.setSelection(editContent.getText().length());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setmEmotionsGridView(){
        mDbUtils= DbUtils.create(getActivity());
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<>();
        try {
            emotionsList = mDbUtils.findAll(EmotionsResult.class);
            for(int i=0;i<emotionsList.size();i++) {
                EmotionsResult emtions = emotionsList.get(i);
                if (emtions != null) {
                    HashMap<String, Object> map = new HashMap<>();
                    Field field;
                    Class mipmapClass = R.mipmap.class;
                    String imgNmae = emtions.getUrl();
                    imgNmae = imgNmae.substring(0, imgNmae.length() - 4);
                    field = mipmapClass.getDeclaredField(imgNmae);
                    int res = field.getInt(emtions.getUrl());
                    map.put("ItemImage", res);//添加图像资源的ID
                    lstImageItem.add(map);

                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        SimpleAdapter saImageItems = new SimpleAdapter(getActivity(),
                lstImageItem,//数据来源
                R.layout.emotion_item_view,
                new String[] {"ItemImage"}, //动态数组与ImageItem对应的子项
                new int[] {R.id.emotion_view}); //ImageItem的XML文件里面的一个ImageView

        mEmotionsGridView.setAdapter(saImageItems);
        mEmotionsGridView.setOnItemClickListener(mEmotionsGridViewItemClickListener);
    }

    private AdapterView.OnItemClickListener mEmotionsGridViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                EmotionsResult emotion = emotionsList.get(position);
                SpannableString ss = Utils.txtToImg(emotion.getValue(), getActivity());
                Editable editable = editContent.getText();
                int start = editContent.getSelectionStart();
                editable.insert(start, ss);
                editContent.setSelection(editContent.getText().length());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener emotionBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changeEmotionShow();
            changeInputShow();
        }
    };

    private View.OnClickListener trendsBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Editable editable = editContent.getText();
            int start = editContent.getSelectionStart();
            editable.insert(start, "##");
            int pos = editContent.getSelectionStart();
            editContent.setSelection(pos - 1);
        }
    };

    private View.OnClickListener mentionBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), FriendsActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    };

    /**
     * 改变软键盘显示状态
     */
    private void changeInputShow(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 改变表情键盘显示状态
     */
    private void changeEmotionShow(){
        PercentRelativeLayout.LayoutParams layoutParams = (PercentRelativeLayout.LayoutParams)publishBbottomBtnView.getLayoutParams();
        layoutParams.addRule(PercentRelativeLayout.ALIGN_PARENT_BOTTOM);
        int visibileSatus = mEmotionsGridView.getVisibility();
        if (visibileSatus == View.VISIBLE){
            mEmotionsGridView.setVisibility(View.GONE);
        }else {
            mEmotionsGridView.setVisibility(View.VISIBLE);
            layoutParams.removeRule(PercentRelativeLayout.ALIGN_PARENT_BOTTOM);
        }
    }


}
