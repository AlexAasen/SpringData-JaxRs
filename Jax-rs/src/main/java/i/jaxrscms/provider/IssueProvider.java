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

import i.jpacms.model.IssueData;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IssueProvider implements MessageBodyWriter<IssueData>, MessageBodyReader<IssueData>
{
	
	private static final Gson gson = new GsonBuilder().registerTypeAdapter(IssueData.class, new IssueDataAdapter()).create();

	@Override
	public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType)
	{
		return aClass.isAssignableFrom(IssueData.class);
	}

	@Override
	public IssueData readFrom(Class<IssueData> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multiValuedMap,
			InputStream inputStream)
					throws IOException, WebApplicationException
	{
		return gson.fromJson(new InputStreamReader(inputStream), IssueData.class);
	}

	@Override
	public long getSize(IssueData issue, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType)
	{
		return 0;
	}

	@Override
	public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType)
	{
		return aClass.isAssignableFrom(IssueData.class);
	}

	@Override
	public void writeTo(IssueData issue, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multiValuedMap,
			OutputStream outputStream)
					throws IOException, WebApplicationException
	{
		try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream)))
		{
			gson.toJson(issue, IssueData.class, writer);
		}
	}


	private static final class IssueDataAdapter implements JsonSerializer<IssueData>, JsonDeserializer<IssueData>
	{
		@Override
		public JsonElement serialize(IssueData issue, Type type, JsonSerializationContext context)
		{
			JsonObject json = new JsonObject();
			json.addProperty("issue", issue.getIssue());

			return json;
		}

		@Override
		public IssueData deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
		{
			JsonObject issueJson = json.getAsJsonObject();
			String issue = issueJson.get("issue").getAsString();

			return new IssueData(issue);
		}
	}
}

