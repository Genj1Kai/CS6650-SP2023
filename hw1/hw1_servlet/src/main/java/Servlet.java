import java.io.BufferedReader;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "Servlet", value = "/Servlet")
public class Servlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/plain");
    String urlPath = request.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing paramterers");
      return;
    }

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)
    BufferedReader bufferedReader = request.getReader();
    StringBuilder stringBuilder = new StringBuilder();
    String requestString;
    String line;
    while ( (line = bufferedReader.readLine()) != null) {
      stringBuilder.append(line);
    }
    requestString = stringBuilder.toString();

    if (!isUrlValid(urlParts)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      response.setStatus(HttpServletResponse.SC_CREATED);
      response.getWriter().write("It works!" + requestString );
    }
  }
  private boolean isUrlValid(String[] urlPath) {
    return urlPath[1].equals("swipe") &&
        (urlPath[2].equals("left") || urlPath[2].equals("right"));
  }
}
