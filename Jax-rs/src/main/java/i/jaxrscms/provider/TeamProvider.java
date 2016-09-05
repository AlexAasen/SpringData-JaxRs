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

import i.jpacms.model.TeamData;
import i.jpacms.model.TeamData.TeamStatus;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class TeamProvider implements MessageBodyWriter<TeamData>, MessageBodyReader<TeamData>
{
	private static final Gson gson = new GsonBuilder().registerTypeAdapter(TeamData.class, new TeamAdapter()).create();

	@Override
	public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType)
	{
		return aClass.isAssignableFrom(TeamData.class);
	}

	@Override
	public TeamData readFrom(Class<TeamData> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multiValuedMap,
			InputStream inputStream)
					throws IOException, WebApplicationException
	{
		return gson.fromJson(new InputStreamReader(inputStream), TeamData.class);
	}

	@Override
	public long getSize(TeamData team, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType)
	{
		return 0;
	}

	@Override
	public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType)
	{
		return aClass.isAssignableFrom(TeamData.class);
	}

	@Override
	public void writeTo(TeamData team, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multiValuedMap,
			OutputStream outputStream)
					throws IOException, WebApplicationException
	{
		try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream)))
		{
			gson.toJson(team, TeamData.class, writer);
		}
	}

	private static final class TeamAdapter implements JsonSerializer<TeamData>, JsonDeserializer<TeamData>
	{
		@Override
		public JsonElement serialize(TeamData team, Type type, JsonSerializationContext context)
		{
			JsonObject json = new JsonObject();
			json.addProperty("id", team.getId());
			json.addProperty("teamId", team.getTeamId());
			json.addProperty("teamName", team.getTeamName());
			json.addProperty("teamStatus", team.getTeamStatus().toString());

			return json;
		}

		@Override
		public TeamData deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
		{
			JsonObject teamJson = json.getAsJsonObject();
			String teamName = teamJson.get("teamName").getAsString();
			String teamStatus = teamJson.get("teamStatus").getAsString();

			return new TeamData(teamName, TeamStatus.valueOf(teamStatus));
		}

	}

}
