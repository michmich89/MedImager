package com.MedImager.ExaminationServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.sql.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

//import org.apache.tomcat.util.http.fileupload.FileUtils;

import medview.datahandling.examination.NoSuchExaminationException;
import misc.foundation.MethodNotSupportedException;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("")
public class MyResource {
	
//	@GET
//	@Path("/search")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List <Examination> search(@QueryParam("value") List<String> values,
//    										 @QueryParam("term") List<String> terms,
//    										 @QueryParam("ageLower") String ageLower,
//    										 @QueryParam("ageUpper") String ageUpper, 
//    										 @QueryParam("gender") String gender,
//    										 @QueryParam("smoke") Boolean smoke,
//    										 @QueryParam("snuff") Boolean snuff) throws MethodNotSupportedException{
//		SearchTermParser stp = new SearchTermParser(new SearchFilter(values, terms, ageLower, ageUpper, gender, smoke, snuff));
//		return stp.getResultListWithFilter();
//    }
	
	@Secured
	@GET
	@Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List <Examination> search2(@Context UriInfo uriInfo) throws MethodNotSupportedException{
		SearchTermParser stp = new SearchTermParser(new SearchFilter(uriInfo));
		return stp.getResultListWithFilter2();
    }

	@GET
	@Path("/patient/{examinationID}")
    @Produces(MediaType.APPLICATION_JSON)
	public List<Examination> getPatientJSON(@PathParam("examinationID") String examinationID) throws MethodNotSupportedException, IOException{
		ExaminationIDParser eidParser = new ExaminationIDParser();
		return eidParser.getMoreFromPatient(examinationID);
	}

	@GET
	@Path("/examination/{examinationID}")
    @Produces(MediaType.APPLICATION_JSON)
	public Examination getExaminationJSON(@PathParam("examinationID") String examinationID) throws MethodNotSupportedException, IOException{
		ExaminationIDParser eidParser = new ExaminationIDParser();
		return eidParser.getMoreFromExamination(examinationID);
	}

