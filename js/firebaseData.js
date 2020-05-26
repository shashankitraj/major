//data is fetched in 3 arrays :
  // --today
  // --past week
  // --past month
  var latiToday = [];//Stores the latitude for today
  var longiToday = [];//stores the longitude for today 
  var altiToday = [];//stores the altitude for today
  var speedToday = []; //stores the speed for today
  var dateToday = [];//store date for today
  var timeToday = [];//stores time for today

  var latiWeek = [];//Stores the latitude for Week
  var longiWeek = [];//stores the longitude for Week 
  var altiWeek = [];//stores the altitude for Week
  var speedWeek = []; //stores the speed for week
  var dateWeek = [];//store date for week
  var timeWeek = [];//stores time for week

  var latiMonth = [];//Stores the latitude for month
  var longiMonth = [];//stores the longitude for month 
  var altiMonth = [];//stores the altitude for month
  var speedMonth = []; //stores the speed for month
  var dateMonth = [];//store date for month
  var timeMonth = [];//stores time for month

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
            month = '0' + month;
          }
          if (day.toString().length == 1) {
            day = '0' + day;
          }
          var dateD = year + "-" + month + "-" + day;
          var mon = grandchildren.date.substring(5, 7);
          var da = grandchildren.date.substring(8, 10);
        }
        if (grandchildren.date == dateD && grandchildren.email==userU.email) {
          altiToday.push(grandchildren.altitude);
          latiToday.push(grandchildren.latitude);
          longiToday.push(grandchildren.longitude);
          dateToday.push(grandchildren.date);
          timeToday.push(grandchildren.time);
          speedToday.push(grandchildren.speed);
        }
        if (parseInt(da) + 7 >= parseInt(day) && (mon == month || parseInt(day) < 7) && grandchildren.email==userU.email) {
          altiWeek.push(grandchildren.altitude);
          latiWeek.push(grandchildren.latitude);
          longiWeek.push(grandchildren.longitude);
          dateWeek.push(grandchildren.date);
          timeWeek.push(grandchildren.time);
          speedWeek.push(grandchildren.speed);
          console.log()
        }
        if (parseInt(mon) + 1 >= parseInt(month) && grandchildren.email==userU.email) {
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
 // console.log(latiToday.length);
  selectElement = document.querySelector('#inputGroupSelect');
  output = selectElement.value;
  if (output == 1) {
    for (var i = 0; i < latiToday.length; i++) {
      points.push(new google.maps.LatLng(latiToday[i], longiToday[i]));
      console.log(points[i]);
    }
  }
}
function initMap() {
  var tumkur = { lat: 13.3269, lng: 77.1261 };
  var map = new google.maps.Map(document.getElementById("map"), {
    zoom: 18,
    center: tumkur
  });
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

  var marker = new google.maps.Marker({ position: tumkur, map: map });
}
//stats display of main page.
var di_date=document.getElementById('display_date');
var di_time=document.getElementById('display_time');
var di_speed=document.getElementById('display_speed');
var di_altitude=document.getElementById('display_altitude');
var di_distance=document.getElementById('display_distance');
di_date.innerHTML += '';

function distance(lat1, lon1, lat2, lon2, unit) {
	if ((lat1 == lat2) && (lon1 == lon2)) {
		return 0;
	}
	else {
		var radlat1 = Math.PI * lat1/180;
		var radlat2 = Math.PI * lat2/180;
		var theta = lon1-lon2;
		var radtheta = Math.PI * theta/180;
		var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
		if (dist > 1) {
			dist = 1;
		}
		dist = Math.acos(dist);
		dist = dist * 180/Math.PI;
		dist = dist * 60 * 1.1515;
		if (unit=="K") { dist = dist * 1.609344 }
		if (unit=="N") { dist = dist * 0.8684 }
		return dist;
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