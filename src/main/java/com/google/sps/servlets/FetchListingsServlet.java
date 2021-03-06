package com.google.sps.servlets;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StringValue;
import com.google.gson.Gson;
import com.google.sps.data.Listing;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for listing tasks. */
@WebServlet("/fetch-listings")
public class FetchListingsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    Query<Entity> query =
        Query.newEntityQueryBuilder().setKind("Listing").setOrderBy(OrderBy.desc("timestamp")).build();
    QueryResults<Entity> results = datastore.run(query);

    List<Listing> listings = new ArrayList<>();
    while (results.hasNext()) {
      Entity entity = results.next();

      long id = entity.getKey().getId();
      String title = entity.getString("title");
      String description = entity.getString("description");
      int capacity = (int) entity.getLong("capacity");
      long timestamp = entity.getLong("timestamp");
      String author = entity.getString("author");
      
      ArrayList<StringValue> updatedList = new ArrayList(entity.getList("users"));
      List<String> users = new ArrayList<>();
      for(StringValue v : updatedList) {
          users.add(v.get());
      }

      Listing listing = new Listing(id, title, description, capacity, timestamp, author, users);
      listings.add(listing);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(listings));
  }
}