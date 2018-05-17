package sample.ims.springboot.imsdbjdbcrest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.ibm.ims.jdbc.IMSDataSource;

@Configuration
@ConfigurationProperties(prefix="imsdatasource")
public class IMSDataSourceConfiguration extends IMSDataSource{
}
