package i.jpacms.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class UserData extends AbstractEntityData
{
	@Column(nullable = false)
	private String username;
	@Column(unique = true, updatable = false)
	private int userId;
	@Column(nullable = false)
	private String firstName;
	@Column(nullable = false)
	private String lastName;
	@Transient
	private static final int MAX_WORK_ITEMS = 5;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private TeamData team;
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	private Set<WorkItemData> workItems;

	@Enumerated(EnumType.STRING)
	private UserStatus userStatus;

	public enum UserStatus
	{
		ACTIVE, ON_VACATION, SICK, INACTIVE;
	}

	protected UserData()
	{
	}

	public UserData(String username, String firstName, String lastName, UserStatus userStatus)
	{
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userStatus = userStatus;
	}

	public void addWorkItem(WorkItemData workItem)
	{
		this.workItems.add(workItem);
	}

	public Set<WorkItemData> getWorkItems()
	{
		return this.workItems;
	}

	public TeamData getTeam()
	{
		return this.team;
	}

	public int getMaxWorkItems()
	{
		return MAX_WORK_ITEMS;
	}

	public String getUsername()
	{
		return this.username;
	}

	public int getUserId()
	{
		return this.userId;
	}

	public String getFirstName()
	{
		return this.firstName;
	}

	public String getLastName()
	{
		return this.lastName;
	}

	public UserStatus getUserStatus()
	{
		return this.userStatus;
	}
	
	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	public void setUserStatus(final UserStatus userStatus)
	{
		this.userStatus = userStatus;
	}

	public void setTeam(TeamData team)
	{
		this.team = team;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public void updateValues(final UserData user)
	{
		this.username = user.username;
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.userStatus = user.userStatus;
	}

	@Override
	public int hashCode()
	{
		int result = 11;
		result += 37 * this.username.hashCode();
		result += 37 * this.firstName.hashCode();
		result += 37 * this.lastName.hashCode();
		result += 37 * this.userStatus.hashCode();

		return result;
	}

	@Override
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		if (other instanceof UserData)
		{
			UserData otherUser = (UserData) other;
			return this.username.equals(otherUser.username) && this.firstName.equals(otherUser.firstName) && this.lastName.equals(otherUser.lastName)
				 && this.userStatus.equals(otherUser.userStatus);
		}
		return false;
	}

	@Override
	public String toString()
	{
		return getId() + ", Userid: " + this.userId + ", Firstname: " + " " + this.firstName + ", Lastname: " + this.lastName + ", Username: " + this.username + ", Status: "
				+ this.userStatus;
	}
}
