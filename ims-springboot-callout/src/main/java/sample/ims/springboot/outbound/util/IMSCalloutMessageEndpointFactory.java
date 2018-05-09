package sample.ims.springboot.outbound.util;

import java.lang.reflect.Method;
import java.util.HashMap;

import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

public class IMSCalloutMessageEndpointFactory implements MessageEndpointFactory {
	
	IMSCalloutHelper imsCalloutHelper = new IMSCalloutHelper();
	private HashMap<Long,IMSCalloutHelper> executionClassInstanceHashMap = new HashMap<Long,IMSCalloutHelper>();

	//return one instance per Thread, with hashmap based on Threadid
	public synchronized MessageEndpoint createEndpoint(XAResource arg0) throws UnavailableException {
		long threadId = Thread.currentThread().getId();
		if (executionClassInstanceHashMap.containsKey(threadId)) {
			return executionClassInstanceHashMap.get(threadId);
		} else {
			imsCalloutHelper = new IMSCalloutHelper();
			executionClassInstanceHashMap.put(threadId, imsCalloutHelper);
			return imsCalloutHelper;
		}
	}
	
	public boolean isDeliveryTransacted(Method arg0) throws NoSuchMethodException {
		return false;
	}

	public MessageEndpoint createEndpoint(XAResource arg0, long arg1) throws UnavailableException {
		return null;
	}

	public String getActivationName() {
		return null;
	}

	public Class<?> getEndpointClass() {
		return IMSCalloutHelper.class;
	}	
}
