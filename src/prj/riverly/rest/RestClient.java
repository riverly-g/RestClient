package prj.riverly.rest;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import prj.riverly.common.HttpUrlClient;
import prj.riverly.http.protocol.Protocol;
import prj.riverly.parser.JsonMapper;
import prj.riverly.rest.RestRequest.RestRequestBuilder;
import prj.riverly.rest.RestResponse.RestResponseBuilder;
import prj.riverly.thread.RequestThreadPool;

public class RestClient extends HttpUrlClient {

	private JsonMapper jsonMapper = JsonMapper.getInstance();
				
	public RestClient(){
		defaultHeaders.put("Content-Type", "application/json");
	}
	
	public RestClient(Protocol protocol){
		defaultHeaders.put("Content-Type", "application/json");
	}	
	
	public RestRequestBuilder request() {
		return RestRequest.builder(this);
	}
	
	public Future<RestResponse> asyncCall(RestRequest restRequest) {
		return RequestThreadPool.getInstance().task(() -> {
			return call(restRequest);
		});
	}
	
	public RestResponse call(RestRequest restRequest) throws IOException{
		String url = restRequest.url();
		String parameter = getQuery(restRequest.parameter());
		String method = restRequest.method();
		Map<String, List<String>> headers = restRequest.headers();
		String data = restRequest.body();
		
		HttpURLConnection conn = super.call(url, method, parameter, headers, data);
		
		RestResponseBuilder restResponseBuilder = RestResponse.builder()
				.protocol(this.protocol.name())
				.version(restRequest.version())
				.url(url.toString())
				.method(method);
		
		try {
			int status = conn.getResponseCode();
			boolean success = status < HttpURLConnection.HTTP_BAD_REQUEST;
			
			String response = toString(conn.getInputStream());
			String msg = success ? conn.getResponseMessage() : toString(conn.getErrorStream());
			
			return restResponseBuilder.success(success)
					.status(status)
					.msg(msg)
					.headers(conn.getHeaderFields())
					.data(success ? jsonMapper.toMap(response) : null)
					.build();
					
		} catch(ConnectException e) {
			return restResponseBuilder.success(false).build();
		}
	}
	
}
