package i.jaxrscms.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class TeamNotFoundException extends WebApplicationException
{
	private static final long serialVersionUID = -1169128691294778445L;

	public TeamNotFoundException(int teamId)
	{
		super(Response.status(Status.NOT_FOUND).entity("Team with teamId: " + teamId + " was not found").build());
	}
}
