package cat.jiu.ai.paint;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NovelAITagHelper extends JFrame {
	private static final long serialVersionUID = -9066780413956606786L;
	static NovelAITagHelper main;
	public static final JsonParser parser = new JsonParser();
	public static void main(String[] args) {
		try {
			I18n.init();
			initTags();
			main = new NovelAITagHelper();
			main.setVisible(true);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	static final Map<String, Integer> ALL_TAGS = new HashMap<>();
	static final Map<String, Object> tagsMap = new HashMap<>();
	static final Map<String, String> CnToEntag = new HashMap<>();
	static final Map<String, String> EnToCntag = new HashMap<>();
	static final Set<String> tags = new HashSet<>();
	static final Font font = new Font(null, 0,20);
	
	final JTextField currentCNTags = new JTextField(15);
	final JTextField currentENTags = new JTextField(15);
	final JButton GenENToCNButton = new JButton(I18n.format("main.entocn"));
	final JButton GenCNToENButton = new JButton(I18n.format("main.cntoen"));
	final JButton ClearButton = new JButton(I18n.format("main.clear"));
	final JButton CacheButton = new JButton(I18n.format("main.cache"));
	final JButton SaveToCacheButton = new JButton(I18n.format("main.savetocache"));
	final JButton SearchButton = new JButton(I18n.format("main.search"));
	final Cache cache = new Cache();
	
	ButtonsFrame currentTagsBtns = null;
	TagFrame currentSearchTags = null;
	CacheFrame currentCacheFrame = null;
	final HashMap<String, Integer> currentTags = new HashMap<>();
	
	private NovelAITagHelper() {
		super("NovelAITagHelper");
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		super.setBounds(0, 0, 670, 295);
		super.setLayout(null);
		super.setResizable(false);
		this.initButtonActionEvent();
		Container c = this.getContentPane();
		this.initFrameBounds(c);
		cache.read();
		tags.addAll(ALL_TAGS.keySet());
		tags.addAll(CnToEntag.keySet());
		currentTags.keySet();
	}
	
	void initFrameBounds(Container c) {
		Font mainFont = new Font(null,0,20);
		currentCNTags.setBounds(3, 0, 648, 35);
		currentCNTags.setFont(mainFont);
		c.add(currentCNTags);
		
		currentENTags.setBounds(3, 36, 648, 35);
		currentENTags.setFont(mainFont);
		c.add(currentENTags);
		
		GenCNToENButton.setBounds(3, 70, 324, 25);
		GenCNToENButton.setFont(mainFont);
		c.add(GenCNToENButton);
		
		GenENToCNButton.setBounds(3+324, 70, 324, 25);
		GenENToCNButton.setFont(mainFont);
		c.add(GenENToCNButton);
		
		ClearButton.setBounds(3, 95, 648, 25);
		ClearButton.setFont(mainFont);
		c.add(ClearButton);
		
		int y = 120;
		
		CacheButton.setBounds(3, y, 150, 40);
		CacheButton.setFont(mainFont);
		c.add(CacheButton);
		
		SaveToCacheButton.setBounds(3, y+45, 150, 40);
		SaveToCacheButton.setFont(mainFont);
		c.add(SaveToCacheButton);
		
		SearchButton.setBounds(3, y+45+45, 150, 40);
		SearchButton.setFont(mainFont);
		c.add(SearchButton);
		
		JPanel tagBtns = new JPanel();
		tagBtns.setLayout(new FlowLayout());
		tagBtns.setBounds(CacheButton.getX()+CacheButton.getWidth(), CacheButton.getY(), 490, 120);
		for(Entry<String, Object> tag : tagsMap.entrySet()) {
			tagBtns.add(new TagChoiceButton(tag.getKey(), tag.getValue()));
		}
		c.add(tagBtns);
	}
	
	void initButtonActionEvent() {
		GenCNToENButton.addActionListener(event -> {
			String[] cnTags = currentCNTags.getText().replace("{", "").replace("}", "").split(",");
			StringJoiner tags = new StringJoiner(",");
			for(int i = 0; i < cnTags.length; i++) {
				String str = Utils.getTag(cnTags[i]);
				if(str!=null) {
					tags.add(str);
				}
			}
			tags.add("");
			currentENTags.setText(tags.toString());
		});
		
		GenENToCNButton.addActionListener(event -> {
			String[] cnTags = currentENTags.getText().replace("{", "").replace("}", "").split(",");
			StringJoiner tags = new StringJoiner(",");
			for(int i = 0; i < cnTags.length; i++) {
				String str = Utils.getTag(cnTags[i]);
				if(str!=null) {
					tags.add(str);
				}
			}
			tags.add("");
			currentCNTags.setText(tags.toString());
		});
		
		ClearButton.addActionListener(event -> {
			currentCNTags.setText("");
			currentENTags.setText("");
		});
		
		SaveToCacheButton.addActionListener(event -> {
			cache.read();
			JDialog confirmDialog = new JDialog(this, I18n.format("main.cache.putname"), true);
			confirmDialog.setLayout(null);
			confirmDialog.setResizable(false);
			confirmDialog.setBounds(main.getX(), main.getY(), main.getWidth()/2, main.getHeight()/2);
			
			JTextField name = new JTextField(9);
			name.setFont(font);
			name.setBounds(5, 5, confirmDialog.getWidth()-25, 35);
			confirmDialog.add(name);
			confirmDialog.add(new SimpleButton(I18n.format("main.cache.confirm"), (confirmDialog.getWidth()/2)-80, 50, 128, 50, new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(!currentENTags.getText().isEmpty()) {
						if(!name.getText().isEmpty()) {
							String[] tags = currentENTags.getText().split(",");
							for(String tag : tags) {
								if(tag.contains("{")||tag.contains("}")) {
									int level = Math.max(tag.substring(0, tag.lastIndexOf("{")+1).length(), tag.substring(tag.indexOf("}"), tag.length()).length());
									cache.add(name.getText(), tag.replace("{", "").replace("}", ""), level);
								}else {
									cache.add(name.getText(), tag, 0);
								}
							}
							cache.save();
							confirmDialog.dispose();
						}else {
							JDialog s = new JDialog(confirmDialog, I18n.format("main.cache.emptyname"), true);
							s.setBounds(confirmDialog.getX(), confirmDialog.getY(), 256, 1);
							s.setVisible(true);
						}
					}else {
						JDialog s = new JDialog(confirmDialog, I18n.format("main.cache.emptytag"), true);
						s.setBounds(confirmDialog.getX(), confirmDialog.getY(), 256, 1);
						s.setVisible(true);
					}
				}
			}, 20));
			
			confirmDialog.setVisible(true);
		});
		
		CacheButton.addActionListener(event -> {
			cache.read();
			if(currentCacheFrame!=null)currentCacheFrame.dispose();
			currentCacheFrame = new CacheFrame(cache);
			currentCacheFrame.setVisible(true);
		});
		
		SearchButton.addActionListener(event -> {
			if(currentSearchTags!=null)currentSearchTags.dispose();
			currentSearchTags = new TagFrame(main, I18n.format("main.search"), null, ALL_TAGS.keySet());
			currentSearchTags.setVisible(true);
		});
	}
	
	public static void initTags() throws IOException {
		{
			JsonArray tags = parser.parse(new InputStreamReader(NovelAITagHelper.class.getResourceAsStream("/cat/jiu/ai/tags.json"), StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonArray("main");
			for(int i = 0; i < tags.size(); i++) {
				JsonArray tag = tags.get(i).getAsJsonArray();
				String name = tag.get(0).getAsString();
				int id = tag.get(1).getAsInt();
				ALL_TAGS.put(name, id);
			}
		}
		{
			JsonObject tagsFile = parser.parse(new InputStreamReader(NovelAITagHelper.class.getResourceAsStream("/cat/jiu/ai/NovelAITags.json"), StandardCharsets.UTF_8)).getAsJsonObject();
			for(Entry<String, JsonElement> tag : tagsFile.entrySet()) {
				String tagName = tag.getKey();
				JsonElement e = tag.getValue();
				if(e.isJsonObject()) {
					tagsMap.put(tagName, initSubTagMap(e.getAsJsonObject()));
				}else if(e.isJsonArray()) {
					List<String> tagList = new ArrayList<>();
					for(int i = 0; i < e.getAsJsonArray().size(); i++) {
						tagList.add(e.getAsJsonArray().get(i).getAsString());
					}
					tagsMap.put(tagName, tagList);
				}
			}
		}
	}
	
	static Map<String,Object> initSubTagMap(JsonObject obj) {
		Map<String,Object> tags = new HashMap<>();
		for(Entry<String, JsonElement> tag : obj.entrySet()) {
			String tagName = tag.getKey();
			JsonElement e = tag.getValue();
			if(e.isJsonObject()) {
				tags.put(tagName, initSubTagMap(e.getAsJsonObject()));
			}else if(e.isJsonPrimitive()) {
				tags.put(e.getAsString(), tagName);
				NovelAITagHelper.CnToEntag.put(e.getAsString(), tagName);
				NovelAITagHelper.EnToCntag.put(tagName, e.getAsString());
			}	
		}
		return tags;
	}
}
