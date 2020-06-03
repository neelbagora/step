// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.sps.data.UserComment;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Handles comment data on the '/data' page.
@WebServlet("/data")
public final class DataServlet extends HttpServlet {
    private ArrayList<String> ipAddresses = new ArrayList<>();

    /*
     * doGet is a method that handles HTTP get requests for Java servlets.
     * In this context, this method is responsible for handling network 
     * requests for the portfolio website. Each call to the method formats
     * the HTML page to contain the comment data in JSON format of the 
     * comments ArrayList. 
     * 
     * This method will track IP addresses making GET requests to the server.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ipAddresses.add(getClientIpAddress(request));
        System.out.println("Client IP Address: " + getClientIpAddress(request));
        
        Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        ArrayList<UserComment> comments = new ArrayList<>();
        for (Entity entity : results.asIterable()) {
            long id = entity.getKey().getId();
            String name = (String) entity.getProperty("name");
            String message = (String) entity.getProperty("text");
            long timestamp = (long) entity.getProperty("timestamp");

            UserComment userComment = new UserComment(id, name, message, timestamp);
            comments.add(userComment);
        }

        Gson gson = new Gson();
        String jsonData = gson.toJson(comments);
        
        response.setContentType("application/json;");
        response.getWriter().println(jsonData);
    }

    /*
     * doPost is the method responsible for updating the global commentData
     * ArrayList with each comment added.
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the input from the form.
        UserComment newComment = parseCommentData(request);

        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("name", newComment.getName());
        commentEntity.setProperty("text", newComment.getText());
        commentEntity.setProperty("timestamp", System.currentTimeMillis());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

        // Respond with the result.
        response.sendRedirect("/index.html");
    }

    /*
     * parseCommentData takes in an HttpServletRequest from the HTML form
     * and parses relevant information such as first name, last name, and
     * message.
     */
    private UserComment parseCommentData(HttpServletRequest request) {
        return new UserComment(getParameter(request, "fname", "") + " " 
                             + getParameter(request, "lname", ""), 
                               getParameter(request, "message", ""));
    }

    /*
     * getParameter is purposed with handling edge cases for input data. If
     * request.getParameter produces a null result, the default value is
     * used.
     */
    private String getParameter(HttpServletRequest request, String name,
                                String defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    // potential IP Headers
    private static final String[] IP_HEADER_CANDIDATES = { 
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    };

    /*
     * getClientIpAddress() is a method used to confirm that the
     * webpage is accepting client connections and to log all IP addresses
     * Utilizes the items in the IP_HEADER_CANDIDATE array to cross check 
     * and verify IP address. 
     * IP addresses are used to keep track of requests that can be used to
     * find potential misuse of server resources.
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if ((ip != null) && (ip.length() != 0) &&
                (!"unknown".equalsIgnoreCase(ip))) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
