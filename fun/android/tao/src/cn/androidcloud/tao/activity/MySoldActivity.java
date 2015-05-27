/**
 * 
 */
package cn.androidcloud.tao.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import cn.androidcloud.tao.util.CommonUtil;
import cn.androidcloud.tao.util.ConstantsUtil;
import cn.androidcloud.tao.util.TaobaoUtil;
import cn.androidcloud.tao.util.TradeUtil;
import com.taobao.api.TaobaoApiException;
import com.taobao.api.TaobaoJsonRestClient;
import com.taobao.api.model.Order;
import com.taobao.api.model.Trade;
import com.taobao.api.model.TradesGetResponse;
import com.taobao.api.model.TradesSoldGetRequest;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * @author WANG Yumin
 * 
 */
public class MySoldActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Bundle bundle = getIntent().getExtras();
		String sessionKey = "2e8c0a843c0a613ea5182def6501176b0";// bundle.getString("sessionKey");
		String fields = "orders.pic_path,orders.title,created,payment,tid,buyer_nick";
		String status = "";
		byte[] imageByte = null;
		ImageView imageView = (ImageView) findViewById(R.id.ivOrderPicPath);

		setTitle(ConstantsUtil.TITLE_MY_SOLD + "("
				+ TradeUtil.getNameByStatus(status) + ")");

		try {

			TradesSoldGetRequest request = new TradesSoldGetRequest();
			request.setFields(fields);
			request.setStatus(status);
			// request.setPageNo(3);
			// request.setPageSize(1);
			TaobaoJsonRestClient client = TaobaoUtil.getJsonRestClient();
			TradesGetResponse response = client.tradesSoldGet(request,
					sessionKey);
			System.out.println(response.getBody());

			List<Trade> trades = response.getTrades();
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			Map<String, Object> item;

			if (null != trades && 0 < trades.size()) {

				Iterator<Trade> iterator = trades.iterator();
				while (iterator.hasNext()) {

					Trade trade = (Trade) iterator.next();

					// 订单表
					List<Order> orders = trade.getOrders();
					String orderPicPath = "";
					String orderTitle = "";
					if (0 < orders.size()) {

						Order order = (Order) orders.get(0);
						orderPicPath = order.getPicPath();
						orderTitle = order.getTitle();
						if (1 < orders.size()) {

							orderTitle = orderTitle
									+ ConstantsUtil.TXT_SUFFIX_ORDER_TITLE;
						}

					} else {

						continue;

					}

					// 交易号
					String tid = ConstantsUtil.LABLE_TID + trade.getTid();
					// 成交于
					String created = ConstantsUtil.LABLE_CREATED
							+ CommonUtil.dateFormat(trade.getCreated(),
									"yyyy-MM-dd HH:mm");
					// 实收款(元)
					String payment = ConstantsUtil.LABLE_PAYMENT
							+ trade.getPayment() + ConstantsUtil.UNIT_RMB;
					// 买家
					String buyerNick = ConstantsUtil.LABLE_BUYER_NICK
							+ trade.getBuyerNick();

					// ----- 网络图片处理过程 -----
					try {

						imageByte = CommonUtil.fetchImage(orderPicPath);
						CommonUtil.saveImage(imageByte, tid + ".gif");
						FileInputStream file = null;

						try {

							file = new FileInputStream(
									ConstantsUtil.PROJECT_PATH + tid + ".gif");

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}

						Drawable drawable = Drawable.createFromStream(file,
								"src");
						imageView.setImageDrawable(drawable);

					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					item = new HashMap<String, Object>();
					try {

						item.put("ivOrderPicPath", imageView);
						item.put("tvOrderTitle", orderTitle);
						item.put("tvTid", tid);
						item.put("tvCreated", created);
						item.put("tvPayment", payment);
						item.put("tvBuyerNick", buyerNick);

					} catch (Exception e) {
						e.printStackTrace();
					}

					data.add(item);
				}
			}

			super.onCreate(savedInstanceState);
			SimpleAdapter adapter = new SimpleAdapter(this, data,
					R.layout.mysold, new String[] { "ivOrderPicPath",
							"tvOrderTitle", "tvTid", "tvCreated", "tvPayment",
							"tvBuyerNick" }, new int[] { R.id.ivOrderPicPath,
							R.id.tvOrderTitle, R.id.tvTid, R.id.tvCreated,
							R.id.tvPayment, R.id.tvBuyerNick });
			ListView listView = new ListView(this);
			listView.setAdapter(adapter);
			setContentView(listView);

		} catch (TaobaoApiException e) {
			e.printStackTrace();
		}
	}
}
