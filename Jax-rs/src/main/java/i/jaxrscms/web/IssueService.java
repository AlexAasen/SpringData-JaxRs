package i.jaxrscms.web;

import static i.jaxrscms.loader.ContextLoader.getBean;

import java.util.Collection;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import i.jpacms.exception.CmsException;
import i.jpacms.model.IssueData;
import i.jpacms.model.WorkItemData;
import i.jpacms.service.CmsService;

@Path("/issues")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class IssueService
{
	@Context
	UriInfo uriInfo;
	
	@POST
	@Path("{workItemId}")
	@Transactional
	public Response addIssueToWorkItem(@PathParam("workItemId") int workItemId, IssueData issue) throws CmsException
	{	
		getBean(CmsService.class).createIssue(issue, workItemId);
		return Response.noContent().build();
	}
	
	@GET
	public Response getAllWorkItemsWithAnIssue()
	{
		Collection<WorkItemData> workItemResult= (Collection<WorkItemData>) getBean(CmsService.class).returnAllWorkItemsWithAnIssue();
		GenericEntity<Collection<WorkItemData>> workItemEntity = new GenericEntity<Collection<WorkItemData>>(workItemResult)
		{
		};
		return Response.ok(workItemEntity).build();
	}
	
	@PUT
	@Path("{workItemId}")
	public Response updateIssue(@PathParam("workItemId") int workItemId, IssueData issue) throws CmsException
	{
		getBean(CmsService.class).updateIssue(workItemId, issue);
		return Response.noContent().build();
	}
	

}
