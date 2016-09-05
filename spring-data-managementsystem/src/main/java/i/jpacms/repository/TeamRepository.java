package i.jpacms.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import i.jpacms.model.TeamData;

public interface TeamRepository extends PagingAndSortingRepository<TeamData, Long>
{
	@Query("select t from TeamData t where t.teamId = ?1")
	TeamData findTeamByTeamId(int teamId);
	
	@Query("select max(t.teamId) from TeamData t")
	Integer findTeamByMaxTeamId();
	
	@Transactional
	List<TeamData> removeByTeamId(int teamId);
}
