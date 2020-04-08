package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class MoveBackward extends AbstractAction {

	private Starter myStarter;
	
	public MoveBackward(Starter starter) {
		myStarter = starter;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		myStarter.moveBackward();
	}
	
	
}
