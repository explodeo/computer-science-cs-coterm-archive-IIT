<?php

function setProducts($conn){
	if (isset($_POST['commentSubmit'])) {
		$p_name = mysqli_real_escape_string($conn, $_POST['productName']);
		$p_desc = mysqli_real_escape_string($conn, $_POST['description']);
		//$p_cid = mysqli_real_escape_string($conn, $_POST['message']);
		$price = mysqli_real_escape_string($conn, $_POST['price']);
		$quantity = mysqli_real_escape_string($conn, $_POST['quantity']);
		$category = mysqli_real_escape_string($conn, $_POST['category']);
		if (empty($p_name) || empty($p_desc) || empty($price) || empty($quantity) || empty($category)) {
		header("Location: ../CS425/Market.php?message=empty");
		exit();
		}
		else{	
		$p_name = $_POST['productName'];
		$p_desc = $_POST['description'];
		$p_cid = $_POST['cid'];
		$price = $_POST['price'];
		$quantity =  $_POST['quantity'];
		$category =  $_POST['category'];


			$sql = "SELECT * FROM categories WHERE category_name = '$category'";
			$result = mysqli_query($conn,$sql);
			$resultCheck = mysqli_num_rows($result);
			if($resultCheck>0){

			}else{
				$sql= "INSERT INTO categories (category_name) VALUES ('$category')";
				$result = mysqli_query($conn,$sql);
				//temporarily here to allow code to work 
				$region = "SELECT customers_region FROM customers WHERE customer_id = '$p_cid'";

				$sql= "INSERT INTO warehouses (category_name, category_description, region_name) VALUES ('$category', '$category', (SELECT region_name FROM regions WHERE region_name = '$region'))";
				$result = mysqli_query($conn,$sql);

			}

			$sql = "INSERT INTO products (product_name, product_description,  product_customer_id, product_price, product_quantity, product_category) VALUES('$p_name', '$p_desc', '$p_cid', '$price', '$quantity', (SELECT category_name FROM categories WHERE category_name = '$category'))";
			$result = mysqli_query($conn, $sql);
		}
	}
} 

function getProducts($conn){
	$sql = "SELECT * FROM products";
	$result = mysqli_query($conn, $sql);
	while ($row = mysqli_fetch_assoc($result)) {
		echo "<div class='comment-box'> 
				<p>"
			 		. $row['uid']. "       "
			 		. $row['date']."<br>"
			 		.nl2br($row['message']). //nlb2r allows it to read spaces in database
			 	"</p>
			 	<form class='edit-form' method='POST' action='editcomment.php'> 
			 		<input type='hidden' name = 'cid' value ='".$row['cid']."'>
			 		<input type='hidden' name = 'uid' value ='".$row['uid']."'>
			 		<input type='hidden' name = 'date' value ='".$row['date']."'>
			 		<input type='hidden' name = 'message' value ='".$row['message']."'>
			 	<form>";

 
			 	if ($_SESSION['u_uid'] == $row['uid']) {
					echo "<button>Edit</button>";
			 	}	

			 	echo "</form>
			 </div>";
	}
}

function editComments($conn){
	if (isset($_POST['editSubmit'])) {
		$cid = $_POST['cid'];
		$uid = $_POST['uid'];
		$date = $_POST['date'];
		$message = $_POST['message'];

		$sql = "UPDATE usercomment SET message='$message' where cid='$cid' ";
		$result = mysqli_query($conn, $sql);
		header("Location: Forums.php");
	}
} 

//SELECT region_name FROM regions WHERE region_name = '$region')