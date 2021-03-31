package com.google.sps.servlets;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

@WebServlet("/new-listing-handler")
public class NewListingServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String title = Jsoup.clean(request.getParameter("title"), Whitelist.none());
    String description = Jsoup.clean(request.getParameter("description"), Whitelist.none());
    int capacity = 0;
    long timestamp = System.currentTimeMillis();

    try {
        capacity = Integer.parseInt(Jsoup.clean(request.getParameter("capacity"), Whitelist.none()));
    }
    catch(NumberFormatException e) {
        response.setContentType("text/html;");
        response.getWriter().println("Invalid capacity!");
        return;
    }

    // Print the input so you can see it in the server logs.
    System.out.println("title: " + title);
    System.out.println("description: " + description);
    System.out.println("capacity: " + capacity);
    System.out.println("timestamp: " + capacity);

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Listing");
    FullEntity listingEntity =
        Entity.newBuilder(keyFactory.newKey())
            .set("title", title)
            .set("description", description)
            .set("capacity", capacity)
            .set("timestamp", timestamp)
            .build();
    datastore.put(listingEntity);

    response.sendRedirect("/index.html");
    }
}