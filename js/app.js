(function() {
  firebase.auth().onAuthStateChanged(firebaseUser => {
    if (firebaseUser) {
      window.location.href = "./main.html";
    } else {
      console.log("Not logged in");
    }
  });

  const txtEmail = document.getElementById("txtEmail");
  const txtPassword = document.getElementById("txtPassword");
  const btnLogin = document.getElementById("btnLogin");
  const btnSignUp = document.getElementById("btnSignUp");
  const btnLogout1 = document.getElementById("btnLogout1");
  const btnLogout2 = document.getElementById("btnLogout2");

  btnLogin.addEventListener("click", e => {
    const email = txtEmail.value;
    const pass = txtPassword.value;
    const auth = firebase.auth();

    const promise = auth.signInWithEmailAndPassword(email, pass);
    promise.catch(e => console.log(e.message));
  });

  btnSignUp.addEventListener("click", e => {
    const email = txtEmail.value;
    const pass = txtPassword.value;
    const auth = firebase.auth();

    const promise = auth.createUserWithEmailAndPassword(email, pass);
    promise.catch(e => console.log(e.message));
  });

  btnLogout1.addEventListener("click", e => {
    firebase.auth().signOut();
  });
  btnLogout2.addEventListener("click", e => {
    firebase.auth().signOut();
  });
})();
