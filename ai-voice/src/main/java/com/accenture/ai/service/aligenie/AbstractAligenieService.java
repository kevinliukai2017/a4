package com.accenture.ai.service.aligenie;

import com.alibaba.da.coin.ide.spi.standard.TaskQuery;
import com.alibaba.da.coin.ide.spi.standard.TaskResult;

public abstract class AbstractAligenieService {
	
	/**
	 * aligenie execute service
	 * @param taskQuery
	 * @return
	 */
	public abstract TaskResult handle(TaskQuery taskQuery);
}
