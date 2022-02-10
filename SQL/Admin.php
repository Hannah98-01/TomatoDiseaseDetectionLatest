<?php require 'conn.php'; ?>
<!DOCTYPE html>
<html lang ="en" dir ="ltr">
    <head>
         <meta charset="ustf-8">
         <title>Admin Page</title>
        </head>
    <body>
        <table border = 1 cellspacing = 0 cellpadding = 10>
        <caption style = "bg-color:red"> Tomato Disease Detection</caption>
        <tr>
            <td>#</td>
            <td>Disease Name</td>
            <td>Image</td>
            </tr>

            <?php
            //datbase connection
            $i= 1;
            $select = mysqli_query($conn, "SELECT * FROM imageData ORDER BY 'id' DESC");
            
            while($row = mysqli_fetch_array($select)){
            ?>
            <tr>
                <td><?php echo $i++; ?></td>
                <td><?php echo$row['diseasename'];?></td>
                <td><img src ="https://tamatodiseasedetection.000webhostapp.com/images/<?php echo $row['image']; ?> "width =200 title="<?php echo $row['image']; ?>"></td>
            </tr>
            <?php } ?>
            </table>
    </body>
    </html>