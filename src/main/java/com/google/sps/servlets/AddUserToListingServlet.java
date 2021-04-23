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

import com.google.cloud.datastore.*;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Entity.*;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.Value;
import com.google.cloud.datastore.StructuredQuery.*;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.StringValue;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;

/** Servlet responsible for adding users to listings */
@WebServlet("/add-user-to-listing-handler")
public class AddUserToListingServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String listingID = request.getParameter("listingID");
    String userID = request.getParameter("userID");
    String referer = request.getHeader("Referer");
    if(listingID == null || userID == null){
        System.out.println("ERROR: Malformed user add request. Missing listingID/userID. Got: userID=" + userID + ", listingID=" + listingID);
        response.sendRedirect(referer);
        return;
    }

    System.out.println("JOIN request recieved: userID=" + userID + ", listingID=" + listingID);

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Listing");
    Key listingKey = keyFactory.newKey(Long.valueOf(listingID));
    System.out.println("Searching for listing with key matching: " + listingKey.getId());

    Query<Entity> query = Query.newEntityQueryBuilder()
    .setKind("Listing")
    //.setFilter(PropertyFilter.eq("__key__", keyFactory.newKey(listingID)))
    .setFilter(PropertyFilter.eq("__key__", listingKey))
    .build();
    QueryResults<Entity> results = datastore.run(query);
    if(!results.hasNext()){
        System.out.println("No results found in listing database.");
        response.sendRedirect(referer);
        return;
    }
    
    Entity targetListing = results.next();
    long currentCapacity = targetListing.getLong("capacity");
    if(currentCapacity <= 0){
        System.out.println(userID + " tried to join listing #" + listingID + " but the listing was already full.");
        response.sendRedirect(referer);
        return;
    }

    //System.out.println("Current array: " + targetListing.getList("users"));
    var updatedList = new ArrayList(targetListing.getList("users"));
    StringValue newUser = new StringValue(userID);
    updatedList.add(newUser);
    Entity newListing = Entity.newBuilder(targetListing)
        .set("users", updatedList)
        .set("capacity", currentCapacity-1)
        .build();

    datastore.update(newListing);

    response.sendRedirect(referer);
  }
}
