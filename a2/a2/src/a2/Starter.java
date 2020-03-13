package a2;

import java.nio.*;
import java.lang.Math;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLContext;
import org.joml.*;

public class Starter extends JFrame implements GLEventListener
{	private GLCanvas myCanvas;
	private double startTime = 0.0;
	private double elapsedTime;
	private int renderingProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[17];
	private float cameraX, cameraY, cameraZ;
	
	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(17);
	private Matrix4fStack mvStack = new Matrix4fStack(8);
	private Matrix4f pMat = new Matrix4f();
	private Matrix4f vMat = new Matrix4f();  // view matrix
	private Matrix4f mMat = new Matrix4f();  // model matrix
	private Matrix4f mvMat = new Matrix4f(); // model-view matrix
	
	private int mvLoc, projLoc;
	private float aspect;
	private double tf;
	//sphere for sun
	private float sunLocX, sunLocY, sunLocZ;
	private Sphere mySun;
	private int numSphereVerts;
	//sphere for Earth
	private float earthLocX, earthLocY, earthLocZ;
	private Sphere myEarth;
	//sphere for Moon
	private float moonLocX, moonLocY, moonLocZ;
	private Sphere myMoon;
	//imported shuttle object
	private int numObjVertices;
	private ImportedModel myModel;
	private int numDolphinObjVertices;
	private ImportedModel myDolphin;
	//textures
	private int earthTexture;
	private int sunTexture;
	private int moonTexture;
	private int pyrTexture;
	private int shuttleTexture;
	private int dolphinTexture;
	
