package cat.jiu.ai.paint;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import cat.jiu.ai.paint.Cache.Tags;
import cat.jiu.ai.paint.Cache.Tags.Tag;

public class CacheFrame extends JFrame {
	private static final long serialVersionUID = -3888944233405830070L;
	
	final DefaultMutableTreeNode root = new DefaultMutableTreeNode(I18n.format("main.cache"), true);
	final JTree tree = new JTree(root);
	final Cache caches;
	final NovelAITagHelper main = NovelAITagHelper.main;
	
	public CacheFrame(Cache caches) {
		super(I18n.format("main.cache"));
		this.caches = caches;
		super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		super.setLayout(new BorderLayout());
		tree.setFont(NovelAITagHelper.font);
		
		super.setBounds(main.getX()+main.getWidth(), main.getY(), 225, main.getHeight());
		
		for(Entry<String, Tags> cache : caches.cacheSet()) {
			Tags tags = cache.getValue();
			DefaultMutableTreeNode cacheNode = new DefaultMutableTreeNode(cache.getKey());
			if(tags.getParent()!=null) {
				cacheNode.add(new DefaultMutableTreeNode(I18n.format("main.cache.parent")+": "+tags.getParent().name));
			}
			
			for(Entry<String, Tag> tag : tags.getTags()) {
				cacheNode.add(new DefaultMutableTreeNode(Utils.getTag(tag.getKey())+"="+tag.getValue().getLevel()));
			}
			root.add(cacheNode);
		}
		
		this.getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);
		
		JPanel north = new JPanel();
		
		north.add(new SimpleButton(I18n.format("main.cache.gen"), new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				genTag();
			}
		}, 20));
		
		north.add(new SimpleButton(I18n.format("main.cache.del"), new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				delTag();
			}
		}, 20));
		
		this.getContentPane().add(north, BorderLayout.NORTH);
		
		JPanel south = new JPanel();
		
		JLabel text = new JLabel(I18n.format("main.cache.level"));
		text.setFont(NovelAITagHelper.font);
		south.add(text);
		
		south.add(new SimpleButton("+1", new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(!tree.isSelectionEmpty()) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
					if(node.getParent()!=null && !((DefaultMutableTreeNode) node.getParent()).isRoot()) {
						String cachename = node.getParent().toString();
						String tagName = node.toString().split("=")[0];
						caches.addLevel(cachename, Utils.getTag(tagName));
						node.setUserObject(tagName+"="+caches.getLevel(cachename, Utils.getTag(tagName)));
						tree.updateUI();
						caches.save();
					}
				}
			}
		}, 20));
		south.add(new SimpleButton("-1", new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(!tree.isSelectionEmpty()) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
					if(node.getParent()!=null && !((DefaultMutableTreeNode) node.getParent()).isRoot()) {
						String cachename = node.getParent().toString();
						String tagName = node.toString().split("=")[0];
						caches.subLevel(cachename, Utils.getTag(tagName));
						node.setUserObject(tagName+"="+caches.getLevel(cachename, Utils.getTag(tagName)));
						tree.updateUI();
						caches.save();
					}
				}
			}
		}, 20));
		
		this.getContentPane().add(south, BorderLayout.SOUTH);
		
		{
			JPopupMenu popmenu = new JPopupMenu();
			
			JMenuItem gen = new JMenuItem(I18n.format("main.cache.gen"));
			gen.addActionListener(e->genTag());
			popmenu.add(gen);
			
			JMenuItem del = new JMenuItem(I18n.format("main.cache.del"));
			del.addActionListener(e->delTag());
			popmenu.add(del);
			
			tree.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					TreePath path = tree.getPathForLocation(e.getX(), e.getY());
					if(path==null || path.getParentPath()==null) return;
					tree.setSelectionPath(path);
					if(e.getButton() == 3) {
						popmenu.show(CacheFrame.this, e.getX(), e.getY());
					}
				}
			});
		}
	}
	
	private void genTag() {
		if(!tree.isSelectionEmpty()) {
			JDialog chioce = new JDialog(CacheFrame.this, "Format", true);
			chioce.setBounds(CacheFrame.this.getX(), CacheFrame.this.getY(), 200, 100);
			chioce.setLayout(null);
			chioce.setResizable(false);
			chioce.add(new SimpleButton("Naifu", 0, 0, chioce.getWidth(), 30, new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();//??????????????????
					if(node.getParent()!=null) {
						if(((DefaultMutableTreeNode)node.getParent()).isRoot()) {
							main.currentENTags.setText(caches.toTag(node.getPath()[node.getPath().length -1].toString(), true));
						}else {
							DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
							main.currentENTags.setText(caches.toTag(parent.getPath()[parent.getPath().length -1].toString(), true));
						}
						chioce.setVisible(false);
					}
				}
			}, 20));
			
			chioce.add(new SimpleButton("WebUI", 0, 30, chioce.getWidth(), 30, new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();//??????????????????
					if(node.getParent()!=null) {
						if(((DefaultMutableTreeNode)node.getParent()).isRoot()) {
							main.currentENTags.setText(caches.toTag(node.getPath()[node.getPath().length -1].toString(), false));
						}else {
							DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
							main.currentENTags.setText(caches.toTag(parent.getPath()[parent.getPath().length -1].toString(), false));
						}
						chioce.setVisible(false);
					}
				}
			}, 20));
			
			chioce.setVisible(true);
		}
	}
	
	private void delTag() {
		if(!tree.isSelectionEmpty()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();//??????????????????
			if(node.getParent()!=null) {
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
				if(parent.isRoot()) {
					caches.remove(node.toString());
				}else {
					caches.remove(parent.toString(), Utils.getTag(node.toString().split("=")[0]));
				}
				
				tree.updateUI();
				caches.save();
			}
		}
	}
	
	public void updataUI() {
		TreePath oldSelectionPath = tree.getSelectionPath();
		root.removeAllChildren();
		for(Entry<String, Tags> cache : caches.cacheSet()) {
			DefaultMutableTreeNode cacheNode = new DefaultMutableTreeNode(cache.getKey());
			Tags tags = cache.getValue();
			for(Entry<String, Tag> tag : tags.getTags()) {
				cacheNode.add(new DefaultMutableTreeNode(Utils.getTag(tag.getKey())+"="+tag.getValue().getLevel()));
			}
			root.add(cacheNode);
		}
		tree.updateUI();
		tree.setSelectionPath(oldSelectionPath);
		tree.expandPath(oldSelectionPath);
	}
}
