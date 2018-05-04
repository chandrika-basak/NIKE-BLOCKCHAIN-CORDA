package com.nike.wms.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.jvnet.hk2.annotations.Service;
import com.example.flow.SkuResponseFlow;
import com.example.model.SkuRequestModel;
import com.example.state.SkuRequestState;

import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;

/**
 * This is a service class for Market place search or search in blockchain.
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
@Service
public class MarketPlaceService implements Callable<SkuRequestState> {

	private SkuRequestModel req;
	private Party responder;
	CordaRPCOps rpcService;
	
	/**
	 * 
	 * @param req
	 * @param responder
	 * @param rpcService
	 */
	public MarketPlaceService(SkuRequestModel req, Party responder, CordaRPCOps rpcService) {
		this.req = req;
		this.responder = responder;
		this.rpcService = rpcService;
	}
	
	/**
	 * 
	 */
	public SkuRequestState call() throws InterruptedException, ExecutionException, ActiveMQException {
		System.out.println("Inside Concurrent Thread::::" );
		SkuRequestState skuRequestState = rpcService.startFlowDynamic(SkuResponseFlow.Initiator.class, req, responder)
				.getReturnValue().get();

		/*
		 * try { Thread.sleep(2000); } catch (InterruptedException ex) {
		 * ex.printStackTrace(); }
		 */
		System.out.println("After Flow Call::::" );
		return skuRequestState;
	}

}
