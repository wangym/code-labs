package cn.thinkpage.vo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dianoyumi.common.Tools;

/**
 * 
 * 解析www.thinkpage.cn天气预报API数据
 * 以下是样本:
 * 
{
    "Stat": "OK",                       //错误信息。正常返回时值为"OK"。
    "Weathers": [{                      //城市天气数组。同时查询多个城市时，该数组则包含多个对象。
        "CityId": "101010100",          //城市ID
        "CityName": "北京",             //城市名
        "Country": "CH",                //国家。仅数据源为yahoo时有效。
        "Current": {                    //实时天气信息
            "Chill": "5",               //当前实时体感温度
            "Code": "20",               //天气图标代码 (查看说明)
            "Direction": "北",          //风向
            "Humidity": "93",           //湿度。单位：百分比%。
            "Index": null,              //生活指数。目前不对API接口开放。
            "Pressure": "1015.92",      //气压。单位：百帕hPa。
            "Rising": "1",              //气压变化。0或steady为稳定，1或rising为升高，2或falling为降低。
            "Speed": "3.22",            //风速。单位：km/h。
            "Sunrise": "6:58 am",       //日出时间。
            "Sunset": "4:56 pm",        //日落时间。
            "Temperature": "5",         //当前实时温度
            "Text": "雾",               //天气情况
            "Visibility": "3.49"        //能见度。单位：公里km。
        },
        "Forecast": [{                  //天气预报数组。smart和cma返回6天预报，msn返回5天预报，yahoo返回2天预报。
            "Code": "31",               //天气图标代码
            "Cop": "20%",                //降水概率。
            "Date": "\/Date(1321371000000+0800)\/",     //日期
            "Day": "周二",              //星期
            "Extra": null,              //无效字段
            "High": "11",               //最高温度
            "Info": null,               //无效字段
            "Link": null,               //无效字段
            "Low": "1",                 //最低温度
            "Text": "晴"                //天气情况
        }, {                            //第二天天气预报
            "Code": "30",
            "Cop": null,
            "Date": "\/Date(1321457400000+0800)\/",
            "Day": "周三",
            "Extra": null,
            "High": "14",
            "Info": null,
            "Link": null,
            "Low": "5",
            "Text": "晴间多云"
        }],
        "LastBuildDate": "11\/15\/2011 11:30:00 PM",    //数据更新时间
        "Region": ""                                    //地区。一般不用。
    }]
}
 * @author robin
 * @see http://www.thinkpage.cn/weather/api/
 */

public class Weather {
	private String cityId;
	private String cityName;
	private String country;
	//体感温度
	private String chill;
	//天气图标代码
	private String code;
	private String direction;
	//湿度 单位:百分比
	private String humidity;
	//生活指数 目前不对API接口开放
	private String index;
	//气压 单位:百帕hPa
	private String pressure;
	//气压变化 0或steady为稳定，1或rising为升高，2或falling为降低。
	private String rising;
	//风速 单位:km/h
	private String speed;
	//日出时间
	private String sunrise;
	//日落时间
	private String sunset;
	//当时实时温度
	private String temperature;
	//天气情况 雾
	private String text;
	//能见度 单位:公里
	private String visibility;
	//生成时间 11\/15\/2011 11:30:00 PM",
	private String lastBuildDate;
	
	private ArrayList<Forecast> forecasts = new ArrayList<Forecast>();
	
	private String json;
	
	
	
	@Override
	public String toString() {
		return json;
	}