	@GET
	@Path("/image/{examinationID}/{index}")
	@Produces("image/jpg")
	public Response getFile(@PathParam("examinationID") String examinationID, @PathParam("index") int index) throws IOException {
		ExaminationIDParser eidParser = new ExaminationIDParser();
		String path = eidParser.getMoreFromExamination(examinationID).getImagePaths().get(index);
		File file = new File(path);
		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition",
			"attachment; filename=" + examinationID + "-" + index + ".jpg");
		return response.build();

	}
	@GET
	@Path("/thumbnail/{examinationID}/{index}")
	@Produces("image/jpg")
	public Response getThumbnailFile(@PathParam("examinationID") String examinationID, @PathParam("index") int index) throws IOException {
		ExaminationIDParser eidParser = new ExaminationIDParser();
		String path = eidParser.getMoreFromExamination(examinationID).getImagePaths().get(index);
		ThumbnailBuilder thumbBuilder = new ThumbnailBuilder();
		File file = thumbBuilder.getThumbnail(path, examinationID, index);
		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition",
			"attachment; filename=" + examinationID + "-" + index + "-thumb.jpg");
		return response.build();
	}
	
	static int userID = 2;
	
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/collection")
    public void createCollectiion(Collection item) throws Exception{
    	Connection c = new DatabaseConnection().getConnection();
    	
	    String addQuerry = "INSERT INTO collection(user_id,name,descr,date)"
	    		+ " VALUES('"+userID+"','"+item.getCollectionName()+"','"+item.getCollectionDescr()+"', NOW());";
	    PreparedStatement preparedStatement = null;
	    preparedStatement = c.prepareStatement(addQuerry);
	    preparedStatement.executeUpdate();	    
	    c.close();
    }
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/collection/{collectionid}")
    public void addItem(CollectionItem item, @PathParam("collectionid") int collectionid) throws Exception{
	    Connection c = new DatabaseConnection().getConnection();
	    String addQuerry = "INSERT INTO collectionitem(examinationID,imageindex,collection_id,user_id)"
	    		+ " VALUES('"+item.getExaminationID()+"',"+item.getIndex()+","+collectionid+","+userID+");";
	    PreparedStatement preparedStatement = null;
	    preparedStatement = c.prepareStatement(addQuerry);
	    preparedStatement.executeUpdate();
	    c.close();
    }
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/collection/")
    public List<Collection> getCollections() throws Exception{
	    Connection c = new DatabaseConnection().getConnection();
	    String addQuerry = "SELECT id,name,descr FROM collection WHERE user_id = " + userID + ";";
	    Statement stmt = c.createStatement();
	    ResultSet rs = stmt.executeQuery(addQuerry);
	    List<Collection> result = new ArrayList<Collection>();
	    while(rs.next()){
	    	Collection col = new Collection();
	    	col.setCollectionID(rs.getInt("id"));
	    	col.setCollectionName(rs.getString("name"));
	    	col.setCollectionDescr(rs.getString("descr"));
	    	result.add(col);
	    }
	    c.close();
        return result;
    }
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/collection/{collectionID}")
    public List<CollectionItem> getCollectionItems(@PathParam("collectionID") int collectionID) throws Exception{
	    Connection c = new DatabaseConnection().getConnection();
	    String addQuerry = "SELECT examinationID,imageindex,note,collection_id,collectionitem_id FROM collectionitem"
	    		+ " WHERE user_ID = " + userID + " AND  collection_id = " + collectionID + ";";
	    Statement stmt = c.createStatement();
	    ResultSet rs = stmt.executeQuery(addQuerry);
	    List<CollectionItem> result = new ArrayList<CollectionItem>();
	    while(rs.next()){
	    	CollectionItem col = new CollectionItem();
	    	col.setExaminationID(rs.getString("examinationID"));
	    	col.setIndex(rs.getInt("imageindex"));
	    	col.setNote(rs.getString("note"));
	    	col.setCollectionID(rs.getInt("collection_id"));
	    	col.setCollectionitemID(rs.getInt("collectionitem_id"));
	    	result.add(col);
	    }
	    c.close();
        return result;
    }
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/collection/note")
    public void addNote(CollectionItem col) throws SQLException{
	    Connection c = new DatabaseConnection().getConnection();
	    String addQuerry = "UPDATE collectionitem SET note ='"+col.getNote()+"'"
	    		+ " WHERE user_id = " + userID + " AND collection_id = " + col.getCollectionID() + " AND collectionitem_id = "+ col.getCollectionitemID() +";";
	    PreparedStatement preparedStatement = null;
	    preparedStatement = c.prepareStatement(addQuerry);
	    preparedStatement.executeUpdate();
	    c.close();
    }
    
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/collection/description")
    public void addDescription(Collection col) throws SQLException{
	    Connection c = new DatabaseConnection().getConnection();
	    String addQuerry = "UPDATE collection SET descr ='"+col.getCollectionDescr()+"'"
	    		+ " WHERE user_id = " + userID + " AND id = " + col.getCollectionID() + ";";
	    PreparedStatement preparedStatement = null;
	    preparedStatement = c.prepareStatement(addQuerry);
	    preparedStatement.executeUpdate();
	    c.close();
    }

    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/collection/")
    public void deleteCollection(Collection col) throws SQLException{
	    Connection c = new DatabaseConnection().getConnection();
	    String addQuerry = "DELETE FROM collection WHERE user_id=" + userID + " AND id=" + col.getCollectionID() + ";";
	    PreparedStatement preparedStatement = null;
	    preparedStatement = c.prepareStatement(addQuerry);
	    preparedStatement.executeUpdate();
	    addQuerry = "DELETE FROM collectionitem WHERE user_id=" + userID + " AND collection_id=" + col.getCollectionID() + ";";
	    preparedStatement = c.prepareStatement(addQuerry);
	    preparedStatement.executeUpdate();
	    c.close();
    }
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/collection/{collectionID}")
    public void deleteImageInCollection(CollectionItem col, @PathParam("collectionID") int collectionID) throws SQLException{
	    Connection c = new DatabaseConnection().getConnection();
	    String addQuerry = "DELETE FROM collectionitem"
	    		+ " WHERE user_id=" + userID + " AND collectionitem_id=" + col.getCollectionitemID() + " AND collection_id=" + collectionID +";";
	    PreparedStatement preparedStatement = null;
	    preparedStatement = c.prepareStatement(addQuerry);
	    preparedStatement.executeUpdate();
	    c.close();
    }
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/collection/share/{targetUserID}")
    public void shareCollectiion(Collection item, @PathParam("targetUserID") int targetUserID) throws Exception{
    	Connection c = new DatabaseConnection().getConnection();
	    String addQuerry = "INSERT INTO bookmark(user_id,collection_id) VALUES('"+targetUserID+"','"+ item.getCollectionID() + "');";
	    PreparedStatement preparedStatement = null;
	    preparedStatement = c.prepareStatement(addQuerry);
	    preparedStatement.executeUpdate();	    
	    c.close();
    }/*
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/collection/share")
    public List<Collection> getSharedCollections() throws Exception{
	    Connection c = new DatabaseConnection().getConnection();
	    String addQuerry = "SELECT share_id FROM bookmark WHERE user_id = " + userID + ";";
	    Statement stmt = c.createStatement();
	    ResultSet rs = stmt.executeQuery(addQuerry);
	    String shareID = null;
	    List<Collection> result = new ArrayList<Collection>();
	    while(rs.next()){
	    	shareID = rs.getString("share_id");
	    	String innerquerry = "SELECT id,name,descr FROM collection WHERE share_id = '"+ shareID +"';";
	    	Statement innerstmt = c.createStatement();
		    ResultSet innerrs = innerstmt.executeQuery(innerquerry);
		    while(innerrs.next()){
		    	Collection col = new Collection();
		    	col.setCollectionID(innerrs.getInt("id"));
		    	col.setCollectionName(innerrs.getString("name"));
		    	col.setCollectionDescr(innerrs.getString("descr"));
		    	result.add(col);
		    }
	    }
	    c.close();
        return result;
    }*/
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/collection/share")
    public List<Collection> getSharedCollections() throws Exception{
	    Connection c = new DatabaseConnection().getConnection();
	    String addQuerry = "SELECT id,name,descr FROM collection"
	    		+ " WHERE EXISTS (SELECT 1 FROM bookmark WHERE bookmark.user_id="+ userID +" AND bookmark.collection_id=collection.id);";
	    Statement stmt = c.createStatement();
	    ResultSet rs = stmt.executeQuery(addQuerry);
	    List<Collection> result = new ArrayList<Collection>();
	    while(rs.next()){
	    	Collection col = new Collection();
	    	col.setCollectionID(rs.getInt("id"));
	    	col.setCollectionName(rs.getString("name"));
	    	col.setCollectionDescr(rs.getString("descr"));
	    	result.add(col);
	    }
	    c.close();
	    rs.close();
        return result;
    }
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/collection/share/{collectionID}")
    public List<CollectionItem> getSharedCollectionItems(@PathParam("collectionID") int collectionID) throws Exception{
	    Connection c = new DatabaseConnection().getConnection();
	    String addQuerry = "SELECT examinationID,imageindex,note,collection_id,collectionitem_id FROM collectionitem"
	    		+ " WHERE EXISTS (SELECT 1 FROM bookmark WHERE bookmark.user_id= " + userID + " AND bookmark.collection_id=" + collectionID + ")"
	    		+ " AND collection_id="+ collectionID +";";
	    Statement stmt = c.createStatement();
	    ResultSet rs = stmt.executeQuery(addQuerry);
	    List<CollectionItem> result = new ArrayList<CollectionItem>();
	    while(rs.next()){
	    	CollectionItem col = new CollectionItem();
	    	col.setExaminationID(rs.getString("examinationID"));
	    	col.setIndex(rs.getInt("imageindex"));
	    	col.setNote(rs.getString("note"));
	    	col.setCollectionID(rs.getInt("collection_id"));
	    	col.setCollectionitemID(rs.getInt("collectionitem_id"));
	    	result.add(col);
	    }
	    c.close();
        return result;
    }
	@GET
	@Path("/initValues")
    @Produces(MediaType.APPLICATION_JSON)
	public InitValues getTreatTypesJSON() throws IOException, NoSuchExaminationException{
		ServerInitializer init = new ServerInitializer();
		return init.initialize();
	}
}