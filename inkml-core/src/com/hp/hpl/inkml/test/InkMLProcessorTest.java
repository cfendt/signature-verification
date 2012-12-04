/*****************************************************************************************
* Copyright (c) 2008 Hewlett-Packard Development Company, L.P.
* Permission is hereby granted, free of charge, to any person obtaining a copy of
* this software and associated documentation files (the "Software"), to deal in
* the Software without restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
* Software, and to permit persons to whom the Software is furnished to do so,
* subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
* PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
* CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
* OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*****************************************************************************************/
/****************************************************************************************
 * SVN MACROS
 *
 * $Revision: 274 $
 * $Author: selvarmu $
 * $LastChangedDate: 2008-07-07 21:24:00 +0530 (Mon, 07 Jul 2008) $
 ************************************************************************************/
package com.hp.hpl.inkml.test;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;
import com.hp.hpl.inkml.*;

/**
 * A sample client application class that uses the InkML processor library.
 * It takes an input InkML file or InkML fragment for processing and save
 * the procerssed inkml data to the given output file. The data is saved in
 * archival style of InkML.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 * Creation date : 11th July, 2007
 */

public class InkMLProcessorTest {
	private static Logger logger =
		 Logger.getLogger(InkMLProcessorTest.class.getName());

	static InkMLProcessor inkmlProcessor;
	/**
	 * @param args
	 */
	class Listener implements InkMLEventListener {
		private Logger listenerLog =
			 Logger.getLogger(Listener.class.getName());

		public void brushChangedEvent(Brush brush) {
			listenerLog.info("Brush change event received: "+brush);
		}

		public void canvasChangedEvent(Canvas canvas) {
			listenerLog.info("Canvas change event received: "+canvas);
		}

		public void canvasTransformChangedEvent(CanvasTransform canvasTransform) {
			listenerLog.info("CanvasTransform change event received: "+canvasTransform);
		}

		public void contextChangedEvent(Context context) {
			listenerLog.info("Context changed event received. "+context);
		}

		public void inkSourceChangedEvent(InkSource inkSource) {
			listenerLog.info("");

		}

		public void timestampChangedEvent(Timestamp timestamp) {
			listenerLog.info("");

		}

		public void traceFormatChangedEvent(TraceFormat traceFormat) {
			listenerLog.info("");
		}

		public void traceGroupReceivedEvent(TraceGroup traceGroup) {
			listenerLog.info("Trace Group received: "+traceGroup.getId());
			traceGroup.printTraceGroup();
		}

		public void traceReceivedEvent(Trace trace) {
			listenerLog.info("\nTrace received: "+trace.getId());
			trace.printTrace();
		}

		public void traceViewReceivedEvent(TraceView traceView) {
			listenerLog.info("\nTrace view received: "+traceView.getId());
			traceView.printSelectedTree();
		}

	}

