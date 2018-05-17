package sample.ims.springboot.imsdbjdbcrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"sample.ims.springboot.imsdbjdbcrest"})
public class SpringBootMainApp 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(SpringBootMainApp.class, args);
    }
}
