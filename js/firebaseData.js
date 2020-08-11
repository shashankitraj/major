//data is fetched in 3 arrays :
// --today
// --past week
// --past month
var latiToday = []; //Stores the latitude for today
var longiToday = []; //stores the longitude for today
var altiToday = []; //stores the altitude for today
var speedToday = []; //stores the speed for today
var dateToday = []; //store date for today
var timeToday = []; //stores time for today

var latiWeek = []; //Stores the latitude for Week
var longiWeek = []; //stores the longitude for Week
var altiWeek = []; //stores the altitude for Week
var speedWeek = []; //stores the speed for week
var dateWeek = []; //store date for week
var timeWeek = []; //stores time for week

var latiMonth = []; //Stores the latitude for month
var longiMonth = []; //stores the longitude for month
var altiMonth = []; //stores the altitude for month
var speedMonth = []; //stores the speed for month
var dateMonth = []; //store date for month
var timeMonth = []; //stores time for month

//2nd stats
var di=document.getElementById("temp");
var di_date1 = document.getElementById("display_date1");
var di_time1 = document.getElementById("display_time1");
var di_speed1 = document.getElementById("display_speed1");
var di_altitude1 = document.getElementById("display_altitude1");
var di_distance1 = document.getElementById("display_distance1");
//stats display of main page.
var di_date = document.getElementById("display_date");
var di_time = document.getElementById("display_time");
var di_speed = document.getElementById("display_speed");
var di_altitude = document.getElementById("display_altitude");
var di_distance = document.getElementById("display_distance");

