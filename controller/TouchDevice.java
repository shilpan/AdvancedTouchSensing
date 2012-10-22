package controller;

import java.util.ArrayList;

public class TouchDevice {
	
	int id;
	int signalLength = 0; //TODO find a way to init 
	public ArrayList<float[]> gestures;
	public ArrayList<String> gestureNames;
	
	public TouchDevice(int id){
		this.id = id;
		gestures = new ArrayList<float[]>();
		gestureNames = new ArrayList<String>();
	}
	
	
	/**
	 * Fast argmax
	 * @param array
	 * @return
	 */
	private int argMax(float[] array){
		int argMax = 0;
		float maxValue = (array.length > 0 ? array[0] : 0);
		for (int i = 0 ; i < array.length ; i++ ){
			if (array[i]>maxValue){
				maxValue = array[i];
				argMax = i;
			}
		}
		return argMax;
		}
	
	public boolean addGesture(float[] gesture, String name){
		if (gesture.length==signalLength){
			gestures.add(gesture);
			gestureNames.add(name);
			return true;
		} else return false;
	};
	
	private float calculateDistance(float[] a, float[] b){
		//TODO finish
		return 0;
	}
}
