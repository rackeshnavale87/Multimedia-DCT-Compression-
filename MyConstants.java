package pkg;

/**
 * @author Rakesh Navale.
 * @Introduction 
 * This Class defines the variables used for the Assignment, extend this class to make use of these variables.
 * 
*/
public abstract class MyConstants 
{
	 /*
	  * @DeliveryMode – an index ranging from 1, 2, 3
	  * */
	 int deliveryMode = -1;
	 String sourceName = "image1.rgb";
	 int[][][] sourceM;

	 /*
	  * quantazLevel and latency input
	  * */
	 int quantazLevel=0;
	 long latency=-1;
	 final int chans=3;
	 
	 /*
	  * Default fixed sized image
	  * */
	 final int width=352;
	 final int height=288;
	 
	 /*
	  * ￼￼Before DCT, each channel of image Before DCT, each channel of image
	  * */
	 final int scale=8;
	 final int blockLength=scale*scale;
	 
	 //final int halfMatrix=36;
	 /*
	  * Variables and matricies to be used while doing DCT transformation 
	  * 
	  */
	 final int scaledWidth=width/scale;
	 final int scaledHeight=height/scale;
	 int[][][] DCT_M;
	 double[][][][] cosValue;
	 
	 /*
	  * Output image after the DCT transformation
	  * */
	 int[][][] outputM;
	
}
