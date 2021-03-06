package a4;

import java.nio.*;
import javax.swing.*;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.Math;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
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
	private int renderingProgram1, renderingProgram2, axesRenderingProgram, 
	textureRenderingProgram, skyboxRenderingProgram, envRenderingProgram,
	geoRenderingProgram, texture3dRenderingProgram, bumpRenderingProgram, tessRenderingProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[24];

	// model stuff
	private ImportedModel pyramid;
	private Torus myTorus;
	private Torus myBumpTorus;
	private Sphere mySphere;
	private int numPyramidVertices, numTorusVertices,
	numTorusIndices, numSphereVerts, numBumpTorusVerts, numBumpTorusIndices;
	//shuttle model
	private int numObjVertices;
	private ImportedModel myModel;
	private int shuttleTexture;
	//dolphin model
	private ImportedModel dolphin;
	private int numDolVertices;
	private int dolphinTexture;
	private int num3dDolVertices;
	//skybox
	private int skyboxTexture;
	//3d texture
	private int stripeTexture;
	private int texHeight= 200;
	private int texWidth = 200;
	private int texDepth = 200;
	private double[][][] tex3Dpattern = new double[texHeight][texWidth][texDepth];
	//moon surface
	private int squareMoonTexture;
	private int squareMoonHeight;
	private int squareMoonNormalMap;
	
	// location of torus, shuttle pyramid, light, sphere, dolphins, terrain and camera
	private Vector3f torusLoc = new Vector3f(1.6f, 4.0f, -0.3f);
	private Vector3f pyrLoc = new Vector3f(-1.0f, 0.1f, 0.3f);
	private Vector3f shutLoc = new Vector3f( -3.0f, 0.4f, 0.6f);
	private Vector3f dolphinLoc = new Vector3f(4.0f, 0.6f, 1.2f);
	private Vector3f cameraLoc = new Vector3f(0.0f, 0.2f, 6.0f);
	private Vector3f lightLoc = new Vector3f(-3.8f, 2.2f, 1.1f);
	private Vector3f sphereLoc = new Vector3f(-5.0f, -2.0f, -1.0f);
	private Vector3f dolphin3dLoc = new Vector3f(4.5f, 0.6f, 1.2f);
	private Vector3f bumpTorusLoc = new Vector3f(-1.6f, 4.0f, -0.3f);
	private Vector3f terLoc = new Vector3f(0.0f, -30.0f, 0.0f);
	//tess
	private float tessInner = 30.0f;
	private float tessOuter = 20.0f;
	private Matrix4f mvpMat = new Matrix4f();
	private int mvpLoc;
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
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(32); //16 
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
	private boolean showAxes;
	
	public Starter()
	{	setTitle("Sean Foley - Assignment 4");
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
		//skyBoxPass();
		passOne();
		
		gl.glDisable(GL_POLYGON_OFFSET_FILL);	// artifact reduction, continued
		
		gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);
	
		//draw skybox
		skyBoxPass();
		gl.glDrawBuffer(GL_FRONT);
		
		passTwo();
	}
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void passOne()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
		gl.glUseProgram(renderingProgram1);

		// draw the torus
		//fix light matrix as we go
		lightVmat.identity().setLookAt(currentLightPos, origin, up);	// vector from light to origin
		lightPmat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
		
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
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); //def 3
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
		//sLoc = gl.glGetUniformLocation(renderingProgram1, "shadowMVP");
		
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		gl.glDrawArrays(GL_TRIANGLES, 0, numPyramidVertices);
		
		//draw the sphere
		gl.glUseProgram(geoRenderingProgram);
		mMat.identity();
		mMat.translate(sphereLoc.x(), sphereLoc.y(), sphereLoc.z());
		mMat.rotateX((float)Math.toRadians(30.0f));
		mMat.rotateY((float)Math.toRadians(40.0f));

		//shadowMVP1.identity();
		//shadowMVP1.mul(lightPmat);
		//shadowMVP1.mul(lightVmat);
		//shadowMVP1.mul(mMat);
		//gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);
		
		//draw the shuttle
		gl.glUseProgram(renderingProgram1);
		mMat.identity();
		mMat.translate(shutLoc.x(), shutLoc.y(), shutLoc.z());
		mMat.rotateX((float)Math.toRadians(30.0f));
		mMat.rotateY((float)Math.toRadians(40.0f));
		
		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(mMat);
		
		//sLoc = gl.glGetUniformLocation(renderingProgram1, "shadowMVP");
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		gl.glDrawArrays(GL_TRIANGLES, 0, numObjVertices);
			
		//draw the dolphin
		mMat.identity();
		mMat.translate(dolphinLoc.x(), dolphinLoc.y(), dolphinLoc.z());
		
		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(mMat);
		
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		gl.glDrawArrays(GL_TRIANGLES, 0, numDolVertices);
		
		//draw 3d dolphin
		gl.glUseProgram(texture3dRenderingProgram);
		mMat.identity();
		mMat.translate(dolphin3dLoc.x(), dolphin3dLoc.y(), dolphin3dLoc.z());
		
		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(mMat);
		
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[17]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		gl.glDrawArrays(GL_TRIANGLES, 0, num3dDolVertices);
		
		//draw bump mapped torus
		gl.glUseProgram(bumpRenderingProgram);
		mMat.identity();
		mMat.translate(bumpTorusLoc.x(), bumpTorusLoc.y(), bumpTorusLoc.z());
		
		shadowMVP1.identity();
		shadowMVP1.mul(lightPmat);
		shadowMVP1.mul(lightVmat);
		shadowMVP1.mul(mMat);
		//sLoc = gl.glGetUniformLocation(bumpRenderingProgram, "shadowMVP");
		gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP1.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[20]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);	
	
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[23]);
		gl.glDrawElements(GL_TRIANGLES, numBumpTorusIndices, GL_UNSIGNED_INT, 0);
		
		//draw moon texture?
		
		
	}
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void passTwo()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	

		gl.glUseProgram(envRenderingProgram);
		mvLoc = gl.glGetUniformLocation(envRenderingProgram, "mv_matrix");
		projLoc = gl.glGetUniformLocation(envRenderingProgram, "proj_matrix");
		nLoc = gl.glGetUniformLocation(envRenderingProgram, "norm_matrix");
		sLoc = gl.glGetUniformLocation(envRenderingProgram, "shadowMVP");

		// draw the torus
		//installTexture(envRenderingProgram, vMat);
		//thisAmb = BmatAmb; // the torus is bronze
		//thisDif = BmatDif;
	    //thisSpe = BmatSpe;
		//thisShi = BmatShi;
		vMat.identity().setTranslation(-cameraLoc.x(), -cameraLoc.y(), -cameraLoc.z());
		
		vMat.mul(camera.getVMat());
		
		currentLightPos.set(lightLoc);
		
		installLights(envRenderingProgram, vMat);
		
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

		//vertex
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		//normals
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_CUBE_MAP, skyboxTexture);
	
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		//indices
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glDrawElements(GL_TRIANGLES, numTorusIndices, GL_UNSIGNED_INT, 0);

		// draw the pyramid
		
		gl.glUseProgram(renderingProgram2);
		mvLoc = gl.glGetUniformLocation(renderingProgram2, "mv_matrix");
		projLoc = gl.glGetUniformLocation(renderingProgram2, "proj_matrix");
		nLoc = gl.glGetUniformLocation(renderingProgram2, "norm_matrix");
		sLoc = gl.glGetUniformLocation(renderingProgram2, "shadowMVP");
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
		
		//draw the sphere for geometry shader
		gl.glUseProgram(geoRenderingProgram);
		//gl.glUseProgram(renderingProgram2);
		mvLoc = gl.glGetUniformLocation(geoRenderingProgram, "mv_matrix");
		projLoc = gl.glGetUniformLocation(geoRenderingProgram, "proj_matrix");
		nLoc = gl.glGetUniformLocation(geoRenderingProgram, "norm_matrix");
		//sLoc = gl.glGetUniformLocation(geoRenderingProgram, "shadowMVP");
		thisAmb = GmatAmb; // the sphere is gold
		thisDif = GmatDif;
		thisSpe = GmatSpe;
		thisShi = GmatShi;
		
		mMat.identity();
		mMat.translate(sphereLoc.x(), sphereLoc.y(), sphereLoc.z());
		mMat.rotateX((float)Math.toRadians(30.0f));
		mMat.rotateY((float)Math.toRadians(40.0f));
		
		currentLightPos.set(lightLoc);
		
		installLights(geoRenderingProgram, vMat);

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		
		//shadowMVP2.identity();
		//shadowMVP2.mul(b);
		//shadowMVP2.mul(lightPmat);
		//shadowMVP2.mul(lightVmat);
		//shadowMVP2.mul(mMat);
		
		mvMat.invert(invTrMat);
		invTrMat.transpose(invTrMat);

		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		//gl.glUniformMatrix4fv(sLoc, 1, false, shadowMVP2.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[16]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);
		
		//draw the shuttle
		//thisAmb = GmatAmb; // the shuttle is gold
		//thisDif = GmatDif;
		//thisSpe = GmatSpe;
		//thisShi = .5f;
		
		mMat.identity();
		mMat.translate(shutLoc.x(), shutLoc.y(), shutLoc.z());
		mMat.rotateX((float)Math.toRadians(30.0f));
		mMat.rotateY((float)Math.toRadians(40.0f));
		
		currentLightPos.set(lightLoc);
		
		installTexture(textureRenderingProgram, vMat);

		gl.glUseProgram(textureRenderingProgram);
		
		mvLoc = gl.glGetUniformLocation(textureRenderingProgram, "mv_matrix");
		projLoc = gl.glGetUniformLocation(textureRenderingProgram, "proj_matrix");
		nLoc = gl.glGetUniformLocation(textureRenderingProgram, "norm_matrix");
		sLoc = gl.glGetUniformLocation(textureRenderingProgram, "shadowMVP");
		
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
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);
		
		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_2D, shuttleTexture);
		
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numObjVertices);
		
		//draw the dolphin
		mMat.identity();
		mMat.translate(dolphinLoc.x(), dolphinLoc.y(), dolphinLoc.z());

		currentLightPos.set(lightLoc);
		
		installTexture(textureRenderingProgram, vMat);

		gl.glUseProgram(textureRenderingProgram);
		
		mvLoc = gl.glGetUniformLocation(textureRenderingProgram, "mv_matrix");
		projLoc = gl.glGetUniformLocation(textureRenderingProgram, "proj_matrix");
		nLoc = gl.glGetUniformLocation(textureRenderingProgram, "norm_matrix");
		sLoc = gl.glGetUniformLocation(textureRenderingProgram, "shadowMVP");
		
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
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		gl.glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);
		
		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_2D, dolphinTexture);

		
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, numDolVertices);
		
		//Draw 3d texture Dolphin
		mMat.identity();
		mMat.translate(dolphin3dLoc.x(), dolphin3dLoc.y(), dolphin3dLoc.z());

		currentLightPos.set(lightLoc);
		
		installTexture(texture3dRenderingProgram, vMat);

		gl.glUseProgram(texture3dRenderingProgram);
		
		mvLoc = gl.glGetUniformLocation(texture3dRenderingProgram, "mv_matrix");
		projLoc = gl.glGetUniformLocation(texture3dRenderingProgram, "proj_matrix");
		nLoc = gl.glGetUniformLocation(texture3dRenderingProgram, "norm_matrix");;
		
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
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[17]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		//gl.glUseProgram(skyboxRenderingProgram);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_3D, stripeTexture);

		
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glDrawArrays(GL_TRIANGLES, 0, num3dDolVertices);
		
        //draw bump mapped torus
		thisAmb = BmatAmb; // the torus is bronze
		thisDif = BmatDif;
	    thisSpe = BmatSpe;
		thisShi = BmatShi;
		
		gl.glUseProgram(bumpRenderingProgram);

		mvLoc = gl.glGetUniformLocation(bumpRenderingProgram, "mv_matrix");
		projLoc = gl.glGetUniformLocation(bumpRenderingProgram, "proj_matrix");
		nLoc = gl.glGetUniformLocation(bumpRenderingProgram, "norm_matrix");
		
		currentLightPos.set(lightLoc);
		
		installLights(bumpRenderingProgram, vMat);
		
		mMat.identity();
		mMat.translate(bumpTorusLoc.x(), bumpTorusLoc.y(), bumpTorusLoc.z());
		
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

		//vertex
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[20]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		//normals
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[21]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
	
		//gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
	
		//indices
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[23]);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glDrawElements(GL_TRIANGLES, numTorusIndices, GL_UNSIGNED_INT, 0);
		
		//draw moon surface
		gl.glUseProgram(tessRenderingProgram);
		mvpLoc = gl.glGetUniformLocation(tessRenderingProgram, "mvp");
		mvLoc = gl.glGetUniformLocation(tessRenderingProgram, "mv_matrix");
		projLoc = gl.glGetUniformLocation(tessRenderingProgram, "proj_matrix");
		nLoc = gl.glGetUniformLocation(tessRenderingProgram, "norm_matrix");
		
		thisAmb = Utils.silverAmbient(); // the moon is "silver'
		thisDif = Utils.silverDiffuse();
	    thisSpe = Utils.silverSpecular();
		thisShi = Utils.silverShininess();
		
		mMat.identity().setTranslation(terLoc.x(), terLoc.y(), terLoc.z());
		mMat.scale(200.0f);
		mvpMat.identity();
		mvpMat.mul(pMat);
		mvpMat.mul(vMat);
		mvpMat.mul(mMat);
		
		shadowMVP2.identity();
		shadowMVP2.mul(b);
		shadowMVP2.mul(lightPmat);
		shadowMVP2.mul(lightVmat);
		shadowMVP2.mul(mMat);
		
		mvMat.invert(invTrMat);
		invTrMat.transpose(invTrMat);
		
		//mMat.scale(20.0f);
		
		currentLightPos.set(lightLoc);		
		installLights(tessRenderingProgram,vMat);
		
		gl.glUniformMatrix4fv(mvpLoc, 1, false, mvpMat.get(vals));
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, squareMoonTexture);
		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_2D, squareMoonHeight);
		gl.glActiveTexture(GL_TEXTURE2);
		gl.glBindTexture(GL_TEXTURE_2D, squareMoonNormalMap);
	
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CW);

		gl.glPatchParameteri(GL_PATCH_VERTICES, 4);
		gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		gl.glDrawArraysInstanced(GL_PATCHES, 0, 4, 64*64);
		
		
		//draw light location
     	gl.glUseProgram(texture3dRenderingProgram);
		mvLoc = gl.glGetUniformLocation(texture3dRenderingProgram, "mv_matrix");
		projLoc = gl.glGetUniformLocation(texture3dRenderingProgram, "proj_matrix");
		nLoc = gl.glGetUniformLocation(texture3dRenderingProgram, "norm_matrix");
		
		currentLightPos.set(lightLoc);
		gl.glPointSize(12);
		mMat.identity();
		mMat.translate(lightLoc.x(), lightLoc.y(), lightLoc.z());
		
		installLights(texture3dRenderingProgram, vMat);

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		
		mvMat.invert(invTrMat);
		invTrMat.transpose(invTrMat);
		
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		
        gl.glDrawArrays(GL_POINTS, 0, 1);
        gl.glPointSize(1);
		
        //draw axes
        if(this.showAxes) {
			gl.glUseProgram(axesRenderingProgram);
			
			mvLoc = gl.glGetUniformLocation(axesRenderingProgram, "mv_matrix");
			projLoc = gl.glGetUniformLocation(axesRenderingProgram, "proj_matrix");
			nLoc = gl.glGetUniformLocation(axesRenderingProgram, "norm_matrix");
			sLoc = gl.glGetUniformLocation(axesRenderingProgram, "shadowMVP");
			
			mvMat.identity();
			mvMat.mul(vMat);
			mvMat.mul(mMat);
			
			mvMat.invert(invTrMat);
			invTrMat.transpose(invTrMat);
			
			gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
			gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
			gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
			gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
			
			gl.glDrawArrays(GL_LINES, 0, 12);
		}
        //System.out.println(showAxes);
	}
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private void skyBoxPass() {
		
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		gl.glUseProgram(skyboxRenderingProgram);
		
		mvLoc = gl.glGetUniformLocation(skyboxRenderingProgram, "v_matrix");
		projLoc = gl.glGetUniformLocation(skyboxRenderingProgram, "proj_matrix");
		//vMat =  gl.glGetUniformLocation(skyboxRenderingProgram, "v_matrix");
		mvMat.identity();
		mMat.translate(-cameraLoc.x(), -cameraLoc.y(), -cameraLoc.z());
		
		mvMat.mul(vMat);
		mvMat.mul(mMat);
		
		mvMat.invert(invTrMat);
		invTrMat.transpose(invTrMat);
		
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glActiveTexture(GL_TEXTURE1);
		gl.glBindTexture(GL_TEXTURE_CUBE_MAP, skyboxTexture);

		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);	     // cube is CW, but we are viewing the inside
		gl.glDisable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
		gl.glEnable(GL_DEPTH_TEST);
		
		
		
	}

	private void installTexture(int renderingProgram, Matrix4f vMatrix) {
	// TODO Auto-generated method stub
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		currentLightPos.mulPosition(vMatrix);
		//movCurrentLightPos.mulPosition(vMatrix);
		
		lightPos[0]=currentLightPos.x(); lightPos[1]=currentLightPos.y(); lightPos[2]=currentLightPos.z();
		//movLightPos[0]= movCurrentLightPos.x(); movLightPos[1]= movCurrentLightPos.y(); movLightPos[2]= movCurrentLightPos.z();
		
		
		// set current material values
		matAmb = thisAmb;
		matDif = thisDif;
		matSpe = thisSpe;
		matShi = .5f;
		
		// get the locations of the light and material fields in the shader
		globalAmbLoc = gl.glGetUniformLocation(renderingProgram, "globalAmbient");
		ambLoc = gl.glGetUniformLocation(renderingProgram, "light.ambient");
		diffLoc = gl.glGetUniformLocation(renderingProgram, "light.diffuse");
		specLoc = gl.glGetUniformLocation(renderingProgram, "light.specular");
		posLoc = gl.glGetUniformLocation(renderingProgram, "light.position");
		//mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
		//mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
		//mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
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

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		renderingProgram1 = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\vert1shader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\frag1shader.glsl");
		renderingProgram2 = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\vert2shader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\frag2shader.glsl");
		axesRenderingProgram = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\axes_vertshader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\axes_fragshader.glsl");
		textureRenderingProgram = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\texVertShader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\texFragshader.glsl");
		skyboxRenderingProgram = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\skyboxVertShader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\skyboxFragShader.glsl ");
		envRenderingProgram = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\emVertShader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\emFragShader.glsl");
		geoRenderingProgram = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\vertShader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\geomShader.glsl","C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\fragShader.glsl");
		texture3dRenderingProgram = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\3dVertShader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\3dFragShader.glsl");
		bumpRenderingProgram = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\bumpVertShader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\bumpFragShader.glsl");
		tessRenderingProgram = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\tVertShader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\tessCShader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\tessEShader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\a4\\tFragShader.glsl");
		//shuttle texture
		shuttleTexture = Utils.loadTexture("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\spstob_1.jpg");
		dolphinTexture = Utils.loadTexture("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\Dolphin_HighPolyUV.png");
		
		//moon textures
		squareMoonTexture = Utils.loadTexture("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\squareMoonMap.jpg");
		squareMoonHeight = Utils.loadTexture("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\squareMoonBump.jpg");
		squareMoonNormalMap = Utils.loadTexture("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\squareMoonNormal.jpg");
		
		//3d texture
		generate3Dpattern();	
		stripeTexture = load3DTexture();
		//skybox
		skyboxTexture = Utils.loadCubeMap("C:\\Users\\Sean Foley\\git\\CS155\\a3\\src\\cubeMap");
		gl.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
		
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
		Vector2f[] torusTexCoords = myTorus.getTexCoords();
		
		vertices = myTorus.getVertices();
		normals = myTorus.getNormals();
		int[] indices = myTorus.getIndices();
		
		float[] torusPvalues = new float[vertices.length*3];
		float[] torusNvalues = new float[normals.length*3];
		float[] torusTvalues = new float[torusTexCoords.length*2];

		for (int i=0; i<numTorusVertices; i++)
		{	torusPvalues[i*3]   = (float) vertices[i].x();
			torusPvalues[i*3+1] = (float) vertices[i].y();
			torusPvalues[i*3+2] = (float) vertices[i].z();
			torusTvalues[i*2]   = (float) torusTexCoords[i].x();
			torusTvalues[i*2+1] = (float) torusTexCoords[i].y();
			torusNvalues[i*3]   = (float) normals[i].x();
			torusNvalues[i*3+1] = (float) normals[i].y();
			torusNvalues[i*3+2] = (float) normals[i].z();
		}
		
		//bump mapped torus definition
		myBumpTorus = new Torus(0.6f, 0.4f, 96); //def 48
		numBumpTorusVerts = myBumpTorus.getNumVertices();
		numBumpTorusIndices = myBumpTorus.getNumIndices();
		Vector2f[] bumpTorusTexCoords = myBumpTorus.getTexCoords();
		
		vertices = myBumpTorus.getVertices();
		normals = myBumpTorus.getNormals();
		int[] bumpIndices = myBumpTorus.getIndices();
		
		float[] bumpTorusPvalues = new float[vertices.length*3];
		float[] bumpTorusNvalues = new float[normals.length*3];
		float[] bumpTorusTvalues = new float[bumpTorusTexCoords.length*2];

		for (int i=0; i<numBumpTorusVerts; i++)
		{	bumpTorusPvalues[i*3]   = (float) vertices[i].x();
			bumpTorusPvalues[i*3+1] = (float) vertices[i].y();
			bumpTorusPvalues[i*3+2] = (float) vertices[i].z();
			bumpTorusTvalues[i*2]   = (float) torusTexCoords[i].x();
			bumpTorusTvalues[i*2+1] = (float) torusTexCoords[i].y();
			bumpTorusNvalues[i*3]   = (float) normals[i].x();
			bumpTorusNvalues[i*3+1] = (float) normals[i].y();
			bumpTorusNvalues[i*3+2] = (float) normals[i].z();
		}
		
		//sphere definition
		mySphere = new Sphere(96);
		numSphereVerts = mySphere.getIndices().length;
	
		int[] sphereIndices = mySphere.getIndices();
		Vector3f[] vert = mySphere.getVertices();
		Vector2f[] tex  = mySphere.getTexCoords();
		Vector3f[] norm = mySphere.getNormals();
		
		float[] pvalues = new float[sphereIndices.length*3];
		float[] tvalues = new float[sphereIndices.length*2];
		float[] nvalues = new float[sphereIndices.length*3];
		
		for (int i=0; i<sphereIndices.length; i++)
		{	pvalues[i*3] = (float) (vert[sphereIndices[i]]).x;
			pvalues[i*3+1] = (float) (vert[sphereIndices[i]]).y;
			pvalues[i*3+2] = (float) (vert[sphereIndices[i]]).z;
			tvalues[i*2] = (float) (tex[sphereIndices[i]]).x;
			tvalues[i*2+1] = (float) (tex[sphereIndices[i]]).y;
			nvalues[i*3] = (float) (norm[sphereIndices[i]]).x;
			nvalues[i*3+1]= (float)(norm[sphereIndices[i]]).y;
			nvalues[i*3+2]=(float) (norm[sphereIndices[i]]).z;
		}
		
		//shuttle definition
		ImportedModel myModel = new ImportedModel("../shuttle.obj");
		numObjVertices = myModel.getNumVertices();
		
		Vector2f[] texCoords = myModel.getTexCoords();
		vertices = myModel.getVertices();
		normals = myModel.getNormals();
		
		float[] shuttlePvalues = new float[numObjVertices*3];
		float[] shuttleNvalues = new float[numObjVertices*3];
		float[] shuttleTvalues = new float[numObjVertices*2];
		
		for (int i=0; i<numObjVertices; i++)
		{	shuttlePvalues[i*3]   = (float) (vertices[i]).x();
			shuttlePvalues[i*3+1] = (float) (vertices[i]).y();
			shuttlePvalues[i*3+2] = (float) (vertices[i]).z();
			shuttleTvalues[i*2]   = (float) (texCoords[i]).x();
			shuttleTvalues[i*2+1] = (float) (texCoords[i]).y();
			shuttleNvalues[i*3]   = (float) (normals[i]).x();
			shuttleNvalues[i*3+1] = (float) (normals[i]).y();
			shuttleNvalues[i*3+2] = (float) (normals[i]).z();
		}
		
		//dolphin
		ImportedModel myDolphin = new ImportedModel("../dolphinHighPoly.obj");
		numDolVertices = myDolphin.getNumVertices();
		
		Vector2f[] dTexCoords = myDolphin.getTexCoords();
		vertices = myDolphin.getVertices();
		normals = myDolphin.getNormals();
		
		float[] dolphinPvalues = new float[numDolVertices*3];
		float[] dolphinNvalues = new float[numDolVertices*3];
		float[] dolphinTvalues = new float[numDolVertices*2];
		
		for (int i=0; i<numDolVertices; i++)
		{	dolphinPvalues[i*3]   = (float) (vertices[i]).x();
			dolphinPvalues[i*3+1] = (float) (vertices[i]).y();
			dolphinPvalues[i*3+2] = (float) (vertices[i]).z();
			dolphinTvalues[i*2]   = (float) (dTexCoords[i]).x();
			dolphinTvalues[i*2+1] = (float) (dTexCoords[i]).y();
			dolphinNvalues[i*3]   = (float) (normals[i]).x();
			dolphinNvalues[i*3+1] = (float) (normals[i]).y();
			dolphinNvalues[i*3+2] = (float) (normals[i]).z();
		}
		
		//3d text dolphin
		ImportedModel my3dDolphin = new ImportedModel("../dolphinHighPoly.obj");
		num3dDolVertices = my3dDolphin.getNumVertices();
		
		Vector2f[] texCoords3d = my3dDolphin.getTexCoords();
		Vector3f[] vertices3d = my3dDolphin.getVertices();
		Vector3f[] normals3d = my3dDolphin.getNormals();
		
		float[] dolphinPvalues3d = new float[num3dDolVertices*3];
		float[] dolphinNvalues3d = new float[num3dDolVertices*3];
		float[] dolphinTvalues3d = new float[num3dDolVertices*2];
		
		for (int i=0; i<numDolVertices; i++)
		{	dolphinPvalues3d[i*3]   = (float) (vertices3d[i]).x();
			dolphinPvalues3d[i*3+1] = (float) (vertices3d[i]).y();
			dolphinPvalues3d[i*3+2] = (float) (vertices3d[i]).z();
			dolphinTvalues3d[i*2]   = (float) (texCoords3d[i]).x();
			dolphinTvalues3d[i*2+1] = (float) (texCoords3d[i]).y();
			dolphinNvalues3d[i*3]   = (float) (normals3d[i]).x();
			dolphinNvalues3d[i*3+1] = (float) (normals3d[i]).y();
			dolphinNvalues3d[i*3+2] = (float) (normals3d[i]).z();
		}
		
		//light as a cube
		float[] cubePositions =
		{	-1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, 1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, 1.0f, -1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,
			-1.0f,  1.0f, -1.0f, 1.0f,  1.0f, -1.0f, 1.0f,  1.0f,  1.0f,
			1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f
		};
		
		//skybox as a cube
		float[] cubeVertexPositions =
			{	-1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
				1.0f, -1.0f, -1.0f, 1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f,
				1.0f, -1.0f, -1.0f, 1.0f, -1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
				1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
				1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
				-1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
				-1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
				-1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
				-1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,
				1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,
				-1.0f,  1.0f, -1.0f, 1.0f,  1.0f, -1.0f, 1.0f,  1.0f,  1.0f,
				1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f
			};
		
		
		// buffers definition
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);

		gl.glGenBuffers(24, vbo, 0);

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
		
		//torus t values

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
		FloatBuffer torTexBuf = Buffers.newDirectFloatBuffer(torusTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, torTexBuf.limit()*4, torTexBuf, GL_STATIC_DRAW);
		
		// load the pyramid normal coordinates into the fourth buffer
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer pyrNorBuf = Buffers.newDirectFloatBuffer(pyramidNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, pyrNorBuf.limit()*4, pyrNorBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[4]);
		IntBuffer idxBuf = Buffers.newDirectIntBuffer(indices);
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxBuf.limit()*4, idxBuf, GL_STATIC_DRAW);
		
		//shuttle
		//vertices
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer shutVertBuf = Buffers.newDirectFloatBuffer(shuttlePvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, shutVertBuf.limit()*4, shutVertBuf, GL_STATIC_DRAW);
		
		//n coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer shutNorBuf = Buffers.newDirectFloatBuffer(shuttleNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, shutNorBuf.limit()*4, shutNorBuf, GL_STATIC_DRAW);
		
		//texture coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		FloatBuffer sTexBuf = Buffers.newDirectFloatBuffer(shuttleTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, sTexBuf.limit()*4, sTexBuf, GL_STATIC_DRAW);
		
		//light
		//vertices (unused for now)
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		FloatBuffer cubeBuf = Buffers.newDirectFloatBuffer(cubePositions);
		gl.glBufferData(GL_ARRAY_BUFFER, cubeBuf.limit()*4, cubeBuf, GL_STATIC_DRAW);
		
		//dolphin
		//vertices
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		FloatBuffer dolVertBuf = Buffers.newDirectFloatBuffer(dolphinPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, dolVertBuf.limit()*4, dolVertBuf, GL_STATIC_DRAW);
		
		//n coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		FloatBuffer dolNorBuf = Buffers.newDirectFloatBuffer(dolphinNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, dolNorBuf.limit()*4, dolNorBuf, GL_STATIC_DRAW);
		
		//texture coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		FloatBuffer dTexBuf = Buffers.newDirectFloatBuffer(dolphinTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, dTexBuf.limit()*4, dTexBuf, GL_STATIC_DRAW);
		
		//skybox
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		FloatBuffer cvertBuf = Buffers.newDirectFloatBuffer(cubeVertexPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, cvertBuf.limit()*4, cvertBuf, GL_STATIC_DRAW);
		
		//sphere
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
		FloatBuffer spVertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, spVertBuf.limit()*4, spVertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[15]);
		FloatBuffer spTexBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, spTexBuf.limit()*4, spTexBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[16]);
		FloatBuffer spNorBuf = Buffers.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, spNorBuf.limit()*4,spNorBuf, GL_STATIC_DRAW);
		
		//3d dolphin
		//vertices
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[17]);
		FloatBuffer dol3dVertBuf = Buffers.newDirectFloatBuffer(dolphinPvalues3d);
		gl.glBufferData(GL_ARRAY_BUFFER, dol3dVertBuf.limit()*4, dol3dVertBuf, GL_STATIC_DRAW);
		
		//n coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[18]);
		FloatBuffer dol3dNorBuf = Buffers.newDirectFloatBuffer(dolphinNvalues3d);
		gl.glBufferData(GL_ARRAY_BUFFER, dol3dNorBuf.limit()*4, dol3dNorBuf, GL_STATIC_DRAW);
		
		//texture coords
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[19]);
		FloatBuffer dT3dexBuf = Buffers.newDirectFloatBuffer(dolphinTvalues3d);
		gl.glBufferData(GL_ARRAY_BUFFER, dT3dexBuf.limit()*4, dT3dexBuf, GL_STATIC_DRAW);
		
		//bump mapped torus
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[20]);
		FloatBuffer bumpVertBuf = Buffers.newDirectFloatBuffer(bumpTorusPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, bumpVertBuf.limit()*4, bumpVertBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[21]);
		FloatBuffer bumpTorusNorBuf = Buffers.newDirectFloatBuffer(bumpTorusNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, bumpTorusNorBuf.limit()*4, bumpTorusNorBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[22]);
		FloatBuffer bumpTorTexBuf = Buffers.newDirectFloatBuffer(bumpTorusTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, bumpTorTexBuf.limit()*4, bumpTorTexBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[23]);
		IntBuffer bumpIdxBuf = Buffers.newDirectIntBuffer(bumpIndices);
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, bumpIdxBuf.limit()*4, bumpIdxBuf, GL_STATIC_DRAW);
		
		}
	
	private void fillDataArray(byte data[])
	{ for (int i=0; i<texWidth; i++)
	  { for (int j=0; j<texHeight; j++)
	    { for (int k=0; k<texDepth; k++)
	      {
		if (tex3Dpattern[i][j][k] == 1.0)
		{	// yellow color
			data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+0] = (byte) 255; //red
			data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+1] = (byte) 255; //green
			data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+2] = (byte) 0;   //blue
			data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+3] = (byte) 0;   //alpha
		}
		else
		{	// blue color
			data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+0] = (byte) 0;   //red
			data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+1] = (byte) 0;   //green
			data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+2] = (byte) 255; //blue
			data[i*(texWidth*texHeight*4)+j*(texHeight*4)+k*4+3] = (byte) 0;   //alpha
		}
	} } } }
	
	private int load3DTexture()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
	
		byte[] data = new byte[texWidth*texHeight*texDepth*4];
		
		fillDataArray(data);

		ByteBuffer bb = Buffers.newDirectByteBuffer(data);

		int[] textureIDs = new int[1];
		gl.glGenTextures(1, textureIDs, 0);
		int textureID = textureIDs[0];

		gl.glBindTexture(GL_TEXTURE_3D, textureID);

		gl.glTexStorage3D(GL_TEXTURE_3D, 1, GL_RGBA8, texWidth, texHeight, texDepth);
		gl.glTexSubImage3D(GL_TEXTURE_3D, 0, 0, 0, 0,
				texWidth, texHeight, texDepth, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, bb);
		
		gl.glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		return textureID;
	}
	/*
	void generate3Dpattern()
	{	for (int x=0; x<texWidth; x++)
		{	for (int y=0; y<texHeight; y++)
			{	for (int z=0; z<texDepth; z++)
				{	if ((y/10)%2 == 0)
						tex3Dpattern[x][y][z] = 0.0;
					else
						tex3Dpattern[x][y][z] = 1.0;
	}	}	}	}
	*/
	//  replace above function with the one below
	//	to change the stripes to a checkerboard.
	
	void generate3Dpattern()
	{	int xStep, yStep, zStep, sumSteps;
		for (int x=0; x<texWidth; x++)
		{	for (int y=0; y<texHeight; y++)
			{	for (int z=0; z<texDepth; z++)
				{	xStep = (x / 10) % 2;
					yStep = (y / 10) % 2;
					zStep = (z / 10) % 2;
					sumSteps = xStep + yStep + zStep;
					if ((sumSteps % 2) == 0)
						tex3Dpattern[x][y][z] = 0.0;
					else
						tex3Dpattern[x][y][z] = 1.0;
	}	}	}	}
	
	
	
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
    	  lightLoc.z = (lightLoc.z + 0.5f);
      }
      else { 
          lightLoc.z = (lightLoc.z - 0.5f);
            
         }
        myCanvas.display();

    }

	@Override
	public void mouseDragged(java.awt.event.MouseEvent arg0) {
		// TODO Auto-generated method stub
		float zLoc = lightLoc.z;
    	if (arg0.getX() > this.getWidth() / 2 && arg0.getX() < this.getWidth()) {
    		lightLoc.x = (this.getWidth() / 2 + (arg0.getX() - this.getWidth()));
    		//currentLightPos.x =  (this.getWidth() / 2 + (e.getX() - this.getWidth()));
    	}
    	else if (arg0.getX() > this.getWidth()){
    		lightLoc.x = this.getWidth();
    		//currentLightPos.x = this.getWidth();
    	}
    	
    	else if(arg0.getX() < this.getWidth() / 2 ) {
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
        //must preserve
        lightLoc.z = zLoc;
        //the light zooms really fast so to scale back
        //drastically divide location
        lightLoc.mul(.02f, .02f, .02f);
    	
    	
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

	}

	public void displayAxes() {
		// TODO Auto-generated method stub
		if(this.showAxes == true) {
			this.showAxes = false;
		}
		else if(this.showAxes == false) {
			this.showAxes = true;
		}
	}
	
}