package servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import service.UserService;

@WebServlet(name = "servlets.StatServlet", value = "/servlets.StatServlet")
public class StatServlet extends HttpServlet {

  private static final Gson gson = new Gson();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    String urlPath = request.getPathInfo();
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("urlPath is null or empty");
      return;
    }
    String[] urlParts = urlPath.split("/");
    if (urlParts.length != 2) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("urlParts length is not 2");
      return;
    }
    String userId = urlParts[1];
    if (userId == null || userId.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("userId is null or empty");
      return;
    }
    try {
      UserService userService = new UserService();
      Map<String, Integer> state = userService.getStats(userId);
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().write(gson.toJson(state));
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("Exception: " + e.getMessage());
    }
  }
}
