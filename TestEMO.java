import java.io.*;
import java.net.*;
import java.util.*;

public class TestEMO {
	public static void main(String[] args) throws Exception {
		String cookie = login();
		getBoardList(cookie);
	}

	private static String login() throws Exception {
		URL url = new URL("http://172.17.101.138/56mobile/Logon/Logon");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);
		conn.setRequestProperty( "Connection", "keep-alive" );
		conn.connect();

		String query = "user=administrator&pwd=pass@INOS";
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
		out.write(query);
		out.flush();
		out.close();

		Map<String, List<String>> header = conn.getHeaderFields();
		String cookie = "";
		for (String key : header.keySet()) {
			List<String> list = header.get(key);
			if (key != null && key.equals("Set-Cookie")) {
				String val = list.get(0);
				int index = val.indexOf(" ");
				cookie = val.substring(0, index);
				cookie += list.get(1);
			}
		}
		return cookie;
	}

	private static void getBoardList(String cookie) throws Exception {
		URL url = new URL("http://172.17.101.138/56mobile/Board/BoardList");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);
		conn.setRequestProperty( "Cookie", cookie );
		conn.setRequestProperty( "Connection", "keep-alive" );
		conn.connect();

		String query = "user=administrator&pwd=pass@INOS";
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
		out.write(query);
		out.flush();
		out.close();

		System.out.println("Response code = " + conn.getResponseCode());

		Map<String, List<String>> header = conn.getHeaderFields();
		for (String key : header.keySet()) {
			List<String> list = header.get(key);
		}
		StringBuffer buf = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
		int c;
		while( (c = reader.read()) != -1) {
			buf.append((char)c);
		}
		System.out.println(buf.toString());
	}
}
