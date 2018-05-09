package sample.ims.springboot.outbound.util;

import java.util.List;

import javax.resource.ResourceException;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import com.ibm.connector2.ims.ico.inbound.IMSActivationSpec;
import com.ibm.j2ca.extension.logging.LogUtils;

//This class loads the properties for one to many IMS ActivationSpecs
//This allows the same service with a single deployment to be used with e.g. different IMS subsystems
//as it is required when multiple separated internal customers running their own IMSes use the same service
//That way a single service deployment can serve different IMSes
@Component
@ConfigurationProperties("imscallout") // prefix imscallout, find imscallout.* values
public class IMSActivationSpecProperties {
	
	private static IMSActivationSpec[] imsActivationSpec = null;
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private List<String> hostname;
	private List<String> username;
	private List<String> password;
	private List<Integer> portnumber;
	private List<String> datastorename;
	private List<String> queuenames;
	private Boolean sslenabled;
	private Boolean connectionpoolenabled;
	private Boolean assuredoncedelivery;
	private String deliverytype;
	private Long durationofidleconnections;
	private Integer failedeventretrylimit;
	private Integer maxconnections;
	private Integer pollperiod;
	private Integer pollquantity;
	private Long poolcleanupfrequency;
	private Boolean retryconnectiononstartup;
	private Integer retryinterval;
	private Integer retrylimit;
	private Boolean sendonlywithack;
	private Boolean stoppollingonerror;
	
	public IMSActivationSpec[] getImsActivationSpec() throws ResourceException {
		//lazy creation of ActivationSpec array
		if (imsActivationSpec == null) {
			//check if property Lists have the same size
			if (!checkIntegersEqual(hostname.size(), username.size(), password.size(), portnumber.size(), datastorename.size(), queuenames.size())) 
				throw new ResourceException("IMS Configuration Property arrays have not the same size.");
			//assume that the size for the activationspec array is equal to hostname.size
			imsActivationSpec = new IMSActivationSpec[hostname.size()];
			for (int i=0; i<hostname.size(); i++) {
				imsActivationSpec[i] = new IMSActivationSpec();
				imsActivationSpec[i].setHostName(hostname.get(i));
				imsActivationSpec[i].setUserName(username.get(i));
				imsActivationSpec[i].setPassword(password.get(i));
				imsActivationSpec[i].setPortNumber(portnumber.get(i));
				imsActivationSpec[i].setDataStoreName(datastorename.get(i));
				imsActivationSpec[i].setQueueNames(queuenames.get(i));				
				imsActivationSpec[i].setSSLEnabled(sslenabled);
				imsActivationSpec[i].setConnectionPoolEnabled(connectionpoolenabled);
				imsActivationSpec[i].setAssuredOnceDelivery(assuredoncedelivery);
				imsActivationSpec[i].setDeliveryType(deliverytype);
				imsActivationSpec[i].setDurationOfIdleConnections(durationofidleconnections);
				imsActivationSpec[i].setFailedEventRetryLimit(failedeventretrylimit);
				imsActivationSpec[i].setMaxConnections(maxconnections);
				imsActivationSpec[i].setPollPeriod(pollperiod);
				imsActivationSpec[i].setPollQuantity(pollquantity);
				imsActivationSpec[i].setPoolCleanupFrequency(poolcleanupfrequency);
				imsActivationSpec[i].setRetryConnectionOnStartup(retryconnectiononstartup);
				imsActivationSpec[i].setRetryInterval(retryinterval);
				imsActivationSpec[i].setRetryLimit(retrylimit);
				imsActivationSpec[i].setSendOnlyWithAck(sendonlywithack);
				imsActivationSpec[i].setStopPollingOnError(stoppollingonerror);		
			}			
		}
		//return the array
		return imsActivationSpec;
	}

	public List<String> getHostname() {
		return hostname;
	}

	public void setHostname(List<String> hostname) {
		this.hostname = hostname;
	}

	public List<String> getUsername() {
		return username;
	}

	public void setUsername(List<String> username) {
		this.username = username;
	}

