package prj.riverly.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse extends HttpMessage {
	private boolean success;
	private int statusCode;
	private String statusMsg;

	protected HttpResponse(String protocol, float protocolVersion, String httpMethod, String requestUrl, boolean success, 
			int statusCode, String statusMsg, Map<String, List<String>> headers, String body) {
		
		super(protocol, protocolVersion, httpMethod, requestUrl, headers, body);
		
		this.success = success;
		this.statusCode = statusCode;
		this.statusMsg = statusMsg;
	}
	
	public boolean success() {
		return success;
	}
	
	public int status() {
		return statusCode;
	}
	
	public String msg() {
		return statusMsg;
	}
	
	protected static HttpResponseBuilder builder() {
		return new HttpResponseBuilder();
	}
	
	protected static class HttpResponseBuilder {
		private String protocol = "";
		private float protocolVersion = 0;
		private String httpMethod = "";
		private String requestUrl = "";
		private boolean success = false;
		private int statusCode = 200;
		private String statusMsg = "";
		private Map<String, List<String>> headers = new HashMap<String, List<String>>();
		private String body = "";
		
		protected HttpResponseBuilder() {}
		
		public HttpResponseBuilder protocol(String protocol) {
			this.protocol = protocol;
			return this;
		}
		
		public HttpResponseBuilder version(float version) {
			this.protocolVersion = version;
			return this;
		}
		
		public HttpResponseBuilder method(String method) {
			this.httpMethod = method;
			return this;
		}
		
		public HttpResponseBuilder url(String url) {
			this.requestUrl = url;
			return this;
		}
		
		public HttpResponseBuilder success(boolean success) {
			this.success = success;
			return this;
		}
		
		public HttpResponseBuilder status(int status) {
			this.statusCode = status;
			return this;
		}
		
		public HttpResponseBuilder msg(String msg) {
			this.statusMsg = msg;
			return this;
		}
		
		public HttpResponseBuilder headers(Map<String, List<String>> headers) {
			this.headers = headers;
			return this;
		}
		
		public HttpResponseBuilder body(String body) {
			this.body = body;
			return this;
		}
		
		public HttpResponse build() {
			return new HttpResponse(protocol, protocolVersion, httpMethod, requestUrl, success, statusCode, statusMsg, headers, body);
		}
	}
}
