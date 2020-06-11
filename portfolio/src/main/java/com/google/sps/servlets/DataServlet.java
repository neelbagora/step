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
import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;
import java.text.SimpleDateFormat;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

// Handles comment data on the '/data' page.
@WebServlet("/data")
public final class DataServlet extends HttpServlet {
  private ArrayList<String> ipAddresses = new ArrayList<>();

	/**
   * doGet is a method that handles HTTP get requests for Java servlets.
   * In this context, this method is responsible for handling network 
   * requests for the portfolio website. Each call to the method formats
   * the HTML page to contain the comment data in JSON format of the 
   * comments ArrayList. 
   * 
   * <b>This method will log IP addresses making requests to the server.</b>
   * 
   * @param request HttpServletRequest used to make POST request.
   * @param response HttpServletResponse used in GET requests.
   * @throws IOException
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		postIpAddress(getClientIpAddress(request));
		
		Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery results = datastore.prepare(query);

		ArrayList<UserComment> comments = new ArrayList<>();

		int limit = Integer.MAX_VALUE;
    if (request.getParameter("limit") != null) {
      try {
        limit = Integer.parseInt(request.getParameter("limit"));
      } catch (NumberFormatException e) {
        limit = 10;
      }
    }

		int counter = 0;
		for (Entity entity : results.asIterable()) {
      if (counter >= limit) {
				break;
			}
			counter++;
			long id = entity.getKey().getId();
			String name = (String) entity.getProperty("name");
			String message = (String) entity.getProperty("text");
			long timestamp = (long) entity.getProperty("timestamp");
      String user_id = (String) entity.getProperty("user");
      boolean edited = (boolean) entity.getProperty("edited");
      String imageUrl = null;
      if (entity.getProperty("image-url") != null) {
        imageUrl = (String) entity.getProperty("image-url");
      }

      UserComment userComment = new UserComment(id, name, message, timestamp, user_id, edited, imageUrl);
			comments.add(userComment);
		}

		Gson gson = new Gson();
		String jsonData = gson.toJson(comments);
		
		response.setContentType("application/json;");
		response.getWriter().println(jsonData);
  }

  /**
   * doPost is the method responsible for updating the global commentData
   * ArrayList with each comment added.
   *
   * @param request HttpServletRequest used to make POST request.
   * @param response HttpServletResponse used in GET requests.
   * @throws IOException
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    UserComment newComment = parseCommentData(request);
    UserService userService = UserServiceFactory.getUserService();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", newComment.getName());
    commentEntity.setProperty("text", newComment.getText());
    commentEntity.setProperty("timestamp", System.currentTimeMillis());
    commentEntity.setProperty("user", userService.getCurrentUser().getEmail());
    commentEntity.setProperty("edited", false);
    String uploadUrl = getUploadedFileUrl(request, "image");
    commentEntity.setProperty("image-url", uploadUrl);

    //String imageUrl = getImageUrl(request, "image");

    Entity logEntity = new Entity("Log");
    logEntity.setProperty("name", newComment.getName());
    logEntity.setProperty("text", newComment.getText());
    logEntity.setProperty("timestamp", convertTime(System.currentTimeMillis()));
    logEntity.setProperty("user", userService.getCurrentUser().getEmail());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    datastore.put(logEntity);

    // Respond with the result.
    response.sendRedirect("/index.html");
  }
/*
  public String getImageUrl(HttpServletRequest request, String parameterName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  }
*/
  /**
   * postIpAddress creates a post request to Java Servlet
   * containing string IP address and the String timestamp.
   *
   * @param ipAddress String IP address being posted.
   */
  private void postIpAddress(String ipAddress) {
    Entity ipEntity = new Entity("IPAddress");
    UserService userService = UserServiceFactory.getUserService();
    ipEntity.setProperty("ip", ipAddress);
    ipEntity.setProperty("timestamp", convertTime(System.currentTimeMillis()));
    if (userService.isUserLoggedIn()) {
      if (userService.getCurrentUser().getUserId() != null) {
        ipEntity.setProperty("user", userService.getCurrentUser().getUserId());
      }
    }
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(ipEntity);
  }

