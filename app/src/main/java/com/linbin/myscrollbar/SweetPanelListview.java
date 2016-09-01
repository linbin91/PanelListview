package com.linbin.myscrollbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/9/1.
 */
public class SweetPanelListview extends ListView implements AbsListView.OnScrollListener{

    private OnPositionChangedChangedListener mListener;
    private View mScrollBarPanel;
    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;
    private int mScrollBarPanelPosition = 0;
    private int thumbOffset = 0;
    private int mLastPostion;
    private Animation mInAnimation;
    private Animation mOutAnimation;


    public SweetPanelListview(Context context, AttributeSet attrs) {
        super(context, attrs);

        super.setOnScrollListener(this);
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.ExtendedListView);
        int layoutId = array.getResourceId(R.styleable.ExtendedListView_scrollBarPanel,0);
        int inAnimation = array.getResourceId(R.styleable.ExtendedListView_scrollBarPanelInAnimation,0);
        int outAnimation = array.getResourceId(R.styleable.ExtendedListView_scrollBarPanelOntAnimation,0);
        array.recycle();

        mScrollBarPanel = LayoutInflater.from(context).inflate(layoutId,this,false);
        mScrollBarPanel.setVisibility(View.GONE);
        requestLayout();
        mInAnimation = AnimationUtils.loadAnimation(context,inAnimation);
        mOutAnimation = AnimationUtils.loadAnimation(context,outAnimation);

        mOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mScrollBarPanel.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }


    /**
     * computeVerticalScrollExtent()  滑动条在纵向滚动范围内它自身厚度
     * computeVerticalScrollRange()  0 - 10000,纵向滚动条，增加精确度
     * computeVerticalScrollOffset() 滚动条纵向幅度位置//比如 5000,
     * @param view
     * @param
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
       if (mScrollBarPanel != null ){


        // 1. 滑动板有多厚  ， 思路 滑动的厚度/listview的高度 = extent /range
        int height = Math.round(getMeasuredHeight() * computeVerticalScrollExtent() / computeVerticalScrollRange());
        //2 . 得到滑块正中间的y坐标
        //思路： 滑块的高度 / extente = thumoffset / offset
        thumbOffset = height * computeVerticalScrollOffset() / computeVerticalScrollExtent();

        thumbOffset += height/2;

        int left = getMeasuredWidth() - mScrollBarPanel.getMeasuredWidth() - getVerticalScrollbarWidth();
        mScrollBarPanelPosition = thumbOffset - mScrollBarPanel.getMeasuredHeight() / 2;

        //不断的修改top
        mScrollBarPanel.layout(left,
                mScrollBarPanelPosition,
                left +  mScrollBarPanel.getMeasuredWidth(),
                mScrollBarPanelPosition + mScrollBarPanel.getMeasuredHeight());

        //监听当期指示器位置在哪里
        for (int i = 0; i < getChildCount(); i++){
            View childView = getChildAt(i);
            if (thumbOffset > childView.getTop() && thumbOffset < childView.getBottom()){
                if (mLastPostion != firstVisibleItem + i){
                    mLastPostion = firstVisibleItem + i;
                    //因为text长度不断变化，宽度也随时变化，所以需要重新测量
                    mListener.positionChangedChanged(this,mLastPostion,mScrollBarPanel);
                    measureChild(mScrollBarPanel,mWidthMeasureSpec,mHeightMeasureSpec);
                    break;
                }
            }
        }
       }
    }
    public interface OnPositionChangedChangedListener{
        public void  positionChangedChanged(SweetPanelListview listview, int position, View view);
    }

    public void setPositionChangedListener(OnPositionChangedChangedListener listener){
        mListener = listener;
    }


    /**
     * 在ViewGroup 绘制的时候再往上面加一个自己绘制
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mScrollBarPanel != null && mScrollBarPanel.getVisibility() == View.VISIBLE){
            drawChild(canvas,mScrollBarPanel,getDrawingTime());
        }
    }

    /**
     * drawChild 的时候又会去调用 父容器的oNMeasure方法
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
        mScrollBarPanel.measure(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mScrollBarPanel != null){
            mScrollBarPanel.layout(getMeasuredWidth() - mScrollBarPanel.getMeasuredWidth()- getVerticalScrollbarWidth(),
                    getMeasuredWidth() - getVerticalScrollbarWidth(),
                    mScrollBarPanelPosition,
                    mScrollBarPanelPosition + mScrollBarPanel.getMeasuredHeight());
        }
    }

    @Override
    protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
        boolean  result = super.awakenScrollBars(startDelay, invalidate);
        if (result && mScrollBarPanel.getVisibility() == View.GONE){
            if (mInAnimation != null){
                mScrollBarPanel.setVisibility(View.VISIBLE);
                mScrollBarPanel.startAnimation(mInAnimation);
                mHandler.removeCallbacks(runnable);
                mHandler.postDelayed(runnable,startDelay);
            }
        }
        return result;
    }

    private Handler mHandler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mOutAnimation != null){
                mScrollBarPanel.startAnimation(mOutAnimation);
            }
        }
    };
}
