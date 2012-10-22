package controller;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.serial.Serial;

public class GraphMain extends PApplet implements gestureAPI {
	
	ArrayList<SerialLink> devices = new ArrayList<SerialLink>();
	SerialLink link;
	
	Graph MyArduinoGraph = new Graph(150, 80, 500, 300, color (200, 20, 20));
	float[] gestureOne=null; //TODO remove when done with multi
	float[] gestureTwo = null;
	float[] gestureThree = null;

	float[][] gesturePoints = new float[4][2]; //TODO finish multi and remove
	float[] gestureDist = new float[4]; //TODO finish multi and remove
	String[] names = {"Nothing", "Leaf 1", "Leaf 2","Branch"}; //TODO finish multi and remove


	public void setup() {


		size(1000, 500); 
		MyArduinoGraph.xLabel="Readnumber";
		MyArduinoGraph.yLabel="Amp";
		MyArduinoGraph.Title=" Graph";  
		noLoop();
		
		int portSelected=2;      
		/* ====================================================================
		 * adjust this (0,1,2...) until the correct port is selected 
		 * In my case 2 for COM4, after I look at the Serial.list() string 
		 * println( Serial.list() );
		 * [0] "COM1"  
		 * [1] "COM2" 
		 * [2] "COM4"
		 * ==================================================================== */
		println(Serial.list());
		link = new SerialLink(this, portSelected); //TODO integrate into multi    
	}


	public void draw() {

		background(255);

		/* Print the graph */
		//TODO add for loop and do this for every input
		if ( link.DataReceived3 ) {
			float[] t3 = toPrimitiveFloatArray(link.Time3);
			pushMatrix();
			pushStyle();
			MyArduinoGraph.yMax=1000; //TODO totally arbitrary values. What's the point      
			MyArduinoGraph.yMin=-200;      
			MyArduinoGraph.xMax=(int) (max(t3));
			MyArduinoGraph.DrawAxis();    
			MyArduinoGraph.smoothLine(t3, toPrimitiveFloatArray(link.Voltage3));
			popStyle();
			popMatrix();

			/* Gesture compare */
			float totalDist = 0;
			int currentMax = 0;
			float currentMaxValue = -1;
			for (int i = 0; i < 4;i++) {

				if (mousePressed && mouseX > 750 && mouseX<800 && mouseY > 100*(i+1) && mouseY < 100*(i+1) + 50)
				{
					fill(255, 0, 0);
					//TODO Change so that maximum is measured in this class.
					gesturePoints[i][0] = link.Time3.get(MyArduinoGraph.maxI);
					gesturePoints[i][1] = link.Voltage3.get(MyArduinoGraph.maxI);
				} else {
					fill(255, 255, 255);
				}

				//calucalte individual dist
				gestureDist[i] = dist( //TODO move into public function in class TouchDevice
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

	public void stop()
	{

		link.myPort.stop();
		super.stop();
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
	
	/**
	 * Should initiate a scan of the device list, ignoring ignorable
	 * ports. Scan is by name, new devices are registered.
	 */
	private void refreshDeviceList(){
		//TODO: Implement
	}
	
	/* gestureAPI */

	@Override
	public String[] getDevices() {
		// TODO Change for multiple devices
		String[] s = {"default"}; 
		return s;
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
