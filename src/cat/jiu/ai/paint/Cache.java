package cat.jiu.ai.paint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Cache {
	static final JsonParser parser = new JsonParser();
	static final File cacheFile = new File("./cache.json");
	final Map<String, Map<String, Integer>> caches = new HashMap<>();
	
	public void add(String cachename, String en, int level) {
		if(!caches.containsKey(cachename)) caches.put(cachename, Utils.newHashMap());
		caches.get(cachename).put(en, level);
	}
	
	public void remove(String cachename, String en) {
		if(caches.containsKey(cachename)) {
			caches.get(cachename).remove(en);
		}
	}
	
	public void remove(String cachename) {
		caches.remove(cachename);
	}
	
	public int getLevel(String cachename, String en) {
		if(!caches.containsKey(cachename)) return 0;
		return caches.get(cachename).get(en);
	}
	
	public void addLevel(String cachename, String en) {
		if(!caches.containsKey(cachename)) caches.put(cachename, Utils.newHashMap());
		caches.get(cachename).put(en, caches.get(cachename).get(en)+1);
	}
	
	public void subLevel(String cachename, String en) {
		if(caches.containsKey(cachename)) {
			caches.get(cachename).put(en, caches.get(cachename).get(en)-1);
			if(caches.get(cachename).get(en)<0) {
				caches.get(cachename).put(en, 0);
			}
		}
	}
	
	public String toTag(String cacheName) {
		StringJoiner tags = new StringJoiner(",");
		for(Entry<String, Integer> tag : caches.get(cacheName).entrySet()) {
			if(tag.getValue()>0) {
				StringBuilder t = new StringBuilder(tag.getKey());
				for(int i = 0; i < tag.getValue(); i++) {
					t.insert(0, '{');
					t.append('}');
				}
				tags.add(t);
			}else {
				tags.add(tag.getKey());
			}
		}
		return tags.toString();
	}
	
	public Set<Entry<String, Map<String, Integer>>> entrySet() {
		return this.caches.entrySet();
	}
	
	public boolean save() {
		try {
			JsonObject json = new JsonObject();
			for(Entry<String, Map<String, Integer>> caches : caches.entrySet()) {
				JsonObject cache = new JsonObject();
				for(Entry<String, Integer> tag : caches.getValue().entrySet()) {
					cache.addProperty(tag.getKey(), tag.getValue());
				}
				json.add(caches.getKey(), cache);
			}
			
			if(cacheFile.exists()) {
				cacheFile.delete();
			}
			cacheFile.createNewFile();
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(cacheFile, true), StandardCharsets.UTF_8);
			out.write(json.toString());
			out.close();
			return true;
		}catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void read() {
		caches.clear();
		if(cacheFile.exists()) {
			try(InputStream is = new FileInputStream(cacheFile)) {
				try(InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
					JsonObject file = parser.parse(isr).getAsJsonObject();
					for(Entry<String, JsonElement> cache : file.entrySet()) {
						caches.put(cache.getKey(), Utils.newHashMap());
						Map<String, Integer> tags = caches.get(cache.getKey());
						for(Entry<String, JsonElement> tag : cache.getValue().getAsJsonObject().entrySet()) {
							tags.put(tag.getKey(), tag.getValue().getAsInt());
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
