package sample.ims.springboot.inbound;

import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.ibm.connector2.ims.ico.IMSConnectionFactory;
import com.ibm.connector2.ims.ico.IMSConnectionSpec;
import com.ibm.connector2.ims.ico.IMSManagedConnectionFactory;
import com.ibm.connector2.ims.ico.IMSResourceAdapter;
import com.ibm.connector2.spi.DefaultConnectionManager;
import com.ibm.connector2.spi.DefaultConnectionPoolProperties;
import com.ibm.connector2.spi.PoolManager;
import com.ibm.j2ca.extension.logging.LogUtils;

import sample.ims.poolmanager.PoolManagerImpl;

@Component
@ConfigurationProperties("imsmcf") // prefix imsmcf, finds imsmcf.* values
public class IMSManagedConnectionFactoryProperties {
	private IMSManagedConnectionFactory mcf = null;
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private String hostname;
	private String datastorename;
	private Integer portnumber;
	private String username;
	private String password;
	@Value("${imsmcf.sslenabled ?: false}")
	private Boolean sslenabled;
	private Boolean connectionpoolenabled;
	@Value("${imsmcf.minconnections ?: 1}")
	private Integer minconnections;
	@Value("${imsmcf.maxconnections ?: 5}")
	private Integer maxconnections;
	private PoolManager poolManager = null;
	private DefaultConnectionPoolProperties connPoolProps = null;
	private DefaultConnectionManager connMgr = null;
	private IMSConnectionFactory icf = null;
	private IMSConnectionSpec ics = null;

	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public String getDatastorename() {
		return datastorename;
	}
	
	public void setDatastorename(String datastorename) {
		this.datastorename = datastorename;
	}
	
	public Integer getPortnumber() {
		return portnumber;
	}
	
	public void setPortnumber(Integer portnumber) {
		this.portnumber = portnumber;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Boolean getSslenabled() {
		return sslenabled;
	}
	
	public void setSslenabled(Boolean sslenabled) {
		this.sslenabled = sslenabled;
	}
	
	public Boolean getConnectionpoolenabled() {
		return connectionpoolenabled;
	}
	
	public void setConnectionpoolenabled(Boolean connectionpoolenabled) {
		this.connectionpoolenabled = connectionpoolenabled;		
	}
	
	public Integer getMinconnections() {
		return minconnections;
	}
	
	public void setMinconnections(Integer minconnections) {
		this.minconnections = minconnections;
	}
	
	public Integer getMaxconnections() {
		return maxconnections;
	}
	
	public void setMaxconnections(Integer maxconnections) {
		this.maxconnections = maxconnections;
	}
	
	//username and password can be null, then mcf username and password are used
	//synchronized to make sure that userid + password are not overwritten by different threads
	@SuppressWarnings("static-access")
	public synchronized Connection getIMSConnection(String username, String password) throws ResourceException {
		if (this.mcf == null) {
			this.mcf =  new IMSManagedConnectionFactory();
			
			this.mcf.setHostName(this.hostname);
			this.mcf.setDataStoreName(this.datastorename);
			this.mcf.setPortNumber(this.portnumber);
			this.mcf.setUserName(this.username);
			this.mcf.setPassword(this.password);
			this.mcf.setSSLEnabled(this.sslenabled);
			
			//IMSTMRA uses java.util.logging
		    final Logger universalLogger = Logger.getLogger("com.ibm.j2ca");
			LogUtils logUtils = new LogUtils(new IMSResourceAdapter(), true);
			//using slf4j does not work here, the cast to java.util.logging fails
			logUtils.setLogger(universalLogger);			
			this.mcf.setLogUtil(logUtils);
			
			if (this.connectionpoolenabled) {
				//create DefaultPoolManagerProperties
				this.connPoolProps = new DefaultConnectionPoolProperties();
				this.connPoolProps.setMinConnections(this.minconnections.intValue());
				this.connPoolProps.setMaxConnections(this.maxconnections.intValue());

				//Create PoolManager
				this.poolManager = new PoolManagerImpl();
				  
				//Create DefaultConnectionManager
				this.connMgr = new DefaultConnectionManager();
				this.connMgr.setConnectionPoolProperties(this.connPoolProps);
				//connMgr.setLogWriter(logWriter);	  
				this.connMgr.setPoolManager(this.poolManager);
				
				//create ConnectionFactory with pool manager
				this.icf = (IMSConnectionFactory)this.mcf.createConnectionFactory(this.connMgr);
			} else {
				//create ConnectionFactory with out pool manager
				this.icf = (IMSConnectionFactory)this.mcf.createConnectionFactory();				
			}
			//create IMS Connection Spec
			ics = new IMSConnectionSpec();			
		}
		//set username and password for this connection request
		ics.setUserName(username);
		ics.setPassword(password);
		
		return icf.getConnection(ics);
	}
	
    @PreDestroy
    public void destroy() {
		//cleanup all connection still in the pool
    	System.out.println("Destroy");
		if (poolManager != null) ((PoolManagerImpl)poolManager).stop();
    }

}