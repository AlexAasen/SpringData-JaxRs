package i.jpacms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import i.jpacms.exception.CmsException;
import i.jpacms.model.IssueData;
import i.jpacms.model.TeamData;
import i.jpacms.model.UserData;
import i.jpacms.model.WorkItemData;
import i.jpacms.model.TeamData.TeamStatus;
import i.jpacms.model.UserData.UserStatus;
import i.jpacms.model.WorkItemData.WorkItemStatus;
import i.jpacms.repository.TeamRepository;
import i.jpacms.repository.UserRepository;
import i.jpacms.repository.WorkItemRepository;
import i.jpacms.validate.Validator;

@Service
public class CmsService
{
	final UserRepository userRepository;
	final TeamRepository teamRepository;
	final WorkItemRepository workItemRepository;

	@Autowired
	public CmsService(UserRepository userRepository, TeamRepository teamRepository, WorkItemRepository workItemRepository)
	{
		this.userRepository = userRepository;
		this.teamRepository = teamRepository;
		this.workItemRepository = workItemRepository;
	}

	// -- User
	public UserData saveUser(UserData user) throws CmsException
	{
		Validator.userIsValid(user);
		return this.userRepository.save(user);
	}

	@Transactional
	public UserData updateUser(int userId, UserData inputUser) throws CmsException
	{
		UserData user = this.userRepository.findUserByUserId(userId);
		Validator.userExists(user);
		user.updateValues(inputUser);
		if (user.getUserStatus().equals(UserStatus.INACTIVE))
		{
			for (WorkItemData workItem : user.getWorkItems())
			{
				workItem.setWorkItemStatus(WorkItemStatus.UNSTARTED);
				workItem.setUser(null);
				saveWorkItem(workItem);
			}
		}
		return saveUser(user);
	}

	public UserData findUserByUserId(int userId)
	{
		return this.userRepository.findUserByUserId(userId);
	}

	public List<UserData> findUserByUsername(String username)
	{
		return this.userRepository.findUserByUsername(username);
	}
	
	public List<UserData> findUserByFirstName(String firstName)
	{
		return this.userRepository.findUserByFirstName(firstName);
	}
	
	public List<UserData> findUserByLastName(String lastName)
	{
		return this.userRepository.findUserByLastName(lastName);
	}

	public List<UserData> returnAllUsersInTeam(int teamId)
	{
		return this.userRepository.findUsersInTeam(teamId);
	}

	public Page<UserData> findAllUsers(Pageable pageable)
	{
		return this.userRepository.findAll(pageable);
	}
	
	public Iterable<UserData> findAllUsers()
	{
		return this.userRepository.findAll();
	}
	
	public Integer findUserByMaxUserId()
	{
		return this.userRepository.findUserByMaxUserId();
	}

	@Transactional
	public UserData addUserToTeam(int teamId, int userId) throws CmsException
	{
		TeamData team = this.teamRepository.findTeamByTeamId(teamId);
		UserData user = findUserByUserId(userId);
		Validator.userExists(user);
		Validator.teamExists(team);
		Validator.userIsNotPartOfATeam(user);
		Validator.teamHasAReasonableAmountOfUsers(team);

		user.setTeam(team);
		team.setTeamStatus(TeamStatus.ACTIVE);
		return saveUser(user);
	}
	
	@Transactional
	public List<UserData> deleteUser(int userId) throws CmsException
	{
		UserData user = userRepository.findUserByUserId(userId);
		Validator.userExists(user);
		
		return this.userRepository.removeByUserId(userId);	
	}

	// --------Team
	public TeamData saveTeam(TeamData team)
	{
		return this.teamRepository.save(team);
	}

	@Transactional
	public TeamData updateTeam(int teamId, TeamData inputTeam) throws CmsException
	{
		TeamData team = this.teamRepository.findTeamByTeamId(teamId);
		Validator.teamExists(team);
		team.updateValues(inputTeam);

		if (team.getTeamStatus().equals(TeamStatus.INACTIVE))
		{
			return saveTeam(makeNecessaryTeamChanges(team));
		}
		return saveTeam(team);
	}

	private TeamData makeNecessaryTeamChanges(TeamData team) throws CmsException
	{
		if (team.getUsers() != null)
		{
			for (UserData user : team.getUsers())
			{
				user.setTeam(null);
				saveUser(user);
			}
		}
		if (team.getWorkItems() != null)
		{
			for (WorkItemData workItem : team.getWorkItems())
			{
				workItem.setTeam(null);
				workItem.setUser(null);
				saveWorkItem(workItem);
			}
		}
		team.getWorkItems().clear();
		team.getUsers().clear();
		return team;
	}
	
	@Transactional
	public List<TeamData> deleteTeam(int teamId) throws CmsException
	{
		TeamData team = teamRepository.findTeamByTeamId(teamId);
		Validator.teamExists(team);
		
		return this.teamRepository.removeByTeamId(teamId);
	}

	@Transactional
	public TeamData removeUserFromTeam(int teamId, int userId) throws CmsException
	{
		TeamData team = this.teamRepository.findTeamByTeamId(teamId);
		UserData user = this.userRepository.findUserByUserId(userId);
		Validator.userExistsInTeam(team, user);

		team.removeUser(user);
		user.setTeam(null);
		return saveTeam(team);
	}

