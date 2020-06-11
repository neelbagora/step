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

// Handles comment data on the '/data' page.
@WebServlet("/edit")
public final class EditComment extends HttpServlet {

  /**
   * doPost is responsible for handling HTTP requests being sent to
   * /edit. Edits specified id key in url parameters.
   *
   * @param request  HttpServletRequest being made by the client.
   * @param response HttpServletResponse being sent back to the client.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("Request Received");
		String id = request.getParameter("id") != null ? request.getParameter("id") : "";
    System.out.println(id);
    Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery results = datastore.prepare(query);
    String newMessage = request.getParameter("message");
    System.out.println(newMessage);
    String username = editUserName(request);
    System.out.println(username);
    long key_id = Long.parseLong(id);
    for (Entity entity : results.asIterable()) {
      if (entity.getKey().getId() == key_id) {
        entity.setProperty("name", username);
        entity.setProperty("text", newMessage);
        entity.setProperty("edited", true);
        entity.setProperty("timestamp", System.currentTimeMillis());
        datastore.put(entity);
        response.sendRedirect("/index.html");
        return;
      }
    }
    
    // Respond with the result.
    response.sendRedirect("/index.html");
  }

  /**
   * editUserName is responsible for handling username inputs being sent to
   * /edit. Edits the nickname of specified id if applicable.
   *
   * @param request  HttpServletRequest being made by the client.
   */  
  public String editUserName(HttpServletRequest request) {
    String username = request.getParameter("uname");
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("UserData").addFilter("email", Query.FilterOperator.EQUAL, request.getParameter("email"));
    PreparedQuery results = datastore.prepare(query);
    String imageUrl = new DataServlet().getUploadedFileUrl(request, "image");
    if (!username.equals("")) {
      Entity entity = results.asSingleEntity();
      entity.setProperty("nickname", username);
      datastore.put(entity);
      query = new Query("Comment").addFilter("user", Query.FilterOperator.EQUAL, userService.getCurrentUser().getEmail());
      results = datastore.prepare(query);
      for (Entity commentEntity : results.asIterable()) {
        commentEntity.setProperty("name", username);
        if (imageUrl != null) {
          commentEntity.setProperty("image-url", imageUrl);
        } 
        datastore.put(commentEntity);
      }
    }
    else {
      username = (String) results.asSingleEntity().getProperty("nickname");
    }
    return username;
  }
}