package controller;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.serial.*;

public class SerialLink{
	PApplet parent;
	int SerialPortNumber=2;
	int portSelected=0;

	/* Global variables */

	int xValue, yValue, Command; 
	boolean error=true;

	boolean UpdateGraph=true;
	int lineGraph; 
	int errorCounter=0;
	int totalReceived=0; 

	/* Local variables */
	boolean DataReceived1=false, DataReceived2=false, DataReceived3=false;

	ArrayList<Float> DynamicArrayTime1, DynamicArrayTime2, DynamicArrayTime3;
	ArrayList<Float> Time1, Time2, Time3; 
	ArrayList<Float> Voltage1, Voltage2, Voltage3;
	ArrayList<Float> current;
	ArrayList<Float> DynamicArray1, DynamicArray2, DynamicArray3;

	ArrayList<Float> PowerArray= new ArrayList<Float>();            // Dynamic arrays that will use the append()
	ArrayList<Float> DynamicArrayPower = new ArrayList<Float>();    // function to add values
	ArrayList<Float> DynamicArrayTime= new ArrayList<Float>();


	/* Serial Init */
	String portName; 
	String[] ArrayOfPorts=new String[SerialPortNumber]; 

	boolean DataReceived=false, Data1Received=false, Data2Received=false;
	int incrament=0;

	/* Comm Layer Init */

	int NumOfSerialBytes=8;                              // The size of the buffer array
	int[] serialInArray = new int[NumOfSerialBytes];     // Buffer array
	int serialCount = 0;                                 // A count of how many bytes received
	int xMSB, xLSB, yMSB, yLSB;		                // Bytes of data

	Serial myPort;        // The serial port object

	/* Constructor */
	
	public SerialLink(PApplet parent, int port) {
		this.parent = parent;
		portSelected = port;
		serialPortSetup();
	}

	
	/**
	 * A once off serail port setup function. In this case the 
	 * selection of the speed, the serial port and clearing the serial
	 * port buffer.  
	 */
	void serialPortSetup() {

		portName= Serial.list()[portSelected];
		ArrayOfPorts=Serial.list();
		parent.println(ArrayOfPorts);
		myPort = new Serial(parent, portName, 115200);

		try {
			wait(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		myPort.clear(); 
		myPort.buffer(20);
	}


	/**
	 * serialEvent will be called when something is sent to the 
	 * serial port being used.
	 * @param myPort
	 */
	void serialEvent(Serial myPort) {

		while (myPort.available ()>0)
		{
			/*  Read the next byte that's waiting in the buffer. */

			int inByte = myPort.read();

			if (inByte==0) {
				serialCount=0;
			};
			if (inByte>255) {
				parent.println(" inByte = "+inByte);    
				parent.exit();
			}

			// Add the latest byte from the serial port to array:

			serialInArray[serialCount] = inByte;
			serialCount++;

			error=true;
			if (serialCount >= NumOfSerialBytes ) {
				serialCount = 0;

				totalReceived++;
				// Checksum 
				int Checksum=0;
				//    Checksum = (Command + yMSB + yLSB + xMSB + xLSB + zeroByte)%255;
				for (int x=0; x<serialInArray.length-1; x++) {
					Checksum=Checksum+serialInArray[x];
				}

				Checksum=Checksum%255;
				// Checksum function
				if (Checksum==serialInArray[serialInArray.length-1]) {
					error = false;
					DataReceived=true;
				}
				else {
					error = true;
					DataReceived=false;
					errorCounter++;
					parent.println("Error:  "+ errorCounter +" / "+ totalReceived+" : "
							+(float) (errorCounter/totalReceived)*100+"%");
				}
			}

			if (!error) {


				int zeroByte = serialInArray[6];

				xLSB = serialInArray[3];
				if ( (zeroByte & 1) == 1) xLSB=0;

				xMSB = serialInArray[2];      
				if ( (zeroByte & 2) == 2) xMSB=0;

				yLSB = serialInArray[5];
				if ( (zeroByte & 4) == 4) yLSB=0;

				yMSB = serialInArray[4];
				if ( (zeroByte & 8) == 8) yMSB=0;


				// combine bytes to form large integers

				Command  = serialInArray[1];

				xValue   = xMSB << 8 | xLSB;                    // Get xValue from yMSB & yLSB  
				yValue   = yMSB << 8 | yLSB;                    // Get yValue from xMSB & xLSB

				/*
			How that works: if xMSB = 10001001   and xLSB = 0100 0011 
	       xMSB << 8 = 10001001 00000000    (shift xMSB left by 8 bits)                       
	       xLSB =          01000011    
	       xLSB | xMSB = 10001001 01000011    combine the 2 bytes using the logic or |
	       xValue = 10001001 01000011     now xValue is a 2 byte number 0 -> 65536  
				 */


				/**
				 * Command, xValue & yValue have now been received from the chip
				 */
				switch(Command) {

				//Receive array1 and array2 from chip, update oscilloscope

				case 1: // Data is added to dynamic arrays
					DynamicArrayTime3.add((float)xValue);
					DynamicArray3.add((float)yValue);

					break;

				case 2: // An array of unknown size is about to be received, empty storage arrays
					DynamicArrayTime3.clear(); 
					DynamicArray3.clear(); 
					break;    

				case 3:  // Array has finished being received, update arrays being drawn 
					Time3=DynamicArrayTime3;
					Voltage3=DynamicArray3;
					DataReceived3=true;
					break;  


					/* Receive array2 and array3 from chip */


				case 4: // Data is added to dynamic arrays
					DynamicArrayTime2.add((float)xValue);
					DynamicArray2.add((yValue-16000f)/32000f*20f); //TODO Enable double
					break;

				case 5: // An array of unknown size is about to be received, empty storage arrays
					DynamicArrayTime2.clear(); 
					DynamicArray2.clear(); 
					break;    

				case 6:  // Array has finished being received, update arrays being drawn 
					Time2=DynamicArrayTime2;
					current=DynamicArray2;
					DataReceived2=true;
					break;  

					/* Receive a value of calculated power consumption & add it to the 
	         PowerArray. */
				case 20:  
					PowerArray.add((float) yValue);
					break; 

				case 21:  
					DynamicArrayTime.add((float) xValue); 
					DynamicArrayPower.add((float) yValue);

					break;
				}
			}
		}
		parent.redraw();  
	}


}
