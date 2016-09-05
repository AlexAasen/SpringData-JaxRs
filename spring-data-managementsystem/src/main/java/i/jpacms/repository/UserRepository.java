package i.jpacms.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import i.jpacms.model.UserData;

public interface UserRepository extends PagingAndSortingRepository<UserData, Long>
{
	@Query("select u from UserData u where u.userId = ?1")
	UserData findUserByUserId(int userId);

	@Query("select u from UserData u where u.username = ?1")
	List<UserData> findUserByUsername(String username);

	@Query("select u from UserData u where u.firstName = ?1")
	List<UserData> findUserByFirstName(String firstName);
	
	@Query("select u from UserData u where u.lastName = ?1")
	List<UserData> findUserByLastName(String lastName);

	@Query("select u from UserData u where u.team.teamId = ?1")
	List<UserData> findUsersInTeam(int teamId);
	
	@Query("select max(u.userId) from UserData u")
	Integer findUserByMaxUserId();
	
	@Transactional
	List<UserData> removeByUserId(int userId);

}
