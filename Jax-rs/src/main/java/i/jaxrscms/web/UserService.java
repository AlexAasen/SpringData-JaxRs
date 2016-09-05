package i.jaxrscms.web;

import static i.jaxrscms.loader.ContextLoader.getBean;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import i.jpacms.exception.CmsException;
import i.jpacms.model.UserData;
import i.jpacms.model.WorkItemData;
import i.jpacms.service.CmsService;

import javax.ws.rs.core.UriInfo;

@Path("/users")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public final class UserService
{
	private static final AtomicInteger ids = new AtomicInteger(1000);

	@Context
	private UriInfo uriInfo;

	@POST
	public Response createUser(UserData user) throws CmsException
	{
		Integer maxMysqlId = getBean(CmsService.class).findUserByMaxUserId();
		if (maxMysqlId != null)
		{
			ids.getAndSet(maxMysqlId + 1);
		}
		int id = ids.getAndIncrement();
		user.setUserId(id);

		getBean(CmsService.class).saveUser(user);
		return Response.status(Status.CREATED).header("Location", "message/" + id).build();
	}

	@GET
	@Path("{userId}")
	public Response getUser(@PathParam("userId") int userId)
	{
		UserData userData = getBean(CmsService.class).findUserByUserId(userId);
		if (userData == null)
		{
			throw new NotFoundException();
		}
		return Response.ok(userData).build();
	}

	// Where is your search? In firstnames, lastnames, workItems? What are you
	// searching for/by in
	// the specific location? are you searching in workItems for all workItems
	// by a specific user?
	@GET
	@Path("{enquiry}/{variable}")
	public Response getBasedOnEnquiryAndVariable(@PathParam("enquiry") String enquiry, @PathParam("variable") String variable)
	{
		Collection<UserData> userResult = null;
		GenericEntity<Collection<UserData>> userEntity = null;
		Collection<WorkItemData> workItemResult = null;
		GenericEntity<Collection<WorkItemData>> workItemEntity = null;

		switch (enquiry)
		{
		case "firstName":
			userResult = getBean(CmsService.class).findUserByFirstName(variable);
			break;
		case "lastName":
			userResult = getBean(CmsService.class).findUserByLastName(variable);
			break;
		case "username":
			userResult = getBean(CmsService.class).findUserByUsername(variable);
			break;
		case "workItems":
			workItemResult = getBean(CmsService.class).findAllWorkItemsByUser(Integer.parseInt(variable));
			workItemEntity = new GenericEntity<Collection<WorkItemData>>(workItemResult)
			{
			};
			break;
		default:
			return Response.status(Status.BAD_REQUEST).build();
		}

		if (userResult != null)
		{
			userEntity = new GenericEntity<Collection<UserData>>(userResult)
			{
			};
			return Response.ok(userEntity).build();
		}
		else if (workItemResult != null)
		{
			return Response.ok(workItemEntity).build();
		}
		throw new WebApplicationException(Status.NOT_FOUND);
	}

	@GET
	public Response getAllUsers()
	{
		Collection<UserData> result = (Collection<UserData>) getBean(CmsService.class).findAllUsers();
		GenericEntity<Collection<UserData>> entity = new GenericEntity<Collection<UserData>>(result)
		{
		};
		return Response.ok(entity).build();
	}

	// Update
	@PUT
	@Path("{userId}")
	public Response updateUser(@PathParam("userId") int userId, UserData inputUser) throws CmsException
	{
		getBean(CmsService.class).updateUser(userId, inputUser);
		return Response.noContent().build();
	}

	@PUT
	@Path("{userId}/{teamId}")
	public Response addUserToTeam(@PathParam("userId") int userId, @PathParam("teamId") int teamId) throws CmsException
	{
		getBean(CmsService.class).addUserToTeam(teamId, userId);
		return Response.noContent().build();
	}

	// Delete
	@DELETE
	@Path("{userId}")
	@Transactional
	public Response deleteUser(@PathParam("userId") int userId) throws CmsException
	{
		getBean(CmsService.class).deleteUser(userId);
		return Response.ok().build();
	}
}
