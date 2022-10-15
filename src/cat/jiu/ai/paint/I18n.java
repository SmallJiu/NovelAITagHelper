package cat.jiu.ai.paint;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class I18n {
	private static final HashMap<String, String> langs = Utils.newHashMap();
	static void init() {
		try(InputStreamReader isr = new InputStreamReader(NovelAITagHelper.class.getResourceAsStream("/cat/jiu/ai/language.json"), StandardCharsets.UTF_8)) {
			JsonObject file = Cache.parser.parse(isr).getAsJsonObject();
			for(Entry<String, JsonElement> lang : file.entrySet()) {
				langs.put(lang.getKey(), lang.getValue().getAsString());
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String format(String key, Object... args) {
		if(langs.containsKey(key)) {
			return String.format(langs.get(key), args);
		}else {
			return key;
		}
	}
}
