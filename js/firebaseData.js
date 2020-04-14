function getData() {
  firebase.auth().onAuthStateChanged(user => {
    if (user) {
      // User logged in already or has just logged in.
      console.log(user.uid);
    } else {
      // User not logged in or has just logged out.
    }
  });
  var ref = firebase.database().ref("Uid");
  ref.once("value", gotData);
  function gotData(data) {
    var usrData = data.val();
    var uid = usrData[0].uid;
    console.log(uid);
  }
}
