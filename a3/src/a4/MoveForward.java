package a4;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class MoveForward extends AbstractAction {

	private Starter myStarter;
	
	public MoveForward(Starter starter) {
		myStarter = starter;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		myStarter.MoveForward();
	}
}
