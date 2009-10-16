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
	private JMenuBar menuBar = null;
	private HashMap<String, JMenu> menus = new HashMap<String, JMenu>();
	private HashMap<String, JMenuItem> menuItems = new HashMap<String, JMenuItem>();
	private HashMap<String, ButtonGroup> menuGroups = new HashMap<String, ButtonGroup>();

	public GameMenu(final Game g, final boolean init) {
		super(g);
		if (init) {
			active = true;
			game = g;
			menuBar = new JMenuBar();
			game.getFrame().setJMenuBar(menuBar);
			add("Game");
			addCheckItem("Game", "Pause", "pause");
			addItem("Game", "Exit", "exit");
		}
	}
	
	public final int getSize() {
		if (active) {
			return menuBar.getHeight();
		} else {
			return 0;
		}
	}

	public final void addRadioItem(final String id, final String name,
			final String cmd, final String group) {
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

	public final void addCheckItem(final String id, final String name, final String cmd) {
		addItems(id, new JCheckBoxMenuItem(name), cmd);
	}

	public final void addItem(final String id, final String name, final String cmd) {
		addItems(id, new JMenuItem(name), cmd);
	}

	private void addItems(final String id, final JMenuItem item, final String cmd) {
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

	public final void enable(final String id, final boolean enable) {
		if (active) {
			get(id).setEnabled(enable);
		}
	}

	public final void enable(final boolean enable) {
		if (active) {
			for (JMenu menu : menus.values()) {
				menu.setEnabled(enable);
			}
		}
	}

	public final JMenu add(final String name) {
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

	public final JMenu get(final String name) {
		if (active && !menus.containsKey(name)) {
			return null;
		} else {
			return menus.get(name);
		}
	}

	public final JMenuItem getItem(final String name) {
		if (!active || !menuItems.containsKey(name)) {
			return null;
		} else {
			return menuItems.get(name);
		}
	}

	public final void select(final String name, final boolean selected) {
		if (active) {
			getItem(name).setSelected(selected);
		}
	}

	public final void actionPerformed(final ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("exit")) {
			game.exitGame();
		} else if (cmd.equals("pause")) {
			game.pause(!game.isPaused());
		} else {
			game.onMenu(cmd);
		}
	}
}
