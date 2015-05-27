/**
 * 
 */
package com.diaoyumi.android.plugin;

import com.diaoyumi.android.activity.R;
import android.content.Context;
import android.util.AttributeSet;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * @author yumin
 * 
 */
public class PullToRefreshListView extends ListView implements OnScrollListener {

	/**
	 * 
	 */
	private static final int TAP_TO_REFRESH = 1;
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;
	//private static final String TAG = "PullToRefreshListView";

	/**
	 * Listener that will receive notifications every time the list scrolls.
	 */
	private LayoutInflater mInflater;
	private OnRefreshListener mOnRefreshListener;
	private OnMoreListener mOnMoreListener;
	private OnScrollListener mOnScrollListener;
	private RotateAnimation mFlipAnimation;
	private RotateAnimation mReverseFlipAnimation;
	// header
	private RelativeLayout mRefreshViewHeader;
	private TextView mRefreshViewTextHeader;
	private ImageView mRefreshViewImage;
	private ProgressBar mRefreshViewProgressHeader;
	private TextView mRefreshViewLastUpdated;
	// footer
	private RelativeLayout mRefreshViewFooter;
	private TextView mRefreshViewTextFooter;
	private ProgressBar mRefreshViewProgressFooter;
	private boolean flag = false;

	/**
	 * 
	 */
	private int mCurrentScrollState;
	private int mRefreshState;
	private int mRefreshViewHeight;
	private int mRefreshOriginalTopPadding;
	private int mLastMotionY;
	private boolean mBounceHack;

