package sample.ims.springboot.imsdbjdbcrest;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.ims.jdbc.IMSDataSource;

@RestController
@RequestMapping("/IMSJdbc")
public class IMSJDBCRestApiController {
	public static final Logger logger = LoggerFactory.getLogger(IMSJDBCRestApiController.class);
	
    private final JdbcTemplate jdbcTemplate;
    private IMSDataSource imsDataSource;

    @Autowired
    public IMSJDBCRestApiController(JdbcTemplate jdbcTemplate, IMSDataSourceConfiguration imsDataSourceConfiguration) {
    	logger.info("JDBCTemplate set.");
        this.jdbcTemplate = jdbcTemplate;
    	logger.info("IMSDataSource set.");
        this.imsDataSource = (IMSDataSource)imsDataSourceConfiguration; 
        ((IMSDataSource)jdbcTemplate.getDataSource()).setDatastoreServer(this.imsDataSource.getDatastoreServer());
        ((IMSDataSource)jdbcTemplate.getDataSource()).setDatastoreName(this.imsDataSource.getDatastoreName());
        ((IMSDataSource)jdbcTemplate.getDataSource()).setPortNumber(this.imsDataSource.getPortNumber());
        ((IMSDataSource)jdbcTemplate.getDataSource()).setUser(this.imsDataSource.getUser());
        ((IMSDataSource)jdbcTemplate.getDataSource()).setPassword(this.imsDataSource.getPassword());
        ((IMSDataSource)jdbcTemplate.getDataSource()).setDriverType(this.imsDataSource.getDriverType());
        logger.info(this.imsDataSource.getDatabaseName());
    }
        
    ObjectMapper objectMapper = new ObjectMapper();

    // -------------------Retrieve Segments with Select * -------------------------------
	 
    @RequestMapping(value = "/{psbname}/{pcbname}/{segmentname}", method = RequestMethod.GET)
    public ResponseEntity<?> getIMSRecordUnqualified(@PathVariable("psbname") String psbname,
    		@PathVariable("pcbname") String pcbname, @PathVariable("segmentname") String segmentname,
    		@RequestParam LinkedHashMap<String, String> requestParams) {
        logger.info("Fetching Record(s) for PSB: {}, PCB: {}, Segment: {}", psbname, pcbname, segmentname);
        try {
        String sqlQuery = "SELECT * FROM " + pcbname + "." + segmentname + " ";
        if (requestParams != null && requestParams.size() > 0) {
        	sqlQuery += "WHERE ";
        	for (Map.Entry<String, String> entry : requestParams.entrySet())
        	{
        	    sqlQuery += entry.getKey() + "='" + entry.getValue() +"'";
        	}
        }
        //Change the PSB name (Database Name) for every query in case it has changed
        ((IMSDataSource)jdbcTemplate.getDataSource()).setDatabaseName(psbname);
        //((IMSDataSource)jdbcTemplate.getDataSource()).getConnection().getMetaData().getPrimaryKeys(catalog, schema, table);

        List<JsonNode> queryResults = jdbcTemplate.query(sqlQuery, new JsonNodeRowMapper(objectMapper));
        return new ResponseEntity<List<JsonNode>>(queryResults, HttpStatus.OK);
        } catch (Exception e) {
        	e.printStackTrace();
            // return the object node list for conversion to rest response
            return new ResponseEntity<List<ObjectNode>>(convertExceptionToObjectNodeList(e), HttpStatus.INTERNAL_SERVER_ERROR);        	
        }
    }

    // -------------------Retrieve Schema from the Catalog -------------------------------
    
    @RequestMapping(value = "/Schema/{psbname}/{pcbname}/{segmentname}", method = RequestMethod.GET)
    public ResponseEntity<?> getIMSSchema(@PathVariable("psbname") String psbname,
    		@PathVariable("pcbname") String pcbname, @PathVariable("segmentname") String segmentname
    		) {
        logger.info("Fetching Schema for PSB: {}, PCB: {}, Segment: {}", psbname, pcbname, segmentname);
        try {
        	String sqlQuery = "SELECT * FROM " + pcbname + "." + segmentname + " FETCH FIRST 1 ROW ONLY";
        	//Change the PSB name (Database Name) for every query in case it has changed
        	((IMSDataSource)jdbcTemplate.getDataSource()).setDatabaseName(psbname);
        	//execute query
        	List<JsonNode> queryResults = jdbcTemplate.query(sqlQuery, new JsonSchemaNodeRowMapper(objectMapper));
        	return new ResponseEntity<List<JsonNode>>(queryResults, HttpStatus.OK);
        } catch (Exception e) {
        	e.printStackTrace();
            // return the object node list for conversion to rest response
            return new ResponseEntity<List<ObjectNode>>(convertExceptionToObjectNodeList(e), HttpStatus.INTERNAL_SERVER_ERROR);        	
        }
    }

    //method to convert a java exception into JSON
    private List<ObjectNode> convertExceptionToObjectNodeList(Exception e) {
    	// convert the message and Exception to Json using jackson fasterxml 
    	// create list of objectnodes
    	List<ObjectNode> objectNodes = new LinkedList<ObjectNode>();
    	// get a single instance of a new node
    	ObjectNode objectNode = objectMapper.createObjectNode();
    	// put the Exception message entry
    	objectNode.put("message", e.getMessage());
    	// add it to the list
    	objectNodes.add(objectNode);
    	// get the stacktrace elements of the exception
        StackTraceElement elements[] = e.getStackTrace();
        // iterate through the elements an make every entry a new node
        // limit the stacktraceelements converted to JSon to 10
        for (int i = 0, el = elements.length; i < el && i < 10; i++) {
        	// get a single instance of a new node
        	ObjectNode stacktraceNode = objectMapper.createObjectNode();
        	// put the stacktrace entry
        	stacktraceNode.put(elements[i].getFileName() + ":" + elements[i].getLineNumber(), elements[i].getMethodName() + "()");
        	// add it to the list
        	objectNodes.add(stacktraceNode);
        }
    	return objectNodes;
    }
    
}
