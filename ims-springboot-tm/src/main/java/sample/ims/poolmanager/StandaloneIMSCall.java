package sample.ims.poolmanager;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;

import com.ibm.connector2.ims.ico.IMSConnectionFactory;
import com.ibm.connector2.ims.ico.IMSConnectionSpec;
import com.ibm.connector2.ims.ico.IMSInteraction;
import com.ibm.connector2.ims.ico.IMSInteractionSpec;
import com.ibm.connector2.ims.ico.IMSManagedConnectionFactory;
import com.ibm.connector2.ims.ico.IMSResourceAdapter;
import com.ibm.connector2.spi.DefaultConnectionManager;
import com.ibm.connector2.spi.DefaultConnectionPoolProperties;
import com.ibm.connector2.spi.PoolManager;
import com.ibm.j2ca.extension.logging.LogUtils;

import sample.ims.springboot.inbound.records.INPUTMSG;
import sample.ims.springboot.inbound.records.OUTPUTAREA;

public class StandaloneIMSCall {
	
	private static Connection conn = null;
	private static IMSInteraction interaction = null;
	private static PoolManager poolManager = null;
	
	public static void main(String[] args) {
		try {
		    //turn on logging if required	  
			/*
		    final Logger universalLogger = Logger.getLogger("com.ibm.j2ca.*");
		    universalLogger.setLevel(Level.FINEST);
			try {
				//FileHandler fh = new FileHandler("C:\\IMSTMRATrace.txt");
				//fh.setFormatter(new SimpleFormatter());
			    //universalLogger.addHandler(fh);
				StreamHandler sh = new StreamHandler(System.out, new SimpleFormatter());
				sh.setLevel(Level.FINEST);
			    universalLogger.addHandler(sh);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			*/				 
			
			//create managed connection factory
			  IMSManagedConnectionFactory mcf =  new
				IMSManagedConnectionFactory();

			  //properties values for accessing IMS using IMS Connect
			  String password = "password";
			  String hostName = "hostname";//IMS Connect Ip-address or hostname
			  Integer portNumber = new Integer(9959);//IMS Connect Portnumber
			  String dataStoreName = "IMS1";//IMS Connect Data Store
			  Boolean sslEnabled = new Boolean(false);//turn off SSL
			  String userName = "userid";//Userid

			  //Create LogUtil
			  LogUtils logUtils = new LogUtils(new IMSResourceAdapter(), true);
			  //turn on logging if required
			  //logUtils.setLogger(universalLogger);
			  
			  //Set Managed Connection Factory Properties
			  mcf.setHostName(hostName);
			  mcf.setDataStoreName(dataStoreName);
			  mcf.setPortNumber(portNumber);
			  //mcf.setLogWriter(logWriter);
			  mcf.setSSLEnabled(sslEnabled);
			  mcf.setUserName(userName);
			  mcf.setPassword(password);
			  //turn on logging if required
			  mcf.setLogUtil(logUtils);
			  
			  //create DefaultPoolManagerProperties
			  DefaultConnectionPoolProperties connPoolProps = new DefaultConnectionPoolProperties();
			  connPoolProps.setMinConnections(3);
			  connPoolProps.setMaxConnections(6);
			  
			  //Create PoolManager
			  poolManager = new PoolManagerImpl();
			  
			  //Create DefaultConnectionManager
			  DefaultConnectionManager connMgr = new DefaultConnectionManager();
			  connMgr.setConnectionPoolProperties(connPoolProps);
			  //connMgr.setLogWriter(logWriter);	  
			  connMgr.setPoolManager(poolManager);
			  
			  //create ConnectionFactory
			  IMSConnectionFactory icf =
				 (IMSConnectionFactory)mcf.createConnectionFactory(connMgr);

			  //create IMS Connection Spec
			  IMSConnectionSpec ics = new IMSConnectionSpec();

			  //create Connection
			  conn = icf.getConnection(ics);

			  //create Interaction
			  interaction =	 (IMSInteraction)conn.createInteraction();

			  //create and fill Interaction specification
			  IMSInteractionSpec interactionSpec = new IMSInteractionSpec();
			  interactionSpec.setInteractionVerb(
				 IMSInteractionSpec.SYNC_SEND_RECEIVE );
			  interactionSpec.setCommitMode(IMSInteractionSpec.SEND_THEN_COMMIT); //could also be set to 1, clientid is generated
			  interactionSpec.setSyncLevel(IMSInteractionSpec.SYNC_LEVEL_NONE);
			  interactionSpec.setExecutionTimeout(600); //how long to wait for an answer from IMS
			  
			  //prepare Input Message
		        INPUTMSG inputMessage = new INPUTMSG();
		        inputMessage.setIn__ll((short)inputMessage.getSize());
		        inputMessage.setIn__zz((short)0);
				inputMessage.setIn__trancode("IVTNV");
				inputMessage.setIn__command("DIS");
				inputMessage.setIn__last__name("LAST4");

				//prepare Output Message
				OUTPUTAREA outputMessage = new OUTPUTAREA();

			  //return code
			  boolean ret=false;

			  //run the IMS interaction
			  ret = interaction.execute( interactionSpec, inputMessage, outputMessage );
			  //check for async output only useful for CM 0 interactions
			  //System.out.println(interactionSpec.getAsyncOutputAvailable()); 

			  //display the output with getter methods
			  System.out.println("Lastname: " + outputMessage.getOut__last__name());
			  System.out.println("Firstname: " + outputMessage.getOut__first__name());
			  System.out.println("Zipcode: " + outputMessage.getOut__zip__code());
			  			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//close
			  try {
				if (interaction != null) interaction.close();
			} catch (ResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	  
			  try {
				if (conn != null) conn.close();
			} catch (ResourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//cleanup all connection still in the pool
			((PoolManagerImpl)poolManager).stop();
		}
	}

}