  /**
   * convertTime takes in the long representation of the current time
   * in milliseconds and converts it to a readable date format in the
   * form "MM/dd/yyyy hh:mm:ss [timezone]".
   *
   * @param timestamp long representing timestamp in milliseconds
   * @return          String date converted from long.
   */
	private String convertTime(long timestamp) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US);
		String timezone = "PST";
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
		Date date = new Date(timestamp);
		return simpleDateFormat.format(date) + " " + timezone;
	}

  /**
	 * parseCommentData takes in an HttpServletRequest from the HTML form
	 * and parses relevant information such as nickname, and
	 * message. If client sends "" as username, the default ldap is used if no
   * nickname is set. If nickname is set, then the comments are associated with
   * that nickname. Updates all entities in the datastore to match new nickname if
   * applicable
	 *
	 * @param request HttpServletRequest used to obtain data from POST request
	 * @return        UserComment constructed from POST request.
	 */
	private UserComment parseCommentData(HttpServletRequest request) {
    String username = getParameter(request, "uname", "");
    UserService userService = UserServiceFactory.getUserService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("UserData").addFilter("email", Query.FilterOperator.EQUAL, getParameter(request, "email", ""));
    PreparedQuery results = datastore.prepare(query);
    if (results.asSingleEntity() == null) {
      Entity entity = new Entity("UserData");
      entity.setProperty("email", userService.getCurrentUser().getEmail());
      if (username.equals("")) {
        username = userService.getCurrentUser().getEmail().substring(0, userService.getCurrentUser().getEmail().indexOf("@"));
      }
      entity.setProperty("nickname", username);
      datastore.put(entity);
    }
    else if (!username.equals("")) {
      Entity entity = results.asSingleEntity();
      entity.setProperty("nickname", username);
      datastore.put(entity);
      query = new Query("Comment").addFilter("user", Query.FilterOperator.EQUAL, userService.getCurrentUser().getEmail());
      results = datastore.prepare(query);
      for (Entity commentEntity : results.asIterable()) {
        commentEntity.setProperty("name", username);
        datastore.put(commentEntity);
      }
    }
    else {
      username = (String) results.asSingleEntity().getProperty("nickname");
    }

		return new UserComment(username, getParameter(request, "message", ""));
	}

	/**
	 * getParameter is purposed with handling edge cases for input data. If
	 * request.getParameter produces a null result, the default value is
	 * used.
	 *
	 * @param request      HttpServletRequest that will be used to getParameter data
	 * @param name         Name of parameter of interest.
	 * @param defaultValue The specified default value returned.
	 * @return             String value from parameter of interest
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

	/**
	 * getClientIpAddress() is a method used to confirm that the
	 * webpage is accepting client connections and to log all IP addresses
	 * Utilizes the items in the IP_HEADER_CANDIDATE array to cross check 
	 * and verify IP address. 
	 * IP addresses are used to keep track of requests that can be used to
	 * find potential misuse of server resources.
	 *
	 * @param request The HttpServletRequest of interest
	 * @return        IP Address of the HTTP request as String
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

    /** Returns a URL that points to the uploaded file, or null if the user didn't upload a file. */
  public String getUploadedFileUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // We could check the validity of the file here, e.g. to make sure it's an image file
    // https://stackoverflow.com/q/10779564/873165

    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);

    // To support running in Google Cloud Shell with AppEngine's devserver, we must use the relative
    // path to the image, rather than the path returned by imagesService which contains a host.
    try {
      URL url = new URL(imagesService.getServingUrl(options));
      return url.getPath();
    } catch (MalformedURLException e) {
      return imagesService.getServingUrl(options);
    }
  }
}
