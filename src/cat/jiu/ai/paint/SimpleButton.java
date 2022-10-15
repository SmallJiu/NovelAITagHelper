package cat.jiu.ai.paint;

import java.awt.Font;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;

public class SimpleButton extends JButton {
	private static final long serialVersionUID = 6756680536166677407L;
	static final Map<Integer, Font> cacheFont = new HashMap<>();
	
	public SimpleButton(String text, MouseListener l, int fontSize) {
		this(text,0,0,10,10,l,fontSize);
	}
	public SimpleButton(String text, int x, int y, int width, int height, MouseListener l, int fontSize) {
		super.setBounds(x, y, width, height);
		super.addMouseListener(l);
		super.setText(text);
		if(cacheFont.containsKey(fontSize)) {
			super.setFont(cacheFont.get(fontSize));
		}else {
			Font font = new Font(null,0,fontSize);
			super.setFont(font);
			cacheFont.put(fontSize, font);
		}
	}
}
