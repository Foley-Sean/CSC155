package a4;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class StrafeRight extends AbstractAction {

	private Starter myStarter;
	
	public StrafeRight(Starter starter) {
		// TODO Auto-generated constructor stub
		myStarter = starter;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		myStarter.strafeRight();
	}

}
