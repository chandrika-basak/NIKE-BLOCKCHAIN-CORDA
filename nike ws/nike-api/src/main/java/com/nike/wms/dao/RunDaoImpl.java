package com.nike.wms.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.example.flow.RunInitiateFlow;
import com.example.model.RunModel;
import com.example.schema.RunSchemaV2;
import com.example.state.RunState;
import com.nike.wms.service.CordaRPCService;
import com.nike.wms.util.NikeConstant;
import com.nike.wms.util.NikeUtil;
import com.nike.wms.vo.RunVO;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.node.services.vault.QueryCriteria.VaultCustomQueryCriteria;
import net.corda.core.node.services.vault.QueryCriteria.VaultQueryCriteria;
import net.corda.core.transactions.SignedTransaction;

/**
 * RunDaoImpl class - this class is the implementation class for RunDao interface, providing definitions for all Run related methods..
 * @author Cognizant Blockchain team
 * @version 1.0
 */
@Repository
public class RunDaoImpl implements RunDao {
	@Autowired
	private CordaRPCService cordaRPCService;
	@Autowired
	private NikeUtil nikeUtil;
	@Value(value = "${node.rpc.hostport}")
	private String nodeRpcHostAndPort;
	@Value(value = "${node.db.connection}")
	private String nodeDbConnection;
	
