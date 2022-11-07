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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cat.jiu.ai.paint.Cache.Tags.Tag;

public class Cache {
	static final JsonParser parser = new JsonParser();
	static final File cacheFile = new File("./cache.json");
	final LinkedHashMap<String, Tags> caches = new LinkedHashMap<>();
	
	public void add(String cachename, String en, int level) {
		if(en!=null && !en.isEmpty()) {
			if(!caches.containsKey(cachename)) caches.put(cachename, new Tags(cachename));
			caches.get(cachename).addTag(new Tag(en,level));
			this.save();
			this.updataFrame();
			
		}
	}
	
	public void remove(String cachename, String en) {
		if(caches.containsKey(cachename)) {
			caches.get(cachename).removeTag(en);
			this.save();
			this.updataFrame();
		}
	}
	
	public void remove(String cachename) {
		caches.remove(cachename);
		this.save();
		this.updataFrame();
	}
	
	public int getLevel(String cachename, String en) {
		if(!caches.containsKey(cachename)) return 0;
		Tag tag = caches.get(cachename).getTag(en);
		if(tag==null) return 0;
		return tag.getLevel();
	}
	
	public void addLevel(String cachename, String en) {
		if(!caches.containsKey(cachename)) caches.put(cachename, new Tags(cachename));
		Tag tag = caches.get(cachename).getTag(en);
		if(tag!=null) {
			tag.addLevel(1);
		}
		this.save();
	}
	
	public void subLevel(String cachename, String en) {
		if(!caches.containsKey(cachename)) caches.put(cachename, new Tags(cachename));
		Tag tag = caches.get(cachename).getTag(en);
		if(tag!=null) {
			tag.subLevel(1);
		}
		this.save();
	}
	
	public void updataFrame() {
		if(NovelAITagHelper.main.currentCacheFrame!=null) NovelAITagHelper.main.currentCacheFrame.updataUI();
	}
	
	public String toTag(String cacheName, boolean isNaifu) {
		Tags tags = caches.get(cacheName);
		if(tags==null) return "no found";
		return tags.toString(isNaifu)+",";
	}
	
	public Set<Entry<String, Tags>> cacheSet() {
		return this.caches.entrySet();
	}
	
	public Set<String> cacheNameSet() {
		return this.caches.keySet();
	}
	
	public boolean save() {
		try {
			JsonObject json = new JsonObject();
			for(Entry<String, Tags> caches : caches.entrySet()) {
				Tags tagsCache = caches.getValue();
				JsonObject cache = new JsonObject();
				
				if(tagsCache.parent!=null) {
					cache.addProperty("parent", tagsCache.parent.name);
				}
				JsonObject tags = new JsonObject();
				for(Entry<String, Tag> tag : tagsCache.getTags()) {
					JsonObject tagObj = new JsonObject();
					
					tagObj.addProperty("name", Utils.getTag(tag.getValue().name));
					tagObj.addProperty("level", tag.getValue().getLevel());
					
					tags.add(tag.getKey(), tagObj);
				}
				cache.add("tags", tags);
				
				json.add(caches.getKey(), cache);
			}
			
			if(cacheFile.exists()) {
				cacheFile.delete();
			}
			cacheFile.createNewFile();
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(cacheFile, true), StandardCharsets.UTF_8);
			out.write(JsonUtil.formatJson(json.toString()));
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
			Map<String, String> parents = new HashMap<>();
			
			try(InputStream is = new FileInputStream(cacheFile)) {
				try(InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
					JsonObject file = parser.parse(isr).getAsJsonObject();
					for(Entry<String, JsonElement> cache : file.entrySet()) {
						Tags tags = new Tags(cache.getKey());
						JsonObject tagsObj = cache.getValue().getAsJsonObject();
						
						if(tagsObj.has("parent")) {
							parents.put(cache.getKey(), tagsObj.get("parent").getAsString());
						}
						if(tagsObj.has("tags")){
							for(Entry<String, JsonElement> tag : tagsObj.getAsJsonObject("tags").entrySet()) {
								String tagName = tag.getKey();
								int level = 0;
								JsonElement tagElement = tag.getValue();
								if(tagElement.isJsonObject()) {
									level = tag.getValue().getAsJsonObject().get("level").getAsInt();
								}else if(tagElement.isJsonPrimitive()) {
									level = tagElement.getAsInt();
								}
								tags.addTag(new Tag(tagName, level));
							}
						}else {
							for(Entry<String, JsonElement> tag : tagsObj.entrySet()) {
								String tagName = tag.getKey();
								int level = 0;
								JsonElement tagElement = tag.getValue();
								if(tagElement.isJsonObject()) {
									level = tag.getValue().getAsJsonObject().get("level").getAsInt();
								}else if(tagElement.isJsonPrimitive()) {
									level = tagElement.getAsInt();
								}
								tags.addTag(new Tag(tagName, level));
							}
						}
						
						caches.put(cache.getKey(), tags);
					}
				}
			}catch (IOException e) {
				e.printStackTrace();
			}finally {
				for(Entry<String, String> tags : parents.entrySet()) {
					if(caches.containsKey(tags.getKey()) && caches.containsKey(tags.getValue())) {
						caches.get(tags.getKey()).setParent(caches.get(tags.getValue()));
					}
				}
			}
		}
	}
	
	public static class Tags {
		public final String name;
		private Tags parent;
		private final LinkedHashMap<String, Tag> tags = Utils.newLinkedHashMap();
		public Tags(String name) {
			this.name = name;
		}
		public void setParent(Tags parent) {
			this.parent = parent;
		}
		public Tags getParent() {
			return parent;
		}
		public void addTag(Tag tag) {
			tags.put(tag.name, tag);
		}
		public Tag getTag(String name) {
			return tags.get(name);
		}
		public void removeTag(String name) {
			tags.remove(name);
		}
		public Set<Entry<String, Tag>> getTags() {
			return tags.entrySet();
		}
		public String toString(boolean isNaifu) {
			StringJoiner str = new StringJoiner(",");
			if(this.parent!=null) {
				str.add(this.parent.toString(isNaifu));
			}
			for(Entry<String, Tag> tag : tags.entrySet()) {
				str.add(tag.getValue().toString(isNaifu));
			}
			return str.toString();
		}
		
		public static class Tag {
			public final String name;
			private int level;
			public Tag(String name, int level) {
				this.name = name;
				this.level = level;
			}
			public void addLevel(int level) {
				this.level+=level;
			}
			public void subLevel(int level) {
				this.level-=level;
			}
			public int getLevel() {
				return level;
			}
			public String toString(boolean isNaifu) {
				if(isNaifu) {
					if(this.level > 0) {
						StringBuilder t = new StringBuilder(this.name);
						for(int i = 0; i < this.level; i++) {
							t.insert(0, '{');
							t.append('}');
						}
						return t.toString();
					}else if(this.level < 0) {
						StringBuilder t = new StringBuilder(this.name);
						for(int i = 0; i < -this.level; i++) {
							t.insert(0, '[');
							t.append(']');
						}
						return t.toString();
					}
				}else {
					if(this.level > 0) {
						StringBuilder t = new StringBuilder(this.name);
						t.insert(0, '(')
						 .append(':')
						 .append(Math.min(3.0F, 1 + (0.1F*this.level)))
						 .append(')');
						return t.toString();
					}else if(this.level < 0) {
						StringBuilder t = new StringBuilder(this.name);
						t.insert(0, '[')
						 .append(':')
						 .append(Math.max(-3.0F, 1.0F - (0.1F*this.level)))
						 .append(']');
						return t.toString();
					}
				}
				return this.name;
			}
		}
	}
}
