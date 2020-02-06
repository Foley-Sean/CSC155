package a1;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class UpDownCommand extends AbstractAction {

	private Starter myStarter;
	
	public UpDownCommand(Starter starter) {
		super("Go up and down");
		myStarter = starter;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(myStarter.getUpDown() == 0) {
			myStarter.setUpDown(1);
			
		}
		else if(myStarter.getUpDown() == 1) {
			myStarter.setUpDown(0);
		}
		
	}

}
