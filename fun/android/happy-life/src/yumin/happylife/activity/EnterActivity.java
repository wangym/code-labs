/**
 * 入口页
 */
package yumin.happylife.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * 
 * @author yumin
 * 
 */
public class EnterActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab);

		final TabHost tabHost = getTabHost();

		tabHost.addTab(tabHost.newTabSpec("tabStoreList").setIndicator(getText(R.string.store_list_title)).setContent(new Intent(this, StoreListActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("tabCouponList").setIndicator(getText(R.string.coupon_list_title)).setContent(new Intent(this, CouponListActivity.class)));

		tabHost.setCurrentTab(0);
	}
}