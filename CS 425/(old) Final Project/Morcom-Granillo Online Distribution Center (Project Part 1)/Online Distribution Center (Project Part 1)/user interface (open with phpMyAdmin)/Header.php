<?php
	//allows user to login
	session_start();
?>

<!DOCTYPE html>	
<html>

<head>
<link href="style.css" type="text/css" rel="stylesheet">
<title>Login</title>
</head>

<body class="general">
		<div class="header">
			<div class="container-h">
			<a href="Home.php"><h1 class="title">College Life Central</h1></a>

						<?php

							if (isset($_SESSION['c_id'])) {							//updates database img info for entire website
								include_once ( getcwd(). '/includes/dbh-inc.php');
								$id = $_SESSION['c_id'];
									$sql="SELECT * FROM customers WHERE customer_id= $id";
									$result=mysqli_query($conn, $sql);
									while ($row = mysqli_fetch_assoc($result)) {
										$img = $row['customers_img_status'];
								}

								//appears when logged in
								echo '<div class="pages">

									  <ul class ="profile-header">
									  
									  <li><a href="Home.php">Home</a></li>
									  <li><a href="Market.php">Market</a></li>';

								if ($img == 0) {
							echo '<li> <a href="Account.php"> <img class= "header-img" src="uploads/profiledefault.jpg">'						
									     . $_SESSION['c_first'] . ' ' . $_SESSION['c_last'] . '</a>' . '</li>';

						}else{											
							$filename = "uploads/profile" . $id . "*";
							$fileinfo = glob($filename);
							$fileext = explode('.', $fileinfo[0]);
							$fileactualext = $fileext[1];
								
							echo '<li> <a href="Account.php"> <img class="header-img" src="uploads/profile' .$id. '.'. $fileactualext . '?'.mt_rand().'">'						
									     . $_SESSION['c_first'] . ' ' . $_SESSION['c_last'] . '</a>' . '</li>';
						} 	


   									 echo '<li>	
										<form action="includes/logout-inc.php" method="POST">
										<button type="submit" name="submit">Logout</button> 
									  	</form> 
									  </li>
									  </ul>
									  </div>';
							}else{
								echo '<div class="pages">
									<ul>
								<li><a href="Home.php">Home</a></li>
									  <li>
										<form class="login-form" action="includes/login-inc.php" method="POST">
											<input type="text" name="uid" placeholder="Username/email" size= "15%">
											<input type="password" name="pwd" placeholder="Password" size="15%">
											<button type="submit" name="submit">Login</button>
										</form>
									 </li>
										<li class="sign-up"><a href="SignUp.php">Sign Up</a></li>
										</ul>
									</div>';
							}
						?>
		
				
		</div>
	</div>