package prj.riverly.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import prj.riverly.http.HttpResponse;
import prj.riverly.parser.JsonMapper;

public class RestResponse extends HttpResponse {

	private Map<String, Object> jsonData;
	
	protected RestResponse(String protocol, float protocolVersion, String httpMethod, String requestUrl, boolean success, 
			int statusCode, String statusMsg, Map<String, List<String>> headers, Map<String, Object> jsonData) {
		super(protocol, protocolVersion, httpMethod, requestUrl, success, statusCode, statusMsg, headers, JsonMapper.getInstance().toJson(jsonData));
		this.jsonData = jsonData;
	}
	
	protected static RestResponseBuilder builder() {
		return new RestResponseBuilder();
	}
	
	public Map<String, Object> data() {
		return jsonData;
	}
	
	protected static class RestResponseBuilder extends HttpResponseBuilder {
		private String protocol = "HTTP";
		private float protocolVersion = 1.1f;
		private String httpMethod = "";
		private String requestUrl = "";
		private boolean success = false;
		private int statusCode = 200;
		private String statusMsg = "";
		private Map<String, List<String>> headers = new HashMap<String, List<String>>();
		private Map<String, Object> data = new HashMap<String, Object>();
		
		private RestResponseBuilder() { super(); }
		
		public RestResponseBuilder httpResponse(HttpResponse httpResponse) {
			this.protocol = httpResponse.protocol();
			this.protocolVersion = httpResponse.version();
			this.httpMethod = httpResponse.method();
			this.requestUrl = httpResponse.url();
			this.success = httpResponse.success();
			this.statusCode = httpResponse.status();
			this.statusMsg = httpResponse.msg();
			this.headers = httpResponse.headers();
			return this;
		}
		
		public RestResponseBuilder protocol(String protocol) {
			this.protocol = protocol;
			return this;
		}
		
		public RestResponseBuilder version(float version) {
			this.protocolVersion = version;
			return this;
		}
		
		public RestResponseBuilder method(String method) {
			this.httpMethod = method;
			return this;
		}
		
		public RestResponseBuilder url(String url) {
			this.requestUrl = url;
			return this;
		}
		
		public RestResponseBuilder success(boolean success) {
			this.success = success;
			return this;
		}
		
		public RestResponseBuilder status(int status) {
			this.statusCode = status;
			return this;
		}
		
		public RestResponseBuilder msg(String msg) {
			this.statusMsg = msg;
			return this;
		}
		
		public RestResponseBuilder headers(Map<String, List<String>> headers) {
			this.headers = headers;
			return this;
		}
		
		public RestResponseBuilder data(Map<String, Object> data) {
			this.data = data;
			return this;
		}
		
		public RestResponseBuilder body(String body) {
			this.data = JsonMapper.getInstance().toMap(body);
			return this;
		}
		
		public RestResponse build() {
			return new RestResponse(protocol, protocolVersion, httpMethod, requestUrl, success, statusCode, statusMsg, headers, data);
		}
	}
}
