<?php
require "conn.php";

if(isset($_POST["username"])&&isset($_POST["password"])&&isset($_POST["email"])&&isset($_POST["mobile"])){
$username=$_POST["username"];
$password=$_POST["password"];
$email=$_POST["email"];
$mobile=$_POST["mobile"];

$isValidEmail = filter_var($email,FILTER_VALIDATE_EMAIL);
if($conn){
    if(strlen($password)>40 || strlen($password)<6){
        echo "Password must be less than 40 and more than 6 characters";
    }
    else if($isValidEmail === false){
        echo"This Email is not valid";  
    }
    else{
        $sqlCheckUsername = "SELECT* FROM `user` WHERE `username` LIKE '$username'";
        $usernameQuery = mysqli_query($conn,$sqlCheckUsername);

        $sqlCheckEmail = "SELECT* FROM `user` WHERE `email` LIKE '$email'";
        $emailQuery = mysqli_query($conn,$sqlCheckEmail);

        if(mysqli_num_rows($usernameQuery)>0){
            echo"Username is already used, Please type another one";          
        }
        else if (mysqli_num_rows($emailQuery)>0){
            echo"This Email is already registered,Plaease type another Email";
        }
        else{
            $sql_register = "INSERT INTO `user`
            (`username`,`email`,`password`,`mobile`) VALUES('$username','$email','$password','$mobile')";
            if(mysqli_query($conn,$sql_register)){
                echo"Successfully Registered";
            }
            else{
                echo"Failed to register";
            }
        }
    }
}
else{
    echo"Connection Error";
}
}
?>
