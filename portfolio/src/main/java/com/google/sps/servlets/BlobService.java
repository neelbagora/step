package com.google.sps.servlets;

import com.google.sps.servlets.DataServlet;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

// Handles comment data on the '/blobs' page.
@WebServlet("/blobs")
public final class BlobService extends HttpServlet {

	/**
   * doGet handles HTTP requests to '/blobs' which takes in an id as a
   * parameter query and responds with the image belonging to the comment
   * with the associated comment ID. 
   * 
   * @param request HttpServletRequest used to make GET request.
   * @param response HttpServletResponse used in GET requests.
   * @throws IOException
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String commentId = request.getParameter("id");
    Query query = new Query("Comment");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery results = datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      if (entity.getKey().getId() == Long.parseLong(commentId)) {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        BlobKey blobKey = (BlobKey) entity.getProperty("image-url");
        blobstoreService.serve(blobKey, response);
        return;
      }
    }
  }
}