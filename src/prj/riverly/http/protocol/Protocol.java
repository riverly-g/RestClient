package prj.riverly.http.protocol;

public enum Protocol {
	HTTP("http://"), HTTPS("https://");
	
	Protocol(String protocol){
		this.protocol = protocol;
	}
	
	private String protocol;
	
	public String getProtocol() {
		return this.protocol;
	}
}
