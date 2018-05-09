package sample.ims.springboot.outbound.util;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import javax.resource.ResourceException;
import javax.resource.cci.Record;
import javax.resource.spi.endpoint.MessageEndpoint;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import commonj.connector.runtime.ExtendedMessageListener;
import commonj.connector.runtime.InboundInteractionSpec;
import commonj.connector.runtime.InboundListener;

//Calling this class is hardcoded in IMSCalloutMessageEndpointFactory
@Component
public class IMSCalloutHelper implements ExtendedMessageListener, InboundListener, MessageEndpoint, ApplicationContextAware {

    private static ApplicationContext context;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;   
    }

    //this is not static, because we want every thread to use a different instance of the business method class
    //otherwise during parallel processing there are occasional uses of the same instance of the business method class
    private Object imsCalloutImpl = null;
    //those are all static, since for performance reasons we want them only once
	private static AtomicInteger calloutCount = new AtomicInteger(0); 
	private static AtomicInteger successCount = new AtomicInteger(0);
	private static String methodName;
	private static String recordName;
	@SuppressWarnings("rawtypes")
	private Class recordClass;
	@SuppressWarnings("rawtypes")
	private static Class implClass;

	public Record onMessage(InboundInteractionSpec inboundInteractionSpec, Record record) throws ResourceException {
		return onMessage(record);
	}

    public Record onMessage(Record record) {
		try {
		//increment count of callout messages sent to the mdb method
		calloutCount.incrementAndGet();
		//initial setup
		if (imsCalloutImpl == null) {
			//get the bean defined with @Service("imscalloutimpl"), its class name and instanciate it
			System.out.println("Initialize imscalloutimpl");
			Object bean = context.getBean("imscalloutimpl");
			implClass = Class.forName(bean.getClass().getName());
			imsCalloutImpl = implClass.newInstance();		
			//assuming there is just one method, get the first method name and the first parameter class name from that method for invokation
			Method[] methods = bean.getClass().getDeclaredMethods();
			methodName = methods[0].getName();
			recordName = methods[0].getParameterTypes()[0].getName();
			recordClass = Class.forName(recordName);
		}
		//instanciate the reflected methods input object
		Object recordObject = recordClass.newInstance();
		//move the bytes from the Record object to the reflekted input object by using Bytearraystreams
		java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
		((javax.resource.cci.Streamable) record)
				.write(outputStream);
		java.io.ByteArrayInputStream inputStream = new java.io.ByteArrayInputStream(
				outputStream.toByteArray());
		((javax.resource.cci.Streamable) recordObject)
				.read(inputStream);
		//get the method object for the methodname
		java.lang.reflect.Method method = imsCalloutImpl
				.getClass().getMethod(methodName,
						new Class[] { recordObject.getClass() });
		//invoke the mdb business method
		Object result = method.invoke(imsCalloutImpl,
				new Object[] { recordObject });
		//if there is no exception yet, the MDB method executed successfully
        successCount.incrementAndGet();
        //return the result object as Record
		if (result instanceof commonj.connector.runtime.RecordHolder)
			return ((commonj.connector.runtime.RecordHolder) result)
					.getRecord();
		else if (result instanceof javax.resource.cci.Record)
			return (javax.resource.cci.Record) result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		//in case of exceptions return null
		return null;
    }

	public void afterDelivery() throws ResourceException {
		
	}

	public void beforeDelivery(Method arg0) throws NoSuchMethodException, ResourceException {
		
	}

	public void release() {
		
	}

	public void onNotification(InboundInteractionSpec arg0, Record arg1) throws ResourceException {
		throw new ResourceException("not implemented");
	}

	public void onNotification(Record arg0) throws ResourceException {
		throw new ResourceException("not implemented");		
	}

	//this method is used by the Spring App Controller to return statistics
	public int getCalloutCount() {
		return calloutCount.get();
	}

	//this method is used by the Spring App Controller to return statistics
	public int getSuccessCount() {
		return successCount.get();
	}
}