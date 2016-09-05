package i.jaxrscms.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class UserNotFoundException extends WebApplicationException
{
	private static final long serialVersionUID = -1781199883737476971L;

	public UserNotFoundException(int userId)
	{
		super(Response.status(Status.NOT_FOUND).entity("User with userId: " + userId + " was not found").build());
	}
}
