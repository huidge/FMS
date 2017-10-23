package clb.core.common.utils;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@SuppressWarnings("deprecation")
public class HttpUtils {

	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	/**
	 * Http 请求方法
	 * @param url
	 * @param content
	 * @return
	 */
	public static String httpPost(String url, String content, boolean needSSL) {
		// 创建HttpPost
		String result = null;
		HttpClient httpClient = getHttpClient(needSSL, StringUtil.getHostFromURL(url));
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED + ";charset=utf-8");
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
			httpPost.setConfig(requestConfig);
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(content.getBytes("utf-8")));
			requestBody.setContentLength(content.getBytes("utf-8").length);
			httpPost.setEntity(requestBody);
			// 执行客户端请求
			HttpEntity entity = httpClient.execute(httpPost).getEntity();
			
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
				EntityUtils.consume(entity);
			}

		} catch (Throwable e) {
			logger.error("【HTTP请求失败】: url={}, content={}", url, content );
		}
		
		return result;
	}
	
	
	public static DefaultHttpClient getHttpClient(boolean sslClient, String host){
		DefaultHttpClient httpclient=null;
		if (sslClient) {
			try {
				SSLHttpClient chc = new SSLHttpClient();
				InetAddress address = null;
				String ip;
				try {
				   address = InetAddress.getByName(host);
				   ip = address.getHostAddress().toString();
				   httpclient = chc.registerSSL(ip,"TLS",443,"https");
				} catch (UnknownHostException e) {
				   logger.error("获取请求服务器地址失败：host = {} " + host);
				   e.getStackTrace().toString();
				}
				HttpParams hParams=new BasicHttpParams();
				hParams.setParameter("https.protocols", "SSLv3,SSLv2Hello");
				httpclient.setParams(hParams);
			} catch (KeyManagementException e) {
				logger.error(e.getStackTrace().toString());
			}catch (NoSuchAlgorithmException e) {
				logger.error(e.getStackTrace().toString());
			}
		}else {
			httpclient=new DefaultHttpClient();
		}
		return httpclient;
	}
	
	/***
	 * http
	 * @param urlstr
	 * @return
	 */
	public static String sendPost(String urlstr,String content) {
		StringBuffer result = new StringBuffer();
		try {
			HttpURLConnection connection = null;
			URL url = new URL(urlstr+"&content="+URLEncoder.encode(content, "UTF-8"));
			connection = (HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(6000);
			connection.connect();
			OutputStreamWriter out = new OutputStreamWriter(
			connection.getOutputStream(), "UTF-8");
			out.flush();
			out.close();
			int HttpResult = connection.getResponseCode();
			if(HttpResult == 200) {
				BufferedReader br = new BufferedReader(
				new InputStreamReader(connection.getInputStream(),"utf-8"));
				String line = null;
				while((line = br.readLine()) != null) {
					result.append(line + "\n");
				}
				br.close();
			}
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
		}
		return result.toString();
	}
}