	public Starter()
	{	setTitle("Assignment 2");
		setSize(600, 600);
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
		gl.glClearColor(0.0f, 0.0f, 0.25f, 1.0f);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		elapsedTime = System.currentTimeMillis() - startTime;

		gl.glUseProgram(renderingProgram);

		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		projLoc = gl.glGetUniformLocation(renderingProgram, "proj_matrix");

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
		gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));

		// push view matrix onto the stack
		mvStack.pushMatrix();
		mvStack.translate(-cameraX, -cameraY, -cameraZ);
		
		tf = elapsedTime/1000.0;  // time factor

		//sun as a sphere//
		mvStack.pushMatrix();
		mvStack.translate(sunLocX, sunLocY, sunLocZ);
		mvStack.pushMatrix();
		mvStack.rotate((float)tf * 0.2f, 0.0f, 1.0f, 0.0f);
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, sunTexture);
		
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);
		mvStack.popMatrix();
		
		
		//Earth, orbits the sun//
		mvStack.pushMatrix();
		mvStack.translate((float)Math.sin(tf)*4.0f, 0.0f, (float)Math.cos(tf)*4.0f);
		mvStack.pushMatrix();
		mvStack.rotate((float)tf * 1.3f, 0.0f, 1.0f, 0.0f);
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, earthTexture);
		
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);
		mvStack.popMatrix();
		
		//Moon, orbits the Earth which orbits the sun
		mvStack.pushMatrix();
		mvStack.translate(0.0f, (float)Math.sin(tf*0.5f)*2.0f, (float)Math.cos(tf*0.5f)*2.0f);
		mvStack.pushMatrix();
		mvStack.rotate((float)tf*1.5f, 0.0f, 1.0f, 0.0f);
		//mvStack.pushMatrix();
		mvStack.scale(0.25f, 0.25f, 0.25f);
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, moonTexture);
		
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDrawArrays(GL_TRIANGLES, 0, numSphereVerts);
		mvStack.popMatrix(); //mvStack.popMatrix();
		
		//shuttle, orbits Earth's moon
		mvStack.pushMatrix();
		mvStack.translate((float)Math.sin(tf*1.5f)*0.33f, (float)Math.sin(tf*1.5f)*0.33f, (float)Math.cos(tf*1.5f)*0.33f);
		mvStack.rotate((float)tf*0.1f, 0.0f, 1.0f, 0.0f);
		mvStack.scale(0.25f, 0.25f, 0.25f);
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, shuttleTexture);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glFrontFace(GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, myModel.getNumVertices());
		mvStack.popMatrix();
		mvStack.popMatrix();
		
		//double pyramid, orbits the sun
		mvStack.pushMatrix();
		mvStack.translate((float)Math.sin(tf*0.2f)*6.0f, 0.0f, (float)Math.cos(tf*0.2f)*6.0f);
		mvStack.pushMatrix();
		mvStack.rotate((float)tf*2.0f, 0.0f, 1.0f, 0.0f);
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, pyrTexture);
		
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
		mvStack.popMatrix();
		
		//Dolphin (lol) orbits the weird planet I made
		mvStack.pushMatrix();
		mvStack.translate((float)Math.sin(tf*1.8f)*1.5f, (float)Math.sin(tf*1.8f), (float)Math.cos(tf*1.8f)*1.5f);
		mvStack.rotate((float)tf*2.0f, 0.0f, 1.0f, 0.0f);
		mvStack.scale(0.8f, 0.8f, 0.8f);
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvStack.get(vals));
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[15]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, dolphinTexture);

		gl.glEnable(GL_DEPTH_TEST);
		gl.glFrontFace(GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, myDolphin.getNumVertices());
		mvStack.popMatrix();
		
		
		
		mvStack.popMatrix(); mvStack.popMatrix(); mvStack.popMatrix(); mvStack.popMatrix(); //mvStack.popMatrix();
			
		
	}

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		startTime = System.currentTimeMillis();
		myModel = new ImportedModel("\\shuttle.obj");
		myDolphin = new ImportedModel("\\dolphinHighPoly.obj");
		renderingProgram = Utils.createShaderProgram("C:\\Users\\Sean Foley\\git\\CS155\\a2\\a2\\src\\a2\\vertShader.glsl", "C:\\Users\\Sean Foley\\git\\CS155\\a2\\a2\\src\\a2\\fragShader.glsl");
		
		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		setupVertices();
		
		cameraX = 0.0f; cameraY = 0.0f; cameraZ = 12.0f;
		sunLocX = 0.0f; sunLocY = 0.0f; sunLocZ = -1.0f;
		
		sunTexture = Utils.loadTexture("C:\\Users\\Sean Foley\\git\\CS155\\a2\\a2\\src\\a2\\2k_sun.jpg");
		earthTexture = Utils.loadTexture("C:\\Users\\Sean Foley\\git\\CS155\\a2\\a2\\src\\a2\\earth_night.jpg");
		moonTexture = Utils.loadTexture("C:\\Users\\Sean Foley\\git\\CS155\\a2\\a2\\src\\a2\\2k_moon.jpg");
		pyrTexture = Utils.loadTexture("C:\\Users\\Sean Foley\\git\\CS155\\a2\\a2\\src\\a2\\awesome_texture.jpg");
		shuttleTexture = Utils.loadTexture("C:\\Users\\Sean Foley\\git\\CS155\\a2\\a2\\src\\a2\\spstob_1.jpg");
		dolphinTexture = Utils.loadTexture("C:\\Users\\Sean Foley\\git\\CS155\\a2\\a2\\src\\a2\\Dolphin_HighPolyUV.png");
	}

	private void setupVertices()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
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
		
		float[] doublePyramidPositions =
		{	
				//top pyramid
			/*	
			-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,    //front
			-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, -1.0f, 0.0f,
			
			1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,    //right
			1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, -1.0f, 0.0f,
			
			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,  //back
			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, -1.0f, 0.0f,
			
			-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,  //left
			-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, -1.0f, 0.0f,
			
			-1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, //LF
			-1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f,
			
			1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,  //RR
			1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f
			*/
				
			0.0f,0.0f,0.5f,     1.0f,0.5f,0.5f,       1.0f,-0.5f,0.5f,
			0.0f,0.0f,0.5f,    -1.0f,0.5f,0.5f,      -1.0f,-0.5f,0.5f,  //front facing

			0.0f,0.0f,-0.5f,   -1.0f,-0.5f,-0.5f,    -1.0f,0.5f,-0.5f,
			0.0f,0.0f,-0.5f,    1.0f,-0.5f,-0.5f,     1.0f,0.5f,-0.5f,   //rear facing

			-1.0f,-0.5f,0.5f,  -1.0f,-0.5f,-0.5f,    1.0f,-0.5f,-0.5f,
			-1.0f,-0.5f,0.5f,   1.0f,-0.5f,-0.5f,    1.0f,-0.5f,0.5f,   //bottom

			-1.0f,0.5f,0.5f,   -1.0f,0.5f,-0.5f,     1.0f,0.5f,0.5f,
			-1.0f,0.5f-0.5f,    1.0f,0.5f,0.5f,     1.0f,0.5f,-0.5f,    //top


			-1.0f,0.5f,0.5f,   -1.0f,0.5f,-0.5f,     -1.0f,0.0f,0.0f,
			-1.0f,-0.5f,-0.5f, -1.0f,-0.5f,0.5f,     -1.0f,0.0f,0.0f,      //left side

			1.0f,0.5f,0.5f,    1.0f,0.5f,-0.5f,      1.0f,0.0f,0.0f,
			1.0f,-0.5f,0.5f,   1.0f,-0.5f,-0.5f,     1.0f,0.0f,0.0f     // right side	
				
				

		};
		
		//spheres
		mySun = new Sphere(96);
		myEarth = new Sphere(96);
		myMoon = new Sphere(96);
		
		//use the sun as a standard for all spheres to get indices
		numSphereVerts = mySun.getIndices().length;
		
		int[] indices = mySun.getIndices();
		Vector3f[] vert = mySun.getVertices();
		Vector2f[] tex  = mySun.getTexCoords();
		Vector3f[] norm = mySun.getNormals();
		
		float[] pvalues = new float[indices.length*3];
		float[] tvalues = new float[indices.length*2];
		float[] nvalues = new float[indices.length*3];
		
		for (int i=0; i<indices.length; i++)
		{	pvalues[i*3] = (float) (vert[indices[i]]).x;
			pvalues[i*3+1] = (float) (vert[indices[i]]).y;
			pvalues[i*3+2] = (float) (vert[indices[i]]).z;
			tvalues[i*2] = (float) (tex[indices[i]]).x;
			tvalues[i*2+1] = (float) (tex[indices[i]]).y;
			nvalues[i*3] = (float) (norm[indices[i]]).x;
			nvalues[i*3+1]= (float)(norm[indices[i]]).y;
			nvalues[i*3+2]=(float) (norm[indices[i]]).z;
		}
		
		//imported space shuttle 
		numObjVertices = myModel.getNumVertices();
		Vector3f[] vertices = myModel.getVertices();
		Vector2f[] texCoords = myModel.getTexCoords();
		Vector3f[] normals = myModel.getNormals();
		
		float[] sPvalues = new float[numObjVertices*3];
		float[] sTvalues = new float[numObjVertices*2];
		float[] sNvalues = new float[numObjVertices*3];
		
		for (int i=0; i<numObjVertices; i++)
		{	sPvalues[i*3]   = (float) (vertices[i]).x();
			sPvalues[i*3+1] = (float) (vertices[i]).y();
			sPvalues[i*3+2] = (float) (vertices[i]).z();
			sTvalues[i*2]   = (float) (texCoords[i]).x();
			sTvalues[i*2+1] = (float) (texCoords[i]).y();
			sNvalues[i*3]   = (float) (normals[i]).x();
			sNvalues[i*3+1] = (float) (normals[i]).y();
			sNvalues[i*3+2] = (float) (normals[i]).z();
		}
		
		//imported dolphin
		numDolphinObjVertices = myDolphin.getNumVertices();
		Vector3f[] dVertices = myDolphin.getVertices();
		Vector2f[] dTexCoords = myDolphin.getTexCoords();
		Vector3f[] dNormals = myDolphin.getNormals();
		
		float[] dPvalues = new float[numDolphinObjVertices*3];
		float[] dTvalues = new float[numDolphinObjVertices*2];
		float[] dNvalues = new float[numDolphinObjVertices*3];
		
		for (int i=0; i<numDolphinObjVertices; i++)
		{	dPvalues[i*3]   = (float) (dVertices[i]).x();
			dPvalues[i*3+1] = (float) (dVertices[i]).y();
			dPvalues[i*3+2] = (float) (dVertices[i]).z();
			dTvalues[i*2]   = (float) (dTexCoords[i]).x();
			dTvalues[i*2+1] = (float) (dTexCoords[i]).y();
			dNvalues[i*3]   = (float) (dNormals[i]).x();
			dNvalues[i*3+1] = (float) (dNormals[i]).y();
			dNvalues[i*3+2] = (float) (dNormals[i]).z();
		}
		
		
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);
		
		//Sun buffers
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer norBuf = Buffers.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit()*4,norBuf, GL_STATIC_DRAW);
		
		//Earth buffers
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer earthVertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, earthVertBuf.limit()*4, earthVertBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer earthTexBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, earthTexBuf.limit()*4, earthTexBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer earthNorBuf = Buffers.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, earthNorBuf.limit()*4, earthNorBuf, GL_STATIC_DRAW);
		
		//Moon buffers
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer	moonVertBuf = Buffers.newDirectFloatBuffer(pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, moonVertBuf.limit()*4, moonVertBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		FloatBuffer moonTexBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, moonTexBuf.limit()*4, moonTexBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		FloatBuffer moonNorBuf = Buffers.newDirectFloatBuffer(nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, moonNorBuf.limit()*4, moonNorBuf, GL_STATIC_DRAW);
		
		//double pyramid buffers
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(doublePyramidPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, pyrBuf.limit()*4, pyrBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		FloatBuffer pyrTexBuf = Buffers.newDirectFloatBuffer(tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, pyrTexBuf.limit()*4, pyrTexBuf, GL_STATIC_DRAW);
		
		//space shuttle buffers
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		FloatBuffer sVertBuf = Buffers.newDirectFloatBuffer(sPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, sVertBuf.limit()*4, sVertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		FloatBuffer sTexBuf = Buffers.newDirectFloatBuffer(sTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, sTexBuf.limit()*4, sTexBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
		FloatBuffer sNorBuf = Buffers.newDirectFloatBuffer(sNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, sNorBuf.limit()*4, sNorBuf, GL_STATIC_DRAW);
		
		//dolphin buffers
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
		FloatBuffer dVertBuf = Buffers.newDirectFloatBuffer(dPvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, dVertBuf.limit()*4, dVertBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[15]);
		FloatBuffer dTexBuf = Buffers.newDirectFloatBuffer(dTvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, dTexBuf.limit()*4, dTexBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[16]);
		FloatBuffer dNorBuf = Buffers.newDirectFloatBuffer(dNvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, dNorBuf.limit()*4, dNorBuf, GL_STATIC_DRAW);
		
	}

	public static void main(String[] args) { new Starter(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.identity().setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
	}
	public void dispose(GLAutoDrawable drawable) {}
}
