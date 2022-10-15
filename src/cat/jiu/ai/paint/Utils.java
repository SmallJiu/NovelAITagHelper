package cat.jiu.ai.paint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Utils {
	public static <K, V> HashMap<K, V> newHashMap(){
		return new HashMap<>();
	}
	public static <E> ArrayList<E> newArrayList(){
		return new ArrayList<>();
	}
	
	public static String getTag(String name) {
		if(NovelAITagHelper.CnToEntag.containsKey(name)) {
			return NovelAITagHelper.CnToEntag.get(name);
		}
		if(NovelAITagHelper.EnToCntag.containsKey(name)) {
			return NovelAITagHelper.EnToCntag.get(name);
		}
		return name;
	}
	
	public static Map<String, String> getTagsMap(String tagname) {
		Map<String, String> tags = new HashMap<>();
		
		try(InputStream is = NovelAITagHelper.class.getResourceAsStream("/cat/jiu/ai/paint/tagsMap/" + tagname + ".txt")) {
			try(InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
				try(BufferedReader br = new BufferedReader(isr)) {
					String s = "";
					while ((s = br.readLine()) != null) {
						if(!s.isEmpty() && !s.equals("") && !s.startsWith("//")) {
							String[] lang = s.split("=");
							tags.put(lang[1], lang[0]);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tags;
	}
	
	public static Set<String> getTagsList(String tagname) {
		Set<String> tags = new HashSet<>();
		
		try(InputStream is = NovelAITagHelper.class.getResourceAsStream("/cat/jiu/ai/paint/tagsMap/" + tagname + ".txt")) {
			try(InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
				try(BufferedReader br = new BufferedReader(isr)) {
					String s = "";
					while ((s = br.readLine()) != null) {
						if(!s.isEmpty() && !s.equals("") && !s.startsWith("//")) {
							tags.add(s);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return tags;
	}
}
