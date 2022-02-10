<?php
$connect = mysqli_connect("localhost","id18120150_admin","1234567890Hannah*","id18120150_tomatodiseasedetection");

$diseasename = $_POST["diseasename"];

if(isset($_POST['image'])){
    $target_dir = "images/";
    $image = $_POST['image'];
    $imageStore = rand()."_".time().".jpeg";
    $target_dir = $target_dir."/".$imageStore;
    file_put_contents($target_dir,base64_decode($image));

    $select = "INSERT into imageData(image,diseasename) VALUES ('$imageStore','$diseasename')";
    $responce = mysqli_query($connect,$select);

    if($responce){
        echo "Image Uploaded";
        mysqli_close($connect);
    }
    else{
        echo "Failed";
    }
}
?>