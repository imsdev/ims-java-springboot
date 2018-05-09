package sample.ims.springboot.inbound;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.connector2.ims.ico.IMSInteraction;
import com.ibm.connector2.ims.ico.IMSInteractionSpec;

import sample.ims.springboot.inbound.records.INPUTMSG;
import sample.ims.springboot.inbound.records.OUTPUTAREA;

@RestController
@RequestMapping("/IVTNV")
public class IVTNVRestApiController {
	public static final Logger logger = LoggerFactory.getLogger(IVTNVRestApiController.class);
	
	@Autowired
	IMSManagedConnectionFactoryProperties imsMcfProps;
	

    // -------------------Retrieve Single Entry from generated COBOL class-------------------------------
	 
    @RequestMapping(value = "/lastname/{lastname}", method = RequestMethod.GET)
    public ResponseEntity<?> getPhonebookRecord(@PathVariable("lastname") String lastname) {
        logger.info("Fetching Record for Lastname {}", lastname);
		//prepare Output Message
		OUTPUTAREA outputMessage = new OUTPUTAREA();

		//never make this global, then different threads use the same connection and interaction
	    Connection conn = null;
	    IMSInteraction interaction = null;

        try {
			  //create Connection
			  conn = imsMcfProps.getIMSConnection(null, null);

			  //create Interaction
			  interaction = (IMSInteraction)conn.createInteraction();
			  logger.info("Connection: {} Interaction: {}", conn.toString(), interaction.toString());
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
			  inputMessage.setIn__last__name(lastname.trim());

			  //return code
			  @SuppressWarnings("unused")
			  boolean ret=false;

			  //run the IMS interaction
			  ret = interaction.execute( interactionSpec, inputMessage, outputMessage );		  
				
			  //display the output with getter methods
			  System.out.println("Lastname: " + outputMessage.getOut__last__name());
			  System.out.println("Firstname: " + outputMessage.getOut__first__name());
			  System.out.println("Zipcode: " + outputMessage.getOut__zip__code());
        	
        } catch (Exception e ) {
            logger.error("Exception occurred: {}", e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<StringError>(new StringError("Exection " + e.getMessage() 
                    + " occurred"), HttpStatus.INTERNAL_SERVER_ERROR);        	
         } finally {
			//close
			  logger.info("Closing Connection: {} Interaction: {}", conn.toString(), interaction.toString());
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
        }
        if (outputMessage.getOut__last__name().trim().length() <= 0) {
            logger.error("Lastname {} not found.", lastname);
            return new ResponseEntity<StringError>(new StringError("Record for Lastname " + lastname 
                    + " not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<OUTPUTAREA>(outputMessage, HttpStatus.OK);
    }


}