	/**
	 * 
	 * @param context
	 */
	public PullToRefreshListView(Context context) {

		super(context);
		init(context);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public PullToRefreshListView(Context context, AttributeSet attrs) {

		super(context, attrs);
		init(context);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * 
	 * @param context
	 */
	private void init(Context context) {

		// Load all of the animations we need in code rather than through XML
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRefreshViewHeader = (RelativeLayout) mInflater.inflate(R.layout.pull_to_refresh_header, this, false);
		mRefreshViewFooter = (RelativeLayout) mInflater.inflate(R.layout.pull_to_refresh_footer, this, false);
		mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(250);
		mFlipAnimation.setFillAfter(true);
		mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);
		mRefreshOriginalTopPadding = mRefreshViewHeader.getPaddingTop();
		mRefreshViewHeight = mRefreshViewHeader.getMeasuredHeight();
		mRefreshState = TAP_TO_REFRESH;

		// header
		mRefreshViewTextHeader = (TextView) mRefreshViewHeader.findViewById(R.id.pull_to_refresh_text_header);
		mRefreshViewImage = (ImageView) mRefreshViewHeader.findViewById(R.id.pull_to_refresh_image);
		mRefreshViewProgressHeader = (ProgressBar) mRefreshViewHeader.findViewById(R.id.pull_to_refresh_progress_header);
		mRefreshViewLastUpdated = (TextView) mRefreshViewHeader.findViewById(R.id.pull_to_refresh_updated_at);
		mRefreshViewImage.setMinimumHeight(50);
		mRefreshViewHeader.setOnClickListener(new OnClickRefreshListener());
		addHeaderView(mRefreshViewHeader);

		// footer
		mRefreshViewTextFooter = (TextView) mRefreshViewFooter.findViewById(R.id.pull_to_refresh_text_footer);
		mRefreshViewProgressFooter = (ProgressBar) mRefreshViewFooter.findViewById(R.id.pull_to_refresh_progress_footer);
		addFooterView(mRefreshViewFooter);

		super.setOnScrollListener(this);
		measureView(mRefreshViewHeader);
	}

	@Override
	protected void onAttachedToWindow() {

		setSelection(1);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {

		super.setAdapter(adapter);
		setSelection(1);
	}

	/**
	 * Set the listener that will receive notifications every time the list scrolls.
	 * 
	 * @param l The scroll listener.
	 */
	@Override
	public void setOnScrollListener(AbsListView.OnScrollListener l) {

		mOnScrollListener = l;
	}

	/**
	 * Register a callback to be invoked when this list should be refreshed.
	 * 
	 * @param onRefreshListener The callback to run.
	 */
	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {

		mOnRefreshListener = onRefreshListener;
	}

	/**
	 * 
	 */
	public void setOnRefreshButtonListener() {

		mRefreshState = REFRESHING;
		setSelection(0);
		prepareForRefresh();
		onRefresh();
	}

	/**
	 * 
	 * @param onMoreListener
	 */
	public void setOnMoreListener(OnMoreListener onMoreListener) {

		mOnMoreListener = onMoreListener;
	}

	/**
	 * Set a text to represent when the list was last updated.
	 * 
	 * @param lastUpdated Last updated at.
	 */
	public void setLastUpdated(CharSequence lastUpdated) {

		if (lastUpdated != null) {
			mRefreshViewLastUpdated.setVisibility(View.VISIBLE);
			mRefreshViewLastUpdated.setText(lastUpdated);
		} else {
			mRefreshViewLastUpdated.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		final int y = (int) event.getY();
		mBounceHack = false;

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (!isVerticalScrollBarEnabled()) {
				setVerticalScrollBarEnabled(true);
			}
			if (getFirstVisiblePosition() == 0 && mRefreshState != REFRESHING) {
				if ((mRefreshViewHeader.getBottom() >= mRefreshViewHeight || mRefreshViewHeader.getTop() >= 0) && mRefreshState == RELEASE_TO_REFRESH) {
					// Initiate the refresh
					mRefreshState = REFRESHING;
					prepareForRefresh();
					onRefresh();
				} else if (mRefreshViewHeader.getBottom() < mRefreshViewHeight || mRefreshViewHeader.getTop() <= 0) {
					// Abort refresh and scroll down below the refresh view
					resetHeader();
					setSelection(1);
				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			applyHeaderPadding(event);
			break;
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 
	 * @param ev
	 */
	private void applyHeaderPadding(MotionEvent ev) {

		// getHistorySize has been available since API 1
		int pointerCount = ev.getHistorySize();

		for (int p = 0; p < pointerCount; p++) {
			if (mRefreshState == RELEASE_TO_REFRESH) {
				if (isVerticalFadingEdgeEnabled()) {
					setVerticalScrollBarEnabled(false);
				}
				int historicalY = (int) ev.getHistoricalY(p);
				// Calculate the padding to apply, we divide by 1.7 to simulate a more resistant effect during pull.
				int topPadding = (int) (((historicalY - mLastMotionY) - mRefreshViewHeight) / 1.7);
				mRefreshViewHeader.setPadding(mRefreshViewHeader.getPaddingLeft(), topPadding, mRefreshViewHeader.getPaddingRight(), mRefreshViewHeader.getPaddingBottom());
			}
		}
	}

	/**
	 * Sets the header padding back to original size.
	 */
	private void resetHeaderPadding() {

		mRefreshViewHeader.setPadding(mRefreshViewHeader.getPaddingLeft(), mRefreshOriginalTopPadding, mRefreshViewHeader.getPaddingRight(), mRefreshViewHeader.getPaddingBottom());
	}

	/**
	 * Resets the header to the original state.
	 */
	private void resetHeader() {

		if (mRefreshState != TAP_TO_REFRESH) {
			mRefreshState = TAP_TO_REFRESH;
			resetHeaderPadding();
			// Set refresh view text to the pull label
			mRefreshViewTextHeader.setText(R.string.pull_to_refresh_tap);
			// Replace refresh drawable with arrow drawable
			mRefreshViewImage.setImageResource(R.drawable.ic_pulltorefresh_arrow);
			// Clear the full rotation animation
			mRefreshViewImage.clearAnimation();
			// Hide progress bar and arrow.
			mRefreshViewImage.setVisibility(View.GONE);
			mRefreshViewProgressHeader.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 * @param child
	 */
	private void measureView(View child) {

		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		/* footer */
		if (!flag && 0 < firstVisibleItem && firstVisibleItem + visibleItemCount >= totalItemCount - 3) {
			mRefreshViewProgressFooter.setVisibility(View.VISIBLE);
			mRefreshViewTextFooter.setText(R.string.pull_to_refresh_loading);
			onMore();
		}

		/* header */
		// When the refresh view is completely visible, change the text to say "Release to refresh..." and flip the arrow drawable.
		if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL && mRefreshState != REFRESHING) {
			if (firstVisibleItem == 0) {
				mRefreshViewImage.setVisibility(View.VISIBLE);
				if ((mRefreshViewHeader.getBottom() >= mRefreshViewHeight + 20 || mRefreshViewHeader.getTop() >= 0) && mRefreshState != RELEASE_TO_REFRESH) {
					mRefreshViewTextHeader.setText(R.string.pull_to_refresh_release);
					mRefreshViewImage.clearAnimation();
					mRefreshViewImage.startAnimation(mFlipAnimation);
					mRefreshState = RELEASE_TO_REFRESH;
				} else if (mRefreshViewHeader.getBottom() < mRefreshViewHeight + 20 && mRefreshState != PULL_TO_REFRESH) {
					mRefreshViewTextHeader.setText(R.string.pull_to_refresh_pull);
					if (mRefreshState != TAP_TO_REFRESH) {
						mRefreshViewImage.clearAnimation();
						mRefreshViewImage.startAnimation(mReverseFlipAnimation);
					}
					mRefreshState = PULL_TO_REFRESH;
				}
			} else {
				mRefreshViewImage.setVisibility(View.GONE);
				resetHeader();
			}
		} else if (mCurrentScrollState == SCROLL_STATE_FLING && firstVisibleItem == 0 && mRefreshState != REFRESHING) {
			setSelection(1);
			mBounceHack = true;
		} else if (mBounceHack && mCurrentScrollState == SCROLL_STATE_FLING) {
			setSelection(1);
		}

		if (mOnScrollListener != null) {
			mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		mCurrentScrollState = scrollState;
		if (mCurrentScrollState == SCROLL_STATE_IDLE) {
			mBounceHack = false;
		}
		if (mOnScrollListener != null) {
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	/**
	 * 
	 */
	public void prepareForRefresh() {

		resetHeaderPadding();
		mRefreshViewImage.setVisibility(View.GONE);
		// We need this hack, otherwise it will keep the previous drawable.
		mRefreshViewImage.setImageDrawable(null);
		mRefreshViewProgressHeader.setVisibility(View.VISIBLE);
		// Set refresh view text to the refreshing label
		mRefreshViewTextHeader.setText(R.string.pull_to_refresh_loading);
		mRefreshState = REFRESHING;
	}

	/**
	 * 
	 */
	public void onRefresh() {

		// Log.d(TAG, "onRefresh");

		if (mOnRefreshListener != null) {
			mOnRefreshListener.onRefresh();
		}
	}

	/**
	 * 
	 */
	public void onMore() {

		// Log.d(TAG, "onMore");

		if (mOnMoreListener != null) {
			mOnMoreListener.onMore();
		}
	}

	/**
	 * Resets the list to a normal state after a refresh.
	 * 
	 * @param lastUpdated Last updated at.
	 */
	public void onRefreshComplete(CharSequence lastUpdated) {

		setLastUpdated(lastUpdated);
		onRefreshComplete();
	}

	/**
	 * Resets the list to a normal state after a refresh.
	 */
	public void onRefreshComplete() {

		// Log.d(TAG, "onRefreshComplete");

		if (flag) flag = false;
		resetHeader();
		// If refresh view is visible when loading completes, scroll down to the next item.
		if (mRefreshViewHeader.getBottom() > 0) {
			invalidateViews();
			setSelection(1);
		}
	}

	/**
	 * 
	 */
	public void onMoreComplete() {

		// Log.d(TAG, "onMoreComplete");

		mRefreshViewProgressFooter.setVisibility(View.GONE);
	}

	/**
	 * 
	 */
	public void onMoreNoData() {

		// Log.d(TAG, "onMoreNoData");

		flag = true;
		mRefreshViewProgressFooter.setVisibility(View.GONE);
		mRefreshViewTextFooter.setText(R.string.pull_to_refresh_no_data);
	}

	/**
	 * Invoked when the refresh view is clicked on. This is mainly used when
	 * there's only a few items in the list and it's not possible to drag the
	 * list.
	 */
	private class OnClickRefreshListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (mRefreshState != REFRESHING) {
				prepareForRefresh();
				onRefresh();
			}
		}

	}

	/**
	 * Interface definition for a callback to be invoked when list should be refreshed.
	 */
	public interface OnRefreshListener {

		/**
		 * Called when the list should be refreshed.
		 * <p>
		 * A call to {@link PullToRefreshListView #onRefreshComplete()} is
		 * expected to indicate that the refresh has completed.
		 */
		public void onRefresh();
	}

	/**
	 * @author yumin
	 * 
	 */
	public interface OnMoreListener {

		/**
		 * 
		 */
		public void onMore();
	}

}
