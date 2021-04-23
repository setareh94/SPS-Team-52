function generateListing(id, title, timestamp, author, description, capacity, subDescription, participants) {
    let listing = "  <div class=\"listing-header\">\n" +
        "                <div class=\"listing-title\">%TITLE%</div>\n" +
        "                <div class=\"listing-age\">%AGE%</div>\n" +
        "            </div>\n" +
        "            <div class=\"listing-subtitle\">created by %AUTHOR%</div>\n" +
        "            <div class=\"listing-description\">%DESC%</div>\n" +
        "            <div class=\"listing-capacity\">%CAPACITY% spot%CAP-MULT% open</div>\n" +
        "            <div class=\"listing-users\">\n" +
        "                <span class=\"listing-users-title\">Current Participants:</span>\n" +
        "                <span class=\"listing-users-list\">%PARTICIPANTS%</span>\n" +
        "            </div>\n" +
        "            <div class=\"listing-sub-description\">%SUB-DESC%</div>\n" +
        "            <div class=\"listing-buttons\">\n" +
        "                <button class=\"listing-button-detail\"><a href=\"details.html\">See details</a></button>\n" +
        "                <button class=\"listing-button-join\">Join</button>\n" +
        "            </div>";
    
    listing = listing.replace("%TITLE%", title)
        .replace("%AGE%", timeSince(new Date(timestamp)))
        .replace("%AUTHOR%", author)
        .replace("%DESC%", description)
        .replace("%CAPACITY%", capacity === 0 ? "No" : capacity)
        .replace("%CAP-MULT%", capacity === 1 ? "" : "s"/*"(s)"/*(capacity==="0" || capacity==="1" ? "s" : "")*/)
        .replace("%PARTICIPANTS%", participants.join(", "))
        .replace("%SUB-DESC%", subDescription);

    let div = document.createElement('div');
    div.setAttribute("class", "listing")
    div.innerHTML = listing;
    div.getElementsByClassName("listing-button-join")[0].addEventListener('click', () => {
        joinListing(id);
    });

    if(capacity === 0){
        div.getElementsByClassName("listing-button-join")[0].setAttribute("disabled", "disabled");
    }

    return div;
}


async function updateListings() {
    //const urlParams = new URLSearchParams(window.location.search);
    let listings = document.createDocumentFragment();
    listingCount = 0;
    await fetch('/fetch-listings').then(response => response.json()).then((listingsResponse) => {
        //const taskListElement = document.getElementById('task-list');
        listingsResponse.forEach((listing) => {
            //let div = document.createElement('div');
            //div.innerHTML = generateListing(listing["title"], listing["timestamp"], "TMP-AUTHOR", listing["description"], "0", "TMP-SUBDESC");
            //listings.appendChild(div);
            listings.appendChild(generateListing(listing["id"], listing["title"], listing["timestamp"], listing["author"], listing["description"], listing["capacity"], "1600 Amphitheatre Parkway Mountain View, CA 94043", listing["users"]));
            ++listingCount;
            //taskListElement.appendChild(createTaskElement(listing));
        })
    });
    document.getElementById('listings').innerHTML = "";
    document.getElementById('listings').appendChild(listings);
    document.getElementById('listing-counter').innerText = listingCount + " of " + listingCount + " listings";
    document.body.appendChild(listings);
}


function post(path, params, method='post') {
  const form = document.createElement('form');
  form.method = method;
  form.action = path;

  for (const key in params) {
    if (params.hasOwnProperty(key)) {
      const hiddenField = document.createElement('input');
      hiddenField.type = 'hidden';
      hiddenField.name = key;
      hiddenField.value = params[key];

      form.appendChild(hiddenField);
    }
  }

  document.body.appendChild(form);
  form.submit();
}


function joinListing(taskID) {
    console.log("User clicked join on task " + taskID);
    const urlParams = new URLSearchParams(window.location.search);
    let user = urlParams.has("user") ? urlParams.get("user") : "unknown username";
    let data = {userID: user, listingID: taskID};
    post('/add-user-to-listing-handler', data);
    // fetch("/add-user-to-listing-handler", {
    //     method: "POST", 
    //     body: JSON.stringify(data)
    // }).then(res => {
    //     console.log("Request complete! response:", res);
    // });
}

function timeSince(timeStamp) {
    const now = new Date();
    const secondsPast = (now.getTime() - timeStamp.getTime()) / 1000;
    if (secondsPast < 60) {
        return parseInt(secondsPast) + 's';
    }
    if (secondsPast < 3600) {
        return parseInt(secondsPast / 60) + 'm';
    }
    if (secondsPast <= 86400) {
        return parseInt(secondsPast / 3600) + 'h';
    }
    if (secondsPast > 86400) {
        day = timeStamp.getDate();
        month = timeStamp.toDateString().match(/ [a-zA-Z]*/)[0].replace(" ", "");
        year = timeStamp.getFullYear() == now.getFullYear() ? "" : " " + timeStamp.getFullYear();
        return day + " " + month + year;
    }
}