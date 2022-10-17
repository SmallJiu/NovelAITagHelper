package cat.jiu.ai.paint;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonGenerator {
	public static void main(String[] args) {
		JsonObject json = new JsonObject();
		{
			JsonObject tags = new JsonObject();
			for(Entry<String, JsonElement> tag : new JsonParser().parse(new InputStreamReader(NovelAITagHelper.class.getResourceAsStream("/cat/jiu/ai/tags.json"), StandardCharsets.UTF_8)).getAsJsonObject().entrySet()) {
				tags.addProperty(tag.getKey(), "未翻译-" + tag.getKey());
			}
			json.add("generate", tags);
		}
		JsonUtil.toJsonFile("./generate.json", json, true);
	}
}
