package i.jpacms.validate;

import java.util.Collection;
import java.util.HashSet;

import i.jpacms.exception.CmsException;
import i.jpacms.model.TeamData;
import i.jpacms.model.UserData;
import i.jpacms.model.WorkItemData;
import i.jpacms.model.UserData.UserStatus;
import i.jpacms.model.WorkItemData.WorkItemStatus;

public final class Validator
{
	public static void workItemCanBeAssigned(WorkItemData workItem, UserData user) throws CmsException
	{
		userExists(user);
		workItemIsUnstarted(workItem);
		userIsActive(user);
		userHasRoomForMoreWorkItems(user);
		workItemIsPartOfUsersTeamsWorkItems(workItem, user);
	}

	public static void userIsValid(UserData user) throws CmsException
	{
		if (userNameIsShorterThan10Chars(user.getUsername()))
		{
			throw new CmsException("Username must be at least 10 characters long.");
		}
	}

	private static boolean userNameIsShorterThan10Chars(String username)
	{
		return username.trim().length() < 10;
	}

	public static boolean userAlreadyExists(TeamData team, UserData user)
	{
		return team.getUsers().contains(user);
	}

	private static void userHasRoomForMoreWorkItems(UserData user) throws CmsException
	{
		Collection<WorkItemData> workItems = new HashSet<WorkItemData>();

		for (WorkItemData workItem : user.getWorkItems())
		{
			if (!(workItem.getWorkItemStatus().equals(WorkItemStatus.DONE)))
			{
				workItems.add(workItem);
			}
		}
		if (workItems.size() >= 5)
		{
			throw new CmsException("Can't assign workItem to user, user has too many active workItems");
		}
	}

	private static void workItemIsPartOfUsersTeamsWorkItems(WorkItemData workItem, UserData user) throws CmsException
	{
		if (!(user.getTeam().getWorkItems().contains(workItem)))
		{
			throw new CmsException("Can't assign workItem to user, the workItem is not part of his/her Teams assignements");
		}
	}

	public static void teamHasAReasonableAmountOfUsers(TeamData team) throws CmsException
	{
		if (team.getUsers().size() >= team.getMaxUsers())
		{
			throw new CmsException("The selected team is already full");
		}
	}

	public static void WorkItemExists(TeamData team, WorkItemData workItem) throws CmsException
	{
		if ((team.getWorkItems().contains(workItem)))
		{
			throw new CmsException("Can't add workItem to list, workItem already exists");
		}
	}

	public static void workItemExists(WorkItemData workItem) throws CmsException
	{
		if (workItem == null)
		{
			throw new CmsException("Can't remove workItem, workItem doesn't exists");
		}
	}

	public static void userExistsInTeam(TeamData team, UserData user) throws CmsException
	{
		if (!(team.getUsers().contains(user)))
		{
			throw new CmsException("Can't remove user from team, user is not part of that team");
		}
	}

	private static void workItemIsUnstarted(WorkItemData workItem) throws CmsException
	{
		if (workItem.getWorkItemStatus().equals(WorkItemStatus.DONE))
		{
			throw new CmsException("WorkItem can't be assigned, it is already finished");
		}
		else if (workItem.getWorkItemStatus().equals(WorkItemStatus.STARTED))
		{
			throw new CmsException("WorkItem can't be assigned, it is already started");
		}
	}

	private static void userIsActive(UserData user) throws CmsException
	{
		if (user.getUserStatus().equals(UserStatus.INACTIVE))
		{
			throw new CmsException("Can't assign workItem to user, user is inactive");
		}
		else if (user.getUserStatus().equals(UserStatus.ON_VACATION))
		{
			throw new CmsException("Can't assign workItem to user at this moment, user is on vacation");
		}
		else if (user.getUserStatus().equals(UserStatus.SICK))
		{
			throw new CmsException("Can't assign workItem to user at this moment, user is sick");
		}
	}

	public static void userIsNotPartOfATeam(UserData user) throws CmsException
	{
		if (user.getTeam() != null)
		{
			throw new CmsException("User is already a part of a team");
		}
	}

	public static void teamExists(TeamData team) throws CmsException
	{
		if (team == null)
		{
			throw new CmsException("Can't inactivate team, team doesn't exist");
		}
	}

	public static void workItemHasAnActiveIssue(WorkItemData workItem) throws CmsException
	{
		if (workItem.getIssue() == null)
		{
			throw new CmsException("Can't update issue, workItem doesn't have an issue");
		}
	}

	public static void workItemIsDone(WorkItemData workItem) throws CmsException
	{
		if (!(workItem.getWorkItemStatus().equals(WorkItemStatus.DONE)))
		{
			throw new CmsException("Can't assign issue, issues can only be assigned to workItems with status DONE");
		}
	}

	public static void StatusChangeIsNotInvalid(WorkItemStatus status, WorkItemData workItem) throws CmsException
	{
		if (workItem.getUser() == null && status.equals(WorkItemStatus.DONE))
		{
			throw new CmsException("Can't change status to DONE, workItem isn't started.");
		}
	}

	public static void userExists(UserData user) throws CmsException
	{
		if(user == null)
		{
			throw new CmsException("User does not exist");
		}
		
	}

}
