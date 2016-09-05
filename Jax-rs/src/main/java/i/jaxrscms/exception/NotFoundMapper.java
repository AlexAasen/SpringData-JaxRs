package i.jaxrscms.exception;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundMapper implements ExceptionMapper<NotFoundException>{
   
   public Response toResponse(NotFoundException exception){
      // om man vill ha med meddelande typ...
      // return Response.status(Status.NOT_FOUND).entity(exception.getMessage()).build();
      return Response.status(Status.NOT_FOUND).build();
   }
   
}