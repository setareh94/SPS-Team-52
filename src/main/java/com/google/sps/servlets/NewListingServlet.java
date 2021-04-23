package com.google.sps.servlets;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Value;
import com.google.cloud.datastore.StringValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    String author = Jsoup.clean(request.getParameter("author"), Whitelist.none());
    int capacity = 0;
    long timestamp = System.currentTimeMillis();

    String referer = request.getHeader("Referer");

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
    System.out.println("timestamp: " + timestamp);
    System.out.println("author: " + author);

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Listing");
    ArrayList<Value<String>> userList = new ArrayList<Value<String>>();
    StringValue newUser = new StringValue(author);
    userList.add(newUser);
    FullEntity listingEntity =
        Entity.newBuilder(keyFactory.newKey())
            .set("title", title)
            .set("description", description)
            .set("capacity", capacity)
            .set("timestamp", timestamp)
            .set("author", author)
            .set("users", userList)
            .build();
    datastore.put(listingEntity);

    response.sendRedirect(referer);
    }
}