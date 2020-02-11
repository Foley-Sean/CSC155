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
		if(myStarter.getOrbit() == 0) {
			myStarter.setOrbit(1);
		}
		else if(myStarter.getOrbit() == 1) {
			myStarter.setOrbit(0);
		}
	}
}


