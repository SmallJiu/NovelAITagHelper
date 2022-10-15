package cat.jiu.ai.paint;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;

@SuppressWarnings("unchecked")
public class TagChoiceButton extends SimpleButton {
	private static final long serialVersionUID = -4345396230559478150L;
	public TagChoiceButton(String title, Object tag) {
		super(title, new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(tag instanceof Collection) {
					new TagFrame(NovelAITagHelper.main, title, NovelAITagHelper.main.currentCNTags, (Collection<String>)tag).setVisible(true);
				}else if(tag instanceof Map) {
					Map<String, Object> tags = (Map<String, Object>) tag;
					List<JButton> btns = new ArrayList<>();
					for(Entry<String, Object> tag : tags.entrySet()) {
						if(tag.getValue() instanceof String) {
							new TagFrame(NovelAITagHelper.main, title, NovelAITagHelper.main.currentCNTags, tags.keySet()).setVisible(true);
							return;
						}else {
							btns.add(new TagChoiceButton(tag.getKey(), tag.getValue()));
						}
					}
					new ButtonsFrame(NovelAITagHelper.main, title, btns, 5 + (btns.size()%5>=1?1:0), 3).setVisible(true);
				}
			}
		}, 15);
	}
}
