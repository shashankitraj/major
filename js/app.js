(function() {
  firebase.auth().onAuthStateChanged(firebaseUser => {
    if (firebaseUser) {
      console.log("Logged In");
      window.location.href = "./main.html";
      var log = document.getElementById("btnLogout");
      log.style.display = "inline";
    } else {
      var log = document.getElementById("btnLogout");
      log.style.display = "none";
      console.log("Not logged in");
    }
  });

  const txtEmail = document.getElementById("txtEmail");
  const txtPassword = document.getElementById("txtPassword");
  const btnLogin = document.getElementById("btnLogin");
  const btnSignUp = document.getElementById("btnSignUp");
  const btnLogout1 = document.getElementById("btnLogout");

  btnLogin.addEventListener("click", e => {
    const email = txtEmail.value;
    const pass = txtPassword.value;
    const auth = firebase.auth();

    const promise = auth.signInWithEmailAndPassword(email, pass);
    promise.catch(e => alert(e.message));
  });

  btnSignUp.addEventListener("click", e => {
    const email = txtEmail.value;
    const pass = txtPassword.value;
    const auth = firebase.auth();

    const promise = auth.createUserWithEmailAndPassword(email, pass);
    promise.catch(e => alert(e.message));
  });

  btnLogout1.addEventListener("click", e => {
    firebase.auth().signOut();
  });
})();
