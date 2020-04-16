package a3;

import java.nio.*;
import javax.swing.*;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.Math;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;

import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;

import org.joml.*;

public class Starter extends JFrame implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener
{	private GLCanvas myCanvas;
	private int renderingProgram1, renderingProgram2;
	private int vao[] = new int[1];
	private int vbo[] = new int[5];

	// model stuff
	private ImportedModel pyramid;
	private Torus myTorus;
	private int numPyramidVertices, numTorusVertices, numTorusIndices;
	//shuttle model
	private int numObjVertices;
	private ImportedModel myModel;
	private int shuttleTexture;
	
	// location of torus, shuttle, pyramid, light, and camera
	private Vector3f torusLoc = new Vector3f(1.6f, 0.0f, -0.3f);
	private Vector3f pyrLoc = new Vector3f(-1.0f, 0.1f, 0.3f);
	private Vector3f shutLoc = new Vector3f( -3.0f, 0.4f, 0.6f);
	private Vector3f cameraLoc = new Vector3f(0.0f, 0.2f, 6.0f);
	private Vector3f lightLoc = new Vector3f(-3.8f, 2.2f, 1.1f);
	//moveable light loc
	private Vector3f movLightLoc = new Vector3f(3.8f, -1.2f, -1.5f);
	
	// white light properties
	private float[] globalAmbient = new float[] { 0.7f, 0.7f, 0.7f, 1.0f };
	private float[] lightAmbient = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	private float[] lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] lightSpecular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	
	//moveable light properties
	private float[]  movLightAmbient = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
	private float[]  movLightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[]  movLightSpecular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	
	// gold material
	private float[] GmatAmb = Utils.goldAmbient();
	private float[] GmatDif = Utils.goldDiffuse();
	private float[] GmatSpe = Utils.goldSpecular();
	private float GmatShi = Utils.goldShininess();
	
	// bronze material
	private float[] BmatAmb = Utils.bronzeAmbient();
	private float[] BmatDif = Utils.bronzeDiffuse();
	private float[] BmatSpe = Utils.bronzeSpecular();
	private float BmatShi = Utils.bronzeShininess();
	
	private float[] thisAmb, thisDif, thisSpe, matAmb, matDif, matSpe;
	private float thisShi, matShi;
	
	// shadow stuff
	private int scSizeX, scSizeY;
	private int [] shadowTex = new int[1];
	private int [] shadowBuffer = new int[1];
	private Matrix4f lightVmat = new Matrix4f();
	private Matrix4f lightPmat = new Matrix4f();
	private Matrix4f shadowMVP1 = new Matrix4f();
	private Matrix4f shadowMVP2 = new Matrix4f();
	private Matrix4f b = new Matrix4f();

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16); //16 
	private Matrix4f pMat = new Matrix4f();  // perspective matrix
	private Matrix4f vMat = new Matrix4f();  // view matrix
	private Matrix4f mMat = new Matrix4f();  // model matrix
	private Matrix4f mvMat = new Matrix4f(); // model-view matrix
	private Matrix4f invTrMat = new Matrix4f(); // inverse-transpose
	private int mvLoc, projLoc, nLoc, sLoc;
	private int globalAmbLoc, ambLoc, diffLoc, specLoc, posLoc, mambLoc, mdiffLoc, mspecLoc, mshiLoc;
	//moveable light
	private int movAmbLoc, movDiffLoc, movSpecLoc, movPosLoc;
	private float aspect;
	private Vector3f currentLightPos = new Vector3f();
	private Vector3f movCurrentLightPos = new Vector3f();
	private float[] lightPos = new float[3];
	//moveable light
	private float[] movLightPos = new float[3];
	private Vector3f origin = new Vector3f(0.0f, 0.0f, 0.0f);
	private Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
	private Camera camera;
	
	public Starter()
	{	setTitle("Chapter8 - program 2");
		setSize(800, 800);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
	    //mouse event listeners
        myCanvas.addMouseWheelListener(this);
        myCanvas.addMouseMotionListener(this);
        
		this.add(myCanvas);
		this.setVisible(true);
		
		//set binds
		JComponent contentPane = (JComponent) this.getContentPane();
		int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap imap = contentPane.getInputMap(mapName);
			
		//move forward
		KeyStroke wKey = KeyStroke.getKeyStroke('w');
		imap.put(wKey, "forward");
		ActionMap amap = contentPane.getActionMap();
		MoveForward myMoveF = new MoveForward(this);
		amap.put("forward", myMoveF);
		this.requestFocus();
				
		//move backwards
		KeyStroke sKey = KeyStroke.getKeyStroke('s');
		imap.put(sKey, "backward");
		amap = contentPane.getActionMap();
		MoveBackward myMoveB = new MoveBackward(this);
		amap.put("backward", myMoveB);
		this.requestFocus();
				
		//strafe left
		KeyStroke aKey = KeyStroke.getKeyStroke('a');
		imap.put(aKey, "strafeLeft");
		amap = contentPane.getActionMap();
		StrafeLeft myStrafeLeft = new StrafeLeft(this);
		amap.put("strafeLeft", myStrafeLeft);
		this.requestFocus();
				
		//strafe right
		KeyStroke dKey = KeyStroke.getKeyStroke('d');
		imap.put(dKey, "strafeRight");
		amap = contentPane.getActionMap();
		StrafeRight myStrafeRight = new StrafeRight(this);
		amap.put("strafeRight", myStrafeRight);
		this.requestFocus();
				
		//move up
		KeyStroke qKey = KeyStroke.getKeyStroke('q');
		imap.put(qKey, "moveUp");
		amap = contentPane.getActionMap();
		MoveUp myMoveUp = new MoveUp(this);
		amap.put("moveUp", myMoveUp);
		this.requestFocus();
				
		//move down
		KeyStroke eKey = KeyStroke.getKeyStroke('e');
		imap.put(eKey, "moveDown");
		amap = contentPane.getActionMap();
		MoveDown myMoveDown = new MoveDown(this);
		amap.put("moveDown", myMoveDown);
		this.requestFocus();
				
		//pan left
		KeyStroke leftKey = KeyStroke.getKeyStroke("LEFT");
		imap.put(leftKey, "panLeft");
		amap = contentPane.getActionMap();
		PanLeft myPanLeft = new PanLeft(this);
		amap.put("panLeft", myPanLeft);
		this.requestFocus();
				
		//pan right
		KeyStroke rightKey = KeyStroke.getKeyStroke("RIGHT");
		imap.put(rightKey, "panRight");
		amap = contentPane.getActionMap();
		PanRight myPanRight = new PanRight(this);
		amap.put("panRight", myPanRight);
		this.requestFocus();
				
		//pitch up
		KeyStroke upKey = KeyStroke.getKeyStroke("UP");
		imap.put(upKey, "pitchUp");
		amap = contentPane.getActionMap();
		PitchUp myPitchUp = new PitchUp(this);
		amap.put("pitchUp", myPitchUp);
		this.requestFocus();
				
		//pitch down
		KeyStroke downKey = KeyStroke.getKeyStroke("DOWN");
		imap.put(downKey, "pitchDown");
		amap = contentPane.getActionMap();
		PitchDown myPitchDown = new PitchDown(this);
		amap.put("pitchDown", myPitchDown);
		this.requestFocus();
				
		//display axes
		KeyStroke showAxes = KeyStroke.getKeyStroke("SPACE");
		imap.put(showAxes, "showAxes");
		amap = contentPane.getActionMap();
		DisplayAxes myShowAxes = new DisplayAxes(this);
		amap.put("showAxes", myShowAxes);
		this.requestFocus();
		
		Animator animator = new Animator(myCanvas);
		animator.start();
	}

	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		
		currentLightPos.set(lightLoc);
		//movCurrentLightPos.set(movLightLoc);
		//Vector3f totalLightPos = currentLightPos.mul( movCurrentLightPos);
		lightVmat.identity().setLookAt(currentLightPos, origin, up);	// vector from light to origin
		//moveable light
		//lightVmat.identity().setLookAt(movCurrentLightPos, origin, up);
		
		lightPmat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer[0]);
		gl.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadowTex[0], 0);
	
		gl.glDrawBuffer(GL_NONE);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_POLYGON_OFFSET_FILL);	//  for reducing
		gl.glPolygonOffset(3.0f, 5.0f);		//  shadow artifacts
		//camera
		
		passOne();
		
		gl.glDisable(GL_POLYGON_OFFSET_FILL);	// artifact reduction, continued
		
		gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);
	
		gl.glDrawBuffer(GL_FRONT);
		
		passTwo();
	}
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void passOne()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
		gl.glUseProgram(renderingProgram1);

		// draw the torus
		
		mMat.identity();
		mMat.translate(torusLoc.x(), torusLoc.y(), torusLoc.z());
		mMat.rotateX((float)Math.toRadians(25.0f));
		
		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(mMat);
		sLoc = gl.glGetUniformLocation(renderingProgram1, "shadowMVP");
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);	
	
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[4]);
		gl.glDrawElements(GL_TRIANGLES, numTorusIndices, GL_UNSIGNED_INT, 0);

		// draw the pyramid
		
		mMat.identity();
		mMat.translate(pyrLoc.x(), pyrLoc.y(), pyrLoc.z());
		mMat.rotateX((float)Math.toRadians(30.0f));
		mMat.rotateY((float)Math.toRadians(40.0f));

		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(mMat);

		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		gl.glDrawArrays(GL_TRIANGLES, 0, numPyramidVertices);
		
		//draw the shuttle
		/*
		mMat.identity();
		mMat.translate(shutLoc.x(), shutLoc.y(), shutLoc.z());
		mMat.rotateX((float)Math.toRadians(30.0f));
		mMat.rotateY((float)Math.toRadians(40.0f));

		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(mMat);
		
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		gl.glDrawArrays(GL_TRIANGLES, 0, numObjVertices);
		*/
		
		
	}
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void passTwo()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
		gl.glUseProgram(renderingProgram2);
		
		mvLoc = gl.glGetUniformLocation(renderingProgram2, "mv_matrix");
		projLoc = gl.glGetUniformLocation(renderingProgram2, "proj_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram2, "norm_matrix");
		sLoc = gl.glGetUniformLocation(renderingProgram2, "shadowMVP");

		// draw the torus
			
		thisAmb = BmatAmb; // the torus is bronze
		thisDif = BmatDif;
		thisSpe = BmatSpe;
		thisShi = BmatShi;
		//original
		vMat.identity().setTranslation(-cameraLoc.x(), -cameraLoc.y(), -cameraLoc.z());
		//vMat.identity().setTranslation(camera.getVMat().m03(), camera.getVMat().m13(), camera.getVMat().m23());
		vMat.mul(camera.getVMat());
		currentLightPos.set(lightLoc);
		//movCurrentLightPos.set(movLightLoc);
		
		installLights(renderingProgram2, vMat);

		mMat.identity();
		mMat.translate(torusLoc.x(), torusLoc.y(), torusLoc.z());
		mMat.rotateX((float)Math.toRadians(25.0f));
		
		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		mvMat.invert(invTrMat);
		invTrMat.transpose(invTrMat);
		
		shadowMVP2.identity();
		shadowMVP2.mul(b);
		shadowMVP2.mul(lightPmat);
		shadowMVP2.mul(lightVmat);
		shadowMVP2.mul(mMat);
		
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP2.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);	
	
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[4]);
		gl.glDrawElements(GL_TRIANGLES, numTorusIndices, GL_UNSIGNED_INT, 0);

		// draw the pyramid
		
		thisAmb = GmatAmb; // the pyramid is gold
		thisDif = GmatDif;
		thisSpe = GmatSpe;
		thisShi = GmatShi;
		
		mMat.identity();
		mMat.translate(pyrLoc.x(), pyrLoc.y(), pyrLoc.z());
		mMat.rotateX((float)Math.toRadians(30.0f));
		mMat.rotateY((float)Math.toRadians(40.0f));
		
		currentLightPos.set(lightLoc);
		
		installLights(renderingProgram2, vMat);

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		
		shadowMVP2.identity();
		shadowMVP2.mul(b);
		shadowMVP2.mul(lightPmat);
		shadowMVP2.mul(lightVmat);
		shadowMVP2.mul(mMat);
		
		mvMat.invert(invTrMat);
		invTrMat.transpose(invTrMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP2.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numPyramidVertices);
		
		//draw the shuttle
		/*
		mMat.identity();
		mMat.translate(shutLoc.x(), shutLoc.y(), shutLoc.z());
		mMat.rotateX((float)Math.toRadians(30.0f));
		mMat.rotateY((float)Math.toRadians(40.0f));
		
		currentLightPos.set(lightLoc);
		
		installLights(renderingProgram2, vMat);
		
		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		
		shadowMVP2.identity();
		shadowMVP2.mul(b);
		shadowMVP2.mul(lightPmat);
		shadowMVP2.mul(lightVmat);
		shadowMVP2.mul(mMat);
		
		mvMat.invert(invTrMat);
	    invTrMat.transpose(invTrMat);
	    
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP2.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numObjVertices);
		*/
		
		
	}

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		renderingProgram1 = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a3\\vert1shader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a3\\frag1shader.glsl");
		renderingProgram2 = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a3\\vert2shader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a3\\frag2shader.glsl");

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		setupVertices();
		setupShadowBuffers();
		
		//camera
		camera = new Camera(0.0f, 0.0f, 6.0f);
		//try
		vMat.identity().setTranslation(-cameraLoc.x(), -cameraLoc.y(), -cameraLoc.z());
		
		b.set(
			0.5f, 0.0f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f, 0.0f,
			0.0f, 0.0f, 0.5f, 0.0f,
			0.5f, 0.5f, 0.5f, 1.0f);
	}
	
	private void setupShadowBuffers()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		scSizeX = myCanvas.getWidth();
		scSizeY = myCanvas.getHeight();
	
		gl.glGenFramebuffers(1, shadowBuffer, 0);
	
		gl.glGenTextures(1, shadowTex, 0);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32,
						scSizeX, scSizeY, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		
		// may reduce shadow border artifacts
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}

	private void setupVertices()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
		// pyramid definition
		ImportedModel pyramid = new ImportedModel("../pyr.obj");
		numPyramidVertices = pyramid.getNumVertices();
		
		Vector3f[] vertices = pyramid.getVertices();
		Vector3f[] normals = pyramid.getNormals();
		
		float[] pyramidPvalues = new float[numPyramidVertices*3];
		float[] pyramidNvalues = new float[numPyramidVertices*3];
		
		for (int i=0; i<numPyramidVertices; i++)
		{	pyramidPvalues[i*3]   = (float) (vertices[i]).x();
			pyramidPvalues[i*3+1] = (float) (vertices[i]).y();
			pyramidPvalues[i*3+2] = (float) (vertices[i]).z();
			pyramidNvalues[i*3]   = (float) (normals[i]).x();
			pyramidNvalues[i*3+1] = (float) (normals[i]).y();
			pyramidNvalues[i*3+2] = (float) (normals[i]).z();
		}

		// torus definition
		myTorus = new Torus(0.6f, 0.4f, 96); //def 48
		numTorusVertices = myTorus.getNumVertices();
		numTorusIndices = myTorus.getNumIndices();
		vertices = myTorus.getVertices();
		normals = myTorus.getNormals();
		int[] indices = myTorus.getIndices();
		
		float[] torusPvalues = new float[vertices.length*3];
		float[] torusNvalues = new float[normals.length*3];

		for (int i=0; i<numTorusVertices; i++)
		{	torusPvalues[i*3]   = (float) vertices[i].x();
			torusPvalues[i*3+1] = (float) vertices[i].y();
			torusPvalues[i*3+2] = (float) vertices[i].z();
			torusNvalues[i*3]   = (float) normals[i].x();
			torusNvalues[i*3+1] = (float) normals[i].y();
			torusNvalues[i*3+2] = (float) normals[i].z();
		}
		
		//shuttle definition
		/*
		myModel = new ImportedModel("../shuttle.obj");
		shuttleTexture = Utils.loadTexture("C:\\Users\\Sean Foley\\git\\CS155\\a3\\a3\\src\\spstob_1.jpg");
		numObjVertices = myModel.getNumVertices();
		Vector3f[] sVertices = myModel.getVertices();
		Vector2f[] sTexCoords = myModel.getTexCoords();
		Vector3f[] sNormals = myModel.getNormals();
		
		float[] sPvalues = new float[numObjVertices*3];
		float[] sTvalues = new float[numObjVertices*2];
		float[] sNvalues = new float[numObjVertices*3];
		
		for (int i=0; i<numObjVertices; i++)
		{	sPvalues[i*3]   = (float) (sVertices[i]).x();
			sPvalues[i*3+1] = (float) (sVertices[i]).y();
			sPvalues[i*3+2] = (float) (sVertices[i]).z();
			sTvalues[i*2]   = (float) (sTexCoords[i]).x();
			sTvalues[i*2+1] = (float) (sTexCoords[i]).y();
			sNvalues[i*3]   = (float) (sNormals[i]).x();
			sNvalues[i*3+1] = (float) (sNormals[i]).y();
			sNvalues[i*3+2] = (float) (sNormals[i]).z();
		}
		*/

		// buffers definition
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);

		gl.glGenBuffers(5, vbo, 0);

		//  put the Torus vertices into the first buffer,
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(torusPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);
		
		//  load the pyramid vertices into the second buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer pyrVertBuf = Buffers.newDirectFloatBuffer(pyramidPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, pyrVertBuf.limit()*4, pyrVertBuf, GL_STATIC_DRAW);
		
		// load the torus normal coordinates into the third buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer torusNorBuf = Buffers.newDirectFloatBuffer(torusNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, torusNorBuf.limit()*4, torusNorBuf, GL_STATIC_DRAW);
		
		// load the pyramid normal coordinates into the fourth buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer pyrNorBuf = Buffers.newDirectFloatBuffer(pyramidNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, pyrNorBuf.limit()*4, pyrNorBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[4]);
		IntBuffer idxBuf = Buffers.newDirectIntBuffer(indices);
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxBuf.limit()*4, idxBuf, GL_STATIC_DRAW);
		
		//shuttle norm coords in 5th buffer
		//space shuttle buffers
		//gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		//FloatBuffer sVertBuf = Buffers.newDirectFloatBuffer(sPvalues);
		//gl.glBufferData(GL_ARRAY_BUFFER, sVertBuf.limit()*4, sVertBuf, GL_STATIC_DRAW);

		//gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		//FloatBuffer sTexBuf = Buffers.newDirectFloatBuffer(sTvalues);
		//gl.glBufferData(GL_ARRAY_BUFFER, sTexBuf.limit()*4, sTexBuf, GL_STATIC_DRAW);

		//gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		//FloatBuffer sNorBuf = Buffers.newDirectFloatBuffer(sNvalues);
		//gl.glBufferData(GL_ARRAY_BUFFER, sNorBuf.limit()*4, sNorBuf, GL_STATIC_DRAW);
	}
	
	private void installLights(int renderingProgram, Matrix4f vMatrix)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
		currentLightPos.mulPosition(vMatrix);
		//movCurrentLightPos.mulPosition(vMatrix);
		
		lightPos[0]=currentLightPos.x(); lightPos[1]=currentLightPos.y(); lightPos[2]=currentLightPos.z();
		//movLightPos[0]= movCurrentLightPos.x(); movLightPos[1]= movCurrentLightPos.y(); movLightPos[2]= movCurrentLightPos.z();
		
		
		// set current material values
		matAmb = thisAmb;
		matDif = thisDif;
		matSpe = thisSpe;
		matShi = thisShi;
		
		// get the locations of the light and material fields in the shader
		globalAmbLoc = gl.glGetUniformLocation(renderingProgram, "globalAmbient");
		ambLoc = gl.glGetUniformLocation(renderingProgram, "light.ambient");
		diffLoc = gl.glGetUniformLocation(renderingProgram, "light.diffuse");
		specLoc = gl.glGetUniformLocation(renderingProgram, "light.specular");
		posLoc = gl.glGetUniformLocation(renderingProgram, "light.position");
		mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
		mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
		mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
		mshiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");
	
		//moveable light
		
		//movAmbLoc = gl.glGetUniformLocation(renderingProgram, "mvLight.ambient");
		//movDiffLoc =  gl.glGetUniformLocation(renderingProgram, "mvLight.diffuse");
		//movSpecLoc = gl.glGetUniformLocation(renderingProgram, "mvLight.specular");
		//movPosLoc = gl.glGetUniformLocation(renderingProgram, "mvLight.position");
		
		//  set the uniform light and material values in the shader
		gl.glProgramUniform4fv(renderingProgram, globalAmbLoc, 1, globalAmbient, 0);
		gl.glProgramUniform4fv(renderingProgram, ambLoc, 1, lightAmbient, 0);
		gl.glProgramUniform4fv(renderingProgram, diffLoc, 1, lightDiffuse, 0);
		gl.glProgramUniform4fv(renderingProgram, specLoc, 1, lightSpecular, 0);
		gl.glProgramUniform3fv(renderingProgram, posLoc, 1, lightPos, 0);
		gl.glProgramUniform4fv(renderingProgram, mambLoc, 1, matAmb, 0);
		gl.glProgramUniform4fv(renderingProgram, mdiffLoc, 1, matDif, 0);
		gl.glProgramUniform4fv(renderingProgram, mspecLoc, 1, matSpe, 0);
		gl.glProgramUniform1f(renderingProgram, mshiLoc, matShi);
		//moveable light
		//gl.glProgramUniform4fv(renderingProgram, movAmbLoc, 1, movLightAmbient, 0);
		//gl.glProgramUniform4fv(renderingProgram, movDiffLoc, 1, movLightDiffuse, 0);
		//gl.glProgramUniform4fv(renderingProgram, movSpecLoc, 1, movLightSpecular, 0);
		//gl.glProgramUniform3fv(renderingProgram, movPosLoc, 1, movLightPos, 0);
	
		
	}

	public static void main(String[] args) { new Starter(); }
	public void dispose(GLAutoDrawable drawable) {}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		setupShadowBuffers();
	}

	public void MoveForward() {
		// TODO Auto-generated method stub
		this.camera.moveForward();
		
	}

	public void moveBackward() {
		// TODO Auto-generated method stub
		this.camera.moveBackward();
	}
	
	public void strafeLeft() {
		this.camera.moveLeft();
	}

	public void strafeRight() {
		// TODO Auto-generated method stub
		this.camera.moveRight();
	}

	public void moveUp() {
		// TODO Auto-generated method stub
		this.camera.moveUp();
	}

	public void moveDown() {
		// TODO Auto-generated method stub
		this.camera.moveDown();
	}

	public void panLeft() {
		// TODO Auto-generated method stub
		this.camera.panLeft();
		
	}

	public void panRight() {
		// TODO Auto-generated method stub
		this.camera.panRight();
	}

	public void pitchUp() {
		// TODO Auto-generated method stub
		this.camera.pitchUp();
		
	}

	public void pitchDown() {
		// TODO Auto-generated method stub
		this.camera.pitchDown();
		
	}
	
	//mouse listeners

    public void mouseMoved(MouseEvent e) {

    }

    public void mouseWheelMoved(MouseWheelEvent e) {
    	//to set z light coord
      if (e.getWheelRotation() < 0) {
    	  lightLoc.z = (lightLoc.z + 1.5f);
      }
      else { 
          lightLoc.z = (lightLoc.z - 1.5f);
            
         }
        myCanvas.display();

    }

	@Override
	public void mouseDragged(java.awt.event.MouseEvent arg0) {
		// TODO Auto-generated method stub
    	if (arg0.getX() > this.getWidth() / 2 && arg0.getX() < this.getWidth()) {
    		lightLoc.x = (this.getWidth() / 2 + (arg0.getX() - this.getWidth()));
    		//currentLightPos.x =  (this.getWidth() / 2 + (e.getX() - this.getWidth()));
    	}
    	else if (arg0.getX() > this.getWidth()){
    		lightLoc.x = this.getWidth();
    		//currentLightPos.x = this.getWidth();
    	}
    	
    	else if(arg0.getX() < this.getWidth() /2 ) {
    		lightLoc.x = (arg0.getX() - this.getWidth() / 2);
    		//currentLightPos.x = (e.getX() - this.getWidth() / 2);
    	}
    	else {
    		lightLoc.x = 0;
    		//currentLightPos.x = 0;
    	}
    	
    	//for y
        if (arg0.getY() > this.getHeight() / 2 && arg0.getY() < this.getHeight()) {
            lightLoc.y =(-(this.getHeight() / 2 + (arg0.getY() - this.getHeight())));
        }
        else if (arg0.getY() > this.getHeight()) { 
        	lightLoc.y = (this.getHeight());
        }
        else if (arg0.getY() < this.getHeight() / 2) {
        	lightLoc.y = (-(arg0.getY() - this.getHeight() / 2));
        }
        else{
        	lightLoc.y = 0;
        }
        
        //for z - since this only moves the light on the x/y axis dont alter
        
    	
    	
    	myCanvas.display();
	}

	@Override
	public void mouseMoved(java.awt.event.MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseEvent e) {
		// TODO Auto-generated method stub
	
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		//movLightPos[0]= movCurrentLightPos.x(); movLightPos[1]= movCurrentLightPos.y(); movLightPos[2]= movCurrentLightPos.z();
		/*
    	if (e.getX() > this.getWidth() / 2 && e.getX() < this.getWidth()) {
    		lightLoc.x = (this.getWidth() / 2 + (e.getX() - this.getWidth()));
    		//currentLightPos.x =  (this.getWidth() / 2 + (e.getX() - this.getWidth()));
    	}
    	else if (e.getX() > this.getWidth()){
    		lightLoc.x = this.getWidth();
    		//currentLightPos.x = this.getWidth();
    	}
    	
    	else if(e.getX() < this.getWidth() /2 ) {
    		lightLoc.x = (e.getX() - this.getWidth() / 2);
    		//currentLightPos.x = (e.getX() - this.getWidth() / 2);
    	}
    	else {
    		lightLoc.x = 0;
    		//currentLightPos.x = 0;
    	}
    	//currentLightPos.x = 0;
    	
    	
    	
    	myCanvas.display();
    	*/
		
	}
	
	/*public void displayAxes() {
		// TODO Auto-generated method stub
		if(this.showAxes == true) {
			this.showAxes = false;
		}
		else if(this.showAxes == false) {
			this.showAxes = true;
		}
	}*/
}