package controller;

public interface gestureAPI {
	
	/**
	 * 
	 * @return the list of devices.
	 */
	public String[] getDevices();
	
	/**
	 * 
	 * @return The designated device name. Should default to port name.
	 */
	public String getName(int deviceID);
	
	/**
	 *  
	 * @return The names of states that a touch unit has.
	 */
	
	public String[] getStates(int deviceID);
	
	/**
	 * 
	 * @return Return the state number 
	 */
	public int getState(int deviceID);
	
	/**
	 * 
	 * @return a nonnegative float with the distance from the memorized state.
	 */
	public float getDelta(int deviceID);
	
}

