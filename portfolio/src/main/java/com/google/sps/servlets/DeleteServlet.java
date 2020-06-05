import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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
@WebServlet("/delete-data")
public final class DeleteServlet extends HttpServlet {

  /**
   * doPost is responsible for handling HTTP requests being sent to
   * /delete-data. Removes specified id key in url parameters.
   *
   * @param request  HttpServletRequest being made by the client.
   * @param response HttpServletResponse being sent back to the client.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String id = request.getParameter("id") != null ? request.getParameter("id") : "";
    System.out.println("ID: " + request.getParameter("id"));
    Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery results = datastore.prepare(query);
    
    for (Entity entity : results.asIterable()) {
      try {
        if (entity.getKey().getId() == Long.parseLong(id)) {
          datastore.delete(entity.getKey());
          break;
        }
      } catch (NumberFormatException e) {
        System.out.println("Improper input");
      }
    }  
    // Respond with the result.
    response.sendRedirect("/index.html");
  }
}