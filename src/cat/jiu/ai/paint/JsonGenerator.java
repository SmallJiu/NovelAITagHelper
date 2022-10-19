package cat.jiu.ai.paint;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonGenerator {
	static Logger log = new Logger();
	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		JsonObject json = new JsonObject();
		{
			Set<Entry<String, JsonElement>> tagsSet = json.entrySet();
			for(Entry<String, JsonElement> tag : new JsonParser().parse(new InputStreamReader(NovelAITagHelper.class.getResourceAsStream("/cat/jiu/ai/tags.json"), StandardCharsets.UTF_8)).getAsJsonObject().entrySet()) {
				JsonArray l = tag.getValue().getAsJsonArray();
				for(int i = 0; i < l.size(); i++) {
					String t = l.get(i).getAsString();
					
					if(t.contains("(") && t.contains(")")) {
						{	
							String tagSp = t.substring(0, t.indexOf("("));
							String str = "";
							try {
								str = GT.translate("en", "zh-CN", tagSp);
							}catch(Exception e) {
								e.printStackTrace();
								str = "Translate " + tagSp +" error:" + e.getLocalizedMessage();
							}
							log.info("{}: {} = {}", tagsSet.size()+"/39777", tagSp, str);
							json.addProperty(tagSp, str);
						}
						
						String s = t.substring(t.indexOf('(')+1, t.lastIndexOf(")"));
						
						if(s.contains("/")) {
							String[] ss = s.split("/");
							for(String string : ss) {
								String str = "";
								try {
									str = GT.translate("en", "zh-CN", string);
								}catch(Exception e) {
									e.printStackTrace();
									str = "Translate " + string +" error:" + e.getLocalizedMessage();
								}
								log.info("{}: {} = {}", tagsSet.size()+"/39777", string, str);
								json.addProperty(string, str);
							}
						}else {
							String str = "";
							try {
								str = GT.translate("en", "zh-CN", s);
							}catch(Exception e) {
								e.printStackTrace();
								str = "Translate " + s +" error:" + e.getLocalizedMessage();
							}
							log.info("{}: {} = {}", tagsSet.size()+"/39777", s, str);
							json.addProperty(s, str);
						}
					}else {
						String str = "";
						try {
							str = GT.translate("en", "zh-CN", t);
						}catch(Exception e) {
							e.printStackTrace();
							str = "Translate " + t +" error:" + e.getLocalizedMessage();
						}
						log.info("{}: {} = {}", tagsSet.size()+"/39777", t, str);
						json.addProperty(t, str);
					}
				}
			}
		}
		JsonObject format = new JsonObject();
		for(Entry<String, JsonElement> tag : json.entrySet()) {
			format.addProperty(tag.getKey(), "未分类-"+tag.getValue().getAsString());
		}
		JsonUtil.toJsonFile("./generate.json", format, true);
		log.info("CurrentTime: {} s", ((float)System.currentTimeMillis()-(float)time)/1000.0F);
	}
}
