
package com.nike.wms;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import com.nike.wms.controller.LoginController;
import com.nike.wms.controller.RequestResponseController;
import com.nike.wms.controller.RunController;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;



/**
 * This class lists down all the configuration details 
 * @author Cognizant Blockchain Team
 * @version 1.0
 */

@Component
@ApplicationPath("/nike")
public class RestConfig extends ResourceConfig {
	public RestConfig() {		
				
		this.register(CORSFilter.class);
		this.register(JacksonFeature.class);	
		this.register(MultiPartFeature.class);
		this.register(RequestResponseController.class);
		this.register(LoginController.class);
		this.register(RunController.class);
		// swagger
		register(ApiListingResource.class);
		register(SwaggerSerializers.class);

		BeanConfig config = new BeanConfig();
		config.setConfigId("nike-api-service");
		config.setTitle("Nike Inventory Management");
		config.setVersion("v1");
		config.setContact("Cognizant Blockchain Team");
		config.setSchemes(new String[] { "http", "https" });
		config.setBasePath("/nike");
		config.setResourcePackage("com.nike.wms.controller");
		config.setPrettyPrint(true);
		config.setScan(true);	
	}
}
