package a1;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.jogamp.opengl.awt.GLCanvas;

public class CircleCommand extends AbstractAction {

	private Starter myStarter;
	
	public CircleCommand(Starter starter) {
		super("Go in a circle");
		myStarter = starter;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		//if(arg0.getActionCommand() != null) {
		if(myStarter.getOrbit() == 0) {
			myStarter.setOrbit(1);
			myStarter.orbit();
		
		}
		else if(myStarter.getOrbit() == 1) {
			myStarter.setOrbit(0);
			myStarter.orbit();
		}
	}
}


