<?php
require"conn.php";

if(isset($_POST["password"])&&isset($_POST["email"])){
$password=$_POST["password"];
$email=$_POST["email"];
$isValidEmail = filter_var($email,FILTER_VALIDATE_EMAIL);

if($conn){
    if($isValidEmail===false){
        echo "This Email is not valid";
   }
   else{
       $sqlCheckEmail = "SELECT* FROM `user` WHERE `email` LIKE '$email'";
       $emailQuery = mysqli_query($conn,$sqlCheckEmail);
       if(mysqli_num_rows($emailQuery)>0){
           $sqlLogin = "SELECT* FROM `user` WHERE `email` LIKE '$email'AND `password` LIKE '$password'";
           $loginQuery = mysqli_query($conn,$sqlLogin);
           if(mysqli_num_rows($loginQuery)>0){
               echo "Login Success";
           }
           else{
               echo "Wrong Password";
           }
       }
   }
}
else{
    echo"Connection Error";
}
}
?>