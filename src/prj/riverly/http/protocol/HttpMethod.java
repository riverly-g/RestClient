package prj.riverly.http.protocol;

public enum HttpMethod {

	GET("GET"),
	POST("POST"),
	PUT("PUT"),
	PATCH("PATCH"),
	DELETE("DELETE"),
	HEAD("HEAD"),
	OPTIONS("OPTIONS");
	
	HttpMethod(String method){
		this.method = method;
	}
	
	private String method;
	
	public String getMethod() {
		return this.method;
	}
	
}
