package controller;

import java.util.ArrayList;

import processing.core.PApplet;

public class GraphMain extends PApplet implements gestureAPI {

	Graph MyArduinoGraph = new Graph(150, 80, 500, 300, color (200, 20, 20));
	float[] gestureOne=null;
	float[] gestureTwo = null;
	float[] gestureThree = null;

	float[][] gesturePoints = new float[4][2];
	float[] gestureDist = new float[4];
	String[] names = {"Nothing", "Leaf 1", "Leaf 2","Branch"};

	SerialLink link;


	public void setup() {


		size(1000, 500); 
		MyArduinoGraph.xLabel="Readnumber";
		MyArduinoGraph.yLabel="Amp";
		MyArduinoGraph.Title=" Graph";  
		noLoop();
		int portSelected=2;      
		/* ====================================================================
	   adjust this (0,1,2...) until the correct port is selected 
	   In my case 2 for COM4, after I look at the Serial.list() string 
	   println( Serial.list() );
	   [0] "COM1"  
	   [1] "COM2" 
	   [2] "COM4"
	   ==================================================================== */
		link = new SerialLink(this, portSelected);     
	}


	public void draw() {

		background(255);

		/* Print the graph */

		if ( link.DataReceived3 ) {
			float[] t3 = toPrimitiveFloatArray(link.Time3);
			pushMatrix();
			pushStyle();
			MyArduinoGraph.yMax=1000;      
			MyArduinoGraph.yMin=-200;      
			MyArduinoGraph.xMax=(int) (max(t3));
			MyArduinoGraph.DrawAxis();    
			MyArduinoGraph.smoothLine(t3, toPrimitiveFloatArray(link.Voltage3));
			popStyle();
			popMatrix();

			float gestureOneDiff =0;
			float gestureTwoDiff =0;
			float gestureThreeDiff =0;

			/* Gesture compare */
			float totalDist = 0;
			int currentMax = 0;
			float currentMaxValue = -1;
			for (int i = 0; i < 4;i++) {

				if (mousePressed && mouseX > 750 && mouseX<800 && mouseY > 100*(i+1) && mouseY < 100*(i+1) + 50)
				{
					fill(255, 0, 0);

					gesturePoints[i][0] = link.Time3.get(MyArduinoGraph.maxI);
					gesturePoints[i][1] = link.Voltage3.get(MyArduinoGraph.maxI);
				} else {
					fill(255, 255, 255);
				}

				//calucalte individual dist
				gestureDist[i] = dist(
						link.Time3.get(MyArduinoGraph.maxI),
						link.Voltage3.get(MyArduinoGraph.maxI), 
						gesturePoints[i][0], 
						gesturePoints[i][1]);
				totalDist = totalDist + gestureDist[i];
				if(gestureDist[i] < currentMaxValue || i == 0) {
					currentMax = i;
					currentMaxValue =  gestureDist[i];
				}
			}
			totalDist=totalDist /3;

			for (int i = 0; i < 4;i++){
				float currentAmmount = 0;
				currentAmmount = 1-gestureDist[i]/totalDist;
				if(currentMax == i){
					fill(currentAmmount*255.0f, 0, 0);
				} else {
					fill(255,255,255);
				}
				stroke(0, 0, 0);
				rect(750, 100 * (i+1), 50, 50);
				fill(0,0,0);
				textSize(30);
				text(names[i],810,100 * (i+1)+25);
				fill(255, 0, 0);
			}


		}
	}
	/**
	 * Helper function because of Java's type system.
	 * @param a The ArrayList to be converted
	 * @return float[]
	 */
	private float[] toPrimitiveFloatArray(ArrayList<Float> a){
		Float[] tempArray = a.toArray(new Float[0]);
		int l = tempArray.length;
		float[] permArray = new float[l];
		for (int i = 0; i < l ; i++){
			Float f = tempArray[i];
			permArray[i] = (f!=null ? f : 0f); //Safety when converting, Float can be null.
		}
		return permArray;
	}

	public void stop()
	{

		link.myPort.stop();
		super.stop();
	}
	
	/* gestureAPI */


	@Override
	public String[] getDevices() {
		// TODO Change for multiple devices
		String[] s = {"default"}; 
		return s;
	}


	@Override
	public String getName(int deviceID) {
		// TODO Change for multiple devices
		return "default";
	}


	@Override
	public String[] getStates(int deviceID) {
		// TODO Change for multiple devices
		return names;
	}


	@Override
	public int getState(int deviceID) {
		// TODO Change for multiple devices
		return 0;
	}


	@Override
	public float getDelta(int deviceID) {
		// TODO Change for multiple devices
		return 0;
	}


}
