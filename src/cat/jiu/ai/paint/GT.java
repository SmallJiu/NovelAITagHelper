package cat.jiu.ai.paint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.JsonArray;

public class GT {
	public static String translate(String langFrom, String langTo, String word) throws Exception {
		String url = "https://translate.googleapis.com/translate_a/single?" +
				"client=gtx&" +
				"sl=" + langFrom +
				"&tl=" + langTo +
				"&dt=t&q=" + URLEncoder.encode(word, "UTF-8");
		
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return parseResult(response.toString());
	}

	private static String parseResult(String inputJson) throws Exception {
		JsonArray jsonArray = Cache.parser.parse(inputJson).getAsJsonArray();
		JsonArray jsonArray2 = jsonArray.get(0).getAsJsonArray();

		String result = "";
		for(int i = 0; i < jsonArray2.size(); i++) {
			result += jsonArray2.get(i).getAsJsonArray().get(0).toString();
		}
		return result;
	}
}
