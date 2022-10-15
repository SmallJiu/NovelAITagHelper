package cat.jiu.ai.paint;

import java.awt.GridLayout;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JFrame;

public class ButtonsFrame extends JFrame {
	private static final long serialVersionUID = 83678693726664057L;
	public ButtonsFrame(JFrame context, String title, Collection<JButton> btns, int rows, int cols) {
		super(title);
		super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		super.setLayout(new GridLayout(rows, 3));
		super.setBounds(context.getX()+context.getWidth(), context.getY(), 275, rows*40);
		for(JButton btn : btns) {
			super.getContentPane().add(btn);
		}
	}
}
