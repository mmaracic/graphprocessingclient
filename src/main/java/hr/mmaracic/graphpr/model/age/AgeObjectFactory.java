package hr.mmaracic.graphpr.model.age;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.mmaracic.graphpr.dao.age.AgeDao;
import hr.mmaracic.graphpr.model.graph.StopNode;
import org.postgresql.util.PGobject;

public class AgeObjectFactory {

    private static final String VERTEX_TYPE = "::vertex";

    //ToDo Builders + again handle mess with custom postgres model and connection to jpa model
    public static Object parse(PGobject input, ObjectMapper objectMapper) throws JsonProcessingException {
       if (!"\"ag_catalog\".\"agtype\"".equals(input.getType())) {
           throw new IllegalArgumentException("The input is not of type: \"ag_catalog\".\"agtype\"");
       }
       if (input.getValue().endsWith(VERTEX_TYPE)) {
           String objectString = input.getValue().substring(0, input.getValue().indexOf(VERTEX_TYPE));
           AgeVertex ageVertex = objectMapper.readValue(objectString, AgeVertex.class);
           StopNode stopNode = new StopNode();
           stopNode.setId(ageVertex.getId());
           stopNode.setName((String) ageVertex.getProperties().get(AgeDao.NAME_PROPERTY));
           return stopNode;
       } else {
           throw new IllegalArgumentException("Unknown agType: " + input.getValue());
       }
    }
}
