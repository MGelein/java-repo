package trb1914.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import trb1914.data.Registry;

public class UsageRenderer extends JPanel implements ListCellRenderer<String>{

	private JLabel nameLabel = new JLabel();
	private JLabel countLabel = new JLabel();
	/**
	 * new UsageRenderer
	 */
	public UsageRenderer(){
		setLayout(new GridLayout(1,2));
		setOpaque(true);
		add(nameLabel);
		add(countLabel);
		nameLabel.setFont(Registry.LIST_FONT);
		countLabel.setFont(nameLabel.getFont());
	}
	@Override
	public Component getListCellRendererComponent(JList<? extends String> list,
			String s, int index, boolean isSelected, boolean cellHasFocus) {
		// TODO Auto-generated method stub
		String[] set = s.split(",");
		nameLabel.setText(set[0]);
		countLabel.setText(set[1]);
		boolean isEvenIndex = (index % 2) == 0;
		nameLabel.setForeground(list.getForeground());
		countLabel.setForeground(nameLabel.getForeground());
		if(isEvenIndex){
			setBackground(list.getBackground());
		}else{
			setBackground(new Color(0xDDDDFF));
		}
		nameLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.lightGray));
		countLabel.setBorder(nameLabel.getBorder());
		return this;
	}
}
