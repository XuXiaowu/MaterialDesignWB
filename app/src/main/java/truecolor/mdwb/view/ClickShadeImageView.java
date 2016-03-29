package truecolor.mdwb.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;

import truecolor.mdwb.R;

/**
 * 点击加上阴影遮罩的ImageView
 * @date 2014-8-28
 * @author Xiaowu.Xu
 */
public class ClickShadeImageView extends ImageView {

    private float mScale;
    private int mShadeColor;

	public ClickShadeImageView(Context context) {
		this(context, null, 0);
	}

    public ClickShadeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClickShadeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClickShadeImageView);
        mScale = ta.getFloat(R.styleable.ClickShadeImageView_imageScale, 0.0f);
        mShadeColor = ta.getColor(R.styleable.ClickShadeImageView_shadeColor, Color.TRANSPARENT);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mScale > 0) {
            int top = getPaddingTop();
            int bottom = getPaddingBottom();
            int left = getPaddingLeft();
            int right = getPaddingRight();
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int)((width - left - right) * mScale) + top + bottom;
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (isPressed()) canvas.drawColor(mShadeColor);
	}

	@Override
	protected void dispatchSetPressed(boolean pressed) {
		super.dispatchSetPressed(pressed);
		invalidate();
	}
}
