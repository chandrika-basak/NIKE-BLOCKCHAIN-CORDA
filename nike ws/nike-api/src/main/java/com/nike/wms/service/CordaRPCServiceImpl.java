package com.nike.wms.service;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.utilities.NetworkHostAndPort;

/**
 * This is an implementation class for CordaRPCService which defines the mehod for Corda RPC Calls
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
@Service
public class CordaRPCServiceImpl implements CordaRPCService {

	public CordaRPCOps getRPCServiceByNode(String hostPort) throws ActiveMQException {
		// hostPort expected to be in format : "localhost:10006"
				/*final HostAndPort nodeAddress = HostAndPort.fromString(hostPort);
				final CordaRPCClient client = new CordaRPCClient(nodeAddress, null, CordaRPCClientConfiguration.getDefault());
		*/		
				final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse(hostPort);
		        final CordaRPCClient client = new CordaRPCClient(nodeAddress);

				final CordaRPCOps proxy = client.start("user1", "test").getProxy();
				return proxy;
	}

	

}

