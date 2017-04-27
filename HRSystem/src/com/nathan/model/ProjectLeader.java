package com.nathan.model;

public class ProjectLeader {

	private String name;
	
	private String company;
	
	private int availablePayCount;
	
	public ProjectLeader(String name, String company, int availablePayCount) {
		this.name = name;
		this.company = company;
		this.availablePayCount = availablePayCount;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * @return the availablePayCount
	 */
	public int getAvailablePayCount() {
		return availablePayCount;
	}

	/**
	 * @param availablePayCount the availablePayCount to set
	 */
	public void setAvailablePayCount(int availablePayCount) {
		this.availablePayCount = availablePayCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProjectLeader [name=" + name + ", company=" + company + ", availablePayCount=" + availablePayCount
				+ "]";
	}
	
}
