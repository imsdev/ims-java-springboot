package sample.ims.springboot.outbound;

import sample.ims.springboot.outbound.record.CALLOUTREQUEST;
import sample.ims.springboot.outbound.record.CALLOUTRESPONSE;

//This is the interface declaration for the MDB Business method
public interface IMSCalloutBusiness {

	public CALLOUTRESPONSE IMSCalloutAction(
			CALLOUTREQUEST cALLOUTREQUEST);
}