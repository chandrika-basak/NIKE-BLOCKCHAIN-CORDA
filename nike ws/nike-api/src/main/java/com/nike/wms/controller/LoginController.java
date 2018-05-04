package com.nike.wms.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.nike.wms.service.LoginService;
import com.nike.wms.vo.LoginVO;
import com.nike.wms.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * This is a Controller class which exposes APIs for all Login functionalities.
 * @author Cognizant Blockchain Team
 * @version 1.0
 */

@Component
@Path("user")
@Api(tags = { "NIKE" })
public class LoginController {

	@Autowired
	private LoginService loginService;
	
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
	 * @param userVO
	 * @return
	 */

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Login", notes = "User Login")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Login Successful"),
			@ApiResponse(code = 401, message = "Unauthorized") })

	public Response login(UserVO userVO) {
		LoginVO loginVO = loginService.login(userVO);
		if (null != loginVO.getRole()) {
			return Response.status(Response.Status.OK).entity(loginVO).build();
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).entity(loginVO).build();
		}
	}
}