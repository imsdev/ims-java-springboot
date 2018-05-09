package sample.ims.springboot.outbound;

import java.text.SimpleDateFormat;
import org.springframework.stereotype.Service;
import sample.ims.springboot.outbound.record.CALLOUTREQUEST;
import sample.ims.springboot.outbound.record.CALLOUTRESPONSE;

//The Service annotation with name imscalloutimpl marks the method for retrieval by IMSCalloutHelper
//This bean is then searched by IMSCalloutHelper and the first method invoked with reflection
//assuming input and output are streamable records equivalent to COBOL definitions used in the
//IMS transaction
@Service("imscalloutimpl")
public class IMSCalloutBusinessImpl implements IMSCalloutBusiness {
	//business method implementation to be called by
	//the onMessage or onNotification methods of an existing MDB
	public CALLOUTRESPONSE IMSCalloutAction (
			CALLOUTREQUEST cALLOUTREQUEST) {
		//For testing concurrency the execution elapse time was set to 1 second
		long start = System.currentTimeMillis();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//create new response message
		CALLOUTRESPONSE response = new CALLOUTRESPONSE();
		//fill response message with values
		response.setCalloutdate("15.11.2017");
		response.setCalloutordernum(10);
		response.setCalloutresponsestr("HALLO FROM MDB");
		long stop = System.currentTimeMillis();
	    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
	    //print out MDB method processing elapse time
	    System.out.println("MDB: " + this.toString()
	    	 + " Elapsetime: " 
	    	 + sdf.format(stop-start)
	    	 + " Input: "
	    	 + cALLOUTREQUEST.getCalloutrequeststr()
	    	 );
	    //return the response to be sent to back to IMS
		return response;
	}
}