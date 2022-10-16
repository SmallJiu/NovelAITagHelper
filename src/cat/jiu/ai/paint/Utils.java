package cat.jiu.ai.paint;

import java.util.ArrayList;
import java.util.HashMap;

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
}
