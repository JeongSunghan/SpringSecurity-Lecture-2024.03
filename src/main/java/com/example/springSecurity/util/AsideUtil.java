package com.example.springSecurity.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:/static/data/myKeys.properties")
public class AsideUtil {
	@Value("${roadAddrKey}") private String roadAddrKey;
	@Value("${kakaoApiKey}") private String kakaoApiKey;
	@Value("${openWeatherApiKey}") private String openWeatherApiKey;

	public String getTodayQuote(String filename) {
		String result = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename), 1024);
			int index = (int) Math.floor(Math.random() * 100);		// 0 ~ 99
			for (int i = 0; i <= index; i++)
				result = br.readLine();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 행안부 도로명주소 API
	public String getRoadAddr(String keyword) {
		String roadAddr = null;
		try {
			keyword = URLEncoder.encode(keyword, "utf-8");
			String apiUrl = "https://www.juso.go.kr/addrlink/addrLinkApi.do"
					+ "?confmKey=" + roadAddrKey + "&keyword=" + keyword
					+ "&currentPage=1&countPerPage=5&resultType=json";
			URL url = new URL(apiUrl);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
			String line = null, result = "";
			while ((line = br.readLine()) != null)
				result += line;
			br.close();
			
			// JSON 데이터에서 원하는 값 추출하기
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result);
			JSONObject results = (JSONObject) object.get("results");
			JSONArray juso = (JSONArray) results.get("juso");
			JSONObject jusoItem = (JSONObject) juso.get(0);
			roadAddr = (String) jusoItem.get("roadAddr");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roadAddr;
	}
	
	// Kakao Local API
	public Map<String, String> getGeocode(String addr) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			String query = URLEncoder.encode(addr, "utf-8");
			String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json"
					+ "?query=" + query;
			
			URL url = new URL(apiUrl);
			// Header setting
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Authorization", "KakaoAK " + kakaoApiKey);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			String line = null, result = "";
			while ((line = br.readLine()) != null)
				result += line;
			br.close();
			
			// JSON 데이터에서 원하는 값 추출하기
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result);
			JSONArray documents = (JSONArray) object.get("documents");
			JSONObject item = (JSONObject) documents.get(0);
			String lon = (String) item.get("x");
			String lat = (String) item.get("y");
			map.put("lon", lon);
			map.put("lat", lat);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	// Open Weather API
	public String getWeather(String lon, String lat) {
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather"
				+ "?lat=" + lat + "&lon=" + lon + "&appid=" + openWeatherApiKey
				+ "&units=metric&lang=kr";
		String weatherStr = null;
		try {
			URL url = new URL(apiUrl);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
			String line = null, result = "";
			while ((line = br.readLine()) != null)
				result += line;
			br.close();
			
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(result);
			JSONArray weather = (JSONArray) obj.get("weather");
			JSONObject weatherItem = (JSONObject) weather.get(0);
			String desc = (String) weatherItem.get("description");
			String iconCode = (String) weatherItem.get("icon");
			JSONObject main = (JSONObject) obj.get("main");
			double temp = (Double) main.get("temp");
			String tempStr = String.format("%.1f", temp);
			String iconUrl = "http://api.openweathermap.org/img/w/" + iconCode + ".png";
			weatherStr = "<img src=\"" + iconUrl + "\" height=\"28\">" + desc + ","
					+ " 온도: " + tempStr + "&#8451";			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return weatherStr;
	}
	
}
