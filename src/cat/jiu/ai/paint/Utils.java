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
		if(NovelAITagHelper.CnToEn.containsKey(name)) {
			return NovelAITagHelper.CnToEn.get(name);
		}
		if(NovelAITagHelper.EnToCn.containsKey(name)) {
			return NovelAITagHelper.EnToCn.get(name);
		}
		if(NovelAITagHelper.unClassifyCnToEn.containsKey(name)) {
			return NovelAITagHelper.unClassifyCnToEn.get(name);
		}
		if(NovelAITagHelper.unClassifyEnToCn.containsKey(name)) {
			return NovelAITagHelper.unClassifyEnToCn.get(name);
		}
		return name;
	}
}
