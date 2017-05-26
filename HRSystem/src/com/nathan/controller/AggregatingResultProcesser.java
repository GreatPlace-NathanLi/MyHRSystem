package com.nathan.controller;

import com.nathan.model.AggregatingCriteria;
import com.nathan.model.AggregatingResultSheet;

public interface AggregatingResultProcesser {
	
	public AggregatingResultSheet process(Object processingData, AggregatingCriteria criteria) throws Exception;

	public void save(AggregatingResultSheet result);
	
	public void print(AggregatingResultSheet result);

}
