package sample.ims.springboot.outbound.util;

import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import com.ibm.connector2.ims.ico.IMSResourceAdapter;
import com.ibm.connector2.ims.ico.inbound.IMSEndpointManager;
import com.ibm.j2ca.extension.logging.LogUtils;

@Component
public class IMSCalloutController implements SmartLifecycle {
	
	private static final Logger logger = LoggerFactory.getLogger(IMSCalloutController.class);
	private boolean running;	
	private IMSEndpointManager endpointManager=null;
	private MessageEndpointFactory[] messageEndpointFactory=null;
	private WorkManager workManager=null;
	LogUtils logUtils;
    IMSActivationSpecProperties imsActivationSpecProperties;

    @Autowired    
    public IMSCalloutController(IMSActivationSpecProperties imsActivationSpecProperties) {
        this.imsActivationSpecProperties = imsActivationSpecProperties;
    }
	
	public boolean isRunning() {
		return running;
	}

	public void start() {
		//the following is required if external logging/tracing to a file is required
		/*
	    final java.util.logging.Logger universalLogger = java.util.logging.Logger.getLogger("com.ibm.j2ca.*");
	    //the following is only required if you want logging/tracing into a file otherwise the line above is enough
		try {
			java.util.logging.FileHandler fh = new java.util.logging.FileHandler("C:\\IMSTMRATrace.txt");
			fh.setFormatter(new java.util.logging.SimpleFormatter());
		    universalLogger.addHandler(fh);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//external logging end
		*/
		//initially the idea was to use JMS listeners provided by Spring
		//this means that spring creates the loops around waiting and receiving messages
		//but the IMS TM Resource Adapter implements this based on the IMSActivationSpec internally
		//So the final approach is just to create the endpoints which does implement everything
		try {
			//Create LogUtil
			logUtils = new LogUtils(new IMSResourceAdapter(), true);
			//the next one is required for output to a log file in addition to spring boot logging
			//logUtils.setLoggingLevel(java.util.logging.Level.FINEST);
			//logUtils.setLogger(universalLogger);
			imsActivationSpecProperties.setLogUtils(logUtils);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//log some app properties to show configuration has worked
    	String appProperties = this.imsActivationSpecProperties.toString();
    	logger.info("Welcome {}", appProperties);
        running = true;
		//store number of ActivationSpecs for further processing
		int numberActivationSpecs = 0;
		try {
			numberActivationSpecs = imsActivationSpecProperties.getImsActivationSpec().length;
		} catch (ResourceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        //Instanciate hierarchic objects that are neccessary to behave like an application server
        //create workmanager
		if (workManager == null) workManager = new IMSCalloutWorkManager();
		//based on workmanager and logutils create the endpointManager
		if (endpointManager == null) endpointManager = new IMSEndpointManager(workManager, logUtils);	
		//create the messageEndpointFactory
		//messageEndpointFactory needs to be an array
		//because of 1:1 relationship between messageEndpointFactory
		//and IMSActivationSpec
		if (messageEndpointFactory == null) messageEndpointFactory = 
				new IMSCalloutMessageEndpointFactory[numberActivationSpecs];
		//add and run the MDB endpoint which creates the Resume TPIPE loop, sockets, pools and all that neccessary for processing
		try {
			for (int i=0; i<numberActivationSpecs; i++) {
				messageEndpointFactory[i] = new IMSCalloutMessageEndpointFactory();
				endpointManager.addEndpoint(messageEndpointFactory[i], imsActivationSpecProperties.getImsActivationSpec()[i]);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//done callout messages can be processed now
	}

	//very important to return phase 0 for startup
	public int getPhase() {
        return 0;
    }
	
	public void stop(Runnable callback) {
		stop();
		//return to callback method
		callback.run();
	}

	//we want auto startup for the MDB and Callout Listeners
    public boolean isAutoStartup() {
        return true;
    }
	
	@SuppressWarnings("static-access")
	public void stop() {
		//set status to not running
        running = false;
        //if the messageEndpointFactory is there
		if (messageEndpointFactory != null) {
			//remove the endpoint(s) from the endpointManager
			try {
				for (int i=0; i<imsActivationSpecProperties.getImsActivationSpec().length; i++) {
					endpointManager.removeEndpoint(messageEndpointFactory[i], imsActivationSpecProperties.getImsActivationSpec()[i]);
					//shutdown the associated workmanager
					((IMSCalloutWorkManager)workManager).executor.shutdownNow();
				}
			} catch (ResourceException e) {
				e.printStackTrace();
			}
			//set the Factory(s) to null
			messageEndpointFactory = null;
			logger.info("Exiting with Callback normally...");
		}
		
	}
}
