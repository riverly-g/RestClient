package prj.riverly.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import prj.riverly.http.HttpClient;
import prj.riverly.http.HttpResponse;
import prj.riverly.http.protocol.HttpMethod;
import prj.riverly.http.protocol.Protocol;
import prj.riverly.parser.JsonMapper;
import prj.riverly.rest.RestRequest.RestRequestBuilder;

public class RestClient extends HttpClient {

	private JsonMapper jsonMapper = JsonMapper.getInstance();
				
	public RestClient(){
		super();
		defaultHeaders.put("Content-Type", "application/json");
	}
	
	public RestClient(Protocol protocol){
		super(protocol);
		defaultHeaders.put("Content-Type", "application/json");
	}	
	
	@Override
	public RestRequestBuilder request() {
		return RestRequest.builder(this);
	}
	
	public RestResponse call(RestRequest restRequest) throws IOException{
		HttpResponse response = super.call(restRequest);
		
		return RestResponse.builder()
		.httpResponse(response)
		.data(jsonMapper.toMap(response.body()))
		.build();
	}
	
}
