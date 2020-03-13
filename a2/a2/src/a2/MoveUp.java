package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class MoveUp extends AbstractAction {

	private Starter myStarter;
	public MoveUp(Starter starter) {
		// TODO Auto-generated constructor stub
		myStarter = starter;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		myStarter.moveUp();
	}

}
