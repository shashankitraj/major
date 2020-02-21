<?php

    session_start();

    $sqluser = "root";
    $sqlpassword = "";

    

    $sqldatabase = "login";

    /*
    for this page to work you have to create a database named login and a table named list in your mysql server.
    To do this, enter following in your mysql server:

    CREATE DATABASE login;

    USE login;

    CREATE TABLE list(
        id int not null auto_increment,
        user_name varchar(255) not null,
        first_name varchar(255) not null,
        last_name varchar(255) not null,
        email varchar(255) not null,
        password varchar(255) not null,
        PRIMARY KEY (id)
    );

    keep 'login' and 'list' and all field names in lowercase, otherwise, it won't work.
    */

    $post = $_SERVER['REQUEST_METHOD']=='POST';
/*    $_POST['uname'] = "";
    $_POST['fname'] = "";
    $_POST['lname'] = "";
    $_POST['email'] = "";
    $_POST['pass'] = "";
    $_POST['repass'] = "";  */
    $_POST['uname'] = (isset($_POST['uname']) ? $_POST['uname'] : '');
    $_POST['fname'] = (isset($_POST['fname']) ? $_POST['fname'] : '');
    $_POST['lname'] = (isset($_POST['lname']) ? $_POST['lname'] : '');
    $_POST['pass'] = (isset($_POST['pass']) ? $_POST['pass'] : '');
    if ($post) {
        if(
            empty($_POST['uname'])||
            empty($_POST['pass'])
        ) $empty_fields = true;

        else {
                try {
                    $pdo = new PDO("mysql:host=localhost;dbname=".$sqldatabase,$sqluser,$sqlpassword);
                    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
                } catch (PDOException $e) {
                    exit($e->getMessage());
                }
                $st = $pdo->prepare('SELECT * FROM list WHERE user_name=?');
                $st->execute(array($_POST['uname']));
                $r=$st->fetch();
                if($r != null && $r["password"]==$_POST['pass']) {
                    echo $_POST["uname"];
                    echo $_POST["pass"];
                    $_SESSION["uname"] = $_POST["uname"];
                    $_SESSION["pass"] = $_POST["pass"];
                    $_SESSION["fname"] = $r["first_name"];
                    echo $_SESSION["uname"];
                    echo $_SESSION["pass"];
                    header("Location:main.html");
                    exit;
                } else $login_err = true;
        }
    }
?>

<!DOCTYPE HTML>
<html>
<head>
<style type="text/css">
    body {
        margin:0px;
        padding:0px;
        font-family: sans-serif;
        font-size:.9em;
        background-image:url(edge.jpg);
        background-size:100%;
        overflow:hidden;
    }
    div {
        top:40%;
        left:17%;
        transform: translate(-50%,-50%);
        -ms-transform: translate(-50%,-50%);
        -moz-transform: translate(-50%,-50%);
        -webkit-transform: translate(-50%,-50%);
        position:absolute;
        width:350px;
        background:#eee;
        padding:10px 20px;
        border-radius: 2px;
        box-shadow:0px 0px 10px #aaa;
        box-sizing:border-box;
    }
    input {
        display: inline-block;
        border: none;
        width:100%;
        border-radius:2px;
        margin:5px 0px;
        padding:7px;
        box-sizing: border-box;
        box-shadow: 0px 0px 2px #ccc;
    }
    #submit {
        border:none;
        background-color: blue;
        color:white;
        font-size:1em;
        box-shadow: 0px 0px 3px #777;
        padding:10px 0px;
    }
    span {
        color:red;
        font-size: 0.75em;
    }
    p {
        text-align: center;
        font-size: 1.75em;
    }
    a {
        text-decoration: none;
        color:blue;
        font-weight: bold;
    }
    h1{
        text-align:center;
        margin:30px -40px;
        font-size:50px;
        color:#f2e8ff;
    }
</style>
</head> 
<body>
<h1>Smart Vehicle System</h1>
<div>
<form method="post" action="<?php echo $_SERVER['PHP_SELF'];?>">
    <p>Login</p>
    <?php 
    echo 'Username<br><input type="text" name="uname" value="'.$_POST['uname'].'" placeholder="Username"><br>';
    echo '<br>Password<br><input type="password" name="pass" value="'.$_POST['pass'].'" placeholder="Password"><br>';
    if(!empty($login_err)&&$login_err) echo "<span>Incorrect Username or password.</span>";
    if(!empty($empty_fields)&&$empty_fields) echo "<span>Enter username and password.</span>";
    ?>
    <br>
    <input type="submit" id="submit" href="main.html" value="Login"><br><br>
    Don't have a account? <a href="signup.php">SignUp</a>.<br><br>
</form>
</div>
</body>
</html>