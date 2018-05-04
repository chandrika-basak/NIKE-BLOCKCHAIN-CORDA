package com.nike.wms;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author 549439
 *
 */
public class CORSFilter implements ContainerResponseFilter {

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.ws.rs.container.ContainerResponseFilter#filter(javax.ws.rs.
	 * container.ContainerRequestContext,
	 * javax.ws.rs.container.ContainerResponseContext)
	 */
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
			throws IOException {
		final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
		headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
		//headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type,Accept, Authorization"); 
		headers.add("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0");
	}

}
