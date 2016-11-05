package com.pangbohao.server.rest.filter;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

@Provider
@Produces("application/json;charset=utf-8")
@Consumes("application/json;charset=utf-8")
public class MyJacksonJsonProvider extends JacksonJaxbJsonProvider {

	public MyJacksonJsonProvider() {
		ObjectMapper mapper = new ObjectMapper();
		// mapper.registerModule(new DefaultScalaModule());
		// System.out.println("==================="
		// + mapper.canSerialize(String.class));
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		// mapper.setSerializationInclusion(Inclusion.NON_DEFAULT);
		mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		super.setMapper(mapper);
	}

}