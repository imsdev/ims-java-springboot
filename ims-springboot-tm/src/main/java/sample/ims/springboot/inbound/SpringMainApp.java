package sample.ims.springboot.inbound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"sample.ims.springboot.inbound"})
public class SpringMainApp 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(SpringMainApp.class, args);
    }
}
