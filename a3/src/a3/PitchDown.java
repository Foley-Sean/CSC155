package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class PitchDown extends AbstractAction {

	private Starter myStarter;
	public PitchDown(Starter starter) {
		// TODO Auto-generated constructor stub
		myStarter = starter;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		myStarter.pitchDown();
	}

}
