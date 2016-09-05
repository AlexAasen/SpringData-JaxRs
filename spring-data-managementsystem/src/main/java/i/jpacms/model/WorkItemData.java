package i.jpacms.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;


@Entity
public class WorkItemData extends AbstractEntityData{
   
   @Column(unique = true, updatable = false)
   private int workItemId;
   @Column(nullable = false)
   private String description;
   @Enumerated(EnumType.STRING)
   private WorkItemStatus workItemStatus;
   @ManyToOne(cascade = CascadeType.PERSIST)
   private TeamData team;
   @ManyToOne(cascade = CascadeType.PERSIST)
   private UserData user;
   @Embedded
   private IssueData issue;
   
   public enum WorkItemStatus{
      DONE, STARTED, UNSTARTED
   }
   
   
   protected WorkItemData(){
   }
   
   
   public WorkItemData(String description){
      this.description = description;
      this.workItemStatus = WorkItemStatus.UNSTARTED;
   }
   
   public WorkItemData(String description, WorkItemStatus workItemStatus){
	      this.description = description;
	      this.workItemStatus = workItemStatus;
	   }
   
   
   public int getWorkItemId(){
      return this.workItemId;
   }
   
   
   public UserData getUser(){
      return this.user;
   }
   
   
   public IssueData getIssue(){
      return this.issue;
   }
   
   
   public TeamData getTeam(){
      return this.team;
   }
   
   
   public WorkItemStatus getWorkItemStatus(){
      return this.workItemStatus;
   }
   
   
   public void setIssue(IssueData issue){
      this.issue = issue;
   }
   
   
   public void setWorkItemStatus(WorkItemStatus workItemStatus){
      this.workItemStatus = workItemStatus;
   }
   
   
   public void setUser(UserData user){
      this.user = user;
   }
   
   
   public void setTeam(TeamData team){
      this.team = team;
   }
   
   
   public String getDescription(){
      return this.description;
   }
   
   
   public void setDescription(String description){
      this.description = description;
   }
   
   public void setWorkItemId(int workItemId)
   {
	   this.workItemId = workItemId;
   }
   
   public void updateValues(WorkItemData workItem)
   {
	   this.description = workItem.description;
	   this.workItemStatus = workItem.workItemStatus;
   }
   
   
   @Override
   public int hashCode(){
      int result = 11;
      result += 37 * this.workItemId;
      return result;
   }
   
   
   @Override
   public boolean equals(Object other){
      if(this == other){
         return true;
      }
      if(other instanceof WorkItemData){
         WorkItemData otherWorkItem = (WorkItemData)other;
         return this.workItemId == otherWorkItem.workItemId;
      }
      return false;
   }
   
   
   public String shortToString(){
      return "id: " + this.workItemId + ", description: " + this.description;
   }
   
   
   @Override
   public String toString(){
      return "id: " + this.workItemId + ", description: " + this.description + ", " + this.issue;
   }
}
