<?php
include_once ( getcwd(). '/includes/dbh-inc.php');
$id = $_SESSION['c_id'];
	


function photoUpload($conn){	
//check if button was clicked 
if (isset($_POST['submitPhoto'])) {
	$file = $_FILES['file'];

	//stores array of info that is automatically generated when uploading 
	$fileName = $_FILES['file']['name'];
	$fileTmpName = $_FILES['file']['tmp_name'];
	$fileSize = $_FILES['file']['size'];
	$fileError = $_FILES['file']['error'];
	$fileType = $_FILES['file']['type'];

	//seperates the file type from file name i.e. .jpeg from my.jpeg
	$fileExt = explode('.', $fileName);
	$fileActualExt = strtolower(end($fileExt));

	$allowed = array('jpg', 'jpeg', 'png');

	//checks if extension is an allowed file type 
	if (in_array($fileActualExt, $allowed)) {
		if ($fileError === 0) {
			if ($fileSize < 500000 ) {

				//if theres a photo already it deletes it
				$sql="SELECT * FROM customers WHERE customer_id= $id";
						$result=mysqli_query($conn, $sql);
						while ($row = mysqli_fetch_assoc($result)) {
							$img = $row['customers_img_status'];
						}
				if ($img == 1) {
					include_once 'delete.php';
				}
				
				$fileNameNew = "profile". $id . "." . $fileActualExt;
				$fileDestination = 'uploads/' . $fileNameNew;
				move_uploaded_file($fileTmpName, $fileDestination);
				$sql = "UPDATE customers SET customers_img_status='1' WHERE customer_id = '$id';";
				$_SESSION['c_img'] = $row['customers_img_status'];
				$result = mysqli_query($conn, $sql);
				header("Location: Account.php?uploadsuccess");
			}else{
				echo "Your file is too big!";
			}
		}else{
			echo "There was an error uploading your file";
		}
	}else{
		echo "You cannot upload files of this type";
	}
}
}