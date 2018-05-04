package com.nike.wms.service;

import org.apache.activemq.artemis.api.core.ActiveMQException;

import net.corda.core.messaging.CordaRPCOps;

/**
 * This is an Interface for CordaRPC call.
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
public interface CordaRPCService {

	public CordaRPCOps getRPCServiceByNode(String hostPort) throws ActiveMQException;
}
