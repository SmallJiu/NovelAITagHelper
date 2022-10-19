package cat.jiu.ai.paint;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.google.gson.JsonObject;

public class TagFrame extends JFrame {
	private static final long serialVersionUID = -120120534058875171L;
	final ArrayList<String> elements;
	final ArrayList<String> searchSuccessReult = new ArrayList<>();
	final ArrayList<String> searchFailReult = new ArrayList<>();
	TagFrame currentSuccessSearch;
	TagFrame currentFailSearch;
	
	public TagFrame(JFrame main, String title, JTextField currentCNTags, Collection<String> tags) {
		super(title);
		this.elements = new ArrayList<>(tags);
		super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		super.setLayout(new BorderLayout());
		super.setBounds(main.getX()+main.getWidth(), main.getY(), 256, main.getHeight());
		
		JList<String> taglist = new JList<>();
		JScrollPane js = new JScrollPane(taglist);
		DefaultListModel<String> normalModel = new DefaultListModel<>();
		for(String tag : this.elements) {
			normalModel.addElement(tag);
		}
		taglist.setFont(NovelAITagHelper.font);
		taglist.setModel(normalModel);
		
		taglist.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() >= 2) {
					addResultToTag(currentCNTags, taglist.getSelectedValue());
				}
			}
		});
		
		js.setBounds(0, 0, 100, 100);
		this.getContentPane().add(js, BorderLayout.CENTER);
		
		JPanel north = new JPanel();
		north.setLayout(new FlowLayout(FlowLayout.LEADING,1,1));
		
		north.add(new SimpleButton(I18n.format("main.cache.add"), 3,5, new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(!taglist.isSelectionEmpty()) {
					JDialog choiceCache = new JDialog(TagFrame.this, I18n.format("main.cache.choice_cache"), true);
					choiceCache.setBounds(TagFrame.this.getX(), TagFrame.this.getY(), 256, TagFrame.this.getHeight());
					choiceCache.setLayout(new BorderLayout());
					
					JList<String> cachelist = new JList<>();
					JScrollPane js = new JScrollPane(cachelist);
					DefaultListModel<String> normalModel = new DefaultListModel<>();
					for(String name : NovelAITagHelper.main.cache.cacheNameSet()) {
						normalModel.addElement(name);
					}
					cachelist.setFont(NovelAITagHelper.font);
					cachelist.setModel(normalModel);
					
					cachelist.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							if(e.getClickCount() >= 2) {
								NovelAITagHelper.main.cache.add(cachelist.getSelectedValue(), Utils.getTag(taglist.getSelectedValue()), 0);
								choiceCache.dispose();
							}
						}
					});
					
					js.setBounds(0, 0, 100, 100);
					choiceCache.add(js, BorderLayout.CENTER);
					choiceCache.setVisible(true);
				}
			}
		},15));
		
		north.add(new SimpleButton(I18n.format("main.search.export"), 3,5, new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JsonObject export = new JsonObject();
				for(String tag : elements) {
					export.addProperty(Utils.getTag(tag), tag);
				}
				JsonUtil.toJsonFile("./export -" + title.substring(title.indexOf("- ")+1) + ".json", export, true);
			}
		},15));
		this.getContentPane().add(north, BorderLayout.NORTH);
		
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout(FlowLayout.LEADING,1,1));
		
		JTextField searchText = new JTextField(10);
		searchText.setFont(NovelAITagHelper.font);
		south.add(searchText);
		
		south.add(new SimpleButton(I18n.format("main.search"), new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(!searchText.getText().isEmpty()) {
					TagFrame.this.searchSuccessReult.clear();
					TagFrame.this.searchFailReult.clear();
					
					for(String tag : TagFrame.this.elements) {
						if(tag.contains(searchText.getText())) {
							if(!TagFrame.this.searchSuccessReult.contains(tag)) {
								TagFrame.this.searchSuccessReult.add(tag);
							}
						}else {
							if(!TagFrame.this.searchFailReult.contains(tag)) {
								TagFrame.this.searchFailReult.add(tag);
							}
						}
					}
					if(TagFrame.this.currentSuccessSearch!=null) TagFrame.this.currentSuccessSearch.dispose();
					TagFrame.this.currentSuccessSearch = new TagFrame(TagFrame.this, I18n.format("main.search.result.fuccess") + searchText.getText(), currentCNTags, TagFrame.this.searchSuccessReult);
					TagFrame.this.currentSuccessSearch.setVisible(true);
					
					if(TagFrame.this.currentFailSearch!=null) TagFrame.this.currentFailSearch.dispose();
					TagFrame.this.currentFailSearch = new TagFrame(TagFrame.this.currentSuccessSearch, I18n.format("main.search.result.fail") + searchText.getText(), currentCNTags, TagFrame.this.searchFailReult);
					TagFrame.this.currentFailSearch.setVisible(true);
				}
			}
		}, 15));
		this.getContentPane().add(south, BorderLayout.SOUTH);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				try{currentSuccessSearch.dispose();}catch (Exception exc) {}
				currentSuccessSearch = null;
				
				try{currentFailSearch.dispose();}catch (Exception exc) {}
				currentFailSearch = null;
				System.gc();
				System.gc();
				System.gc();
			}
		});
	}
	
	protected void addResultToTag(JTextField currentCNTags, String result) {
		if(currentCNTags!=null) {
			currentCNTags.setText(currentCNTags.getText()+result+",");
		}else {
			String trs = Utils.getTag(result);
			if(NovelAITagHelper.EnToCn.containsKey(trs)) {
				NovelAITagHelper.main.currentCNTags.setText(NovelAITagHelper.main.currentCNTags.getText()+result+",");
			}else {
				NovelAITagHelper.main.currentENTags.setText(NovelAITagHelper.main.currentENTags.getText()+result+",");
			}
		}
	}
}
