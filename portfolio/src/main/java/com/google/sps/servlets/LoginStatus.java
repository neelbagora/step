package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.sps.data.UserComment;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;

// Handles comment data on the '/data' page.
@WebServlet("/login")
public final class LoginStatus extends HttpServlet {
  
  /**
   * doGet is tasked with handling GET requests to the '/login' sub url of the page.
   * When called, if the user is signed in, the '/login' will write the user ID in JSON
   * format in order for the user to verify that they are logged in.
   *
   * @param request HttpServletRequest used to make POST request.
   * @param response HttpServletResponse used in GET requests.
   * @throws IOException
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    Gson gson = new Gson();
    if (userService.isUserLoggedIn()) {
      response.setContentType("application/json;");
		  response.getWriter().println(gson.toJson(userService.getCurrentUser().getEmail()));
    }
    else {
      String loginUrl = userService.createLoginURL("/index.html");
      response.sendRedirect(loginUrl);
    }
  }

  /**
   * doPost is tasked with handling POSTS requests to the '/login' sub url of the page.
   * When called, it requires user to be signed in, If user is not signed in, it will prompt
   * user to a login page where they can sign in using Google sign in page.
   *
   * @param request HttpServletRequest used to make POST request.
   * @param response HttpServletResponse used in GET requests.
   * @throws IOException
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String loginUrl = userService.createLoginURL("/index.html");
    System.out.println(loginUrl);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect(loginUrl);
    }
    // Respond with the result.
    response.sendRedirect("/index.html");
  }
}