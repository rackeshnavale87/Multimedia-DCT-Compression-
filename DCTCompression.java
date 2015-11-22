package pkg;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * @author Rakesh Navale.
 * @Introduction
 * Here you will be attempting to understand the working of DCT and how it is used by standard compression algorithms like JPEG and MPEG. 
 * Specifically, you will implement a DCT based coder-decoder for compressing an image and simulate decoding using the baseline mode as well as 
 * progressive modes of data delivery. You program will take as input 4 parameters and be invoked as myProgram InputImage quantizationLevel 
 * DeliveryMode Latency where the parameters are defined as : 
 * InputImage – is the image to input to your coder-decoder (you may assume a fixed size and format that will be described on the class website) 
 * QuantizationLevel – a factor that will decrease/increase compression as explained below. This value will range from 0 to 7.
 * DeliveryMode – an index ranging from 1, 2, 3. A 1 implies baseline delivery, a 2 implies progressive delivery using spectral selection, a 3 implies progressive delivery using successive bit approximation.
 * Latency – a variable in milliseconds, which will give a suggestive “sleep” time between data blocks during decoding. This parameter will be used to “simulate” low and high band width decoding to properly evaluate the simulation of your delivery modes.
 *
 */
public class DCTCompression extends MyConstants
{
	/**
	 * @param
	 *  @InputImage – is the image to input to your coder-decoder (you may assume a fixed size and format that will be described on the class website)
	 * 	@QuantizationLevel – a factor that will decrease/increase compression as explained below. This value will range from 0 to 7
	 * 	@DeliveryMode – an index ranging from 1, 2, 3. 
	 * 		1 implies baseline delivery,
	 * 		2 implies progressive delivery using spectral selection,
	 * 		3 implies progressive delivery using successive bit approximation.
	 * 	@Latency – a variable in milliseconds, chan will give a suggestive “sleep” time between data blocks during decoding. 
	 * 	This parameter will be used to “simulate” low and high band width decoding to properly evaluate the simulation of your delivery modes.
	 *  e.g. 1. myProgram Foreman.rgb 0 1 0
	 *	Here you are encoding Foreman.rgb and using a 0 quantization level, chan means no quantization. 
	 *	You are using the baseline mode and there is no latency so you should see the whole output image almost instantaneously
	 * S
	 * @return
	 */
	private boolean readArgs(String[] args)
	{
		//System.out.println(args.length);
		if (args.length >= 4) 
		{
		    try 
		    {
		    	sourceName=args[0];
		    	quantazLevel =1 << Integer.parseInt(args[1]);
		    	if(quantazLevel<1 || quantazLevel>128)
		    	{
		    		System.out.println("Error! invalid Quantization level!");
		    		return false;
		    	}
		    	deliveryMode=Integer.parseInt(args[2]);
		    	if(deliveryMode<1 || deliveryMode>3)
		    	{
		    		System.out.println("Error! invalid Delivery mode!");
		    		return false;
		    	}
		    	latency=Integer.parseInt(args[3]);
		    	if(latency<0)
		    	{
		    		System.out.print("Error! invalid Latency!");
		    		return false;
		    	}
		    	return true;
		    }
		    catch (NumberFormatException e) 
		    {
		        System.err.println("Error! Argument" + " must be an integer");
		        return false;
		    }
		}
		System.out.println("Error! invalid program args!");
		return false;
	}
	
