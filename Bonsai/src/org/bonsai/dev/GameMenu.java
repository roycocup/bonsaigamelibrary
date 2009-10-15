package org.bonsai.dev;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class GameMenu extends GameComponent implements ActionListener {
	private boolean active = false;
	protected JMenuBar menuBar = null;
	private HashMap<String, JMenu> menus = new HashMap<String, JMenu>();
	private HashMap<String, JMenuItem> menuItems = new HashMap<String, JMenuItem>();
	private HashMap<String, ButtonGroup> menuGroups = new HashMap<String, ButtonGroup>();

	public GameMenu(Game g, boolean active) {
		super(g);
		if (active) {
			this.active = true;
			game = g;
			menuBar = new JMenuBar();
			game.frame.setJMenuBar(menuBar);
			add("Game");
			addCheckItem("Game", "Pause", "pause");
			addItem("Game", "Exit", "exit");
		}
	}

	public void addRadioItem(String id, String name, String cmd, String group) {
		if (active) {
			ButtonGroup g = null;
			if (menuGroups.containsKey(group)) {
				g = menuGroups.get(group);
			} else {
				g = new ButtonGroup();
				menuGroups.put(group, g);
			}
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(name);
			g.add(item);
			addItems(id, item, cmd);
		}
	}

	public void addCheckItem(String id, String name, String cmd) {
		addItems(id, new JCheckBoxMenuItem(name), cmd);
	}

	public void addItem(String id, String name, String cmd) {
		addItems(id, new JMenuItem(name), cmd);
	}

	private void addItems(String id, JMenuItem item, String cmd) {
		if (active) {
			JMenu menu = get(id);
			if (menu != null && !menuItems.containsKey(cmd)) {
				item.setActionCommand(cmd);
				item.addActionListener(this);
				menu.add(item);
				menuItems.put(cmd, item);
			}
		}
	}

	public void enable(String id, boolean enable) {
		if (active) {
			get(id).setEnabled(enable);
		}
	}

	public void enable(boolean enable) {
		if (active) {
			for (JMenu menu : menus.values()) {
				menu.setEnabled(enable);
			}
		}
	}

	public JMenu add(String name) {
		if (active && !menus.containsKey(name)) {
			JMenu menu = new JMenu(name);
			menus.put(name, menu);
			menuBar.add(menu);
			menuBar.validate();
			menu.setEnabled(false);
			return menu;
		} else {
			return null;
		}
	}

	public JMenu get(String name) {
		if (active && !menus.containsKey(name)) {
			return null;
		} else {
			return menus.get(name);
		}
	}

	public JMenuItem getItem(String name) {
		if (!active || !menuItems.containsKey(name)) {
			return null;
		} else {
			return menuItems.get(name);
		}
	}

	public void select(String name, boolean selected) {
		if (active) {
			getItem(name).setSelected(selected);
		}
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("exit")) {
			game.exitGame();
		} else if (cmd.equals("pause")) {
			game.pause(!game.paused);
		} else {
			game.onMenu(cmd);
		}
	}
}
