package prj.riverly.http;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import prj.riverly.common.HttpUrlClient;
import prj.riverly.http.HttpRequest.HttpRequestBuilder;
import prj.riverly.http.HttpResponse.HttpResponseBuilder;
import prj.riverly.http.protocol.Protocol;
import prj.riverly.thread.RequestThreadPool;

public class HttpClient extends HttpUrlClient {

	public HttpClient(){}
	
	public HttpClient(Protocol protocol){
		this.protocol = protocol;
	}
	
	public HttpRequestBuilder request() {
		return HttpRequest.builder(this);
	}
	
	public Future<HttpResponse> asyncCall(HttpRequest httpRequest) {
		return RequestThreadPool.getInstance().task(() -> {
			return call(httpRequest);
		});
	}
	
	public HttpResponse call(HttpRequest httpRequest) throws IOException {
		String url = httpRequest.url();
		Map<String, Object> parameter = httpRequest.parameter();
		String method = httpRequest.method();
		Map<String, List<String>> headers = httpRequest.headers();
		String data = httpRequest.body();
		
		HttpURLConnection conn = super.call(url, method, parameter, headers, data);
		
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
	
}
