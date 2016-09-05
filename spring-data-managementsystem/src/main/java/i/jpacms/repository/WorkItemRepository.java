package i.jpacms.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import i.jpacms.model.WorkItemData;
import i.jpacms.model.WorkItemData.WorkItemStatus;

public interface WorkItemRepository extends PagingAndSortingRepository<WorkItemData, Long>
{

	@Query("select w from WorkItemData w where w.workItemId = ?1")
	WorkItemData findWorkItemByWorkItemId(int workItemId);

	@Query("select w from WorkItemData w where w.workItemStatus = ?1")
	List<WorkItemData> findWorkItemsByworkItemStatus(WorkItemStatus workItemStatus);

	@Query("select w from WorkItemData w where w.team.teamId = ?1")
	List<WorkItemData> findWorkItemsByTeam(int teamId);

	@Query("select w from WorkItemData w where w.user.userId = ?1")
	List<WorkItemData> findWorkItemsByUser(int userId);

	List<WorkItemData> findByDescriptionLike(String description);

	@Query("select u from WorkItemData u where u.issue is not null")
	List<WorkItemData> findWorkItemByIssue();

	@Query("select w from WorkItemData w where w.lastModifiedDate >= ?1 and w.lastModifiedDate <= ?2 and w.workItemStatus = ?3")
	Page<WorkItemData> workItemCompletedBetweenDates(Date fromDate, Date toDate, WorkItemStatus workItemStatus, Pageable pageable);
	
	@Query("select max(w.workItemId) from WorkItemData w")
	Integer findWorkItemByMaxWorkItemId();

	@Transactional
	List<WorkItemData> removeByWorkItemId(int finalWorkItemId);

}
