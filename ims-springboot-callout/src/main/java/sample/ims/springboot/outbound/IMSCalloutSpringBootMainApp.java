package sample.ims.springboot.outbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sample.ims.springboot.outbound.util.IMSCalloutHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@SpringBootApplication(scanBasePackages={"sample.ims.springboot.outbound.util","sample.ims.springboot.outbound"})
public class IMSCalloutSpringBootMainApp {

	private static final Logger logger = LoggerFactory.getLogger(IMSCalloutSpringBootMainApp.class);
	@Autowired
	IMSCalloutHelper imscallouthelper;
		    
	//RestController and this method are just for looking at the statistics
    @RequestMapping("/statistics")
    String getStatistics() {
    	logger.info("http statistics endpoint called.");
        return "IMS Callout Request Statistics.\nReceived: " + imscallouthelper.getCalloutCount() + "\nSucessful processed: " + imscallouthelper.getSuccessCount();
    }

    public static void main(String[] args) throws Exception {
    	SpringApplication.run(IMSCalloutSpringBootMainApp.class, args);
    }
}