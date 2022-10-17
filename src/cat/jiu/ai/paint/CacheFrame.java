package cat.jiu.ai.paint;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class CacheFrame extends JFrame {
	private static final long serialVersionUID = -3888944233405830070L;
	
	final DefaultMutableTreeNode root = new DefaultMutableTreeNode(I18n.format("main.cache"), true);
	final JTree tree = new JTree(root);
	final Cache caches;
	
	public CacheFrame(Cache caches) {
		super(I18n.format("main.cache"));
		this.caches = caches;
		super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		super.setLayout(new BorderLayout());
		
		NovelAITagHelper main = NovelAITagHelper.main;
		super.setBounds(main.getX()+main.getWidth(), main.getY(), 225, main.getHeight());
		
		for(Entry<String, Map<String, Integer>> cache : caches.cacheSet()) {
			DefaultMutableTreeNode cacheNode = new DefaultMutableTreeNode(cache.getKey());
			for(Entry<String, Integer> tag : cache.getValue().entrySet()) {
				cacheNode.add(new DefaultMutableTreeNode(Utils.getTag(tag.getKey())+"="+tag.getValue()));
			}
			root.add(cacheNode);
		}
		
		this.getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);
		
		JPanel north = new JPanel();
		
		north.add(new SimpleButton(I18n.format("main.cache.gen"), new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(!tree.isSelectionEmpty()) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();//获取节点名称
					if(node.getParent()!=null) {
						if(((DefaultMutableTreeNode)node.getParent()).isRoot()) {
							main.currentENTags.setText(caches.toTag(node.getPath()[node.getPath().length -1].toString()));
						}else {
							DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
							main.currentENTags.setText(caches.toTag(parent.getPath()[parent.getPath().length -1].toString()));
						}
					}
				}
			}
		}, 20));
		north.add(new SimpleButton(I18n.format("main.cache.del"), new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(!tree.isSelectionEmpty()) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();//获取节点名称
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
	}
	
	public void updataUI() {
		TreePath oldSelectionPath = tree.getSelectionPath();
		root.removeAllChildren();
		for(Entry<String, Map<String, Integer>> cache : caches.cacheSet()) {
			DefaultMutableTreeNode cacheNode = new DefaultMutableTreeNode(cache.getKey());
			for(Entry<String, Integer> tag : cache.getValue().entrySet()) {
				cacheNode.add(new DefaultMutableTreeNode(Utils.getTag(tag.getKey())+"="+tag.getValue()));
			}
			root.add(cacheNode);
		}
		tree.updateUI();
		tree.setSelectionPath(oldSelectionPath);
		tree.expandPath(oldSelectionPath);
	}
}
