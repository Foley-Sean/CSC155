package a4;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
	private float xLoc;
	private float yLoc;
	private float zLoc;
	private float pan = 0;
	private float pitch = 0;
	
	private Matrix4f vMat;
	
	public Camera(float x, float y, float z) {
		this.xLoc = x;
		this.yLoc = y;
		this.zLoc = z;
		
		vMat = new Matrix4f();
		setViewMatrix();
	}
	
	
	public void setViewMatrix() {
		Vector3f camLoc = new Vector3f(xLoc, yLoc, zLoc);
		
		//create u,v,n vecs
		//interactions with pan and pitch
		float cosPitch = (float) Math.cos(Math.toRadians(pitch));
		float sinPitch = (float) Math.sin(Math.toRadians(pitch));
		float cosPan = (float) Math.cos(Math.toRadians(pan));
		float sinPan = (float) Math.sin(Math.toRadians(pan));
		
		Vector3f u = new Vector3f(cosPan, 0, -sinPan);
		Vector3f v = new Vector3f(sinPan * sinPitch, cosPitch, cosPan * sinPitch);
		Vector3f n = new Vector3f(sinPan * cosPitch, -sinPitch, cosPitch * cosPan);
		
		//load uvn into vMat
		float[] vMatVals = new float[] {
				u.x, v.x, n.x, 0,
				u.y, v.y, n.y, 0,
				u.z, v.z, n.z, 0, 
				-((camLoc.x)), -((camLoc.y)), -((camLoc.z)), 1
		};
		//set new vMat
		vMat.set(vMatVals);
		
		
	}
	//get view mat
	public Matrix4f getVMat() {
		return vMat;
	}
	
	//get locations
	public float getX() {
		return xLoc;
	}
	
	public float getY() {
		return yLoc;
	}
	
	public float getZ() {
		return zLoc;
	}
	//movement functions
	
	public void moveForward() {
		this.zLoc -= .1;
		setViewMatrix();
	}
	
	public void moveBackward() {
		this.zLoc += .1;
		setViewMatrix();
	}
	
	public void moveLeft() {
		this.xLoc -= .1;
		setViewMatrix();
	}
	
	public void moveRight() {
		this.xLoc += .1;
		setViewMatrix();
	}
	
	public void moveUp() {
		this.yLoc += .1;
		setViewMatrix();
	}
	
	public void moveDown() {
		this.yLoc -= .1;
		setViewMatrix();
	}
	
	public void panLeft() {
		this.pan += 1;
		setViewMatrix();
	}
	
	public void panRight() {
		this.pan -= 1;
		setViewMatrix();
	}
	
	public void pitchUp() {
		this.pitch += 1;
		setViewMatrix();
	}
	
	public void pitchDown() {
		this.pitch -= 1;
		setViewMatrix();
	}
	
	

}
