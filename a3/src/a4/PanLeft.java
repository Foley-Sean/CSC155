package a4;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class PanLeft extends AbstractAction {

	private Starter myStarter;
	public PanLeft(Starter starter) {
		// TODO Auto-generated constructor stub
		myStarter = starter;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		myStarter.panLeft();
	}

}
