package prj.riverly.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import prj.riverly.http.HttpRequest.HttpRequestBuilder;
import prj.riverly.http.HttpResponse.HttpResponseBuilder;
import prj.riverly.http.protocol.HttpMethod;
import prj.riverly.http.protocol.Protocol;

public class HttpClient {

	protected Protocol protocol = Protocol.HTTP;
	protected String baseUrl = "";
	protected int connectionTimeout = 3000;
	protected int readTimeout = 5000;
	
	protected Map<String, String> defaultHeaders = new HashMap<String, String>();
	
	public HttpClient(){}
	
	public HttpClient(Protocol protocol){
		this.protocol = protocol;
	}
	
	public HttpRequestBuilder request() {
		return HttpRequest.builder(this);
	}
	
	public HttpResponse call(HttpRequest httpRequest) throws IOException{
		String uri = httpRequest.url();
		String parameter = getQuery(httpRequest.parameter());
		String method = httpRequest.method();
		Map<String, List<String>> headers = httpRequest.headers();
		String data = httpRequest.body();
		
		if(parameter != null) {
			if (uri.contains("?")) {
				uri += "&" + parameter;
			} else {
				uri += "?" + parameter;
			}
		}
		
		URL url = new URL(getUrl(uri));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
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
		
		conn.setDoOutput(true);
		
		if(HttpMethod.GET.getMethod() != method && data != null) {
			try(OutputStream os = conn.getOutputStream()){
				byte[] input = data.getBytes();
				os.write(input);
				os.flush();
			}
		}
		
		HttpResponseBuilder httpResponseBuilder = HttpResponse.builder()
				.protocol(this.protocol.name())
				.version(httpRequest.version())
				.url(url.toString())
				.method(method);
		
		try {
			int status = conn.getResponseCode();
			boolean success = status < HttpURLConnection.HTTP_BAD_REQUEST;
			
			String response = toString(conn.getInputStream());
			String msg = success ? conn.getResponseMessage() : toString(conn.getErrorStream());
			
			return httpResponseBuilder.success(success)
					.status(status)
					.msg(msg)
					.headers(conn.getHeaderFields())
					.body(success ? response : null)
					.build();
					
		} catch(ConnectException e) {
			return httpResponseBuilder.success(false).build();
		}
		
	}
	
	private String getUrl(String uri) {
		
		StringBuffer sb = new StringBuffer();
		
		if(baseUrl != null && baseUrl != "") {
			sb.append(baseUrl);
		}
		
		if(uri.length() > 0 && uri.charAt(0) != '/') {
			sb.append('/');
		}
		
		sb.append(uri);
		
		return sb.toString();
	}

	private String getQuery(Map<String, Object> parameter) {
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
	
	private String toString(InputStream in) throws IOException {
		return toString(in, Charset.defaultCharset());
	}
	
	private String toString(InputStream in, Charset charset) throws IOException {
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
	
	public void baseUrl(String baseUrl) {
		this.baseUrl = (baseUrl.indexOf("://") == -1 || baseUrl.indexOf(":") == -1 ? protocol.getProtocol() : "") + baseUrl;
	}
	
	public String protocol() {
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
