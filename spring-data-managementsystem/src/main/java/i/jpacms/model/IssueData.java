package i.jpacms.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class IssueData
{
	@Column
	private String issue;

	protected IssueData()
	{
	}

	public IssueData(String issue)
	{
		this.issue = issue;
	}

	public String getIssue()
	{
		return this.issue;
	}

	public void setIssue(String issue)
	{
		this.issue = issue;
	}

	@Override
	public String toString()
	{
		return "Issue: " + this.issue;
	}
}
