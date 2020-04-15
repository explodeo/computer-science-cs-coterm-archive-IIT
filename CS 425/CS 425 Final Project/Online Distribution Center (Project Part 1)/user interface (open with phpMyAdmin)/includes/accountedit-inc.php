<?php

function setComments($conn){
	if (isset($_POST['commentSubmit'])) {
		$message = mysqli_real_escape_string($conn, $_POST['message']);
		if (empty($message)) {
		header("Location: ../Website/Forums.php?message=empty");
		exit();
		}
		else{	
			$uid = $_POST['uid'];
			$date = $_POST['date'];
			$message = $_POST['message'];

			$sql = "INSERT INTO usercomment (uid, date,  message) VALUES('$uid', '$date', '$message')";
			$result = mysqli_query($conn, $sql);
		}
	}
} 

function getaccount($conn){
	$sql = "SELECT * FROM customers WHERE customer_id= " . $_SESSION['c_id'] . "";
	$result = mysqli_query($conn, $sql);
	while ($row = mysqli_fetch_assoc($result)) {
		echo "<table class='text-seperate'>
							<tr>
								<td>  Name: " .  $row['customer_first'] .' '.  $row['customer_last'] . " </td>

							</tr>
							<tr>
								<td> Username: " .  $row['customer_username'] . " </td>
								

							</tr>
							<tr>
								<td> Region: " . $row['customers_region'] . " </td>
							</tr>	
							<tr>

							</tr>

								<tr>
									<td> Email: " .  $row['customer_email'] . " </td>
									<td></td>
									<td>
										<form class='edit-form' method='POST' action='editaccount.php'>
											<input type='hidden' name = 'c_id' value ='".$_SESSION['c_id']."'>
							 				<input type='hidden' name = 'c_first' value ='".$_SESSION['c_first']."'>
							 				<input type='hidden' name = 'c_last' value ='".$_SESSION['c_last']."'>
							 				<input type='hidden' name = 'c_email' value ='".$_SESSION['c_email']."'>
							 				<input type='hidden' name = 'c_username' value ='".$_SESSION['c_username']."'>
							 				<input type='hidden' name = 'c_region' value ='".$_SESSION['c_region']."'>
					 					<form>
									<button type='submit' value='edit' name='edit'>Edit</button>
									</td>
								</tr>
							</table>";
	}
}

function editaccountFirst($conn){
	if (isset($_POST['editFirst'])) {
		$cid = $_POST['c_id'];
		$update = $_POST['update'];
		$sql = "UPDATE customers SET customer_first='$update' where customer_id='$cid' ";
		$result = mysqli_query($conn, $sql);
		header("Location: Account.php");
	}
} 

function editaccountLast($conn){
	if (isset($_POST['editLast'])) {
		$cid = $_POST['c_id'];
		$update = $_POST['update'];
		$sql = "UPDATE customers SET customer_Last='$update' where customer_id='$cid' ";
		$result = mysqli_query($conn, $sql);
		header("Location: Account.php");
	}
} 

function editaccountUsername($conn){
	if (isset($_POST['editUsername'])) {
		$cid = $_POST['c_id'];
		$update = $_POST['update'];
		$sql = "UPDATE customers SET customer_username='$update' where customer_id='$cid' ";
		$result = mysqli_query($conn, $sql);
		header("Location: Account.php");
	}
} 

function editaccountEmail($conn){
	if (isset($_POST['editEmail'])) {
		$cid = $_POST['c_id'];
		$update = $_POST['update'];
		$sql = "UPDATE customers SET customer_email='$update' where customer_id='$cid' ";
		$result = mysqli_query($conn, $sql);
		header("Location: Account.php");
	}
} 



function editaccountRegion($conn){
	if (isset($_POST['editRegion'])) {
		$cid = $_POST['c_id'];
		$update = $_POST['update'];
		$region = "SELECT region_name FROM regions WHERE region_name = '$update'";
		$sql = "UPDATE customer SET customers_region='$region' where customer_id='$cid' ";
		$result = mysqli_query($conn, $sql);
		header("Location: Account.php");
	}
} 


