package a1;

import java.nio.*;
import java.sql.Time;

import javax.swing.*;

import org.joml.Vector2d;

import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.GLContext;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.*;

public class Starter extends JFrame implements GLEventListener
{	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];

	private float x = 0.0f;
	private float y = 0.0f;
	private float inc = 0.01f;
	
	//vars for moving in a circle
	private float radius = 0.2f;
	private float speed = 0.01f;
	private float angle = 0.0f;
	
	
	public Starter()
	{	setTitle("Chapter 2 - program 6");
		setSize(400, 200);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);
		Animator animator = new Animator(myCanvas);
		animator.start();
	}

	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glUseProgram(renderingProgram);
		
		//Original Code to make triangle move left and right
		/*
		x += inc;
		if (x > 1.0f) inc = -0.01f;
		if (x < -1.0f) inc = 0.01f;
		int offsetLoc = gl.glGetUniformLocation(renderingProgram, "inc");
		gl.glProgramUniform1f(renderingProgram, offsetLoc, x);
		*/
		
		//rotate in a circle
		
		inc += 0.05f;
		if(inc > 2 *  Math.PI ) inc = 0.0f;
		//angle = speed * inc;
		x = (float) Math.cos(inc);
		int offsetLoc = gl.glGetUniformLocation(renderingProgram, "inc");
		gl.glProgramUniform1f(renderingProgram, offsetLoc, x);
		y =  (float) Math.sin(inc);
		offsetLoc = gl.glGetUniformLocation(renderingProgram, "yinc");
		gl.glProgramUniform1f(renderingProgram, offsetLoc, y);
		
		
		
		gl.glDrawArrays(GL_TRIANGLES,0,3);
	}
	
	

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		//this needs to be changed to access the shader files on any machine
		renderingProgram = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a1\\src\\a1\\vertShader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a1\\src\\a1\\fragShader.glsl");
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
	}

	public static void main(String[] args) { new Starter(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}
}

