package i.jaxrscms.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonWriter;

import i.jpacms.model.WorkItemData;
import i.jpacms.model.WorkItemData.WorkItemStatus;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class WorkItemProvider implements MessageBodyWriter<WorkItemData>, MessageBodyReader<WorkItemData>
{
	private static final Gson gson = new GsonBuilder().registerTypeAdapter(WorkItemData.class, new WorkItemAdapter()).create();

	@Override
	public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType)
	{
		return aClass.isAssignableFrom(WorkItemData.class);
	}

	@Override
	public WorkItemData readFrom(Class<WorkItemData> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multiValuedMap,
			InputStream inputStream)
					throws IOException, WebApplicationException
	{
		return gson.fromJson(new InputStreamReader(inputStream), WorkItemData.class);
	}

	@Override
	public long getSize(WorkItemData workItem, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType)
	{
		return 0;
	}

	@Override
	public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType)
	{
		return aClass.isAssignableFrom(WorkItemData.class);
	}

	@Override
	public void writeTo(WorkItemData workItem, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multiValuedMap,
			OutputStream outputStream)
					throws IOException, WebApplicationException
	{
		try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream)))
		{
			gson.toJson(workItem, WorkItemData.class, writer);
		}
	}

	private static final class WorkItemAdapter implements JsonSerializer<WorkItemData>, JsonDeserializer<WorkItemData>
	{
		@Override
		public JsonElement serialize(WorkItemData workItem, Type type, JsonSerializationContext context)
		{
			JsonObject json = new JsonObject();
			json.addProperty("id", workItem.getId());
			json.addProperty("workItemId", workItem.getWorkItemId());
			json.addProperty("description", workItem.getDescription());
			json.addProperty("workItemStatus", workItem.getWorkItemStatus().toString());

			return json;
		}

		@Override
		public WorkItemData deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
		{
			JsonObject workItemJson = json.getAsJsonObject();
			String description = workItemJson.get("description").getAsString();
			String workItemStatus = workItemJson.get("workItemStatus").getAsString();

			return new WorkItemData(description, WorkItemStatus.valueOf(workItemStatus));
		}
	}
}