	public TeamData findTeamByTeamId(int teamId)
	{
		return this.teamRepository.findTeamByTeamId(teamId);
	}

	public Page<TeamData> findAllTeams(Pageable pageable)
	{
		return this.teamRepository.findAll(pageable);
	}
	
	public Iterable<TeamData> findAllTeams()
	{
		return this.teamRepository.findAll();
	}
	
	public Integer findTeamByMaxTeamId()
	{
		return this.teamRepository.findTeamByMaxTeamId();
	}

	// ---------WorkItems
	public WorkItemData saveWorkItem(WorkItemData workItem)
	{
		return this.workItemRepository.save(workItem);
	}

	@Transactional
	public WorkItemData addWorkItemToTeam(int teamId, int workItemId) throws CmsException
	{
		WorkItemData workItem = this.workItemRepository.findWorkItemByWorkItemId(workItemId);
		TeamData team = this.teamRepository.findTeamByTeamId(teamId);

		Validator.workItemExists(workItem);

		workItem.setTeam(team);
		return saveWorkItem(workItem);
	}

	@Transactional
	public WorkItemData assignWorkItem(int workItemId, int userId) throws CmsException
	{
		UserData user = this.userRepository.findUserByUserId(userId);
		WorkItemData workItem = this.workItemRepository.findWorkItemByWorkItemId(workItemId);

		Validator.workItemCanBeAssigned(workItem, user);

		workItem.setUser(user);
		workItem.setWorkItemStatus(WorkItemStatus.STARTED);
		return saveWorkItem(workItem);
	}

	@Transactional
	public WorkItemData updateWorkItem(int workItemId, WorkItemData inputWorkItem) throws CmsException
	{
		WorkItemData workItem = this.workItemRepository.findWorkItemByWorkItemId(workItemId);
		Validator.workItemExists(workItem);
		Validator.StatusChangeIsNotInvalid(inputWorkItem.getWorkItemStatus(), workItem);
		
		workItem.updateValues(inputWorkItem);
		if(workItem.getWorkItemStatus().equals(WorkItemStatus.UNSTARTED))
		{
			workItem.setUser(null);
			workItem.setTeam(null);
		}
		return saveWorkItem(workItem);
	}

	@Transactional
	public List<WorkItemData> removeWorkItem(int workItemId) throws CmsException
	{
		WorkItemData workItem = this.workItemRepository.findWorkItemByWorkItemId(workItemId);
		Validator.workItemExists(workItem);
		return this.workItemRepository.removeByWorkItemId(workItemId);
	}

	public List<WorkItemData> findAllWorkItemsByStatus(WorkItemStatus status)
	{
		return this.workItemRepository.findWorkItemsByworkItemStatus(status);
	}

	public WorkItemData findWorkItemById(int workItemId)
	{
		return this.workItemRepository.findWorkItemByWorkItemId(workItemId);
	}

	public List<WorkItemData> findAllWorkItemsInTeam(int teamId)
	{
		return this.workItemRepository.findWorkItemsByTeam(teamId);
	}

	public List<WorkItemData> findAllWorkItemsByUser(int userId)
	{
		return this.workItemRepository.findWorkItemsByUser(userId);
	}

	public List<WorkItemData> findWorkItemsByDescription(String description)
	{
		return this.workItemRepository.findByDescriptionLike((description + "%"));
	}

	public Page<WorkItemData> findAllWorkItems(Pageable pageable)
	{
		return this.workItemRepository.findAll(pageable);
	}
	
	public Iterable<WorkItemData> findAllWorkItems()
	{
		return this.workItemRepository.findAll();
	}

	public Page<WorkItemData> completedWorkItemsBetweenDates(Date fromDate, Date toDate, Pageable pageable)
	{
		return this.workItemRepository.workItemCompletedBetweenDates(fromDate, toDate, WorkItemStatus.DONE, pageable);
	}

	public Page<WorkItemData> workItemsBetweenDatesWithStatus(Date fromDate, Date toDate, WorkItemStatus workItemStatus, Pageable pageable)
	{
		return this.workItemRepository.workItemCompletedBetweenDates(fromDate, toDate, workItemStatus, pageable);
	}
	
	public Integer findWorkItemByMaxWorkItemId()
	{
		return this.workItemRepository.findWorkItemByMaxWorkItemId();
	}

	// --------------Issue
	@Transactional
	public WorkItemData createIssue(IssueData issue, int workItemId) throws CmsException
	{
		WorkItemData workItem = this.workItemRepository.findWorkItemByWorkItemId(workItemId);
		Validator.workItemIsDone(workItem);
		workItem.setIssue(issue);
		workItem.setWorkItemStatus(WorkItemStatus.UNSTARTED);
		workItem.setUser(null);
		return saveWorkItem(workItem);
	}

	public WorkItemData updateIssue(int workItemId, IssueData updatedIssue) throws CmsException
	{
		WorkItemData workItem = this.workItemRepository.findWorkItemByWorkItemId(workItemId);
		Validator.workItemHasAnActiveIssue(workItem);
		workItem.setIssue(updatedIssue);
		return saveWorkItem(workItem);
	}

	public List<WorkItemData> returnAllWorkItemsWithAnIssue()
	{
		return this.workItemRepository.findWorkItemByIssue();
	}
}
