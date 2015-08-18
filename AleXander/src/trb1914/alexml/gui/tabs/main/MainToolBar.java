package trb1914.alexml.gui.tabs.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import trb1914.alexml.Main;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.Registry;
import trb1914.alexml.gui.CodeSettingsEditor;
import trb1914.alexml.gui.ProgramSettingsFrame;
/**
 * The main toolbar that provides acces to saving, opening and the
 * File-menu
 * @author Mees Gelein
 *
 */
public class MainToolBar extends JToolBar {

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu settingsMenu;
	private JMenuItem saveItem;
	private JMenuItem saveAsItem;
	private JMenu recentItem;
	private JButton saveButton;
	private JButton openButton;
	private JButton newButton;
	
	private JMenuItem file1Item;
	private JMenuItem file2Item;
	private JMenuItem file3Item;
	private JMenuItem file4Item;
	private JMenuItem file5Item;

	/**
	 * creates a new MainToolBar.
	 */
	public MainToolBar(){
		setFloatable(false);
		makeGUI();
	}
	
	/**
	 * builds the GUI
	 */
	private void makeGUI(){
		menuBar = new JMenuBar();

		fileMenu = new JMenu(LanguageRegistry.MENU_FILE);
		JMenuItem openItem = new JMenuItem(LanguageRegistry.MENU_OPEN);
		saveItem = new JMenuItem(LanguageRegistry.MENU_SAVE);
		JMenuItem newItem = new JMenuItem(LanguageRegistry.MENU_NEW);
		JMenuItem quitItem = new JMenuItem(LanguageRegistry.MENU_QUIT);
		saveAsItem = new JMenuItem(LanguageRegistry.MENU_SAVE_AS);
		recentItem = new JMenu(LanguageRegistry.MENU_RECENT);
		fileMenu.add(newItem);
		fileMenu.addSeparator();
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.addSeparator();
		fileMenu.add(recentItem);
		fileMenu.addSeparator();
		fileMenu.add(quitItem);
		menuBar.add(fileMenu);
		
		settingsMenu = new JMenu(LanguageRegistry.MENU_SETTINGS);
		JMenuItem programSettingsItem = new JMenuItem(LanguageRegistry.MENU_PROGRAM_SETTINGS);
		JMenuItem codeSettingsItem = new JMenuItem(LanguageRegistry.MENU_CODE_HIGHLIGHT_SETTINGS);
		JMenuItem infoItem = new JMenuItem(LanguageRegistry.MENU_INFO);
		settingsMenu.add(programSettingsItem);
		settingsMenu.add(codeSettingsItem);
		settingsMenu.addSeparator();
		settingsMenu.add(infoItem);
		menuBar.add(settingsMenu);
		

		saveButton = new JButton(new ImageIcon(FileRegistry.SAVE_ICON));
		saveButton.setToolTipText(LanguageRegistry.SAVE_FILE_BUTTON);
		saveButton.setEnabled(false);
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Main.currentFile != null){
					Main.saveSilently();
				}else{
					Main.saveToFile();
				}
			}
		});

		openButton = new JButton(new ImageIcon(FileRegistry.OPEN_ICON));
		openButton.setToolTipText(LanguageRegistry.OPEN_FILE_BUTTON);
		openButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.openFile();
			}
		});

		newButton = new JButton(new ImageIcon(FileRegistry.NEW_ICON));
		newButton.setToolTipText(LanguageRegistry.NEW_FILE_BUTTON);
		newButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Main.currentFile != null){
					if(JOptionPane.showConfirmDialog(null, LanguageRegistry.NEW_DOCUMENT_WARNING) == JOptionPane.YES_OPTION){
						Main.createNewFile();
					}
				}else{
					Main.createNewFile();
				}
			}
		});

		
		add(newButton);
		addSeparator();
		add(saveButton);
		add(openButton);
		
		programSettingsItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new ProgramSettingsFrame();
			}
		});
		
		codeSettingsItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new CodeSettingsEditor();
			}
		});
		
		infoItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JOptionPane.showMessageDialog(null, LanguageRegistry.APPLICATION_INFO);
			}
		});

		newItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.createNewFile();
			}
		});
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Registry.CTRL_MASK_CROSS_PLATFORM));

		quitItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.closeWindow();
			}
		});
		quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));

		saveItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(Main.currentFile != null){
					Main.saveSilently();
				}else{
					Main.saveToFile();
				}
			}
		});
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Registry.CTRL_MASK_CROSS_PLATFORM));

		saveAsItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.saveToFile();
			}
		});
		saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Registry.CTRL_MASK_CROSS_PLATFORM|ActionEvent.SHIFT_MASK));


		openItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.openFile();
			}
		});
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Registry.CTRL_MASK_CROSS_PLATFORM));
		updateRecentItem();
	}
	
	/**
	 * updates the recent files in the menu
	 */
	public void updateRecentItem(){
		recentItem.removeAll();

		final File f1 = Main.recentFiles.get(0);
		String s = "...";
		if(f1!= null){s = f1.getAbsolutePath();}
		file1Item = new JMenuItem(s);
		if(f1!= null){
			file1Item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Main.openSilently(f1);
				}
			});
		}
		file1Item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		recentItem.add(file1Item);

		final File f2 = Main.recentFiles.get(1);
		s = "...";
		if(f2!= null){s = f2.getAbsolutePath();}
		file2Item = new JMenuItem(s);
		if(f2!= null){
			file2Item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Main.openSilently(f2);
				}
			});
		}
		file2Item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
		recentItem.add(file2Item);

		final File f3 = Main.recentFiles.get(2);
		s = "...";
		if(f3!= null){s = f3.getAbsolutePath();}
		file3Item = new JMenuItem(s);
		if(f3!= null){
			file3Item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Main.openSilently(f3);
				}
			});
		}
		file3Item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK));
		recentItem.add(file3Item);

		final File f4 = Main.recentFiles.get(3);
		s = "...";
		if(f4!= null){s = f4.getAbsolutePath();}
		file4Item = new JMenuItem(s);
		if(f4!= null){
			file4Item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Main.openSilently(f4);
				}
			});
		}
		file4Item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.ALT_MASK));
		recentItem.add(file4Item);

		final File f5 = Main.recentFiles.get(4);
		s = "...";
		if(f5!= null){s = f5.getAbsolutePath();}
		file5Item = new JMenuItem(s);
		if(f5!= null){
			file5Item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Main.openSilently(f5);
				}
			});
		}
		file5Item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.ALT_MASK));
		recentItem.add(file5Item);
	}
	
	/**
	 * sets the state of the toolbar to the specified value
	 * @param b		true = on, false = off
	 */
	public void setState(boolean b){
		saveItem.setEnabled(b);
		saveAsItem.setEnabled(b);
		saveButton.setEnabled(b);
	}

	/**
	 * the JMenuBar from the mainToolBar
	 * @return
	 */
	public JMenuBar getJMenuBar(){
		return menuBar;
	}
}
