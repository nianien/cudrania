package com.cudrania.web;

import com.nianien.core.exception.ExceptionHandler;
import com.nianien.core.io.Closer;
import com.nianien.core.io.Files;
import com.nianien.core.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 模拟客户端进行http请求,支持post和get方法
 * 
 * @author skyfalling
 * 
 */
public class HttpClient {

	/**
	 * Get请求,返回请求结果<br>
	 * 注:这里不对请求url进行编码处理
	 * 
	 * @param url
	 * @return
	 */
	public static String doGet(String url) {

		return send(url, "GET", null);
	}

	/**
	 * Post请求,返回请求结果<br>
	 * 注:这里不对请求url进行编码处理
	 * 
	 * @param url
	 * @return
	 */
	public static String doPost(String url) {

		return send(url, "POST", null);
	}

	/**
	 * Get请求,返回请求结果<br>
	 * 注:这里不对请求url进行编码处理<br>
	 * 而对于请求参数将以指定编码格式进行编码,并作为消息体发送到客户端
	 * 
	 * @param urlString
	 * @param parameters
	 * @param encode
	 * @return
	 */
	public static String doGet(String urlString,
			Map<String, String> parameters, String encode) {

		return send(urlString, "GET", urlEncode(parameters, encode));
	}

	/**
	 * Post请求,返回请求结果<br>
	 * 注:这里不对请求url进行编码处理<br>
	 * 而对于请求参数将以指定编码格式进行编码,并作为消息体发送到客户端
	 * 
	 * @param urlString
	 * @param parameters
	 * @param encode
	 * @return
	 */
	public static String doPost(String urlString,
			Map<String, String> parameters, String encode) {

		return send(urlString, "POST", urlEncode(parameters, encode));
	}

	/**
	 * 发送请求
	 * 
	 * @param urlString
	 * @param requestMethod
	 * @param requestBody
	 * @return
	 */
	private static String send(String urlString, String requestMethod,
			String requestBody) {
		InputStream in = null;
		OutputStream out = null;
		try {
			StringBuilder sb = new StringBuilder();
			URL url = new URL(urlString);
			// 建立连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(requestMethod);
			conn.setDoOutput(true);
			// 写入http请求消息体
			if (StringUtils.isNotEmpty(requestBody)) {
				out = conn.getOutputStream();
				out.write(requestBody.getBytes());
				out.flush();
				out.close();
			}
			// 获取读入流
			in = conn.getInputStream();
			// 读取字符串
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line).append(Files.newLine);
			}
			return new String(sb);
		} catch (Exception e) {
			throw  ExceptionHandler.throwException(e);
		} finally {
			Closer.close(in);
			Closer.close(out);
		}

	}

	/**
	 * 将请求参数以指定编码格式进行拼接
	 * 
	 * @param parameters
	 * @param encode
	 * @return
	 */
	private static String urlEncode(Map<String, String> parameters,
			String encode) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (Entry<String, String> en : parameters.entrySet()) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append("&");
			}
			sb.append(urlEncode(en.getKey(), encode)).append("=")
					.append(urlEncode(en.getValue(), encode));
		}
		return sb.toString();
	}

	/**
	 * 对URL进行编码
	 * 
	 * @param value
	 * @param encode
	 * @return
	 */
	private static String urlEncode(String value, String encode) {
		try {
			return URLEncoder.encode(value, encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return value;
		}
	}

}