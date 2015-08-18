package trb1914;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.JFrame;

import trb1914.util.DatePicker;
import trb1914.util.DatePicker.IDatePickReady;

public class Entry implements IDatePickReady{

	
	private static DatePicker startDatePicker;
	private static DatePicker endDatePicker;
	public static void main(String[] args){
		
		JFrame frame = new JFrame("Testing DatePicker");
		startDatePicker = new DatePicker(new Date());
		Entry entry = new Entry();
		startDatePicker.addReadyListener(entry);
		endDatePicker = new DatePicker(new Date());
		endDatePicker.addReadyListener(entry);
		frame.setLayout(new BorderLayout());
		frame.add(startDatePicker, BorderLayout.WEST);
		frame.add(endDatePicker, BorderLayout.EAST);
		startDatePicker.requestFocus();
		frame.setVisible(true);
		frame.pack();
	}

	@Override
	public void datePickReady(DatePicker parent) {
		if(parent.equals(startDatePicker)){
			endDatePicker.askFocus();
		}else if(parent.equals(endDatePicker)){
			endDatePicker.transferFocusUpCycle();
		}
	}
}
