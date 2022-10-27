package prj.riverly.parser;

public class ObjectParser {

	public Object parseObject(String str) {
		String s = trim(str);
		int len = s.length();
		
		if(len == 0) return s;
		if("true".equals(s) || "false".equals(s)) return Boolean.parseBoolean(s);
		
		int digit = 0;
		int chars = 0;
		int dotc = 0;
		int doti = 0;
		
		for(int i = 0; i < len; i++) {
			char ch = s.charAt(i);
			
			if(ch >= '0' && ch <= '9') digit++;
			else if(ch == '.') {dotc++; doti = i;}
			else chars++;
		}
		
		if(chars == 0 && digit > 0) {
			if(dotc == 0) return Integer.parseInt(s);
			else if(dotc == 1 && doti > 0 && doti < len-2) return Double.parseDouble(s); 
		}
		
		return s;
	}
	
	private String trim(String str) {
		String trim = str.trim();
		
		int start = 0;
		int end = trim.length();
		
		if(trim.charAt(0) == '"') start = 1;
		if(trim.charAt(end-1) == '"') end = end-1;
		
		if(start != 0 || end != trim.length()) trim = trim.substring(start, end);
		
		return trim;
	}
}
