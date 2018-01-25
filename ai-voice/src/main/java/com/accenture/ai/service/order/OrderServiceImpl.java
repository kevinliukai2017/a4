package com.accenture.ai.service.order;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl {
	public String getOrderCount(String month, String status) {
		String result = "";
		if ("审核中".equals(status)) {
			result = "状态为审核中的订单数为" + convertNumToChinese(String.valueOf(getRandom(199, 500))) + "张";
		} else {
			int min = !month.equals("网站整体") ? 1000 : 1000 * 12;
			int max = !month.equals("网站整体") ? 9999 : 9999 * 12;
			result = "好的" + month + "的订单数为" + convertNumToChinese(String.valueOf(getRandom(min, max))) + "张";
		}
		return result;
	}

	// to do
	// move to util
	public int getRandom(int min, int max) {
		Random random = new Random();
		return random.nextInt(max) % (max - min + 1) + min;
	}

	// to do
	// move to util
	public String convertNumToChinese(String value) {
		String[] s1 = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
		String[] s2 = { "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千" };

		String result = "";

		int n = value.length();
		for (int i = 0; i < n; i++) {

			int num = value.charAt(i) - '0';

			if (i != n - 1 && num != 0) {
				result += s1[num] + s2[n - 2 - i];
			} else {
				result += s1[num];
			}
		}
		return result;
	}

}
