package com.MedImager.ExaminationServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import medview.common.data.MedViewUtilities;
import medview.datahandling.examination.MVDHandler;
import medview.datahandling.images.ExaminationImage;
import misc.foundation.MethodNotSupportedException;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("search")
public class MyResource {

	//SearchTermParser search = new SearchTermParser("Pollen");
	//MedViewUtilities util = new MedViewUtilities();
	@GET
	@Path("{searchTerm}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Examination> getJSONResult(@PathParam("searchTerm") String searchTerm) throws MethodNotSupportedException{
		SearchTermParser search = new SearchTermParser(searchTerm);
		return search.getResultList();
    }
}