package prj.riverly.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import prj.riverly.http.HttpRequest;
import prj.riverly.parser.JsonMapper;

public class RestRequest extends HttpRequest {

	private Map<String, Object> jsonData;
	
	protected RestRequest(String protocol, float protocolVersion, String httpMethod, String requestUrl, 
			Map<String, List<String>> headers, Map<String, Object> jsonData) {
		super(protocol, protocolVersion, httpMethod, requestUrl, headers, JsonMapper.getInstance().toJson(jsonData));
		this.jsonData = jsonData;
	}
	
	protected static RestRequestBuilder builder(RestClient restClient) {
		return new RestRequestBuilder(restClient);
	}
	
	public Map<String, Object> data() {
		return jsonData;
	}
	
	public static class RestRequestBuilder {
		private String protocol = "HTTP";
		private float protocolVersion = 1.1f;
		private String httpMethod = "GET";
		private String requestUrl = "";
		private Map<String, List<String>> headers = new HashMap<String, List<String>>();
		private Map<String, Object> jsonData = new HashMap<String, Object>();
		
		private RestClient restClient;
		
		private RestRequestBuilder(RestClient restClient) {
			this.restClient = restClient;
			this.protocol = restClient.getProtocol();
		}
				
		public RestRequestBuilder method(String method) {
			this.httpMethod = method;
			return this;
		}
		
		public RestRequestBuilder url(String url) {
			this.requestUrl = url;
			return this;
		}
		
		public RestRequestBuilder header(String key, String value) {
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
		
		public RestRequestBuilder headers(Map<String, List<String>> headers) {
			if(this.headers != null) this.headers = headers;
			return this;
		}
		
		public RestRequestBuilder data(String key, Object value) {
			if(this.jsonData != null) this.jsonData.put(key, value);
			return this;
		}
		
		public RestRequestBuilder data(Map<String, Object> data) {
			this.jsonData = data;
			return this;
		}
		
		public RestRequest build() {
			return new RestRequest(protocol, protocolVersion, httpMethod, requestUrl, headers, jsonData);
		}
		
		public RestResponse get() throws IOException {
			this.method("GET");
			return restClient.call(build());
		}
		
		public RestResponse post() throws IOException {
			this.method("POST");
			return restClient.call(build());
		}
		
		public RestResponse put() throws IOException {
			this.method("PUT");
			return restClient.call(build());
		}
		
		public RestResponse patch() throws IOException {
			this.method("PATCH");
			return restClient.call(build());
		}
		
		public RestResponse delete() throws IOException {
			this.method("DELETE");
			return restClient.call(build());
		}
		
	}
}
