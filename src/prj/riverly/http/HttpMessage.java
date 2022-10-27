package prj.riverly.http;

import java.util.List;
import java.util.Map;

public class HttpMessage {
	private String protocol;
	private float protocolVersion;
	private String httpMethod;
	private String requestUrl;
	private Map<String, List<String>> headers;
	private String body;

	protected HttpMessage(String protocol, float protocolVersion, String httpMethod, String requestUrl, Map<String, List<String>> headers, String body) {
		this.protocol = protocol;
		this.protocolVersion = protocolVersion;
		this.httpMethod = httpMethod;
		this.requestUrl = requestUrl;
		this.headers = headers;
		this.body = body;
	}
	
	public String protocol() {
		return protocol;
	}
	
	public float version() {
		return protocolVersion;
	}
	
	public String method() {
		return httpMethod;
	}
	
	public String url() {
		return requestUrl;
	}
	
	public Map<String, List<String>> headers() {
		return headers;
	}
	
	public Object header(String headerName) {
		if(headers.containsKey(headerName)) return headers.get(headerName);
		return null;
	}
	
	public String body() {
		return body;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("{");
		sb.append("protocol:");
		sb.append(protocol);
		sb.append(",version:");
		sb.append(protocolVersion);
		sb.append(",method:");
		sb.append(httpMethod);
		sb.append(",url:");
		sb.append(requestUrl);
		sb.append(",headers:");
		sb.append(headers);
		sb.append(",body:");
		sb.append(body);
		sb.append("}");
		
		return sb.toString();
	}

}
