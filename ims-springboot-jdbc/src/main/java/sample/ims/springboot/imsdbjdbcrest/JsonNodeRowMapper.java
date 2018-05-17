package sample.ims.springboot.imsdbjdbcrest;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeRowMapper implements RowMapper<JsonNode> {

    private final ObjectMapper mapper;
    
    public JsonNodeRowMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JsonNode mapRow(ResultSet rs, int rowNum) throws SQLException {
    	//create a new object node as the top hierarchy
        ObjectNode objectNode = mapper.createObjectNode();
        //get the metadata from the open resultset
        ResultSetMetaData rsmd = rs.getMetaData();
        //get number of columns
        int columnCount = rsmd.getColumnCount();
    	//loop around number of fields
        for (int index = 1; index <= columnCount; index++) {
        	//get the column name
        	String column = JdbcUtils.lookupColumnName(rsmd, index);
        	//get the field value as object
            Object value = rs.getObject(column);
            //based on what type of object it is, put it into the object Node
            if (value == null) {
                objectNode.putNull(column);
            } else if (value instanceof Integer) {
                objectNode.put(column, (Integer) value);
            } else if (value instanceof Short) {
                objectNode.put(column, (Short) value);
            } else if (value instanceof String) {
                objectNode.put(column, (String) value);                
            } else if (value instanceof Boolean) {
                objectNode.put(column, (Boolean) value);           
            } else if (value instanceof Date) {
                objectNode.put(column, ((Date) value).getTime());                
            } else if (value instanceof Long) {
                objectNode.put(column, (Long) value);                
            } else if (value instanceof Double) {
                objectNode.put(column, (Double) value);                
            } else if (value instanceof Float) {
                objectNode.put(column, (Float) value);                
            } else if (value instanceof BigDecimal) {
                objectNode.put(column, (BigDecimal) value);
            } else if (value instanceof Byte) {
                objectNode.put(column, (Byte) value);
            } else if (value instanceof byte[]) {
                objectNode.put(column, (byte[]) value);                
            } else {
                throw new IllegalArgumentException("Unmappable object type: " + value.getClass());
            }
        }
        //return the object node for the current row for conversion to JSON
        return objectNode;
    }

}