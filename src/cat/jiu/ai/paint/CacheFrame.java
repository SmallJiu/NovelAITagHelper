package cat.jiu.ai.paint;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

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
		super.setBounds(main.getX()+main.getWidth(), main.getY(), 298, main.getHeight());
		
		for(Entry<String, Map<String, Integer>> cache : caches.entrySet()) {
			DefaultMutableTreeNode cacheNode = new DefaultMutableTreeNode(cache.getKey());
			for(Entry<String, Integer> tag : cache.getValue().entrySet()) {
				cacheNode.add(new DefaultMutableTreeNode(Utils.getTag(tag.getKey())+"="+tag.getValue()));
			}
			root.add(cacheNode);
		}
		
		JScrollPane treePane = new JScrollPane(tree);
		this.getContentPane().add(treePane, BorderLayout.CENTER);
		
		JPanel south = new JPanel();
		south.add(new SimpleButton("+1", new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
				String cachename = node.getParent().toString();
				String tagName = node.toString().split("=")[0];
				caches.addLevel(cachename, Utils.getTag(tagName));
				node.setUserObject(tagName+"="+caches.getLevel(cachename, Utils.getTag(tagName)));
				tree.updateUI();
				caches.save();
			}
		}, 20));
		south.add(new SimpleButton("-1", new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
				String cachename = node.getParent().toString();
				String tagName = node.toString().split("=")[0];
				caches.subLevel(cachename, Utils.getTag(tagName));
				node.setUserObject(tagName+"="+caches.getLevel(cachename, Utils.getTag(tagName)));
				tree.updateUI();
				caches.save();
			}
		}, 20));
		south.add(new SimpleButton(I18n.format("main.cache.gen"), new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
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
		}, 20));
		south.add(new SimpleButton(I18n.format("main.cache.del"), new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();//获取节点名称
				if(node.getParent()!=null) {
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
					if(parent.isRoot()) {
						caches.remove(node.toString());
						root.remove(node);
					}else {
						caches.remove(parent.toString(), Utils.getTag(node.toString().split("=")[0]));
						parent.remove(node);
					}
					
					tree.updateUI();
					caches.save();
				}
			}
		}, 20));
		this.getContentPane().add(south, BorderLayout.SOUTH);
	}
	
	public void updataUI() {
		root.removeAllChildren();
		for(Entry<String, Map<String, Integer>> cache : caches.entrySet()) {
			DefaultMutableTreeNode cacheNode = new DefaultMutableTreeNode(cache.getKey());
			for(Entry<String, Integer> tag : cache.getValue().entrySet()) {
				cacheNode.add(new DefaultMutableTreeNode(Utils.getTag(tag.getKey())+"="+tag.getValue()));
			}
			root.add(cacheNode);
		}
		tree.updateUI();
	}
}
