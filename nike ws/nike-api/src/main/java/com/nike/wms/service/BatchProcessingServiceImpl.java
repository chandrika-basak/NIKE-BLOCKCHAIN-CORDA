package com.nike.wms.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.flow.RunModifyFlow;
import com.example.state.RunState;
import com.nike.wms.util.NikeConstant;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.node.services.vault.QueryCriteria.VaultQueryCriteria;
import net.corda.core.transactions.SignedTransaction;

/**
 * This class is the implementation class for BatchProcessService interface which defines all the methods for Batch process.
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
@Service
@EnableScheduling
public class BatchProcessingServiceImpl implements BatchProcessingService {
	@Autowired
	private CordaRPCService cordaRPCService;
	@Value(value = "${node.rpc.hostport}")
	private String nodeRpcHostAndPort;
	
	@Scheduled(fixedRate = 15000, initialDelay = 5000)
    public void getUnconsumedRunStates() throws ActiveMQException, InterruptedException, ExecutionException {
    
    	CordaRPCOps rpcService = cordaRPCService.getRPCServiceByNode(nodeRpcHostAndPort);
    	QueryCriteria vaultCriteria = new VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
    	
    	Vault.Page<RunState> vaultStates = rpcService.vaultQueryByCriteria(vaultCriteria, RunState.class);
    	List<StateAndRef<RunState>> stateRefs = vaultStates.getStates(); 
    	if(stateRefs != null && stateRefs.size()>0){
	    	//stateRefs.get(0).getState().getData();
	    	for(StateAndRef<RunState> runStateRef: stateRefs) {
	    		String runnerPosition = runStateRef.getState().getData().getRunModel().getRunnerPosition();
	    		String runId = runStateRef.getState().getData().getRunModel().getRunID();
	    		if(runnerPosition.equalsIgnoreCase(NikeConstant.CONFIRMED_RUN)) {
	    			updateRunStateStatus(runId,NikeConstant.LEFT_STORE);
	    		} else if (runnerPosition.equalsIgnoreCase(NikeConstant.LEFT_STORE)) {
	    			updateRunStateStatus(runId,NikeConstant.ENTERED_PARTNER_STORE);
				}else if (runnerPosition.equalsIgnoreCase(NikeConstant.ENTERED_PARTNER_STORE)) {
					updateRunStateStatus(runId,NikeConstant.RECIEVED_SKU);
				}else if (runnerPosition.equalsIgnoreCase(NikeConstant.RECIEVED_SKU)) {
					updateRunStateStatus(runId,NikeConstant.LEFT_PARTNER_STORE);
				}else if (runnerPosition.equalsIgnoreCase(NikeConstant.LEFT_PARTNER_STORE)) {
					updateRunStateStatus(runId,NikeConstant.RUNNER_BACK_IN_STORE);
				}/*else if (runnerPosition.equalsIgnoreCase(NikeConstant.RUNNER_BACK_IN_STORE)) {
					updateRunStateStatus(runId,NikeConstant.DELIVERED_SKU);
				}else if (runnerPosition.equalsIgnoreCase(NikeConstant.DELIVERED_SKU)) {
					updateRunStateStatus(runId,NikeConstant.SKU_PURCHASED);
				}*/
    		
    		}
    	}
    }
	
	/**
	 * 
	 * @param runId
	 * @param runnerPostion
	 * @throws ActiveMQException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
    
    private void updateRunStateStatus(String runId, String runnerPostion) throws ActiveMQException, InterruptedException, ExecutionException {
    	CordaRPCOps rpcService = cordaRPCService.getRPCServiceByNode(nodeRpcHostAndPort);
    	SignedTransaction txn = rpcService.startFlowDynamic(RunModifyFlow.Initiator.class,runId, runnerPostion).getReturnValue().get();
    }
    
    
	

}

