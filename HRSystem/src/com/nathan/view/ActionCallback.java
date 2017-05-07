package com.nathan.view;

public interface ActionCallback {

	/**
     * Invoked when an action occurs.
	 * @throws Exception 
     */
	public void actionPerformed(ActionType actionType) throws Exception;
	
	/**
     * Invoked when an action suspend occurs.
	 * @throws Exception 
     */
	public void actionSuspend(ActionType actionType) throws Exception;
	
	/**
     * Invoked when an return action occurs.
	 * @throws Exception 
     */
	public void returnPerformed(ActionType actionType) throws Exception;
	
	/**
     * Invoked when an exit action occurs.
	 * @throws Exception 
     */
	public void exitPerformed(ActionType actionType) throws Exception;
	
}
