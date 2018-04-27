package com.accenture.ai.service.aligenie.v2;

import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;

public interface AligenieHandler {
	
	/**
	 * aligenie execute service
	 * @param taskQuery
	 * @return
	 */
	public TaskResult handle(TaskQuery taskQuery);
}
