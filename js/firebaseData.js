function getData() {
  var userUid;
  firebase.auth().onAuthStateChanged((user) => {
    if (user) {
      // User logged in already or has just logged in.
      userUid = user.uid;

      //console.log(user.uid);
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
        console.log("Altitude = " + grandchildren.altitude);
        console.log("Latitude = " + grandchildren.latitude);
        console.log("Longitude = " + grandchildren.longitude);
      }
    }
    // console.log(typeof usrData);
  }
}
