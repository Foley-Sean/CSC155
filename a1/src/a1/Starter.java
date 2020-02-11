package a1;

import java.awt.BorderLayout;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.*;
import java.sql.Time;

import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.JButton;

import org.joml.Vector2d;

import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.*;

public class Starter extends JFrame implements GLEventListener, MouseWheelListener
{	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];
	private GL4 gl;
	private int orbit = 0;
	private int upDown = 0;
	private int changeColor;
	private float scale = 0.0f;
	private float x = 0.0f;
	private float y = 0.0f;
	private float inc = 0.01f;
	private float deltaMouseWheel;

	public Starter()
	{	setTitle("Chapter 2 - program 6");
		setSize(400, 200);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);
		
		
		//circle motion button
		JPanel botPanel = new JPanel();
		this.add(botPanel,BorderLayout.SOUTH);
		JButton circleButton = new JButton ("Orbit In Circle");
		botPanel.add(circleButton);
		CircleCommand circleCommand = new CircleCommand(this);
		circleButton.setAction(circleCommand);
		
		//up down button
		JPanel botpanel = new JPanel();
		this.add(botPanel, BorderLayout.SOUTH);
		JButton upDownButton = new JButton ("Up-Down");
		botPanel.add(upDownButton);
		UpDownCommand upDownCommad = new UpDownCommand(this);
		upDownButton.setAction(upDownCommad);
		
		//change color key binding
		JComponent contentPane = (JComponent) this.getContentPane();
		int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap imap = contentPane.getInputMap(mapName);
		KeyStroke cKey = KeyStroke.getKeyStroke('c');
		imap.put(cKey, "color");
		ActionMap amap = contentPane.getActionMap();
		ColorCommand myColorCommand = new ColorCommand(this);
		amap.put("color", myColorCommand);
		this.requestFocus();
		
		//mouse wheel listener
		this.addMouseWheelListener(this);
		
		Animator animator = new Animator(myCanvas);
		animator.start();
	}

	public void display(GLAutoDrawable drawable)
	{	gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glUseProgram(renderingProgram);
		
		if(this.getOrbit() == 1) {
			this.orbit();
		}
		if(this.getUpDown() == 1) {
			this.upDown();
		}
		
		this.scaleTriangle();
		this.changeColor();
		
		gl.glDrawArrays(GL_TRIANGLES,0,3);
	}
	
	
	public void init(GLAutoDrawable drawable)
	{	gl = (GL4) GLContext.getCurrentGL();
		//this needs to be changed to access the shader files on any machine
		renderingProgram = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a1\\src\\a1\\vertShader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a1\\src\\a1\\fragShader.glsl");
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
	}

	public void orbit() {

	    int offsetLock = gl.glGetUniformLocation(renderingProgram, "orbit");
		gl.glProgramUniform1f(renderingProgram, offsetLock, orbit);
		
		inc += 0.05f;
		if(inc >= 2 * Math.PI)  inc = 0.0f;
		
		x = (float) Math.cos(inc);
		int offsetLoc = gl.glGetUniformLocation(renderingProgram, "oxinc");
		gl.glProgramUniform1f(renderingProgram, offsetLoc, x);
		
		y =  (float) Math.sin(inc);
		offsetLoc = gl.glGetUniformLocation(renderingProgram, "oyinc");
		gl.glProgramUniform1f(renderingProgram, offsetLoc, y);
		
	}
	
	public void upDown() {
		
		y += inc;
		if (y > 1.0f) inc = -0.01f;
		if (y < -1.0f) inc = 0.01f;
		int offsetLoc = gl.glGetUniformLocation(renderingProgram, "upDown");
		gl.glProgramUniform1f(renderingProgram, offsetLoc, upDown);
		
		offsetLoc = gl.glGetUniformLocation(renderingProgram, "yinc");
		gl.glProgramUniform1f(renderingProgram, offsetLoc, y);
		
	}
	
	public void changeColor() {

		int offsetLoc = gl.glGetUniformLocation(renderingProgram, "changeColor");
		gl.glProgramUniform1f(renderingProgram, offsetLoc, changeColor);
	    
	}
	
	private void scaleTriangle() {

	    int offsetLoc = gl.glGetUniformLocation(renderingProgram, "sinc");
	    gl.glProgramUniform1f(renderingProgram, offsetLoc, scale);
	    
	}


	public static void main(String[] args) { new Starter(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}

	//getters and setters
	
	public void setOrbit(int i) {
		this.orbit = i;
	}

	public int getOrbit() {
		return this.orbit;
	}

	public int getUpDown() {
		return this.upDown;
	}

	public void setUpDown(int i) {
		this.upDown = i;
		
	}

	public int getChangeColor() {
		return this.changeColor;
	}

	public void setChangeColor(int i) {
		this.changeColor = i;
	}
	public float getScale() {
		return this.scale;
	}
	
	//mouse wheel listener
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		
		if(arg0.getWheelRotation() > 0) scale += 0.01f;
		else if(arg0.getWheelRotation() < 0) scale -= 0.01f;
	}

}

