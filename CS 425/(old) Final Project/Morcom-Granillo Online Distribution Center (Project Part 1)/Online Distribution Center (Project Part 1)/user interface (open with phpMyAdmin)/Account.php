<?php
	include_once 'Header.php';	
?>



	<div class="container">
		<div class="text">
			<h2> User Profile </h2>
				<?php 		
					include ( getcwd(). '/upload.php');
					include ( getcwd(). '/includes/accountedit-inc.php');
					include_once ( getcwd(). '/includes/dbh-inc.php');
					$id = $_SESSION['c_id'];
					if(isset($id)) { //updates the database
						$sql="SELECT * FROM customers WHERE customer_id= $id";
						$result=mysqli_query($conn, $sql);
						while ($row = mysqli_fetch_assoc($result)) {
							$img = $row['customers_img_status'];
						}
					}
					echo "<div class= 'user-container'>";
						if ($img == 0) {
							echo "<img src='uploads/profiledefault.jpg'>";
						}else{

							echo "<img src='uploads/profile" .$id. "." . $fileactualext . "?" .mt_rand()."'>";
						} 
					/*echo "<p onclick='makeEditable(this)' onblur='makeReadOnly(this, '$_SESSION['c_first']')' class='text-seperate' 'display:inline;'>" .  $_SESSION['c_first'] . "</p> 
						  <p onclick='makeEditable(this)' onblur='makeReadOnly(this, '$_SESSION['c_last']')' class='text-seperate' 'display:inline;'>" .  $_SESSION['c_last'] . "</p> 
						  <p onclick='makeEditable(this)' onblur='makeReadOnly(this, '$_SESSION['c_email']')' class='text-seperate' 'display:inline;'>" .  $_SESSION['c_email'] . "</p>"  */
					getaccount($conn);

				 	echo "		<div class='upload-button'>
				 				<h2>Profile img currently broken do not attempt to use</h2>
			  					<form  method= 'POST' action= '".photoUpload($conn)."' enctype= 'multipart/form-data'>
	   		  						<input type='file' name= 'file'>
	   		    					<button type='submit' name='submitPhoto'>UPLOAD</button>
	   						</div>
	   						<div class='delete-button'>
	   							</form>
	   							<form action= 'delete.php' method= 'POST'>	   		  				
	   		    					<button type='submit' name='Delete'>Remove Photo</button>
	   							</form>
	   						</div>";

				 ?>
				 			




		</div>

	</div>

<?php
	include_once 'Footer.php';	
?>