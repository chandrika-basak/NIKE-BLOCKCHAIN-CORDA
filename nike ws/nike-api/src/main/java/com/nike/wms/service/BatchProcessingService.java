package com.nike.wms.service;

import java.util.concurrent.ExecutionException;

import org.apache.activemq.artemis.api.core.ActiveMQException;

/**
 * This is a Service interface for Batch processes.
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
public interface BatchProcessingService {
 public void getUnconsumedRunStates() throws ActiveMQException, InterruptedException, ExecutionException;
}
