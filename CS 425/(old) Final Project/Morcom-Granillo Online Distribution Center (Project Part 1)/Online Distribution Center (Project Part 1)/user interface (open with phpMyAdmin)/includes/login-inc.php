<?php 
session_start();
	if(isset($_POST['submit'])) {

		include 'dbh-inc.php';
		//people cant code into login
		$uid = mysqli_real_escape_string($conn, $_POST['uid']);
		$pwd = mysqli_real_escape_string($conn, $_POST['pwd']);		
		//error handlers 
		//check if inputs are empty 
		if (empty($uid) || empty($pwd)) {
			header("Location: ../SignUp.php?login=empty");
			exit();
		}else{
			$sql = "SELECT * FROM customers WHERE customer_username = '$uid' OR customer_email = '$uid'";
			$result = mysqli_query($conn,  $sql);
			$resultCheck = mysqli_num_rows($result);
			if ($resultCheck < 1) {
				header("Location: ../SignUp.php?login=error1");
				exit();
			}else{
				if ($row = mysqli_fetch_assoc($result)) {
					//De-hashing the password 
					$hashedPwdCheck = password_verify($pwd, $row['customers_password']);
					if ($hashedPwdCheck == true) {
						header("Location: ../SignUp.php?login=error2");
						exit();
					}elseif ($hashedPwdCheck == false) {
						//Login the user here 
						$_SESSION['c_id'] = $row['customer_id'];
						$_SESSION['c_type'] = $row['customer_type'];
						$_SESSION['c_first'] = $row['customer_first'];
						$_SESSION['c_last'] = $row['customer_last'];
						$_SESSION['c_email'] = $row['customer_email'];
						$_SESSION['c_username'] = $row['customer_username'];
						$_SESSION['c_region'] = $row['customers_region'];
						$_SESSION['c_img'] = $row['customers_img_status'];
						header("Location: ../Home.php?login=success");
						exit();
					}
				}
			}
		}

	}else{
		header("Location: ../SignUp.php?login=error3");
		exit();
	}