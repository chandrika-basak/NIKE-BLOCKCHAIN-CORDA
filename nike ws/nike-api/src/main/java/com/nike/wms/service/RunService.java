package com.nike.wms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import com.nike.wms.vo.RunVO;
import net.corda.core.transactions.SignedTransaction;

/**
 * This is a service interface for Run which declares all methods for Run functionalities.
 * @author Cognizant Blockchain Team
 * @version 1.0
 */
public interface RunService {
	 public List<RunVO> listOfRuns(String status) throws ActiveMQException, InterruptedException, ExecutionException;
	 public List<RunVO> runDetails(String runId) throws ActiveMQException, InterruptedException, ExecutionException, SQLException;
	 public RunVO runLatestStatus(String runId) throws ActiveMQException, InterruptedException, ExecutionException, SQLException, NoSuchFieldException, SecurityException;
	 public List<RunVO> approachingRun() throws ActiveMQException, NoSuchFieldException, SecurityException;
	
	 public Integer initiateRun(RunVO runVO) throws ClassNotFoundException, SQLException;
	 public RunVO notifyRunner() throws SQLException, ClassNotFoundException;
	 public SignedTransaction confirmRun(RunVO runVO) throws ActiveMQException, InterruptedException, ExecutionException, ClassNotFoundException, SQLException;
}
