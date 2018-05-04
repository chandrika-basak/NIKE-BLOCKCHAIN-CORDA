package com.nike.wms.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import com.example.state.RunState;
import com.nike.wms.vo.RunVO;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.transactions.SignedTransaction;

/**
 * RunDao class - this is an interface which declares all Run related methods. 
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
public interface RunDao {
	public List<StateAndRef<RunState>> listOfRuns(String status)throws ActiveMQException, InterruptedException, ExecutionException;
	public List<RunVO> runDetails(String runId)
			throws ActiveMQException, InterruptedException, ExecutionException, SQLException;
	
	public RunVO runLatestStatus(String runId) throws ActiveMQException, InterruptedException, ExecutionException,
			SQLException, NoSuchFieldException, SecurityException;
	
	public List<StateAndRef<RunState>> approachingRun() throws ActiveMQException, NoSuchFieldException, SecurityException;
	
	public Integer initiateRun(RunVO runVo) throws ClassNotFoundException, SQLException;
	
	public RunVO notifyRunner() throws SQLException, ClassNotFoundException;
	public SignedTransaction confirmRun(RunVO runVO) throws ActiveMQException, InterruptedException, ExecutionException, ClassNotFoundException, SQLException;
}
