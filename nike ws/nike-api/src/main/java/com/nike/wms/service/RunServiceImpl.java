package com.nike.wms.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.example.state.RunState;
import com.nike.wms.dao.RunDao;
import com.nike.wms.vo.RunVO;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.transactions.SignedTransaction;

/**
 * This is an implementation class for RunService interface, defining all the methods for Run.
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
@Service
@EnableScheduling
public class RunServiceImpl implements RunService {
	@Autowired
	private RunDao runDao;
	@Value(value = "${node.rpc.hostport}")
	private String nodeRpcHostAndPort;
	@Value(value = "${node.db.connection}")
	private String nodeDbConnection;
	
	/**
	 * 
	 */
	@Override
	public List<RunVO> listOfRuns(String status) throws ActiveMQException, InterruptedException, ExecutionException {
		List<StateAndRef<RunState>> stateRefs = new ArrayList();
		List<RunVO> listRun = new ArrayList<RunVO>();
		stateRefs = runDao.listOfRuns(status);
		if (stateRefs != null && stateRefs.size() > 0) {
			listRun = populateRunObject(stateRefs);
		}
		return listRun;
	}
	
	/**
	 * 
	 */
	@Override
	public List<RunVO> runDetails(String runId)
			throws ActiveMQException, InterruptedException, ExecutionException, SQLException {
		
		List<RunVO> listRun = runDao.runDetails(runId);
		return listRun;
	}

	/**
	 * 
	 * @param stateRefs
	 * @return
	 */
	private List<RunVO> populateRunObject(List<StateAndRef<RunState>> stateRefs) {
		List<RunVO> runList = new ArrayList<>();

		for (int i = 0; i < stateRefs.size(); i++) {
			RunVO runVo = new RunVO();
			runVo.setRunID(stateRefs.get(i).getState().getData().getRunModel().getRunID());
			runVo.setSkuID(stateRefs.get(i).getState().getData().getRunModel().getSkuID());
			runVo.setResponseID(stateRefs.get(i).getState().getData().getRunModel().getResponseID());
			runVo.setRunnerID(stateRefs.get(i).getState().getData().getRunModel().getRunnerID());
			runVo.setRunnerName(stateRefs.get(i).getState().getData().getRunModel().getRunnerName());
			runVo.setStoreName(stateRefs.get(i).getState().getData().getRunModel().getStoreName());
			runVo.setRunnerPosition(stateRefs.get(i).getState().getData().getRunModel().getRunnerPosition());
			runVo.setStatusChangeTimeStamp(
					stateRefs.get(i).getState().getData().getRunModel().getStatusChangeTimeStamp().toString());

			runList.add(runVo);
		}
		return runList;
	}
	
	/**
	 * 
	 */
	@Override
	public RunVO runLatestStatus(String runId) throws ActiveMQException, InterruptedException, ExecutionException,
			SQLException, NoSuchFieldException, SecurityException {

		RunVO runVo = runDao.runLatestStatus(runId);
		return runVo;
	}

	/**
	 * 
	 */
	@Override
	public List<RunVO> approachingRun() throws ActiveMQException, NoSuchFieldException, SecurityException {
		List<StateAndRef<RunState>> stateRefs = new ArrayList();
		List<RunVO> listRun = new ArrayList<>();
		
		if (stateRefs != null && stateRefs.size() > 0) {
			listRun = populateRunObject(stateRefs);

		}
		return listRun;
	}
	
	/**
	 * 
	 */
	@Override
	public Integer initiateRun(RunVO runVO) throws ClassNotFoundException, SQLException {
		Integer rs = runDao.initiateRun(runVO);
		return rs;
	}
	
	/**
	 * 
	 */
	@Override
	public RunVO notifyRunner() throws SQLException, ClassNotFoundException {
		RunVO runVO = runDao.notifyRunner();
		return runVO;
	}
	
	/**
	 * 
	 */
	@Override
	public SignedTransaction confirmRun(RunVO runVO)
			throws ActiveMQException, InterruptedException, ExecutionException, ClassNotFoundException, SQLException {
		SignedTransaction txn = runDao.confirmRun(runVO);
		return txn;
	}
}
