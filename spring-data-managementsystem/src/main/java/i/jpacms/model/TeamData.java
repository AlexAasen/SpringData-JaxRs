package i.jpacms.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class TeamData extends AbstractEntityData
{
	@Column(unique = true, updatable = false)
	private int teamId;
	@Column(nullable = false)
	private String teamName;
	@OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
	private Collection<UserData> users;
	@Transient
	private static final int MAX_USERS = 10;
	@OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
	private Collection<WorkItemData> workItems;

	@Enumerated(EnumType.STRING)
	private TeamStatus teamStatus;

	public enum TeamStatus
	{
		ACTIVE, INACTIVE;
	}

	protected TeamData()
	{
	}

	public TeamData(String teamName)
	{
		this.teamName = teamName;
		this.teamStatus = TeamStatus.INACTIVE;
	}
	
	public TeamData(String teamName, TeamStatus teamStatus)
	{
		this.teamName = teamName;
		this.teamStatus = teamStatus;
	}

	public TeamStatus getTeamStatus()
	{
		return this.teamStatus;
	}

	public void setTeamStatus(TeamStatus teamStatus)
	{
		this.teamStatus = teamStatus;
	}
	
	public void setTeamId(int teamId)
	{
		this.teamId = teamId;
	}

	public String getTeamName()
	{
		return this.teamName;
	}

	public void addUserToTeam(UserData user)
	{
		this.users.add(user);
	}

	public void addWorkItem(WorkItemData workItem)
	{
		this.workItems.add(workItem);
	}

	public Collection<WorkItemData> getWorkItems()
	{
		return this.workItems;
	}

	public Collection<UserData> getUsers()
	{
		return this.users;
	}

	public int getMaxUsers()
	{
		return MAX_USERS;
	}

	public int getTeamId()
	{
		return this.teamId;
	}

	public void removeUser(UserData user)
	{
		this.users.remove(user);
	}
	
	public void setTeamName(String teamName)
	{
		this.teamName = teamName;
	}

	@Override
	public int hashCode()
	{
		int result = 11;
		result += 37 * this.teamName.hashCode();
		result += 37 * this.teamStatus.hashCode();

		return result;
	}

	@Override
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		if (other instanceof TeamData)
		{
			TeamData otherTeam = (TeamData) other;
			return this.teamName.equals(otherTeam.teamName) && this.teamStatus.equals(otherTeam.teamStatus);
		}
		return false;
	}

	public String shortToString()
	{
		return this.teamName;
	}

	@Override
	public String toString()
	{
		return this.teamId + " " + this.teamName + ", Status: " + this.teamStatus + ", Users: " + this.users.toString() + ", WorkItems: " + this.workItems.toString();
	}

	public void updateValues(TeamData inputTeam)
	{
		this.teamName = inputTeam.teamName;
		this.teamStatus = inputTeam.teamStatus;
	}
}
