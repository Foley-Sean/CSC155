package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class PitchUp extends AbstractAction {

	private Starter myStarter;
	public PitchUp(Starter starter) {
		// TODO Auto-generated constructor stub
		myStarter = starter;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		myStarter.pitchUp();
	}

}
