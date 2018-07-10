package cc.touchuan.jbus.common.helper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonHelper {

	public static String map2json(Map<String, Object> map) {
		return new Gson().toJson(map);
	}

	public static String list2json(List<Object> list) {

		return new Gson().toJson(list);
	}

	public static Map<String, Object> json2map(String json) {

        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> map = gson.fromJson(json, type);
        
        return map;
	}

	public static List<Object> json2list(String json) {

        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

        Type type = new TypeToken<List<Object>>() {}.getType();
        List<Object> list = gson.fromJson(json, type);
        
        return list;
	}
	
}
