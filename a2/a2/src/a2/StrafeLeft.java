package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class StrafeLeft extends AbstractAction {
	
	private Starter myStarter;
	public StrafeLeft(Starter starter) {
		// TODO Auto-generated constructor stub
		myStarter = starter;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		myStarter.strafeLeft();
	}

}
