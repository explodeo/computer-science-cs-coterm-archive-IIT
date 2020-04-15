<?php
session_start();
include_once ( getcwd(). '/includes/dbh-inc.php');
$id = $_SESSION['c_id'];

$filename = "uploads/profile" . $id . "*";
$fileinfo = glob($filename);//glob searches
$fileext = explode('.', $fileinfo[0]);
$fileactualext = $fileext[1];

$files = "uploads/profile". $id . "." . $fileactualext;

if (!unlink($files)) {
	//file was deleted
}else{
	//file was not deleted
}

$sql = "UPDATE customers SET customers_img_status= 0 WHERE customer_id = '$id';";
mysqli_query($conn, $sql);
header("Location: Account.php?Success");