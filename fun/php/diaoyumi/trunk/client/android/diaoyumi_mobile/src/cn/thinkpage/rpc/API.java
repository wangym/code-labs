package cn.thinkpage.rpc;

import com.diaoyumi.android.etc.HttpClient;
import com.diaoyumi.android.etc.Util;


import cn.thinkpage.common.Location;
import cn.thinkpage.vo.Weather;

public class API {
	private String appKey = "2FE5F661E1D4E302";
	private String baseUrl = "http://www.thinkpage.cn/weather/api.svc/";
	public Weather getWeather(Double lng, Double lat){
		String cityCode = Location.getInstance().getCityCodeByLngAndLat(lng, lat);
		String weatherUrl = baseUrl + "getWeather?language=zh-chs&provider=smart&unit=c&key=" + appKey + "&city=" + cityCode;
		String response = HttpClient.get(weatherUrl);
		if (Util.isNotEmpty(response)){
			Weather ret = new Weather();
			if (ret.loadJson(response)) return ret;
		}
		return null; 
	}
	
	public static void main(String[] args) {
		API api = new API();
		Weather w = api.getWeather(120.08,30.23);
		System.out.println(w);
		System.out.println("城市：" + w.getCityName());
		System.out.println("温度:" + w.getTemperature());
		System.out.println("气压:" + w.getPressure());
		System.out.println("天气:" + w.getCodeCaption(w.getCode()));
		w = api.getWeather(120.08, 45.23);
		System.out.println("城市：" + w.getCityName());
		System.out.println("温度:" + w.getTemperature());
		System.out.println("气压:" + w.getPressure());
		System.out.println("天气:" + w.getCodeCaption(w.getCode()));
	}

}
