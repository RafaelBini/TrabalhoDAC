package webservices;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapperProvider implements ExceptionMapper<Throwable>{

    @Override
    public Response toResponse(Throwable e) {
        e.printStackTrace();
        return Response.serverError().entity(e.getMessage()).build();
    }

}
