package gui;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RestHandlers {
	private static final Logger LOGGER = Logger.getGlobal();
	private static final String SERVER_IP = "http://3.22.76.191:8000";
	private static final int TIMEOUT_VALUE = 5000;
	private static final int SUCCESS_STATUS = 200;

	private enum SupportedHTTPMethods {
		GET, POST, DELETE
	}

	public static boolean isValidUsername(String uname) throws IOException {
		String query = SERVER_IP + "/unameAgent/?uname=" + uname;
		log(query);
		URL url = new URL(query);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(SupportedHTTPMethods.GET.toString());
		conn.setRequestProperty("Accept-Charset", "UTF-8");
		conn.setConnectTimeout(TIMEOUT_VALUE);
		conn.setReadTimeout(TIMEOUT_VALUE);
		log("open HTTP connection");
		int status = conn.getResponseCode();
		if (status == SUCCESS_STATUS) {
			log("user found");
			return true;
		} else {
			log("user not found");
			return false;
		}
	}

	public static boolean registerUser(String uname) throws IOException {
		String query = SERVER_IP + "/unameAgent/";
		log(query);
		URL url = new URL(query);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(SupportedHTTPMethods.POST.toString());
		conn.setRequestProperty("Content-Type", "application/raw");
		conn.setRequestProperty("Accept-Charset", "UTF-8");
		conn.setUseCaches(false);
		conn.setConnectTimeout(TIMEOUT_VALUE);
		conn.setReadTimeout(TIMEOUT_VALUE);
		log("open HTTP connection");
		// send body
		try (DataOutputStream wstream = new DataOutputStream(conn.getOutputStream())) {
			wstream.write(("uname=" + uname).getBytes(Charset.forName("UTF-8")));
			wstream.flush();
			wstream.close();
		}
		int status = conn.getResponseCode();
		if (status == SUCCESS_STATUS) {
			log("user registered");
			return true;
		} else {
			log("server error, failed to register user");
			return false;
		}
	}

	public static boolean addOrders(String uname, String orders) throws IOException {
		String query = SERVER_IP + "/orderAgent/";
		log(query);
		URL url = new URL(query);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(SupportedHTTPMethods.POST.toString());
		conn.setRequestProperty("Content-Type", "application/raw");
		conn.setRequestProperty("Accept-Charset", "UTF-8");
		conn.setUseCaches(false);
		conn.setConnectTimeout(TIMEOUT_VALUE);
		conn.setReadTimeout(TIMEOUT_VALUE);
		log("open HTTP connection");
		// form body
		String body = "uname=" + uname + "&";
		String[] list = orders.split(",");
		if (list.length == 1) {
			list = orders.split(" ");
			if (list.length == 1) {
				list = orders.split("\n");
			}
		}
		for (int i = 0; i < list.length; i++) {
			body += i + "=" + list[i] + "&";
		}
		// remove last "&"
		body = body.substring(0, body.length() - 1);
		// send body
		try (DataOutputStream wstream = new DataOutputStream(conn.getOutputStream())) {
			wstream.write(body.getBytes(Charset.forName("UTF-8")));
			wstream.flush();
			wstream.close();
		}
		int status = conn.getResponseCode();
		if (status == SUCCESS_STATUS) {
			log("orders updated");
			return true;
		} else {
			log("server error, failed to add orders");
			return false;
		}
	}
	
	public static boolean deleteOrders(String uname, String order) throws IOException {
		String query = SERVER_IP + "/deleteAgent/?uname=" + uname + "&orderid=" + order;
		log(query);
		URL url = new URL(query);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(SupportedHTTPMethods.GET.toString());
		conn.setRequestProperty("Accept-Charset", "UTF-8");
		conn.setConnectTimeout(TIMEOUT_VALUE);
		conn.setReadTimeout(TIMEOUT_VALUE);
		log("open HTTP connection");
		int status = conn.getResponseCode();
		if (status == SUCCESS_STATUS) {
			log("orders deleted");
			return true;
		} else {
			log("server error, failed to delete orders");
			return false;
		}
	}
	
	public static boolean updateOrders(String uname, String order, String status) throws IOException {
		String query = SERVER_IP + "/updateAgent/";
		log(query);
		URL url = new URL(query);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(SupportedHTTPMethods.POST.toString());
		conn.setRequestProperty("Content-Type", "application/raw");
		conn.setRequestProperty("Accept-Charset", "UTF-8");
		conn.setUseCaches(false);
		conn.setConnectTimeout(TIMEOUT_VALUE);
		conn.setReadTimeout(TIMEOUT_VALUE);
		log("open HTTP connection");
		// form body
		String statusId = (status.equals(Status.DELIVERED.toString())) ? "1" : "0";
		String body = "uname=" + uname + "&orderid=" + order + "&status=" + statusId;
		// send body
		try (DataOutputStream wstream = new DataOutputStream(conn.getOutputStream())) {
			wstream.write(body.getBytes(Charset.forName("UTF-8")));
			wstream.flush();
			wstream.close();
		}
		int resCode = conn.getResponseCode();
		if (resCode == SUCCESS_STATUS) {
			log("orders updated");
			return true;
		} else {
			log("server error, failed to update orders");
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static String[][] getOrders(String uname) {
		try {
			String query = SERVER_IP + "/orderAgent/?uname=" + uname;
			log(query);
			URL url = new URL(query);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(SupportedHTTPMethods.GET.toString());
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setConnectTimeout(TIMEOUT_VALUE);
			conn.setReadTimeout(TIMEOUT_VALUE);
			log("open HTTP connection");
			int status = conn.getResponseCode();
			if (status == SUCCESS_STATUS) {
				log("orders found");
				InputStream stream = conn.getInputStream();
				byte[] output = IOUtils.toByteArray(stream);
				String str = new String(output, StandardCharsets.UTF_8);
				log(str);
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(str);
				String[][] entries = new String[((Set<Map.Entry<Object, Object>>) obj.entrySet()).size()][3];
				int row = 0;
				for (Map.Entry<Object, Object> expectedEntry : (Set<Map.Entry<Object, Object>>) obj.entrySet()) {
					String order = (String) expectedEntry.getKey();
					JSONObject props = (JSONObject) expectedEntry.getValue();
					String state = null; String date = null;
					for (Map.Entry<Object, Object> propEntry : (Set<Map.Entry<Object, Object>>) props.entrySet()) {
						if (((String) propEntry.getKey()).equals("state")) {
							switch (Long.valueOf((Long) propEntry.getValue()).intValue()) {
								case 0:
									state = Status.SHIPPED.toString();
									break;
								case 1:
									state = Status.DELIVERED.toString();
									break;
								default:
									state = "";
									break;
							}
						} else if (((String) propEntry.getKey()).equals("date")) {
							long itemLong = (long) (Double.valueOf((Double) propEntry.getValue()).longValue() * 1000);
							Date itemDate = new Date(itemLong);
							date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(itemDate);
						}
					}
					String[] entry = new String[] { order, state, date };
					entries[row] = entry;
					row++;
				}
				return entries;
			} else {
				log("orders not found");
				return new String[0][0];
			}
		} catch (Exception err) {
			log(err.getMessage());
			return new String[0][0];
		}
	}

	private static void log(String msg) {
		LOGGER.info("[RestHandler]: " + msg);
	}
}
