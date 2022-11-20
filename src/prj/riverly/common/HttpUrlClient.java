package prj.riverly.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import prj.riverly.http.protocol.HttpMethod;
import prj.riverly.http.protocol.Protocol;

public class HttpUrlClient {
	
	protected Protocol protocol = Protocol.HTTP;
	protected String baseUrl = "";
	protected int connectionTimeout = 3000;
	protected int readTimeout = 5000;
	
	protected Map<String, String> defaultHeaders = new HashMap<String, String>();	
	
	protected HttpURLConnection call(String url, String method, String queryString, Map<String, List<String>> headers, String body) throws IOException{		
		URL requestUrl = new URL(getUrl(url));
		HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
		
		conn.setRequestMethod(method);
		conn.setConnectTimeout(connectionTimeout);
		conn.setReadTimeout(readTimeout);
		
		defaultHeaders.forEach((key, value) -> {
			conn.setRequestProperty(key, value);
		});
		
		if(headers != null) {
			headers.entrySet().forEach(entry -> {
				List<String> values = entry.getValue();
				int len = values.size();
				for(int i = 0; i < len; i++)
					conn.addRequestProperty(entry.getKey(), values.get(i));
			});
		}
		
		if(HttpMethod.GET.getMethod() != method && body != null) {
			conn.setDoOutput(true);
			
			try(OutputStream os = conn.getOutputStream()){
				byte[] input = body.getBytes();
				os.write(input);
				os.flush();
			}
		}
		
		return conn;
	}
	
	protected String getUrl(String url) {
		
		if(checkProtocoliInUrl(url)) {
			return url;
		}
		
		StringBuffer sb = new StringBuffer();
		
		if(baseUrl != null && baseUrl != "") {
			sb.append(baseUrl);
		}
		
		if(url.length() > 0 && url.charAt(0) != '/') {
			sb.append('/');
		}
		
		sb.append(url);
		
		return sb.toString();
	}

	protected String getQuery(Map<String, Object> parameter) {
		if(parameter == null || parameter.size() == 0) return null;
		
		StringBuffer sb = new StringBuffer();
		
		parameter.entrySet().forEach(entry -> {
			int len = sb.length();
			try {
				if(len != 0) sb.append("&");
				sb.append(URLEncoder.encode(entry.getKey(), Charset.defaultCharset()));
				sb.append("=");
				sb.append(URLEncoder.encode(String.valueOf(entry.getValue()), Charset.defaultCharset()));
			} catch(Exception e) {
				sb.setLength(len);
			}
		});

		return sb.toString();
	}
	
	private boolean checkProtocoliInUrl(String url) {
		String up = url.toUpperCase();
		return up.startsWith(Protocol.HTTP.name()) || up.startsWith(Protocol.HTTPS.name()); 
	}
	
	public String toString(InputStream in) throws IOException {
		return toString(in, Charset.defaultCharset());
	}
	
	public String toString(InputStream in, Charset charset) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(in);
		byte[] bytes = bis.readAllBytes();
		
		return new String(bytes, charset);
	}
	
	public void setDefaultHeader(String key, String value) {
		defaultHeaders.put(key, value);
	}
	
	public void removeDefaultHeader(String key) {
		if(!defaultHeaders.containsKey(key)) return;
		defaultHeaders.remove(key);
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = (baseUrl.indexOf("://") == -1 || baseUrl.indexOf(":") == -1 ? protocol.getProtocol() : "") + baseUrl;
	}
	
	public String getProtocol() {
		return this.protocol.name();
	}
	
	public void setConnectionTimeout(int timeout) {
		this.connectionTimeout = timeout;
	}
	
	public void setReadTimeout(int timeout) {
		this.readTimeout = timeout;
	}

	public String getDefaultHeader(String key) {
		return defaultHeaders.get(key);
	}
}
