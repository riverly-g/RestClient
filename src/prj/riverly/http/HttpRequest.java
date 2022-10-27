package prj.riverly.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import prj.riverly.rest.RestClient;

public class HttpRequest extends HttpMessage {

	private HashMap<String, Object> parameter;
	
	protected HttpRequest(String protocol, float protocolVersion, String httpMethod, String requestUrl, 
			Map<String, List<String>> headers, String body) {
		
		super(protocol, protocolVersion, httpMethod, requestUrl, headers, body);
	}
	
	public HashMap<String, Object> parameter() {
		return parameter;
	}
	
	protected static HttpRequestBuilder builder(HttpClient httpClient) {
		return new HttpRequestBuilder(httpClient);
	}
	
	protected static class HttpRequestBuilder {
		private String protocol = "HTTP";
		private float protocolVersion = 1.1f;
		private String httpMethod = "GET";
		private String requestUrl = "";
		private Map<String, List<String>> headers = new HashMap<String, List<String>>();
		private String body = "";
		
		private HttpClient httpClient;
		
		protected HttpRequestBuilder(HttpClient httpClient) {
			this.httpClient = httpClient;
			this.protocol = httpClient.protocol();
		}
				
		public HttpRequestBuilder method(String method) {
			this.httpMethod = method;
			return this;
		}
		
		public HttpRequestBuilder url(String url) {
			this.requestUrl = url;
			return this;
		}
		
		public HttpRequestBuilder header(String key, String value) {
			if(this.headers != null) { 
				if(headers.containsKey(key)) {
					this.headers.get(key).add(value);
				} else {
					ArrayList<String> list = new ArrayList<String>();
					list.add(value);
					this.headers.put(key, list);
				}
			}
			return this;
		}
		
		public HttpRequestBuilder headers(Map<String, List<String>> headers) {
			if(headers != null) this.headers = headers;
			return this;
		}
		
		public HttpRequestBuilder data(String data) {
			this.body = data;
			return this;
		}
		
		public HttpRequest build() {
			return new HttpRequest(protocol, protocolVersion, httpMethod, requestUrl, headers, body);
		}
		
		public HttpResponse get() throws IOException {
			this.method("GET");
			return httpClient.call(build());
		}
		
		public HttpResponse post() throws IOException {
			this.method("POST");
			return httpClient.call(build());
		}
		
		public HttpResponse put() throws IOException {
			this.method("PUT");
			return httpClient.call(build());
		}
		
		public HttpResponse patch() throws IOException {
			this.method("PATCH");
			return httpClient.call(build());
		}
		
		public HttpResponse delete() throws IOException {
			this.method("DELETE");
			return httpClient.call(build());
		}
	}
}
