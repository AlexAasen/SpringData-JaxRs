package i.jaxrscms.web;

import static i.jaxrscms.loader.ContextLoader.getBean;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

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

import i.jpacms.exception.CmsException;
import i.jpacms.model.WorkItemData;
import i.jpacms.model.WorkItemData.WorkItemStatus;
import i.jpacms.service.CmsService;

import javax.ws.rs.core.UriInfo;

@Path("/workItems")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public final class WorkItemService
{
	private static final AtomicInteger ids = new AtomicInteger(3000);
	@Context
	UriInfo uriInfo;

	@POST
	public Response createWorkItem(WorkItemData workItem)
	{
		Integer maxMysqlId = getBean(CmsService.class).findWorkItemByMaxWorkItemId();
		if(maxMysqlId != null)
		{
			ids.getAndSet(maxMysqlId + 1);
		}
		int id = ids.getAndIncrement();
		workItem.setWorkItemId(id);
		
		getBean(CmsService.class).saveWorkItem(workItem);
		return Response.status(Status.CREATED).header("Location", "message/" + id + workItem).build();
	}

	@GET
	@Path("{workItemId}")
	public Response getWorkItem(@PathParam("workItemId") int workItemId)
	{
		WorkItemData workItem = getBean(CmsService.class).findWorkItemById(workItemId);

		if (workItem == null)
		{
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return Response.ok(workItem).build();
		}
	
	//Where are you looking for workItems/What are you searching by? status or description?
	//Which status/description are you searching by?
	//QueryParams, build natural paths teams/teamId/WorkItems  - put, get etc
	@GET
	@Path("{enquiry}/{variable}")
	public Response getWorkItemsBasedOnEnquiry(@PathParam("enquiry") String enquiry, @PathParam("variable") String variable)
	{
		Collection<WorkItemData> workItemResult = null;
		GenericEntity<Collection<WorkItemData>> workItemEntity = null;
		
		switch(enquiry)
		{
			case "status":
				workItemResult = getBean(CmsService.class).findAllWorkItemsByStatus(WorkItemStatus.valueOf(variable));
				break;
			case "description":
				workItemResult = getBean(CmsService.class).findWorkItemsByDescription(variable);
				break;
			default:
				return Response.status(Status.BAD_REQUEST).build();
		}
		if(workItemResult != null)
		{
			workItemEntity = new GenericEntity<Collection<WorkItemData>>(workItemResult)
			{
			};
			return Response.ok(workItemEntity).build();
		}
		throw new WebApplicationException(Status.NOT_FOUND);
	}
	
	@GET
	public Response getAllWorkItems()
	{
		Collection<WorkItemData> workItemResult= (Collection<WorkItemData>)getBean(CmsService.class).findAllWorkItems();
		GenericEntity<Collection<WorkItemData>> workItemEntity = new GenericEntity<Collection<WorkItemData>>(workItemResult)
		{
		};
		return Response.ok(workItemEntity).build();
	}
	
	@PUT
	@Path("{workItemId}/{type}/{id}")
	public Response addWorkItemToSpecificTypesId(@PathParam("workItemId") int workItemId, @PathParam("type") String type, @PathParam("id") int id) throws CmsException
	{
		switch(type)
		{
			case "user":
				getBean(CmsService.class).assignWorkItem(workItemId, id);
				break;
			case "team":
				getBean(CmsService.class).addWorkItemToTeam(id, workItemId);
				break;
			default: 
				return Response.status(Status.BAD_REQUEST).build();
		}
		return Response.noContent().build();
	}
	
	@PUT
	@Path("{workItemId}")
	public Response updateWorkItem(@PathParam("workItemId") int workItemId, WorkItemData inputWorkItem) throws CmsException
	{
		getBean(CmsService.class).updateWorkItem(workItemId, inputWorkItem);
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("{workItemId}")
	public Response deleteWorkItem(@PathParam("workItemId") int workItemId) throws CmsException
	{
		getBean(CmsService.class).removeWorkItem(workItemId);
		return Response.noContent().build();
	}
}