	public List<String> getPassword() {
		return password;
	}

	public void setPassword(List<String> password) {
		this.password = password;
	}

	public List<Integer> getPortnumber() {
		return portnumber;
	}

	public void setPortnumber(List<Integer> portnumber) {
		this.portnumber = portnumber;
	}

	public List<String> getDatastorename() {
		return datastorename;
	}

	public void setDatastorename(List<String> datastorename) {
		this.datastorename = datastorename;
	}
	
    public List<String> getQueuenames() {
		return queuenames;
	}

	public void setQueuenames(List<String> queuenames) {
		this.queuenames = queuenames;
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

	public Boolean getAssuredoncedelivery() {
		return assuredoncedelivery;
	}

	public void setAssuredoncedelivery(Boolean assuredoncedelivery) {
		this.assuredoncedelivery = assuredoncedelivery;
	}

	public String getDeliverytype() {
		return deliverytype;
	}

	public void setDeliverytype(String deliverytype) {
		this.deliverytype = deliverytype;
	}

	public Long getDurationofidleconnections() {
		return durationofidleconnections;
	}

	public void setDurationofidleconnections(Long durationofidleconnections) {
		this.durationofidleconnections = durationofidleconnections;
	}

	public Integer getFailedeventretrylimit() {
		return failedeventretrylimit;
	}

	public void setFailedeventretrylimit(Integer failedeventretrylimit) {
		this.failedeventretrylimit = failedeventretrylimit;
	}

	public Integer getMaxconnections() {
		return maxconnections;
	}

	public void setMaxconnections(Integer maxconnections) {
		this.maxconnections = maxconnections;
	}

	public Integer getPollperiod() {
		return pollperiod;
	}

	public void setPollperiod(Integer pollperiod) {
		this.pollperiod = pollperiod;
	}

	public Integer getPollquantity() {
		return pollquantity;
	}

	public void setPollquantity(Integer pollquantity) {
		this.pollquantity = pollquantity;
	}

	public Long getPoolcleanupfrequency() {
		return poolcleanupfrequency;
	}

	public void setPoolcleanupfrequency(Long poolcleanupfrequency) {
		this.poolcleanupfrequency = poolcleanupfrequency;
	}

	public Boolean getRetryconnectiononstartup() {
		return retryconnectiononstartup;
	}

	public void setRetryconnectiononstartup(Boolean retryconnectiononstartup) {
		this.retryconnectiononstartup = retryconnectiononstartup;
	}

	public Integer getRetryinterval() {
		return retryinterval;
	}

	public void setRetryinterval(Integer retryinterval) {
		this.retryinterval = retryinterval;
	}

	public Integer getRetrylimit() {
		return retrylimit;
	}

	public void setRetrylimit(Integer retrylimit) {
		this.retrylimit = retrylimit;
	}

	public Boolean getSendonlywithack() {
		return sendonlywithack;
	}

	public void setSendonlywithack(Boolean sendonlywithack) {
		this.sendonlywithack = sendonlywithack;
	}

	public Boolean getStoppollingonerror() {
		return stoppollingonerror;
	}

	public void setStoppollingonerror(Boolean stoppollingonerror) {
		this.stoppollingonerror = stoppollingonerror;
	}

	@Override
    public String toString() {
        return "IMSActivationSpecProperties{" +
                "hostname='" + hostname + '\'' +
                "username='" + username + '\'' +
                "password='" + password + '\'' +
                "portnumber='" + portnumber + '\'' +
                ", datastore='" + datastorename + '\'' +
                '}';
    }
	
	public void setLogUtils(LogUtils logUtils) throws ResourceException {
		if (imsActivationSpec == null) getImsActivationSpec();
		for (int i=0; i<imsActivationSpec.length; i++) {
			imsActivationSpec[i].setLogUtils(logUtils);
		}
	}
	
	public static boolean checkIntegersEqual(int... integers)
	{
	    int firstValue = integers[0];
	    for (int i = 1; i < integers.length; i++) {
	        if (integers[i] != firstValue) return false;	        
	    }
	    return true;
	}	
}