	/**
	 * Main method of test program
	 * Usage:  java -cp . com.hp.hpl.inkml.test.InkMLProcessorTest input.inkml
	 * Output: It generate the output file which contains the InkML data same in semantic as it is not doing any manipulation
	 *, but the format will be in 'archival style' meaning the elements will have all reference variable as explicit and those reference variaable targets are placed in a definitions eleement.
	 *  The output file name is Out_{inputFile}.inkml, for the above example, it is Out_input.inkml.
	 * @param args command line argument to input input inkml filename
	 */
	public static void main(String[] args) {
		if(args.length!=1) {
			logger.severe("\n Invalid command.\n Usage: java -cp . "+
					"InkMLProcessorTest input.inkml\n"+
					"Output: It generate the output file which contains the InkML data same in semantic"+
					"as it is not doing any manipulation but the format will be in 'archival style' "+
					"meaning the elements will have all reference variable as explicit and "+
					"those reference variaable targets are placed in a definitions eleement."+
					"The output file name is Out_{inputFile}.inkml, for the above example, it is Out_input.inkml.");
			System.exit(-1);
		}
		String inputFName = args[0];
		String outInkMLFName="";
		int index=inputFName.lastIndexOf("\\");
		StringBuffer nameBuff = new StringBuffer(inputFName.substring(0, index+1));
		String endStr=inputFName.substring(index+1);
		nameBuff.append("Out\\out_").append(endStr);
		String outputFName1 = nameBuff.toString();
		InkMLProcessorTest testObject = new InkMLProcessorTest();
		inkmlProcessor = new InkMLProcessor();
		inkmlProcessor.addInkMLEventListener(testObject.new Listener());
		try {
			//START of TC1
			//step 1
			// load and parse input .inkml file using InkMLProcessor
			inkmlProcessor.parseInkMLFile(inputFName);

			// serialize inkml objects to output1 .inkml using InkMLProcessor
			try {
				inkmlProcessor.saveInkSession(outputFName1);
			} catch (FileNotFoundException e1) {
				logger.severe("Invalid output file path, "+outputFName1);
			} catch (IOException e1) {
				logger.severe("Error while saving output InkML file. \n"+e1.getMessage());
			}
			inkmlProcessor.endInkSession();
			// Manually verify the the content of those 3 .inkML files
			// END of TC1

			// START of TC2
			//Define an array of InkML XML fragments
			/*String[] inkMLFragments = {
				"<definitions> "+
				"<brush id=\"brush1\"><annotationXML><color>red</color><width>2</width></annotationXML></brush> "+
				"<brush id=\"brush2\"><annotationXML><color>green</color></annotationXML></brush> "+
				"<brush id=\"brush3\"><annotationXML><width>5</width></annotationXML></brush> "+
				"</definitions>",
				"<trace> 1 2, 4 5, 1 2, 1 6 </trace>",
				"<context brushRef=\"#brush1\"></context>",
				"<trace> 1 25, 4 5, 1 2, 1 6 </trace>",
				"<trace brushRef=\"#brush2\"> 1 2, 4 5, 1 49, 1 6 </trace>",
				"<trace brushRef=\"#brush3\"> 1 25, 4 5, 1 2, 1 45 </trace>"
			};
			*/
			//@@@@@@@@@@@@@@@  MORE SAMPLE DATA @@@@@@@@@@@@@@@@@@@@@@@@@@
			/*String[] inkMLFragments1 = {
					"<context><brush><annotationXML><color>red</color></annotationXML></brush></context> ",
					"<trace> 1 2, 4 5, 1 2, 1 6 </trace>",
					"<context><brush><annotationXML><width>4</width></annotationXML></brush></context>",
					"<trace> 1 25, 4 5, 1 2, 1 6 </trace>"

				};
*/

			/*String[] inkMLFragments = {
			"<context><inkSource id=\"isTablet-shyam\"><traceFormat id=\"tfTablet-shyam\"><channel name=\"X\" min=\"0\" max=\"10848\" units=\"dev\"></channel><channel name=\"Y\" min=\"0\" max=\"9260\" units=\"dev\"></channel><channel name=\"F\" min=\"0\" max=\"511\" units=\"dev\"></channel></traceFormat><channelProperties><channelProperty channel=\"X\" name=\"resolution\" value=\"0.0010\" units=\"cm\"></channelProperty><channelProperty channel=\"Y\" name=\"resolution\" value=\"0.0010\" units=\"cm\"></channelProperty><channelProperty channel=\"F\" name=\"resolution\" value=\"1.0\" units=\"dev\"></channelProperty></channelProperties></inkSource><canvasTransform id=\"ctTablet-shyam\" invertible=\"true\"><mapping type=\"mathml\"><bind target=\"X\" variable=\"X\"></bind><math><apply><times></times><cn type=\"real\">0.010000001</cn><ci>X</ci></apply></math><bind target=\"Y\" variable=\"Y\"></bind><math><apply><times></times><cn type=\"real\">0.010000001</cn><ci>Y</ci></apply></math><bind target=\"F\" variable=\"F\"></bind><math><apply><times></times><cn type=\"real\">1.0</cn><ci>F</ci></apply></math></mapping></canvasTransform></context>", "<context><inkSource id=\"isMouse-shyam\"><traceFormat id=\"tfMouse-shyam\"><channel name=\"X\" min=\"0\" max=\"10848\" units=\"dev\"></channel><channel name=\"Y\" min=\"0\" max=\"9260\" units=\"dev\"></channel></traceFormat><channelProperties><channelProperty channel=\"X\" name=\"resolution\" value=\"0.0010\" units=\"cm\"></channelProperty><channelProperty channel=\"Y\" name=\"resolution\" value=\"0.0010\" units=\"cm\"></channelProperty></channelProperties></inkSource><canvasTransform id=\"ctMouse-shyam\" invertible=\"true\"><mapping type=\"mathml\"><bind target=\"X\" variable=\"X\"></bind><math><apply><times></times><cn type=\"real\">0.010000001</cn><ci>X</ci></apply></math><bind target=\"Y\" variable=\"Y\"></bind><math><apply><times></times><cn type=\"real\">0.010000001</cn><ci>Y</ci></apply></math></mapping></canvasTransform></context>"
			};*/

			/*String[] inkMLFragments = {
				"<context>      <inkSource id=\"isTablet-hptest1\">        <traceFormat id=\"tfTablet-hptest1\">          <channel name=\"X\" min=\"0\" max=\"10848\" units=\"dev\"/>         <channel name=\"Y\" min=\"0\" max=\"9260\" units=\"dev\"/>       <channel name=\"F\" min=\"0\" max=\"511\" units=\"dev\"/>       </traceFormat>       <channelProperties>         <channelProperty channel=\"X\" name=\"resolution\" value=\"0.0010\" units=\"cm\"/>         <channelProperty channel=\"Y\" name=\"resolution\" value=\"0.0010\" units=\"cm\"/>         <channelProperty channel=\"F\" name=\"resolution\" value=\"1.0\" units=\"dev\"/></channelProperties>   </inkSource>   <canvasTransform id=\"ctTablet-hptest1\" invertible=\"true\">   <mapping type=\"mathml\">        <bind target=\"X\" variable=\"X\"/>  <bind target=\"Y\" variable=\"Y\"/><bind target=\"F\" variable=\"F\"/>     <math xmlns=\"http://www.w3.org/1998/Math/MathML\"> <apply><times/>      <cn type=\"real\">0.010000001</cn>          </apply>                                        <apply>              <times/> <cn type=\"real\">0.010000001</cn>         <ci>Y</ci>         </apply>                           <apply>              <times/>              <cn type=\"real\">1.0</cn>              <ci>F</ci>            </apply>          </math>        </mapping>      </canvasTransform></context>",
				" <context>      <inkSource id=\"isMouse-hptest1\">        <traceFormat id=\"tfMouse-hptest1\">          <channel name=\"X\" min=\"0\" max=\"10848\" units=\"dev\"/>          <channel name=\"Y\" min=\"0\" max=\"9260\" units=\"dev\"/> </traceFormat><channelProperties>      <channelProperty channel=\"X\" name=\"resolution\" value=\"0.0010\" units=\"cm\"/><channelProperty channel=\"Y\" name=\"resolution\" value=\"0.0010\" units=\"cm\"/>   </channelProperties></inkSource><canvasTransform id=\"ctMouse-hptest1\" invertible=\"true\">     <mapping type=\"mathml\"><bind target=\"X\" variable=\"X\"/><bind target=\"Y\" variable=\"Y\"/>        <math xmlns=\"http://www.w3.org/1998/Math/MathML\">        <apply><times/>             <cn type=\"real\">0.010000001</cn>              <ci>X</ci>            </apply>                          <apply><times/><cn type=\"real\">0.010000001</cn>             <ci>Y</ci>           </apply>          </math>      </mapping></canvasTransform>   </context>",
				" <trace> 582 2540, 556 2540, 503 2461, 450 2408, 450 2355, 450 2275, 450 2223, 450 2170, 450 2064, 450 1958, 450 1826, 450 1720, 450 1588, 450 1508, 450 1402, 476 1323, 529 1217, 582 1138, 661 1058, 714 1032, 794 979, 847 926, 926 900, 1005 873, 1085 847, 1138 847, 1191 847, 1244 847, 1296 847, 1376 847, 1429 847, 1482 847, 1561 847, 1588 847, 1640 847, 1720 847, 1746 847, 1773 820, 1799 820, 1799 820, 1826 794, 1852 794</trace>"
			};*/

			/*String[] inkMLFragments = {
			"<context xmlns=\"jabber:x:inkml\"><inkSource id=\"isTablet-hptest1\"><traceFormat id=\"tfTablet-hptest1\"><channel name=\"X\" min=\"0\" max=\"10848\" units=\"dev\"/><channel name=\"Y\" min=\"0\" max=\"9260\" units=\"dev\"/><channel name=\"F\" min=\"0\" max=\"511\" units=\"dev\"/></traceFormat><channelProperties><channelProperty channel=\"X\" name=\"resolution\" value=\"0.0010\" units=\"cm\"/><channelProperty channel=\"Y\" name=\"resolution\" value=\"0.0010\" units=\"cm\"/><channelProperty channel=\"F\" name=\"resolution\" value=\"1.0\" units=\"dev\"/></channelProperties></inkSource><canvasTransform id=\"ctTablet-hptest1\" invertible=\"true\"><mapping type=\"mathml\"><bind target=\"X\" variable=\"X\"/><bind target=\"Y\" variable=\"Y\"/><bind target=\"F\" variable=\"F\"/><math xmlns=\"http://www.w3.org/1998/Math/MathML\"><apply><times/><cn type=\"real\">0.010000001</cn><ci>X</ci></apply><apply><times/><cn type=\"real\">0.010000001</cn><ci>Y</ci></apply><apply><times/><cn type=\"real\">1.0</cn><ci>F</ci></apply></math></mapping></canvasTransform></context>",
			" <context>      <inkSource id=\"isMouse-hptest1\">        <traceFormat id=\"tfMouse-hptest1\">          <channel name=\"X\" min=\"0\" max=\"10848\" units=\"dev\"/>          <channel name=\"Y\" min=\"0\" max=\"9260\" units=\"dev\"/> </traceFormat><channelProperties>      <channelProperty channel=\"X\" name=\"resolution\" value=\"0.0010\" units=\"cm\"/><channelProperty channel=\"Y\" name=\"resolution\" value=\"0.0010\" units=\"cm\"/>   </channelProperties></inkSource><canvasTransform id=\"ctMouse-hptest1\" invertible=\"true\">     <mapping type=\"mathml\"><bind target=\"X\" variable=\"X\"/><bind target=\"Y\" variable=\"Y\"/>        <math xmlns=\"http://www.w3.org/1998/Math/MathML\">        <apply><times/>             <cn type=\"real\">0.010000001</cn>              <ci>X</ci>            </apply>                          <apply><times/><cn type=\"real\">0.010000001</cn>             <ci>Y</ci>           </apply>          </math>      </mapping></canvasTransform>   </context>",
			"<context traceFormatRef='#tfMouse-hptest1'/>",
			" <trace> 582 2540, 556 2540, 503 2461, 450 2408, 450 2355, 450 2275, 450 2223, 450 2170, 450 2064, 450 1958, 450 1826, 450 1720, 450 1588, 450 1508, 450 1402, 476 1323, 529 1217, 582 1138, 661 1058, 714 1032, 794 979, 847 926, 926 900, 1005 873, 1085 847, 1138 847, 1191 847, 1244 847, 1296 847, 1376 847, 1429 847, 1482 847, 1561 847, 1588 847, 1640 847, 1720 847, 1746 847, 1773 820, 1799 820, 1799 820, 1826 794, 1852 794</trace>"
			};
			*/

			/*
			String[] inkMLFragments = {
					"<trace>450 2355, 450 2275, 450 2223, 450 2170, 450 2064</trace>"
			};
			*/

			/*String[] inkMLFragments = {
				"<trace>450 2355, 450 2275, 450 2223, 450 2170, 450 2064</trace>",
				"<context> <brush><annotationXML><color>5,5,5</color></annotationXML></brush></context>",
				"<trace>582 2540, 556 2540, 503 2461, 450 2408</trace>",
				"<context> <brush><annotationXML><width>4</width></annotationXML></brush></context>",
				"<annotationXML><canvasImage>abc.jpg</canvasImage></annotationXML>",
				"<trace>582 2540, 556 2540, 503 2461, 450 2408</trace>",
				"<context> <brush><annotationXML><color>52,52,52</color></annotationXML></brush></context>",
				"<trace>582 2540, 556 2540, 503 2461, 450 2408</trace>",
				"<context> <brush><annotationXML><color>10,10,10</color><width>10</width></annotationXML></brush></context>",
				"<trace>450 2275, 450 2223, 450 2170, 450 2064</trace>"
			};
			*/

			//For each element in the InkML fagmrnt array perform the below steps
			// begin Ink Session
			/*inkmlProcessor.resetInkSession();
			try {
			for(index = 0; index<inkMLFragments.length; index++) {
				// parse the inkml fragment string
				logger.info("\n Parsing xml fragment: \n"+inkMLFragments[index]+"\n");
				inkmlProcessor.parseInkMLString(inkMLFragments[index]);
				// serialize inkml objects into fragment<index>.inkml file
								outInkMLFName = "fragment"+index+".inkml";
								logger.info("Saving file, "+outInkMLFName);
								inkmlProcessor.saveInkSession(outInkMLFName);
			}
			// serialize the final state of inkml objects into allFragments.inkml file
			//inputFName ="";
			outInkMLFName = inputFName+"_allFragments.inkml";
			logger.info("output File:"+outInkMLFName);

				inkmlProcessor.saveInkSession(outInkMLFName);
			} catch (FileNotFoundException e1) {
				logger.severe("Invalid output file path, "+outInkMLFName);
			} catch (IOException e1) {
				logger.severe("Error while saving output InkML file,"+outInkMLFName+
						"\nReason: "+e1.getMessage());
			}
			// end Ink Session
			inkmlProcessor.endInkSession();*/
			// verify the output file contents
			// END of TC2
		}catch(InkMLException inkmlExp){
			logger.severe("Error in parsing input inkml file.\n Message: "+
							inkmlExp.getMessage());
		}
	}
 }
