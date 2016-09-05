package i.jaxrscms.web;

import static org.hamcrest.CoreMatchers.equalTo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import i.jaxrscms.provider.UserProvider;
import i.jpacms.model.TeamData;
import i.jpacms.model.TeamData.TeamStatus;
import i.jpacms.model.UserData;
import i.jpacms.model.UserData.UserStatus;

public class TestUserService
{
	private static final String userUrl = "http://localhost:8080/web-cms/users";
	private static final String teamUrl = "http://localhost:8080/web-cms/teams";
	private static Client client;
	private static UserStatus userStatus = UserStatus.ACTIVE;
	static UserData user1;
	private static UserData user2;
	private static UserData user3;
	private static GenericType<List<UserData>> userType = new GenericType<List<UserData>>() {
	};
	
//	@Rule
//	public ExpectedException thrown = ExpectedException.none();

	@BeforeClass
	public static void initializeTests()
	{
		client = ClientBuilder.newClient().register(UserProvider.class);
		user1 = new UserData("firstUsername", "firstName1", "lastName1", userStatus);   //1000
		user2 = new UserData("secondUserName", "firstName2", "lastName2", userStatus);   //1001
		user3 = new UserData("thirdUsername", "firstName3", "lastName3", userStatus);  //1002	
		
		client.target(userUrl).request().post(Entity.entity(user1,  MediaType.APPLICATION_JSON), UserData.class);
		client.target(userUrl).request().post(Entity.entity(user2,  MediaType.APPLICATION_JSON), UserData.class);
		client.target(userUrl).request().post(Entity.entity(user3,  MediaType.APPLICATION_JSON), UserData.class);
	}
	
	@Before
	public void setUpMethods()
	{	
		user1 = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1000).request().get(UserData.class);		
		user2 = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1001).request().get(UserData.class);
		user3 = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1002).request().get(UserData.class);
	}

	@Test
	public void canGetUserById()
	{	
		UserData persistedUser = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1000).request().get(UserData.class);
	    assertThat(persistedUser, equalTo(user1));
	}
	
	@Test
	public void canUpdateUser()
	{
		UserData updated = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1001).request().get(UserData.class);
		updated.setUsername("updatedUsername");
		
		client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1001).request().put(Entity.entity(updated,  MediaType.APPLICATION_JSON), UserData.class);
		UserData persistedUser = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1001).request().get(UserData.class);
	
		assertThat(persistedUser, equalTo(updated));
	}
	
	@Test
	public void canGetUserBasedOnFirstName()
	{
		List<UserData> persistedUser = client.target(userUrl).path("/{enquiry}/{variable}").resolveTemplate("enquiry", "firstName").resolveTemplate("variable", "firstName1").request().get(userType);
		assertThat(persistedUser.size(), equalTo(1));
		assertThat(persistedUser.get(0), is(equalTo(user1)));
	}
	
	@Test
	public void canGetUserBasedOnLastName()
	{
		List<UserData> persistedUser = client.target(userUrl).path("/{enquiry}/{variable}").resolveTemplate("enquiry", "lastName").resolveTemplate("variable", "lastName2").request().get(userType);
		assertThat(persistedUser.size(), equalTo(1));
		assertThat(persistedUser.get(0), is(equalTo(user2)));
	}
	 
	@Test
	public void canGetUserBasedOnUsername()
	{
		List<UserData> persistedUser = client.target(userUrl).path("/{enquiry}/{variable}").resolveTemplate("enquiry", "username").resolveTemplate("variable", "firstUsername").request().get(userType);
	    assertThat(persistedUser.size(), equalTo(1));
	    assertThat(persistedUser.get(0), is(equalTo(user1)));
	}
	
	@Test
	public void canGetAllUsers()
	{
		List<UserData> persistedUsers = client.target(userUrl).request().get(userType);
		assertThat(persistedUsers.size(), equalTo(3));
	}
	
	@Test
	public void canRemoveUser()
	{	
		UserData user = new UserData("fourthUsername", "firstName4", "lastName4", UserStatus.ACTIVE);
		client.target(userUrl).request().post(Entity.entity(user, MediaType.APPLICATION_JSON), UserData.class);
		
		UserData persistedUser = client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1003).request().get(UserData.class);
		assertThat(persistedUser, is(equalTo(user)));
		
		client.target(userUrl).path("/{userId}").resolveTemplate("userId", 1003).request().delete(UserData.class);
		List<UserData> persistedUsers = client.target(userUrl).request().get(userType);
		assertFalse(persistedUsers.contains(user));
	}
	
	@Test
	public void canAddUserToTeam()
	{
		TeamData team = new TeamData("teamName1", TeamStatus.INACTIVE);
		client.target(teamUrl).request().post(Entity.entity(team, MediaType.APPLICATION_JSON), TeamData.class);
		client.target(userUrl).path("/{userId}/{teamId}").resolveTemplate("userId", 1000).resolveTemplate("teamId", 2000).request().put(Entity.entity(user1,  MediaType.APPLICATION_JSON), UserData.class);
		
		List<UserData> users = client.target(teamUrl).path("/{teamId}/{enquiry}").resolveTemplate("teamId", 2000).resolveTemplate("enquiry", "users").request().get(userType);
		assertTrue(users.contains(user1));
	}

}