	/**
	 * Method to fetch all the Runs
	 */
	@Override
	public List<StateAndRef<RunState>> listOfRuns(String status) throws ActiveMQException {
		List<StateAndRef<RunState>> stateRefs = new ArrayList();
		CordaRPCOps rpcService = cordaRPCService.getRPCServiceByNode(nodeRpcHostAndPort);
		QueryCriteria vaultCriteria = new VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
		Field runnerPositionField;
		try {
			runnerPositionField = RunSchemaV2.RunStateV2.class.getDeclaredField(NikeConstant.RUNNER_POSITION);

			Connection dbConn = null;
			ResultSet rs = null;
			Statement runInsertStatement = null;

			List<RunVO> listRun = new ArrayList<RunVO>();

			if ((status != null) && status.equalsIgnoreCase(NikeConstant.COMPLETED_RUN)) {
				CriteriaExpression runStatusExp = Builder.equal(runnerPositionField, NikeConstant.RUNNER_BACK_IN_STORE);
				QueryCriteria runCriteria = new VaultCustomQueryCriteria(runStatusExp);
				QueryCriteria criteria = vaultCriteria.and(runCriteria);

				Vault.Page<RunState> vaultStates = rpcService.vaultQueryByCriteria(criteria, RunState.class);
				stateRefs = vaultStates.getStates();


			} else {
				CriteriaExpression runStatusExp = Builder.notEqual(runnerPositionField,
						NikeConstant.RUNNER_BACK_IN_STORE);
				QueryCriteria runCriteria = new VaultCustomQueryCriteria(runStatusExp);
				QueryCriteria criteria = vaultCriteria.and(runCriteria);

				Vault.Page<RunState> vaultStates = rpcService.vaultQueryByCriteria(criteria, RunState.class);
				stateRefs = vaultStates.getStates();
			}
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return stateRefs;
	}
	
	/**
	 * Method to fetch run details based on given runID
	 */
	@Override
	public List<RunVO> runDetails(String runId)
			throws ActiveMQException, InterruptedException, ExecutionException, SQLException {
		Statement runInsertStatement = null;		
		Connection dbConn = null;
		ResultSet rs = null;
		List<RunVO> listRun = new ArrayList();
		try {

			dbConn = nikeUtil.getDBConnection();
			String sqlGetRun = "SELECT * FROM RUN_DETAILS WHERE RUNID='" + runId + "';";
			runInsertStatement = dbConn.createStatement();

			rs = runInsertStatement.executeQuery(sqlGetRun);
			while (rs != null && rs.next()) {

				RunVO runVO = new RunVO();
				runVO.setRunID(rs.getString(6));
				runVO.setResponseID(rs.getString(5));
				runVO.setSkuID(rs.getString(11));
				runVO.setRunnerID(rs.getString(7));
				runVO.setRunnerName(rs.getString(8));
				runVO.setStoreName(rs.getString(10));
				runVO.setRunnerPosition(rs.getString(9));
				runVO.setStatusChangeTimeStamp(rs.getString(4));

				listRun.add(runVO);

			}

		} catch (SQLException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (rs != null) {
					rs.close();
				}

				if (runInsertStatement != null) {
					runInsertStatement.close();
				}

				if (dbConn != null) {
					dbConn.close();
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				throw e;
			}

		}
		return listRun;
	}
	/**
	 * Method to fetch latest run status based on given runId
	 */
	@Override
	public RunVO runLatestStatus(String runId) throws ActiveMQException, InterruptedException, ExecutionException,
			SQLException, NoSuchFieldException, SecurityException {
		List<StateAndRef<RunState>> stateRefs = new ArrayList<StateAndRef<RunState>>();
		CordaRPCOps rpcService = cordaRPCService.getRPCServiceByNode(nodeRpcHostAndPort);
		QueryCriteria vaultCriteria = new VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
		Field runIdField = RunSchemaV2.RunStateV2.class.getDeclaredField("runID");

		RunVO runVo = new RunVO();

		CriteriaExpression<?, Boolean> runStatusExp = Builder.equal(runIdField, runId);
		QueryCriteria runCriteria = new VaultCustomQueryCriteria(runStatusExp);
		QueryCriteria criteria = vaultCriteria.and(runCriteria);

		Vault.Page<RunState> vaultStates = rpcService.vaultQueryByCriteria(criteria, RunState.class);
		stateRefs = vaultStates.getStates();
		if (stateRefs != null && stateRefs.size() > 0) {
			runVo.setRunID(stateRefs.get(0).getState().getData().getRunModel().getRunID());
			runVo.setSkuID(stateRefs.get(0).getState().getData().getRunModel().getSkuID());
			runVo.setResponseID(stateRefs.get(0).getState().getData().getRunModel().getResponseID());
			runVo.setRunnerID(stateRefs.get(0).getState().getData().getRunModel().getRunnerID());
			runVo.setRunnerName(stateRefs.get(0).getState().getData().getRunModel().getRunnerName());
			runVo.setStoreName(stateRefs.get(0).getState().getData().getRunModel().getStoreName());
			runVo.setRunnerPosition(stateRefs.get(0).getState().getData().getRunModel().getRunnerPosition());
			runVo.setStatusChangeTimeStamp(
					stateRefs.get(0).getState().getData().getRunModel().getStatusChangeTimeStamp().toString());

		}
		return runVo;
	}
	
	/**
	 * Method triggered for runner while approaching destination store BoH
	 */
	@Override
	public List<StateAndRef<RunState>> approachingRun() throws ActiveMQException, NoSuchFieldException, SecurityException {
		List<StateAndRef<RunState>> stateRefs = new ArrayList();
		CordaRPCOps rpcService = cordaRPCService.getRPCServiceByNode(nodeRpcHostAndPort);
		QueryCriteria vaultCriteria = new VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
		Field runPosition = RunSchemaV2.RunStateV2.class.getDeclaredField(NikeConstant.RUNNER_POSITION);

		List<RunVO> listRun = new ArrayList<>();

		CriteriaExpression runStatusExp = Builder.equal(runPosition, NikeConstant.CONFIRMED_RUN);
		QueryCriteria runCriteria = new VaultCustomQueryCriteria(runStatusExp);
		QueryCriteria criteria = vaultCriteria.and(runCriteria);

		Vault.Page<RunState> vaultStates = rpcService.vaultQueryByCriteria(criteria, RunState.class);
		stateRefs = vaultStates.getStates();
		return stateRefs;
	}
	
	/**
	 * Method triggered by store athlete to initiate a run for runner
	 */
	@Override
	public Integer initiateRun(RunVO runVO) throws ClassNotFoundException, SQLException {
		Statement runInsertStatement = null;

		Connection dbConn = null;
		Integer rs = null;
		Random random = new Random();

		try {

			dbConn = nikeUtil.getDBConnection();
			runVO.setRunID("RUN" + System.currentTimeMillis() + random.nextInt(99));
			runVO.setRunnerPosition("PENDING RUNNER CONFIRMATION");

			String sqlInsert = "INSERT INTO INFORM_RUNNER VALUES ('" + runVO.getRunID() + "','" + runVO.getResponseID()
					+ "','" + runVO.getSkuID() + "','" + runVO.getRunnerID() + "','" + runVO.getRunnerName() + "','"
					+ runVO.getStoreName() + "','" + runVO.getRunnerPosition() + "','" + System.currentTimeMillis()
					+ "')";
			runInsertStatement = dbConn.createStatement();
			rs = runInsertStatement.executeUpdate(sqlInsert);
			
		} catch (SQLException e) {
			throw e;
		} finally {
			// finally block used to close resources
			try {

				if (runInsertStatement != null) {
					runInsertStatement.close();
				}

				if (dbConn != null) {
					dbConn.close();
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				throw e;
			}

		}
		return rs;
}
	/**
	 * 
	 */
	@Override
	public RunVO notifyRunner() throws SQLException, ClassNotFoundException {
		Statement runInsertStatement = null;

		Connection dbConn = null;
		ResultSet rs = null;
		RunVO runVO = new RunVO();

		try {

			dbConn = nikeUtil.getDBConnection();
			String sqlGetRun = "SELECT * FROM(SELECT * FROM INFORM_RUNNER WHERE RUNNERPOSITION='PENDING RUNNER CONFIRMATION' order by STATUSCHANGETIMESTAMP DESC) where ROWNUM = 1";
			runInsertStatement = dbConn.createStatement();
			rs = runInsertStatement.executeQuery(sqlGetRun);
			if (null != rs && rs.next()) {
				runVO.setRunID(rs.getString(1));
				runVO.setResponseID(rs.getString(2));
				runVO.setSkuID(rs.getString(3));
				runVO.setStoreName(rs.getString(6));
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			// finally block used to close resources
			try {
				if (rs != null) {
					rs.close();
				}

				if (runInsertStatement != null) {
					runInsertStatement.close();
				}

				if (dbConn != null) {
					dbConn.close();
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				throw e;
			}

		}
		return runVO;
	}
	
	/**
	 * 
	 */
	@Override
	public SignedTransaction confirmRun(RunVO runVO)
			throws ActiveMQException, InterruptedException, ExecutionException, ClassNotFoundException, SQLException {
		CordaRPCOps rpcService = cordaRPCService.getRPCServiceByNode(nodeRpcHostAndPort);

		RunModel model = new RunModel(runVO.getRunID(), runVO.getResponseID(), runVO.getSkuID(), runVO.getRunnerID(),
				runVO.getRunnerName(), runVO.getStoreName(), NikeConstant.CONFIRMED_RUN, LocalDateTime.now());
		SignedTransaction txn = rpcService.startFlowDynamic(RunInitiateFlow.Initiator.class, model).getReturnValue()
				.get();
		Connection dbConn = null;
		Statement runInsertStatement = null;

		try {

			dbConn = nikeUtil.getDBConnection();
			String sqlGetRun = "DELETE FROM INFORM_RUNNER WHERE RUNID='" + runVO.getRunID() + "'";
			runInsertStatement = dbConn.createStatement();
			runInsertStatement.executeUpdate(sqlGetRun);

		} catch (SQLException e) {
			throw e;
		} finally {
			// finally block used to close resources
			try {

				if (runInsertStatement != null) {
					runInsertStatement.close();
				}

				if (dbConn != null) {
					dbConn.close();
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				throw e;
			}

		}
		return txn;
	}
}