	public boolean loadJson(String json){
		try {
			JSONObject res = new JSONObject(Tools.jsonClean(json));
			if (res != null && "OK".equals(res.get("Stat"))){
				JSONArray weathers = res.getJSONArray("Weathers");
				JSONObject weather = weathers.getJSONObject(0);
				this.cityId = weather.getString("CityId");
				this.cityName = weather.getString("CityName");
				this.country = weather.getString("Country");
				this.lastBuildDate = weather.getString("LastBuildDate");
				JSONObject currentWeather = weather.getJSONObject("Current");
				this.code = currentWeather.getString("Code");
				this.chill = currentWeather.getString("Chill");
				this.direction = currentWeather.getString("Direction");
				this.humidity = currentWeather.getString("Humidity");
				this.index = currentWeather.getString("Index");
				this.pressure = currentWeather.getString("Pressure");
				this.rising = currentWeather.getString("Rising");
				this.speed = currentWeather.getString("Speed");
				this.sunrise = currentWeather.getString("Sunrise");
				this.sunset = currentWeather.getString("Sunset");
				this.temperature = currentWeather.getString("Temperature");
				this.text = currentWeather.getString("Text");
				this.visibility = currentWeather.getString("Visibility");
				JSONArray forecast = weather.getJSONArray("Forecast");
				if (forecast != null && forecast.length() > 0){
					for(int i = 0 ; i < forecast.length(); i++){
						JSONObject row = forecast.getJSONObject(i);
						Forecast forecastObj = new Forecast();
						forecastObj.setCode(row.getString("Code"));
						forecastObj.setCop(row.getString("Cop"));
						forecastObj.setDate(row.getString("Date"));
						forecastObj.setDay(row.getString("Day"));
						forecastObj.setHigh(row.getString("High"));
						forecastObj.setLow(row.getString("Low"));
						forecastObj.setText(row.getString("Text"));
						this.forecasts.add(forecastObj);
				
					}
				}
				this.json = json;
				return true;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	public String getLastBuildDate() {
		return lastBuildDate;
	}


	public void setLastBuildDate(String lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}


	public ArrayList<Forecast> getForecasts() {
		return forecasts;
	}


	public void setForecasts(ArrayList<Forecast> forecasts) {
		this.forecasts = forecasts;
	}


	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getChill() {
		return chill;
	}

	public void setChill(String chill) {
		this.chill = chill;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getPressure() {
		return pressure;
	}

	public void setPressure(String pressure) {
		this.pressure = pressure;
	}

	public String getRising() {
		return rising;
	}

	public void setRising(String rising) {
		this.rising = rising;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getSunrise() {
		return sunrise;
	}

	public void setSunrise(String sunrise) {
		this.sunrise = sunrise;
	}

	public String getSunset() {
		return sunset;
	}

	public void setSunset(String sunset) {
		this.sunset = sunset;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getCodeCaption(String code){
		return getCodeCaption(Integer.parseInt(code));
	}
	
	public String getCodeCaption(int code){
		switch (code) {
		case 0: return "龙卷风";
		case 1: return "热带风暴";
		case 2: return "飓风";
		case 3: return "雷暴雨";
		case 4: return "雷雨";
		case 5: return "雨夹雪";
		case 6: return "雨夹雹";
		case 7: return "雪夹雹";
		case 8: return "小冻雨";
		case 9: return "细雨";
		case 10: return"冻雨";
		case 11: return "阵雨";
		case 12: return "阵雨";
		case 13: return "小雪";
		case 14: return "小阵雪";
		case 15: return "飞雪";
		case 16: return "雪";
		case 17: return "冰雹";
		case 18: return "雨夹雪";
		case 19: return "沙尘";
		case 20: return "雾";
		case 21: return "阴霾";
		case 22: return "烟雾";
		case 23: return "大风";
		case 24: return "风";
		case 25: return "寒冷";
		case 26: return "多云";
		case 27: return "夜间多云";
		case 28: return "白天多云";
		case 29: return "夜间晴间多云";
		case 30: return "白天晴间多云";
		case 31: return "夜间晴";
		case 32: return "阳光充足";
		case 33: return "夜间晴朗";
		case 34: return "白天晴朗";
		case 35: return "雨夹冰雹";
		case 36: return "炎热";
		case 37: return "局部暴雨";
		case 38: return "零星暴雨";
		case 39: return "零星暴雨";
		case 40: return "零星阵雨";
		case 41: return "大雪";
		case 42: return "零星阵雪";
		case 43: return "大雪";
		case 44: return "";//未知天气
		case 45: return "雷阵雨";
		case 46: return "阵雪";
		case 47: return "局部雷阵雨";
		default:
			return null;
		}
	}

	
	public static void main(String[] args) {
		String json = "{\"Stat\": \"OK\","+
   "\"Weathers\":  [{\"CityId\": \"101010100\",\"CityName\": \"北京\",\"Country\": \"CH\","+             
   "\"Current\": {\"Chill\": \"5\",\"Code\":\"20\",\"Direction\":\"北\",\"Humidity\": \"93\",\"Index\":null,\"Pressure\":\"1015.92\",\"Rising\":\"1\",\"Speed\":\"3.22\",\"Sunrise\":\"6:58  am\",\"Sunset\":\"4:56 pm\",\"Temperature\":\"5\",\"Text\":\"雾\",\"Visibility\":\"3.49\"}," +
   "\"Forecast\":[{\"Code\": \"31\",\"Cop\": \"20%\",\"Date\": \"Date(1321371000000+0800)\",\"Day\": \"周二\",\"Extra\": null,\"High\": \"11\",\"Info\": null,\"Link\": null,\"Low\": \"1\",\"Text\": \"晴\"},"+
   "{\"Code\": \"30\",\"Cop\":null,\"Date\":\"Date(1321457400000+0800)\",\"Day\":\"周三\",\"Extra\":null,\"High\":\"14\",\"Info\": null,\"Link\": null,\"Low\": \"5\",\"Text\": \"晴间多云\"}]," +
   "\"LastBuildDate\":\"11152011 11:30:00 PM\",\"Region\":\"\"}]}";
		Weather weather = new Weather();
		weather.loadJson(json);
		System.out.println(weather);
	}
}
