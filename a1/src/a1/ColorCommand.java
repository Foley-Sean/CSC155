package a1;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ColorCommand extends AbstractAction {

	private Starter myStarter;
	
	public ColorCommand(Starter starter) {
		myStarter = starter;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(myStarter.getChangeColor() == 0) {
			myStarter.setChangeColor(1);
			
		}
		else if(myStarter.getChangeColor() == 1) {
			myStarter.setChangeColor(0);
		}
		
	}
	

}
