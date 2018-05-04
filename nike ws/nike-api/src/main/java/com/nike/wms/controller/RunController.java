package com.nike.wms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nike.wms.service.BatchProcessingService;
import com.nike.wms.service.RunService;
import com.nike.wms.vo.RunVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.corda.core.transactions.SignedTransaction;

/**
 * This is a Controller class which exposes APIS for all Run related services.
 * @author Cognizant Blockchain Team
 * @version 1.0
 *
 */
@Component
@Path("run")
@Api(tags = { "NIKE" })
public class RunController{

	@Autowired
	private BatchProcessingService batchProcessingService;
	
	@Autowired
	private RunService runService;
	
	@Value(value = "${node.rpc.hostport}")
	private String nodeRpcHostAndPort;

	@Value(value = "${node.db.connection}")
	private String nodeDbConnection;

	@Value(value = "${ext.db.connection}")
	private String extDbConnection;

	@Value(value = "${ext.username}")
	private String extUsername;

	@Value(value = "${ext.password}")
	private String extPassword;

	@Value(value = "${sql.catalogue.item.details}")
	private String sqlItemDetails;

	@Value(value = "${sql.catalogue.list}")
	private String sqlCatalogueList;

	@Value(value = "${sql.catalogue.suggestion}")
	private String sqlCatalogueSuggestion;

	@Value(value = "${sql.sap.inventory.fetch}")
	private String sqlSapFetch;

	
	/**
	 * 
	 * @param runVO
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	
	@POST
	@Path("/initiateRun")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Initiate Run", notes = "Initiate Run")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Sent"),
			@ApiResponse(code = 401, message = "Failure") })
	public Response initiateRun(RunVO runVO) throws ClassNotFoundException, SQLException {
			Integer rs = runService.initiateRun(runVO);
		if(rs != 0){
			return Response.status(Response.Status.OK).entity(runVO).build();
		} else {

			return Response.status(Response.Status.BAD_REQUEST).entity(new RunVO()).build();
		}

	}
	/**
	 * 
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */

	@GET
	@Path("/notifyRunner")
	@ApiOperation(value = "Notify Runner", notes = "Notify Runner")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Sent"),
			@ApiResponse(code = 401, message = "Failure") })
	public Response notifyRunner() throws SQLException, ClassNotFoundException {
		
		RunVO runVO = runService.notifyRunner();
		return Response.status(Response.Status.OK).entity(runVO).build();

	}

	/**
	 * 
	 * @param runVO
	 * @return
	 * @throws ActiveMQException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@POST
	@Path("/confirmRun")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Confirm Run Request", notes = "Confirm Run Request")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Sent"),
			@ApiResponse(code = 401, message = "Failure") })
	public Response confirmRun(RunVO runVO)
			throws ActiveMQException, InterruptedException, ExecutionException, ClassNotFoundException, SQLException {

		SignedTransaction txn = runService.confirmRun(runVO);
		System.out.println("Test Success:" + txn.getId());

		return Response.status(Response.Status.OK).entity(txn.getTx().getId().toString()).build();
	}
	
    /**
     * 
     * @return
     * @throws ActiveMQException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
	@GET
	@Path("/test")
	@ApiOperation(value = "Request", notes = "Test Run")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Sent"),
			@ApiResponse(code = 401, message = "Failure") })
	public Response testRun()
			throws ActiveMQException, InterruptedException, ExecutionException, ClassNotFoundException, SQLException {

		batchProcessingService.getUnconsumedRunStates();

		return Response.status(Response.Status.OK).entity("Success").build();
	}
	
	/**
	 * 
	 * @param status
	 * @return
	 * @throws ActiveMQException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@GET
	@Path("/listOfRuns/{status}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "List of Runs", notes = "List of Runs - Completed and Ongoing")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Sent"),
			@ApiResponse(code = 401, message = "Failure") })
	public Response listOfRuns(@PathParam("status") String status) throws ActiveMQException, InterruptedException,
			ExecutionException, ClassNotFoundException, SQLException, NoSuchFieldException, SecurityException {
		List<RunVO> runList = runService.listOfRuns(status);
		return Response.status(Response.Status.OK).entity(runList).build();

	}
	
	/**
	 * 
	 * @param runId
	 * @return
	 * @throws ActiveMQException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@GET
	@Path("/runDetails/{runId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Run Details", notes = "Details of a Selected Run")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Sent"),
			@ApiResponse(code = 401, message = "Failure") })
	public Response runDetails(@PathParam("runId") String runId) throws ActiveMQException, InterruptedException,
			ExecutionException, ClassNotFoundException, SQLException, NoSuchFieldException, SecurityException {
		
		List<RunVO> listRun = runService.runDetails(runId);
		return Response.status(Response.Status.OK).entity(listRun).build();

	}
	
	/**
	 * 
	 * @param runId
	 * @return
	 * @throws ActiveMQException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@GET
	@Path("/runLatestStatus/{runId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Run Details", notes = "Details of a Selected Run")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Sent"),
			@ApiResponse(code = 401, message = "Failure") })
	public Response runLatestStatus(@PathParam("runId") String runId) throws ActiveMQException, InterruptedException,
			ExecutionException, ClassNotFoundException, SQLException, NoSuchFieldException, SecurityException {
		
		RunVO latestRunStatus = runService.runLatestStatus(runId);
		return Response.status(Response.Status.OK).entity(latestRunStatus).build();

	}
	
	/**
	 * 
	 * @return
	 * @throws ActiveMQException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	@GET
	@Path("/runApproaching")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Run Details", notes = "Details of a Selected Run")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Sent"),
			@ApiResponse(code = 401, message = "Failure") })
	public Response runApproaching() throws ActiveMQException, InterruptedException,
			ExecutionException, ClassNotFoundException, SQLException, NoSuchFieldException, SecurityException {
		
		List<RunVO> approachingRun = runService.approachingRun();
		return Response.status(Response.Status.OK).entity(approachingRun).build();

	}
}