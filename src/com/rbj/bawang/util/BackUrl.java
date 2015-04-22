package com.rbj.bawang.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class BackUrl {
	public String openBowers(String url) {
		// TODO Auto-generated method stub
		String standard = "^[http://][\\S]+\\.(com|org|net|mil|edu|COM|ORG|NET|MIL|EDU)$";
		//String standard = "^[http://www.|www.][\\S]+\\.(com|org|net|mil|edu|COM|ORG|NET|MIL|EDU)$";
		Pattern pattern = Pattern.compile(standard);
		Matcher match = pattern.matcher(url);
		if (match.find()) {
			url = "http://" + url;
			Log.e("isurl", "yes");
		} else {
			Log.e("isurl", "no");
			url = "http://www.baidu.com/s?wd=" + url;
		}
		return url;
		
	}
}
