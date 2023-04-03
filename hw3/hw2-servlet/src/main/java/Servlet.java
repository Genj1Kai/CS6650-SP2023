import com.google.gson.Gson;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@WebServlet(name = "Servlet", value = "/Servlet")
public class Servlet extends HttpServlet {
    private ChannelPool channelPool;
    private DynamoDbConn dynamoDbConn;
    private static final Gson gson = new Gson();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            this.channelPool = new ChannelPool();
            dynamoDbConn = DynamoDbConn.createDynamoDbConn();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String urlPath = request.getPathInfo();
        String[] urlParts = urlPath.split("/");
        if (!isValidGetPath(urlParts)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid GET path"); // HTTP 404
            return;
        }
        String tag = urlParts[1];
        int userId = Integer.parseInt(urlParts[2]);
        try {
            String respBody;
            if (tag.equals("matches")) {
                MatchesResponse matchesResponse = dynamoDbConn.queryTableMatches("SwipeMatch", "Id", String.valueOf(userId), "#Id");
                if (matchesResponse == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found"); // HTTP 404
                    return;
                }
                respBody = gson.toJson(matchesResponse);
            } else {
                StatsResponse statsResponse = dynamoDbConn.queryTableStats("SwipeAction", "Id", String.valueOf(userId), "#Id");
                if (statsResponse == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found"); // HTTP 404
                    return;
                }
                respBody = gson.toJson(statsResponse);
            }
            response.setStatus(HttpServletResponse.SC_OK); // HTTP 200
//            response.getWriter().write(respBody.replace("\\","").replace("\"", ""));
            response.getWriter().write(respBody);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Inputs"); // HTTP 400
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String urlPath = request.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");
//        System.out.println(urlPath);
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

        Gson gson = new Gson();

        try {
            SwipeDetail swipeDetail = gson.fromJson(requestString, SwipeDetail.class);

            String msg = urlParts[2] + "/"+ swipeDetail.getSwiper() + "/" + swipeDetail.getSwipee() + "/" + swipeDetail.getComment();
            boolean flag = channelPool.sendToQueue(msg);
            if (!isUrlValid(urlParts, swipeDetail) || !flag) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("It works!" + requestString );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private boolean isValidGetPath(String[] urlPath) {
        return (urlPath[1].equals("matches") || urlPath[1].equals("stats"));
    }

    private boolean isUrlValid(String[] urlPath, SwipeDetail swipeDetail) {
        return urlPath[1].equals("swipe") &&
            (urlPath[2].equals("left") || urlPath[2].equals("right"))
            && swipeDetail.getSwiper() != null
            && swipeDetail.getSwipee() != null &&
            swipeDetail.getComment() != null;
    }
}
