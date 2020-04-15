<?php
/*code for running the signup script button*/	
if (isset($_POST['submit'])) {
	
	include_once 'dbh-inc.php';
	//my sql is an added security measure
	$first = mysqli_real_escape_string($conn, $_POST['first']);
	$last = mysqli_real_escape_string($conn, $_POST['last']);
	$email = mysqli_real_escape_string($conn, $_POST['email']);
	$uid = mysqli_real_escape_string($conn, $_POST['uid']);
	$pwd = mysqli_real_escape_string($conn, $_POST['pwd']);
	$region = mysqli_real_escape_string($conn, $_POST['region']);

// checks for empty fields (error handlers)	
	if (empty($first) || empty($last) || empty($email) || empty($uid) || empty($pwd)) {
		header("Location: ../SignUp.php?signup=empty");
		exit();	
		
	}else{

		//check if input characters are valid 
		if (!preg_match("/^[a-z A-Z]*$/", $first) || !preg_match("/^[a-z A-Z]*$/", $last)) {
			header("Location: ../SignUp.php?signup=invalid");
			exit();	
		}else{
			//check if email is valid
			if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
				header("Location: ../SignUp.php?signup=email");
				exit();
			}else{
				$sql = "SELECT * FROM customers WHERE customer_username = '$uid' ";
				$result = mysqli_query($conn, $sql);
				$resultCheck = mysqli_num_rows($result);

				if($resultCheck > 0){
					header("Location: ../SignUp.php?signup=	userTaken");
					exit();					
				}else{
					//Hashing the password 
					$hashedPwd = password_hash($pwd, PASSWORD_DEFAULT);


					$sql= "SELECT * FROM customers";
					$result = mysqli_query($conn,$sql);
					$resultCheck = mysqli_num_rows($result);
					if($resultCheck>0){
						//Insert the user into the database
					$sql = "INSERT INTO customers (customer_type, customer_first, customer_last, customer_email, customer_username, customers_password, customers_region, customers_img_status) 
										   VALUES (3,'$first', '$last', '$email', '$uid', '$hashedPwd', (SELECT region_name FROM regions WHERE region_name = '$region'), 0)"; //order by + allows rand assigment
					mysqli_query($conn, $sql);
					header("Location: ../SignUp.php?signup =success");
					exit();
					}
					else{
						//Insert the user into the database
					$sql = "INSERT INTO customers (customer_type, customer_first, customer_last, customer_email, customer_username, customers_password, customers_region, customers_img_status) 
										   VALUES (1,'$first', '$last', '$email', '$uid', '$hashedPwd', (SELECT region_name FROM regions WHERE region_name = '$region'), 0)"; //order by + allows rand assigment
					mysqli_query($conn, $sql);
					header("Location: ../SignUp.php?signup =success");
					exit();
					}

					
				}
			}
		}
	}


}	else{
	header("Location: ../SignUp.php");
	exit();
}