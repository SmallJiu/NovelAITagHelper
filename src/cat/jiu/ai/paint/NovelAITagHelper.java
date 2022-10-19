package cat.jiu.ai.paint;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringJoiner;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NovelAITagHelper extends JFrame {
	private static final long serialVersionUID = -9066780413956606786L;
	static final Logger log = new Logger();
	static NovelAITagHelper main;
	public static final JsonParser parser = new JsonParser();
	public static void main(String[] args) {
		try {
			I18n.init();
			initTags();
			main = new NovelAITagHelper();
			main.setVisible(true);
		}catch(Throwable e) {
			e.printStackTrace();
		}
	}

	static final HashSet<String> tags = new HashSet<>();
	static final HashMap<String, Object> tagsMap = new HashMap<>();
	// for translate
	static final HashMap<String, String> CnToEn = new HashMap<>();
	static final HashMap<String, String> EnToCn = new HashMap<>();
	static final HashMap<String, String> unClassifyCnToEn = new HashMap<>();
	static final HashMap<String, String> unClassifyEnToCn = new HashMap<>();
	
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
//	final HashMap<String, Integer> currentTags = new HashMap<>();
	
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
		tags.addAll(CnToEn.keySet());
		tags.addAll(unClassifyCnToEn.keySet());
//		currentTags.keySet();
	}
	
	void initFrameBounds(Container c) {
		currentCNTags.setBounds(3, 0, 648, 35);
		currentCNTags.setFont(font);
		c.add(currentCNTags);
		
		currentENTags.setBounds(3, 36, 648, 35);
		currentENTags.setFont(font);
		c.add(currentENTags);
		
		GenCNToENButton.setBounds(3, 70, 324, 25);
		GenCNToENButton.setFont(font);
		c.add(GenCNToENButton);
		
		GenENToCNButton.setBounds(3+324, 70, 324, 25);
		GenENToCNButton.setFont(font);
		c.add(GenENToCNButton);
		
		ClearButton.setBounds(3, 95, 648, 25);
		ClearButton.setFont(font);
		c.add(ClearButton);
		
		int y = 120;
		
		CacheButton.setBounds(3, y, 150, 40);
		CacheButton.setFont(font);
		c.add(CacheButton);
		
		SaveToCacheButton.setBounds(3, y+45, 150, 40);
		SaveToCacheButton.setFont(font);
		c.add(SaveToCacheButton);
		
		SearchButton.setBounds(3, y+45+45, 150, 40);
		SearchButton.setFont(font);
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
			currentSearchTags = new TagFrame(main, I18n.format("main.search"), null, tags);
			currentSearchTags.setVisible(true);
		});
	}
	
	public static void initTags() {
		for(Entry<String, JsonElement> tag : parser.parse(new InputStreamReader(NovelAITagHelper.class.getResourceAsStream("/cat/jiu/ai/NovelAITags.json"), StandardCharsets.UTF_8)).getAsJsonObject().entrySet()) {
			String tagName = tag.getKey();
			JsonElement e = tag.getValue();
			if(e.isJsonObject()) {
				if(tagName.contains("未分类")) {
					for(Entry<String, JsonElement> unI18nTag : e.getAsJsonObject().entrySet()) {
						unClassifyCnToEn.put(unI18nTag.getValue().getAsString(), unI18nTag.getKey());
						unClassifyEnToCn.put(unI18nTag.getKey(), unI18nTag.getValue().getAsString());
					}
					tagsMap.put(tagName, unClassifyCnToEn);
				}else {
					tagsMap.put(tagName, initSubTagMap(e.getAsJsonObject()));
				}
			}else if(e.isJsonArray()) {
				List<String> tagList = new ArrayList<>();
				for(int i = 0; i < e.getAsJsonArray().size(); i++) {
					tagList.add(e.getAsJsonArray().get(i).getAsString());
				}
				tagsMap.put(tagName, tagList);
			}
		}
		
		int entocn = unClassifyEnToCn.size();
		int cntoen = unClassifyCnToEn.size();
		log.info("未分类的EN转CN的Tag数: {} 个", entocn);
		log.info("未分类的CN转EN的Tag数: {} 个", cntoen);
		log.info("清除已分类的Tag中...");
		
		long time = System.currentTimeMillis();
		
		for(Entry<String, String> tag : EnToCn.entrySet()) {
			unClassifyEnToCn.remove(tag.getKey());
		}
		
		unClassifyCnToEn.clear();
		for(Entry<String, String> tag : unClassifyEnToCn.entrySet()) {
			unClassifyCnToEn.put(tag.getValue(), tag.getKey());
		}
		
		log.info("成功，耗时 {} 毫秒", System.currentTimeMillis()-time);
		log.info("剩余的未分类的EN转CN的Tag数: {} 个, 清除了 {} 个已分类Tag.", unClassifyEnToCn.size(), entocn-unClassifyEnToCn.size());
		log.info("剩余的未分类的CN转EN的Tag数: {} 个, 清除了 {} 个已分类Tag.", unClassifyCnToEn.size(), cntoen-unClassifyCnToEn.size());
	}
	
	static HashMap<String,Object> initSubTagMap(JsonObject obj) {
		HashMap<String,Object> tags = new HashMap<>();
		for(Entry<String, JsonElement> tag : obj.entrySet()) {
			String tagName = tag.getKey();
			JsonElement e = tag.getValue();
			if(e.isJsonObject()) {
				tags.put(tagName, initSubTagMap(e.getAsJsonObject()));
			}else if(e.isJsonArray()) {
				List<String> tagList = new ArrayList<>();
				for(int i = 0; i < e.getAsJsonArray().size(); i++) {
					tagList.add(e.getAsJsonArray().get(i).getAsString());
				}
				tags.put(tagName, tagList);
			}else if(e.isJsonPrimitive()) {
				tags.put(e.getAsString(), tagName);
				NovelAITagHelper.CnToEn.put(e.getAsString(), tagName);
				NovelAITagHelper.EnToCn.put(tagName, e.getAsString());
			}
		}
		return tags;
	}
}
