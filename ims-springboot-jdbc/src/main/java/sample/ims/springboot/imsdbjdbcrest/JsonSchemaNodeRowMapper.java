package sample.ims.springboot.imsdbjdbcrest;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonSchemaNodeRowMapper implements RowMapper<JsonNode> {

    private final ObjectMapper mapper;
    
    public JsonSchemaNodeRowMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JsonNode mapRow(ResultSet rs, int rowNum) throws SQLException {
    	//create a new object node as the top hierarchy
        ObjectNode objectNode = mapper.createObjectNode();
        //get the metadata from the open resultset
        ResultSetMetaData rsmd = rs.getMetaData();
        //get number of columns
        int columnCount = rsmd.getColumnCount();
        //put header information for segment
        objectNode.put("description", "schema for segment " + rsmd.getTableName(1));
        objectNode.put("type", "object");
        //create a sub node for the fields of a segment to be put as an array to the objectNode
    	ObjectNode columnArrayNode = mapper.createObjectNode();
    	//get the dataTypeMap for mapping into JSON types
    	Map<String, String> dataTypeMap = getDataTypeMap();
    	//loop around number of fields
        for (int index = 1; index <= columnCount; index++) {
        	//create a sub node for the properties of each segment to be put as an array to the column node
            ObjectNode typeNode = mapper.createObjectNode();
            //put the datatype by using the dataType Map
            typeNode.put("type", dataTypeMap.get(rsmd.getColumnTypeName(index).toLowerCase()));
            //put the maximum length
            typeNode.put("maximum", rsmd.getPrecision(index));
            //add the typenode to the parent column
            columnArrayNode.set(rsmd.getColumnName(index), typeNode);
        }
        //add the column node to the parent object node
        objectNode.set("properties", columnArrayNode);
        //return the hierarchic node structure for conversion to JSON
        return objectNode;
    }

    //might not cover all supported datatypes
    public Map<String, String> getDataTypeMap() {
        Map<String, String> dataTypeMap =
            new HashMap<String, String>();
        dataTypeMap.put("byteint", "int");
        dataTypeMap.put("smallint", "int");
        dataTypeMap.put("short", "int");
        dataTypeMap.put("integer", "int");
        dataTypeMap.put("bigint", "long");
        dataTypeMap.put("float", "float");
        dataTypeMap.put("decimal", "double");
        dataTypeMap.put("char", "string");
        dataTypeMap.put("varchar", "string");
        dataTypeMap.put("byte", "bytes");
        dataTypeMap.put("varbyte", "bytes");
        dataTypeMap.put("date", "date");
        dataTypeMap.put("time", "time");
        dataTypeMap.put("timestamp", "timestamp");
        dataTypeMap.put("clob", "string");
        dataTypeMap.put("blob", "string");
        dataTypeMap.put("structured udt", "array");
        dataTypeMap.put("double precision", "float");
        dataTypeMap.put("numeric", "double");
        dataTypeMap.put("real", "float");
        dataTypeMap.put("character", "string");
        dataTypeMap.put("char varying", "string");
        dataTypeMap.put("character varying", "string");
        dataTypeMap.put("long varchar", "string");
        dataTypeMap.put("interval", "string");
        return dataTypeMap;
      }
    
}