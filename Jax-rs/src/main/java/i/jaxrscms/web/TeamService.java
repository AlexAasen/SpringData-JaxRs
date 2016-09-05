package i.jaxrscms.web;

import static i.jaxrscms.loader.ContextLoader.getBean;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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

import i.jaxrscms.exception.TeamNotFoundException;
import i.jpacms.exception.CmsException;
import i.jpacms.model.TeamData;
import i.jpacms.model.UserData;
import i.jpacms.model.WorkItemData;
import i.jpacms.service.CmsService;

import javax.ws.rs.core.UriInfo;

@Path("/teams")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public final class TeamService
{
	private static final AtomicInteger ids = new AtomicInteger(2000);
	@Context
	private UriInfo uriInfo;

	@POST
	public Response createTeam(TeamData team)
	{
		Integer maxMysqlId = getBean(CmsService.class).findTeamByMaxTeamId();
		if (maxMysqlId != null)
		{
			ids.getAndSet(maxMysqlId + 1);
		}
		int id = ids.getAndIncrement();
		team.setTeamId(id);

		getBean(CmsService.class).saveTeam(team);
		return Response.status(Status.CREATED).header("Location", "message/" + id).build();
	}

	@GET
	@Path("{teamId}")
	public Response getTeam(@PathParam("teamId") int teamId)
	{
		TeamData team = getBean(CmsService.class).findTeamByTeamId(teamId);
		if (team == null)
		{
			throw new TeamNotFoundException(teamId);
		}
		return Response.ok(team).build();
	}

	//In this team, I'm searching for....
	@GET
	@Path("{teamId}/{enquiry}")
	public Response makeAnEnquiryInTheSecifikTeam(@PathParam("enquiry") String enquiry, @PathParam("teamId") int teamId)
	{
		Collection<UserData> userResult = null;
		GenericEntity<Collection<UserData>> userEntity = null;

		Collection<WorkItemData> workItemResult = null;
		GenericEntity<Collection<WorkItemData>> workItemEntity = null;

		switch (enquiry)
		{
		case "users":
			userResult = getBean(CmsService.class).returnAllUsersInTeam(teamId);
			userEntity = new GenericEntity<Collection<UserData>>(userResult)
			{
			};
		case "workItems":
			workItemResult = getBean(CmsService.class).findAllWorkItemsInTeam(teamId);
			workItemEntity = new GenericEntity<Collection<WorkItemData>>(workItemResult)
			{
			};
			break;
		default:
			return Response.status(Status.BAD_REQUEST).build();
		}

		if (userResult == null)
		{
			if (workItemResult != null)
			{
				return Response.ok(workItemEntity).build();
			}
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return Response.ok(userEntity).build();
	}

	@GET
	public Response getAllTeams()
	{
		Collection<TeamData> result = (Collection<TeamData>) getBean(CmsService.class).findAllTeams();
		GenericEntity<Collection<TeamData>> entity = new GenericEntity<Collection<TeamData>>(result)
		{
		};
		return Response.ok(entity).build();
	}

	@PUT
	@Path("{teamId}")
	public Response updateTeam(@PathParam("teamId") int teamId, TeamData inputTeam) throws CmsException
	{
		getBean(CmsService.class).updateTeam(teamId, inputTeam);
		return Response.noContent().build();
	}

	@DELETE
	@Path("{teamId}")
	@Transactional
	public Response deleteTeam(@PathParam("teamId") int teamId) throws CmsException
	{
		getBean(CmsService.class).deleteTeam(teamId);
		return Response.noContent().build();
	}
}
