package prj.riverly.parser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class JsonMapper {

	private ObjectParser objectParser = new ObjectParser();
	
	private JsonMapper(){}
	
	private static class JsonMapperHolder {
		private static final JsonMapper instance = new JsonMapper();
	}
	
	public static JsonMapper getInstance() {
		return JsonMapperHolder.instance;
	}
	
	@SuppressWarnings("serial")
	private HashSet<Class<?>> dataTypeSet = new HashSet<Class<?>>(){{
		add(byte.class);
		add(Byte.class);
		add(char.class);
		add(Character.class);
		add(boolean.class);
		add(Boolean.class);
		add(short.class);
		add(Short.class);
		add(int.class);
		add(Integer.class);
		add(long.class);
		add(Long.class);
		add(float.class);
		add(Float.class);
		add(double.class);
		add(Double.class);
	}};
	
	public String toJson(Object obj) {
		if(obj == null) return "null";
		Class<?> clazz = obj.getClass();
		StringBuilder sb = new StringBuilder();
		
		if(isDataType(clazz) || clazz.isEnum()) {
			return String.valueOf(obj);
		} else if(clazz == String.class) {
			sb.append("\"");
			sb.append(obj);
			sb.append("\"");
			return sb.toString();
		} else if(clazz.isArray() || obj instanceof Collection) {
			sb.append("[");
		
			int len = foreach(obj, v -> {
				sb.append(toJson(v));
				sb.append(",");
			});
			
			if(len > 0) sb.setLength(sb.length()-1);
			
			sb.append("]");
			return sb.toString();
		} else if(obj instanceof Map) {
			return toJson((Map<?,?>) obj);
		} else {
			sb.append("{");
			
			Field[] fields = clazz.getDeclaredFields();
			int len = fields.length;
			
			for(int i = 0; i < len; i++) {
				Field field = fields[i];
				try {
					field.setAccessible(true);
				} catch(InaccessibleObjectException e) {
					continue;
				}
				
				sb.append(field.getName());
				sb.append(":");
				
				try {
					sb.append(toJson(field.get(obj)));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					sb.append("null");
				}
				sb.append(",");
			}
			if(len > 0) sb.setLength(sb.length()-1);

			sb.append("}");
			return sb.toString();
		}
	}
	
	private boolean isDataType(Class<?> type) {
		return dataTypeSet.contains(type);
	}
	
	private int foreach(Object obj, Consumer<? super Object> consumer) {
		Class<?> clazz = obj.getClass();
		
		if(clazz.isArray()) {
			int len = Array.getLength(obj);
			for(int i = 0; i < len; i++) {
				consumer.accept(Array.get(obj, i));
			}
			return len;
		} else if(obj instanceof Collection) {
			Collection<?> collection = (Collection<?>) obj;
			Iterator<?> iter = collection.iterator();
			int count = 0;
			
			while(iter.hasNext()) {
				consumer.accept(iter.next());
				count++;
			}
			return count;
		}
		
		return 0;
	}
	
	private String toJson(Map<?, ?> map) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		
		map.entrySet().forEach(e -> {
			sb.append(toJson(e.getKey()));
			sb.append(":");
			sb.append(toJson(e.getValue()));
			sb.append(",");
		});
		
		if(map.size() > 0) sb.setLength(sb.length()-1);

		sb.append("}");
		return sb.toString();
	}
	
	public Map<String, Object> toMap(String json) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		if(json == null) return map;
		
		int len = json.length();
		
		if(json.charAt(0) != '{' || json.charAt(len-1) != '}')  return map;
		
		StringBuilder key = new StringBuilder();
		StringBuilder value = new StringBuilder();
		
		for(int i = 1; i < len-1; i++) {
			char ch = json.charAt(i);
			
			while(ch != ':') {
				key.append(ch);
				ch = json.charAt(++i);
			}
			
			int count = 0;
			i++;
			
			while(i < len-1 && ((ch = json.charAt(i)) != ',' || count > 0)) {
				value.append(ch);
				
				if(ch == '{' || ch == '[') count++;
				else if (ch == '}' || ch == ']') count--;
				i++;
			}
			
			if(value.charAt(0) == '{' && value.charAt(value.length()-1) == '}') {
				map.put(trim(key.toString()), toMap(value.toString()));
			} else if(value.charAt(0) == '[' && value.charAt(value.length()-1) == ']') {
				map.put(trim(key.toString()), toArray(value.toString()));
			} else {
				map.put(trim(key.toString()), objectParser.parseObject(value.toString()));
			}
			
			key.setLength(0);
			value.setLength(0);
		}
		
		return map;
	}
	
	private List<Object> toArray(String array) {
		ArrayList<Object> list = new ArrayList<Object>();
		
		int len = array.length();
		
		if(array.charAt(0) != '[' || array.charAt(len-1) != ']')  return list;
		
		StringBuilder value = new StringBuilder();
		
		for(int i = 1; i < len-1; i++) {
			char ch = array.charAt(i);
			
			int count = 0;
			
			while(i < len-1 && ((ch = array.charAt(i)) != ',' || count > 0)) {
				value.append(ch);
				
				if(ch == '{' || ch == '[') count++;
				else if (ch == '}' || ch == ']') count--;
				i++;
			}
			
			if(value.charAt(0) == '{' && value.charAt(value.length()-1) == '}') {
				list.add(toMap(value.toString()));
			} else if(value.charAt(0) == '[' && value.charAt(value.length()-1) == ']') {
				list.add(toArray(value.toString()));
			} else {
				list.add(objectParser.parseObject(value.toString()));
			}
			
			value.setLength(0);
		}
		
		return list;
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
