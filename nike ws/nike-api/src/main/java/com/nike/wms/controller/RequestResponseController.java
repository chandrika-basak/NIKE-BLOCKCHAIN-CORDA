package com.nike.wms.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.flow.ResponseLedgerWriteFlow;
import com.example.model.SkuRequestModel;
import com.example.state.SkuRequestState;
import com.nike.wms.service.CordaRPCService;
import com.nike.wms.service.MarketPlaceService;
import com.nike.wms.util.NikeUtil;
import com.nike.wms.vo.ProductDetails;
import com.nike.wms.vo.SkuRequestVO;
import com.nike.wms.vo.SkuResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;

/**
 * This is controller class which exposes all the APIs required for Search and Update Product Inventory functionalities. 
 * @author Cognizant Blockchain Team
 * @version 1.0
 */

@Component
@Path("search")
@Api(tags = { "NIKE" })
public class RequestResponseController {

	@Autowired
	private CordaRPCService cordaRPCService;
	
	@Autowired
	private NikeUtil nikeUtil;
	@Value(value = "${node.rpc.hostport}")
	private String nodeRpcHostAndPort;

	@Value(value = "${node.db.connection}")
	private String nodeDbConnection;

	@Value(value = "${ext.db.connection}")
	private String extDbConnection;


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
	 * @param requestVO
	 * @return
	 * @throws ActiveMQException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@POST
	@Path("/marketplaceSearch")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Marketplace Request", notes = "Sku Blockchain Request")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Sent"),
			@ApiResponse(code = 401, message = "Failure") })
	public Response requestProduct(SkuRequestVO requestVO)
			throws ActiveMQException, InterruptedException, ExecutionException, ClassNotFoundException, SQLException {

		CordaRPCOps rpcService = cordaRPCService.getRPCServiceByNode(nodeRpcHostAndPort);

		List<NodeInfo> networkSnapShot = rpcService.networkMapSnapshot();
		System.out.println("Snapshot ::::"+networkSnapShot.size());
		Set<NodeInfo> networkSet = new HashSet<NodeInfo>(networkSnapShot);
		List<NodeInfo> broadcastRecepients = new ArrayList<>();
		broadcastRecepients.addAll(networkSet);
		List<Party> allPartyList = new ArrayList<>();
		// final Party notary =
		// getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
		final Party notary = rpcService.wellKnownPartyFromX500Name(new CordaX500Name("Controller", "London", "GB"));
		final Party me = rpcService.wellKnownPartyFromX500Name(new CordaX500Name("Nike", "London", "GB"));
		List<SkuRequestState> listSkuRequestState = new ArrayList<>();
		Random random = new Random();

		for (NodeInfo otherParty : broadcastRecepients) {
			CordaX500Name partyName = otherParty.getLegalIdentities().get(0).getName();
			if (!((partyName.getOrganisation()).equals(notary.getName().getOrganisation())
					|| (partyName.equals(me.getName())))) {
				System.out.println("Party ::::" + partyName.toString());
				allPartyList.add(rpcService.wellKnownPartyFromX500Name(partyName));
				System.out.println("Final list::::"+allPartyList.size());
			}

		}
		
		ExecutorService executor = Executors.newCachedThreadPool();
		String requestId = "REQUEST" + System.currentTimeMillis() + random.nextInt(99);
		System.out.println("Request ID ::::" + requestId);
		SkuRequestModel skuRequestModel = new SkuRequestModel(requestId, "", requestVO.getSkuId(), "",
				LocalDateTime.now());
		for (Party responder : allPartyList) {
			System.out.println("RESPONDER ::::"+responder.toString());

			Future<SkuRequestState> futureCall = executor
					.submit(new MarketPlaceService(skuRequestModel, responder, rpcService));
			try {

				SkuRequestState result = futureCall.get();
				listSkuRequestState.add(result);

			} catch (InterruptedException | ExecutionException ex) {
				ex.printStackTrace();
			}

		}

		executor.shutdown();
		List<SkuResponseVO> skuResponseVOs = new ArrayList<>();
		for (SkuRequestState skuRequestState : listSkuRequestState) {
			SkuResponseVO responseVO = new SkuResponseVO();
			responseVO.setRequsetId(skuRequestState.getSkuRequestModel().getRequestID());
			responseVO.setResponseId(skuRequestState.getSkuRequestModel().getResponseID());
			responseVO.setSkuId(skuRequestState.getSkuRequestModel().getSkuID());
			responseVO.setSkuAvailability(skuRequestState.getSkuRequestModel().getItemAvailability());
			responseVO.setStoreName(skuRequestState.getSender().getName().getOrganisation());
			skuResponseVOs.add(responseVO);
		}

		asyncLedgerupdate(listSkuRequestState);

		System.out.println("Test Success:" + skuResponseVOs);

		return Response.status(Response.Status.OK).entity(skuResponseVOs).build();
	}

	/**
	 * 
	 * @param listSkuRequestState
	 */
	private void asyncLedgerupdate(final List<SkuRequestState> listSkuRequestState) {
		Runnable task = new Runnable() {
			public void run() {

				CordaRPCOps rpcService;
				try {
					rpcService = cordaRPCService.getRPCServiceByNode(nodeRpcHostAndPort);
					for (SkuRequestState skuRequestState : listSkuRequestState) {
						SignedTransaction listSkuResponseState = rpcService
								.startFlowDynamic(ResponseLedgerWriteFlow.Initiator.class, skuRequestState)
								.getReturnValue().get();
					}
				} catch (ActiveMQException | InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		};
		new Thread(task, "ServiceThread").start();

	}
	/**
	 * 
	 * @param productId
	 * @return
	 * @throws ActiveMQException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */

	@POST
	@Path("/catalogueItem")
	@ApiOperation(value = "Catalogue Item Details", notes = "Product Catalogue")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Sent"),
			@ApiResponse(code = 401, message = "Failure") })
	public Response catalogueSearch(String productId)
			throws ActiveMQException, InterruptedException, ExecutionException, ClassNotFoundException, SQLException {

		Statement productStatement = null;

		Connection dbConn = null;
		ResultSet rs = null;
		ProductDetails productDetails = new ProductDetails();
		dbConn = nikeUtil.getextDBConnection();

		String sqlCreate = sqlItemDetails + productId.toLowerCase() + "'";
		try {
			productStatement = dbConn.createStatement();
			rs = productStatement.executeQuery(sqlCreate);

			if (rs != null && rs.next()) {

				productDetails.setItemNo(rs.getString(1));
				productDetails.setItemName(rs.getString(2));
				productDetails.setItemDesc(rs.getString(3));
				productDetails.setLongItemDesc(rs.getString(4));
				productDetails.setCurUnitPrice(rs.getString(5));
				productDetails.setMfgSuggestPrc(rs.getString(6));

			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (productStatement != null) {
				productStatement.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}

		return Response.status(Response.Status.OK).entity(productDetails).build();
	}

	/**
	 * 
	 * @param catalogueName
	 * @return
	 * @throws ActiveMQException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@POST
	@Path("/catalogueList")
	@ApiOperation(value = "Catalogue List", notes = "Product Catalogue List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Sent"),
			@ApiResponse(code = 401, message = "Failure") })
	public Response catalogueListSearch(String catalogueName)
			throws ActiveMQException, InterruptedException, ExecutionException, ClassNotFoundException, SQLException {

		Statement productStatement = null;

		Connection dbConn = null;
		ResultSet rs = null;
		List<ProductDetails> productDetailsList = new ArrayList<ProductDetails>();
		dbConn = nikeUtil.getextDBConnection();

		String sqlCreate = sqlCatalogueList + catalogueName.toLowerCase() + "%'";
		try {
			productStatement = dbConn.createStatement();
			rs = productStatement.executeQuery(sqlCreate);

			while (rs != null && rs.next()) {
				ProductDetails productDetails = new ProductDetails();
				productDetails.setItemNo(rs.getString(1));
				productDetails.setItemName(rs.getString(2));
				productDetails.setItemDesc(rs.getString(3));
				productDetails.setLongItemDesc(rs.getString(4));
				productDetails.setCurUnitPrice(rs.getString(5));
				productDetails.setMfgSuggestPrc(rs.getString(6));
				productDetailsList.add(productDetails);

			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (productStatement != null) {
				productStatement.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}

		return Response.status(Response.Status.OK).entity(productDetailsList).build();
	}
	
	/**
	 * 
	 * @param catalogueName
	 * @return
	 * @throws ActiveMQException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */

	@POST
	@Path("/catalogueSuggestion")
	@ApiOperation(value = "Catalogue Suggestion", notes = "Product Catalogue Typeahead Suggestion")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Sent"),
			@ApiResponse(code = 401, message = "Failure") })
	public Response catalogueTypeAhead(String catalogueName)
			throws ActiveMQException, InterruptedException, ExecutionException, ClassNotFoundException, SQLException {

		Statement productStatement = null;

		Connection dbConn = null;
		ResultSet rs = null;
		List<String> productSuggestion = new ArrayList<String>();
		dbConn = nikeUtil.getextDBConnection();

		String sqlCreate = sqlCatalogueSuggestion + catalogueName.toLowerCase() + "%'";
		try {
			productStatement = dbConn.createStatement();
			rs = productStatement.executeQuery(sqlCreate);

			while (rs != null && rs.next()) {

				productSuggestion.add(rs.getString(2));

			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (productStatement != null) {
				productStatement.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}

		return Response.status(Response.Status.OK).entity(productSuggestion).build();
	}

	/**
	 * 
	 * @param productId
	 * @return
	 * @throws ActiveMQException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@GET
	@Path("/skuAvailability/{productId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Sku Availability", notes = "Sku Local Availability")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request Sent"),
			@ApiResponse(code = 401, message = "Failure") })
	public Response skuAvailability(@PathParam("productId") String productId)
			throws ActiveMQException, InterruptedException, ExecutionException, ClassNotFoundException, SQLException {

		Statement productStatement = null;

		Connection dbConn = null;
		ResultSet rs = null;
		String skuAvailability = "SKU UNAVAILABLE";
		dbConn = nikeUtil.getextDBConnection();

		String sqlCreate = sqlSapFetch + productId.toLowerCase() + "'";
		try {
			productStatement = dbConn.createStatement();
			rs = productStatement.executeQuery(sqlCreate);

			while (rs != null && rs.next()) {
				if (rs.getInt(1) > 0) {
					skuAvailability = "AVAILABLE";
				}

			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (productStatement != null) {
				productStatement.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}

		return Response.status(Response.Status.OK).entity(skuAvailability).build();
	}


	


	
	/*
	 * @POST
	 * 
	 * @Path("/skusoftlock/{status}")
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON)
	 * 
	 * @ApiOperation(value = "Request", notes = "Sku Softlock")
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 200, message =
	 * "Request Sent"),
	 * 
	 * @ApiResponse(code = 401, message = "Failure") }) public Response
	 * productSoftlocking(@PathParam("status") String status, String skuId)
	 * throws ActiveMQException, InterruptedException, ExecutionException,
	 * ClassNotFoundException, SQLException {
	 * 
	 * CordaRPCOps rpcService =
	 * cordaRPCService.getRPCServiceByNode(nodeRpcHostAndPort);
	 * 
	 * SignedTransaction txn =
	 * rpcService.startFlowDynamic(SkuBlockConfirmationFlow.Initiator.class,
	 * skuId, status) .getReturnValue().get();
	 * 
	 * System.out.println("Test Success:" + txn);
	 * 
	 * return Response.status(Response.Status.OK).entity(txn).build(); }
	 */

	/*
	 * @GET
	 * 
	 * @Path("/updateInventory/{productId}")
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON)
	 * 
	 * @ApiOperation(value = "Sku Availability", notes = "Sku Availability")
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 200, message =
	 * "Request Sent"),
	 * 
	 * @ApiResponse(code = 401, message = "Failure") }) public Response
	 * updateInventory(@PathParam("productId") String productId) throws
	 * ActiveMQException, InterruptedException, ExecutionException,
	 * ClassNotFoundException, SQLException {
	 * 
	 * Statement productStatement = null;
	 * 
	 * Connection dbConn = null; ResultSet rs = null; String skuAvailability =
	 * "SKU UNAVAILABLE"; dbConn = getextDBConnection();
	 * 
	 * String sqlCreate =
	 * "UPDATE NIKE_INVENTORY.PRODUCT_MASTER SET STATUS = 'SOFTLOCKED' WHERE Item_no='"
	 * + productId.toLowerCase() + "'"; try { productStatement =
	 * dbConn.createStatement(); rs = productStatement.executeQuery(sqlCreate);
	 * 
	 * while (rs != null && rs.next()) { if (rs.getInt(1) > 0) { skuAvailability
	 * = "SUCCESS"; }
	 * 
	 * }
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * 
	 * } finally { if (rs != null) { rs.close(); } if (productStatement != null)
	 * { productStatement.close(); } if (dbConn != null) { dbConn.close(); } }
	 * 
	 * return
	 * Response.status(Response.Status.OK).entity(skuAvailability).build(); }
	 */
}
