async function getData() {
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
  var ref = firebase.database().ref("Gps Details");
  await ref.once("value", gotData);
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
        }
        var mon = grandchildren.date.substring(5, 7);
        var da = grandchildren.date.substring(8, 10);
        if (grandchildren.date == dateD) {
          altiToday.push(grandchildren.altitude);
          latiToday.push(grandchildren.latitude);
          longiToday.push(grandchildren.longitude);
          dateToday.push(grandchildren.date);
          timeToday.push(grandchildren.time);
          speedToday.push(grandchildren.speed);

        }
        if (parseInt(da) + 7 >= parseInt(day) && (mon == month || parseInt(day) < 7)) {
          altiWeek.push(grandchildren.altitude);
          latiWeek.push(grandchildren.latitude);
          longiWeek.push(grandchildren.longitude);
          dateWeek.push(grandchildren.date);
          timeWeek.push(grandchildren.time);
          speedWeek.push(grandchildren.speed);
        }
        if (parseInt(mon) + 1 >= parseInt(month)) {
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
    }
  }
  console.log(points);


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