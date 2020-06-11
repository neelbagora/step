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
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import com.google.gson.Gson;
import java.util.Map;
import java.util.Enumeration;

// Handles image data on the '/images' page.
@WebServlet("/images")
public final class ImageHandler extends HttpServlet {

	/**
   * doGet handles HTTP requests to '/images' and prints URL in json format
   * to '/images' servlet page. Is compatible with edit requests and generic
   * comment post requests.
   * 
   * @param request HttpServletRequest used to make POST request.
   * @param response HttpServletResponse used in GET requests.
   * @throws IOException
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String uploadServerUrl = "/data";
    if (request.getParameter("edit") != null) {
      uploadServerUrl = "/edit?id=" + request.getParameter("id");
      System.out.println("EDIT URL: " + uploadServerUrl);
    }
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadUrl = blobstoreService.createUploadUrl(uploadServerUrl);
    Gson gson = new Gson();
    String jsonData = gson.toJson(uploadUrl);
    response.setContentType("application/json;");
		response.getWriter().println(jsonData);
  } 
}