//Finding the current users gps details.
function getData() {
  var userUid;
  var userU;
  firebase.auth().onAuthStateChanged((user) => {
    if (user) {
      // User logged in already or has just logged in.
      //getting the uid of the user
      userUid = user.uid;
      //getting reference for the user
      userU = user;
    } else {
      // User not logged in or has just logged out.
    }
  });

  var ref = firebase.database().ref("Gps Details");
  ref.once("value", gotData);
  function gotData(data) {
    var usrData = data.val();
    for (var child in usrData) {
      var children = usrData[child];
      for (var grandchild in children) {
        var grandchildren = children[grandchild];
        if (grandchildren.email == userU.email) {
          var now = new Date();
          var year = now.getFullYear();
          var month = now.getMonth() + 1;
          var day = now.getDate();
          if (month.toString().length == 1) {
            month = "0" + month;
          }
          if (day.toString().length == 1) {
            day = "0" + day;
          }
          var dateD = year + "-" + month + "-" + day;
          var mon = grandchildren.date.substring(5, 7);
          var da = grandchildren.date.substring(8, 10);
        }
        if (grandchildren.date == dateD && grandchildren.email == userU.email) {
          altiToday.push(grandchildren.altitude);
          latiToday.push(grandchildren.latitude);
          longiToday.push(grandchildren.longitude);
          dateToday.push(grandchildren.date);
          timeToday.push(grandchildren.time);
          speedToday.push(grandchildren.speed);
        }
        if (
          parseInt(da) + 7 >= parseInt(day) &&
          (mon == month || parseInt(day) < 7) &&
          grandchildren.email == userU.email
        ) {
          altiWeek.push(grandchildren.altitude);
          latiWeek.push(grandchildren.latitude);
          longiWeek.push(grandchildren.longitude);
          dateWeek.push(grandchildren.date);
          timeWeek.push(grandchildren.time);
          speedWeek.push(grandchildren.speed);
          console.log();
        }
        if (
          parseInt(mon) + 1 >= parseInt(month) &&
          grandchildren.email == userU.email
        ) {
          altiMonth.push(grandchildren.altitude);
          latiMonth.push(grandchildren.latitude);
          longiMonth.push(grandchildren.longitude);
          dateMonth.push(grandchildren.date);
          timeMonth.push(grandchildren.time);
          speedMonth.push(grandchildren.speed);
        }
      }
    }
  }
}
var points = [];
function setPath() {
  
  di_date.innerHTML= "";
  di_time.innerHTML ="";
  di_altitude.innerHTML = "";
  di_distance.innerHTML =  "";
    di_speed.innerHTML = "";
    di_date.innerHTML= "Date of last travel:";
    di_time.innerHTML ="Time of last travel:";
    di_altitude.innerHTML = "Last altitude recorded:";
    di_distance.innerHTML =  "Total distance travelled: ";
      di_speed.innerHTML = "Last speed recorded:";
  // console.log(latiToday.length);
  selectElement = document.querySelector("#inputGroupSelect");
  output = selectElement.value;

  var jsr = { lat: 22.811106, lng: 86.167485 };
  var map = new google.maps.Map(document.getElementById("map"), {
    zoom: 15,
    center: jsr,
  });

  if (output == 1) {
    di.style.visibility = 'hidden';
     di.style.display = 'none';
    for (var i = 0; i < latiToday.length; i++) {
      points.push(new google.maps.LatLng(latiToday[i], longiToday[i]));
    }

    di_date.innerHTML += dateToday[0];
    di_time.innerHTML += timeToday[0];
    di_altitude.innerHTML += altiToday[0];
    var dist = distance(
      latiToday[0],
      longiToday[0],
      latiToday[1],
      longiToday[1],
      "K"
    );
    di_distance.innerHTML += dist + "KM";
    di_speed.innerHTML += speedToday[0];

    var flightPath = new google.maps.Polyline({
      path: points,
      strokeWeight: 7,
      strokeColor: "#ffae00",
    });
    flightPath.setMap(null);
    flightPath.setMap(map);
  }
  if (output == 2) {
    di.style.visibility = 'hidden';
     di.style.display = 'none';
    for (var i = 0; i < latiWeek.length; i++) {
      points.push(new google.maps.LatLng(latiWeek[i], longiWeek[i]));
    }

    di_date.innerHTML += dateWeek[0];
    di_time.innerHTML += timeWeek[0];
    di_altitude.innerHTML += altiWeek[0];
    var dist = distance(
      latiWeek[0],
      longiWeek[0],
      latiWeek[1],
      longiWeek[1],
      "K"
    );
    di_distance.innerHTML += dist + "KM";
    di_speed.innerHTML += speedWeek[0];

    var flightPath = new google.maps.Polyline({
      path: points,
      strokeWeight: 7,
      strokeColor: "#ffae00",
    });
    flightPath.setMap(null);
    flightPath.setMap(map);
  }
  if (output == 3) {
    di.style.visibility = 'hidden';
     di.style.display = 'none';
    for (var i = 0; i < latiMonth.length; i++) {
      points.push(new google.maps.LatLng(latiMonth[i], longiMonth[i]));
    }

    di_date.innerHTML += dateMonth[0];
    di_time.innerHTML += timeMonth[0];
    di_altitude.innerHTML += altiMonth[0];
    var dist = distance(
      latiMonth[0],
      longiMonth[0],
      latiMonth[2],
      longiMonth[2],
      "K"
    );
    di_distance.innerHTML += dist + "KM";
    di_speed.innerHTML += speedMonth[0];

    var flightPath = new google.maps.Polyline({
      path: points,
      strokeWeight: 7,
      strokeColor: "#ffae00",
    });
    flightPath.setMap(null);
    flightPath.setMap(map);
  }
  if (output == 4) {
    di.style.visibility = 'visible';
    di.style.display = 'block';
    var mypoints = [
      new google.maps.LatLng(22.812369, 86.16183),
      new google.maps.LatLng(22.814051, 86.161578),
      new google.maps.LatLng(22.815745, 86.173252),
      new google.maps.LatLng(22.813839, 86.177067),
    ];
    var mypoints1 = [
      new google.maps.LatLng(22.817445, 86.171803),
      new google.maps.LatLng(22.817445, 86.171803),
      new google.maps.LatLng(22.821384, 86.173919),
      new google.maps.LatLng(22.819881, 86.175528),
    ];
    di_date.innerHTML += "2020-07-26";
    di_time.innerHTML += "09:10:46";
    di_altitude.innerHTML +="99 M";
    var dist = distance(22.814051, 86.161578, 22.815745, 86.173252, "K");
    di_distance.innerHTML += dist + "KM";
    di_speed.innerHTML += "21 KM/HR";
    
    di_date1.innerHTML += "2020-07-26";
    di_time1.innerHTML += "09:18:46";
    di_altitude1.innerHTML +="99 M";
    var dist = distance(22.817445, 86.171803, 22.819881, 86.175528, "K");
    di_distance1.innerHTML += dist + "KM";
    di_speed1.innerHTML += "34 KM/HR";
    var flightPath = new google.maps.Polyline({
      path: mypoints,
      strokeWeight: 7,
      strokeColor: "#337AB7",
    });
    var flightPath1 = new google.maps.Polyline({
      path: mypoints1,
      strokeWeight: 7,
      strokeColor: "#17A2B8",
    });
    
    flightPath.setMap(null);
    flightPath.setMap(map);
    flightPath1.setMap(null);
    flightPath1.setMap(map);
  }
}
function initMap() {
  var jsr = { lat: 22.817655, lng: 86.170762 };
  var map = new google.maps.Map(document.getElementById("map"), {
    zoom: 15,
    center: jsr,
  });

  // var flightPath = new google.maps.Polyline({
  //   path: points,
  //   strokeWeight: 2,
  // });

  // flightPath.setMap(map);
  /*
  var flightPlanCoordinates = [
    { 37.772, -122.214},
    { 21.291, -157.821},
    {-18.142, 178.431},
    { -27.467, 153.027}
  ];
  var flightPath = new google.maps.Polyline({
    path: flightPlanCoordinates,
    strokeWeight: 2
  });

  flightPath.setMap(map);
*/

  var marker = new google.maps.Marker({ position: jsr, map: map });
}

function distance(lat1, lon1, lat2, lon2, unit) {
  if (lat1 == lat2 && lon1 == lon2) {
    return 0;
  } else {
    var radlat1 = (Math.PI * lat1) / 180;
    var radlat2 = (Math.PI * lat2) / 180;
    var theta = lon1 - lon2;
    var radtheta = (Math.PI * theta) / 180;
    var dist =
      Math.sin(radlat1) * Math.sin(radlat2) +
      Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
    if (dist > 1) {
      dist = 1;
    }
    dist = Math.acos(dist);
    dist = (dist * 180) / Math.PI;
    dist = dist * 60 * 1.1515;
    if (unit == "K") {
      dist = dist * 1.609344;
    }
    if (unit == "N") {
      dist = dist * 0.8684;
    }
    return dist.toFixed(2);
  }
}
/* Use when required to access time.
         var hour = now.getHours();
         var minute = now.getMinutes();
         var second = now.getSeconds();
         if (hour.toString().length == 1) {
           hour = '0' + hour;
         }
         if (minute.toString().length == 1) {
           minute = '0' + minute;
         }
         if (second.toString().length == 1) {
           second = '0' + second;
         }
         var timeT = hour + ":" + minute + ":" + second;
         console.log(dateD);
         console.log(timeT);
         */