	/**
	 * Read source image which will be used for DCT transformation
	 * @return boolean
	 */
	private boolean readImageSrc()
	{
		BufferedImage sourceImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    try 
	    {
		    File file = new File(sourceName);
		    @SuppressWarnings("resource")
			InputStream inStream = new FileInputStream(file);
		    long length = file.length();
		    byte[] bytes = new byte[(int)length];
		    int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length && (numRead=inStream.read(bytes, offset, bytes.length-offset)) >= 0) 
	        {
	            offset += numRead;
	        }	
	    	int ind = 0;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					byte r,g,b;
					r = bytes[ind];
					sourceM[0][y][x]=r&0xff;
					g = bytes[ind+height*width];
					sourceM[1][y][x]= g&0xff;
					b = bytes[ind+height*width*2]; 
					sourceM[2][y][x]=b&0xff; 
					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					sourceImage.setRGB(x,y,pix);
					ind++;
				}
			}
			File outputfile = new File("source.png");
		    ImageIO.write(sourceImage, "png", outputfile);
	    }
	    catch (FileNotFoundException e)
	    {
	    	e.printStackTrace();
	    	return false;
	    }
	    catch (IOException e)
	    {
	    	e.printStackTrace();
	    	return false;
	    }
	    return true;
	}
	
	/**
	 * Discrete Cosine Transform function used for the DCT encoding and decoding of input image. Important foctors are channesl, 
	 * scaled height & width, scale (8x8 in this case) 
	 * 
	 */
	private void DiscreteCosineTransform()
	{
		int chan = 0, y = 0, x = 0, v = 0, u = 0, j = 0;
	    for(chan=0; chan<chans; chan++)
	    {
	    	for(y=0; y<scaledHeight; y++)
	    	{
	    		for(x=0; x<scaledWidth; x++)
	    		{
	    			for(v=0; v<scale; v++)
	    			{
	    				for(u=0 ;u<scale; u++)
	    				{
	    					double summaryDCT = 0;
	    					double c = 0;
	    					if(u != 0 && v != 0)
	    						c=0.25;
	    					else if((u == 0 && v != 0)||(u != 0 && v == 0))
	    						c=0.25*0.707;
	    					else
	    						c=0.125;
	    					
	    					for(j=0;j<scale;j++)
	    					{
	    						for(int i=0;i<scale;i++)
	    							summaryDCT=summaryDCT+sourceM[chan][j+y*scale][i+x*scale]*cosValue[v][u][j][i];
	    					}
	    					DCT_M[chan][v+y*scale][u+x*scale]=(int)(summaryDCT*c/quantazLevel);
	    				}
	    			}
	    		}
	    	}
	    }
	}
	
	/**
	 * @param y is the coordinates in the matrix, img is the image,
	 * @param x is the coordinates in the matrix, img is the image,
	 * @param img
	 * @param limit used for delivery mode 2 as the number of coefficients
	 * @param limit2 used for delivery mode 3 as the number of bits
	 * @return void
	 */
	private void inDiscreteCosineTransform(int y,int x,BufferedImage im,int limit,int limit2)
	{
		int get_bits=0;
		//int[] significance;
		int sh =0, sh2 =0;
		if(deliveryMode==3)
		{
			int count=0;
			for(int i=4*scale;i>=1;i--)
			{
				get_bits+=(1<<i);
				count++;
				if(count==limit2)
					break;
			}
		}
	    for(int chan=0 ;chan<chans; chan++)
	    {
	    	//int sh=0;//this is for specifically changing R G or B value
	    	//int sh2=0;
	    	if(chan==0)
	    	{
	    		sh=0xff00ffff;
	    		sh2=0x00ff0000;
	    	}
	    	else if(chan==1)
	    	{
	    		sh=0xffff00ff;
	    		sh2=0x0000ff00;
	    	}
	    	else if(chan ==2)
	    	{
	    		sh=0xffffff00;
	    		sh2=0x000000ff;
	    	}
			for(int j=0;j<scale;j++)
			{
				for(int i=0;i<scale;i++)
				{
					double summary=0, c=0;	//sum of the block & 1/4*C(u)*C(v)
					int v=0,u=0, count=0;	//Limit count
					boolean direction=true;	//Direction of the zig-zag
					if(deliveryMode==3) {}
					for(;u<scale || v<scale;)
					{
    					if(u!=0 && v!=0)
    						c=0.25;
    					else if((u==0&&v!=0)||(u!=0&&v==0))
    						c=0.25*0.707;
    					else
    						c=0.125;
    					
    					if(deliveryMode==3)
    						summary=summary+(DCT_M[chan][v+y][x+u]&get_bits)*cosValue[v][u][j][i]*c;
    					else
    						summary=summary+DCT_M[chan][v+y][x+u]*cosValue[v][u][j][i]*c;
						if(count++==limit)
							break;
						if(direction)
						{
							if(u==scale-1)
							{
								direction=false;v++;continue;
							}
							if(v==0)
							{
								direction=false;u++;continue;
							}
							v--;u++;continue;
						}
						else
						{
							if(v==scale-1)
							{
								direction=true;u++;continue;
							}
							if(u==0)
							{
								direction=true;v++;continue;
							}
							u--;v++;continue;
						}
					}
					outputM[chan][j+y][i+x]=(int)(summary*quantazLevel);//System.out.print(chan_row2[i+i_base]+" ");
					int rgb=im.getRGB(x+i, y+j)&sh;
					im.setRGB(x+i, y+j, rgb|((outputM[chan][j+y][i+x]<<(8*(2-chan))&sh2)));
				}
			}
		}    		
	}
	
	
	/**
	 * @param matrix : representation of image
	 * @return BufferedImage : converted image form matrix
	 */
	private BufferedImage matrixToImage(int[][][] matrix)
	{
		BufferedImage img=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				byte r = (byte) (matrix[0][y][x]);
				byte g = (byte) matrix[1][y][x];
				byte b = (byte) matrix[2][y][x]; 
				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				img.setRGB(x,y,pix);
			}
		}
		return img;
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		Assignment2 assObj =new Assignment2(args);
	}
	/**
	 * @param args : input argument parameters used to decide the Quantization level, delivery mode, Latency
	 */
	public Assignment2(String[] args)
	{
		sourceM=new int[chans][height][width];
		DCT_M=new int[chans][height][width];
		outputM=new int[chans][height][width];
		if(readArgs(args)==false)
			return;
		//System.out.println(sourceName+" "+quantazLevel+" "+deliveryMode+" "+latency);
		if(readImageSrc()==false)
		{
			System.out.println("Image file not valid!");
			return;
		}
		
		cosValue=new double[scale][scale][scale][scale];
		double pi=Math.PI;
		for(int i1=0;i1<scale;i1++)
		{
		    int i1_2=2*i1+1;
		    for(int j1=0;j1<scale;j1++)
		    {
		        for(int i2=0;i2<scale;i2++)
		        {
		            int i2_2=2*i2+1;
		            for(int j2=0;j2<scale;j2++)
		            {
		                cosValue[j1][j2][i1][i2]=(Math.cos(pi*i1_2*j1*0.0625)*Math.cos(pi*i2_2*j2*0.0625));
		            }
		        }
		    }
		}
		
		DiscreteCosineTransform();
		
		BufferedImage output_im = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    JFrame frame = new JFrame();
	    JLabel label = new JLabel(new ImageIcon(matrixToImage(sourceM)));
	    JScrollPane scroller = new JScrollPane(label);
	    frame.getContentPane().add(label, BorderLayout.WEST);
	    JLabel label2 = new JLabel(new ImageIcon(output_im));
	    label2.setPreferredSize(new Dimension(width, height));
	    frame.getContentPane().add(label2, BorderLayout.EAST);
		frame.getContentPane().add (scroller, BorderLayout.CENTER);
	    frame.pack();
	    frame.setVisible(true);
	    frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    int limit=0;
	    
	    //if(deliveryMode== 1 / 2 /3 )
	    
	    switch(deliveryMode)
	    {
	    case 1:
	    	{
	    		if(latency==0)
	    		{
			    for(int j=0;j<scaledHeight;j++)
			    {
			    	for(int i=0;i<scaledWidth;i++)
			    	{
			    		inDiscreteCosineTransform(j*scale,i*scale,output_im,blockLength-1,-1);
			    	}
			    }
	    	    label2 = new JLabel(new ImageIcon(output_im));
	    	    frame.getContentPane().add(label2, BorderLayout.EAST);
	    	    label2.updateUI();
	    	    }
		    	else
		    	{
		    	    for(int j=0;j<scaledHeight;j++)
		    	    {
		    	    	for(int i=0;i<scaledWidth;i++)
		    	    	{
		    	    		try 
		    	    		{
		    					inDiscreteCosineTransform(j*scale,i*scale,output_im,blockLength-1,-1);
		    				    label2 = new JLabel(new ImageIcon(output_im));
		    				    frame.getContentPane().add(label2, BorderLayout.EAST);
		    				    label2.updateUI();
		    					Thread.sleep(latency);
		    				}
		    	    		catch (InterruptedException e)
		    	    		{
		    					// TODO Auto-generated catch block
		    	    			e.printStackTrace();
		    				}
		    	    	}
		    	    }
		    	}
	    	}	
	    	break;
	    case 2:
	    	{
	    		while(limit<(blockLength))
	    		{
	    			for(int j=0;j<scaledHeight;j++)
	    			{
	    				for(int i=0;i<scaledWidth;i++)
	    				{
	    						inDiscreteCosineTransform(j*scale,i*scale,output_im,limit,-1);
	    				}
	    			}
	    			limit++;
					label2 = new JLabel(new ImageIcon(output_im));
					frame.getContentPane().add(label2, BorderLayout.EAST);
					label2.updateUI();
					try 
					{
						Thread.sleep(latency);
					}
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
	    		}
	    		//System.out.println("end here,limit is"+limit);
	    	}
	    	break;
	    	
    	case 3:
	    	{
	    		while(limit++<=(4*scale))
	    		{
	    			for(int j=0;j<scaledHeight;j++)
	    			{
	    				for(int i=0;i<scaledWidth;i++)
	    				{
	    						inDiscreteCosineTransform(j*scale,i*scale,output_im,blockLength-1,limit);
	    				}
	    			}
					label2 = new JLabel(new ImageIcon(output_im));
					frame.getContentPane().add(label2, BorderLayout.EAST);
					label2.updateUI();
					try
					{
						Thread.sleep(latency);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
	    		}
		    }
	    	break;
	    	
	    default : 
	    	System.out.println("Error!");
	    	return;
	    }//switch end	
	}
}
