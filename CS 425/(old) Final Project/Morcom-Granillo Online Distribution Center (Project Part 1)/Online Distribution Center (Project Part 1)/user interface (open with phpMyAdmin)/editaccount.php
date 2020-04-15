<?php
	include_once 'Header.php';	
?>

	<div class="container">
		<div class="text">
			<?php
				include ( getcwd(). '/includes/accountedit-inc.php');
				include ( getcwd(). '/includes/dbh-inc.php');
				$id = $_SESSION['c_id'];

				if (isset($_SESSION['c_id']) ) {
					$c_id = $_POST['c_id'];
					$c_first = $_POST['c_first'];
					$c_last = $_POST['c_last'];
					$c_email = $_POST['c_email'];
					$c_username = $_POST['c_username'];
					$c_region = $_POST['c_region'];
					
					echo "<h2>Account editor 1.0!</h2><br>


							<h3>Edit First Name</h3>
						  <form class='forums' method='POST' action='".editaccountFirst($conn)."'>
							<input type='hidden' name='c_id' value='".$id."'>
							<textarea name='update'>".$c_first."</textarea><br>
							<button type='submit' name='editFirst'>Edit</button>
					 	  </form>

					 	  	<h3>Edit Last Name</h3>
						  <form class='forums' method='POST' action='".editaccountLast($conn)."'>
							<input type='hidden' name='c_id' value='".$id."'>
							<textarea name='update'>".$c_last."</textarea><br>
							<button type='submit' name='editLast'>Edit</button>
					 	  </form>

					 	  	<h3>Edit Email Name</h3>
						  <form class='forums' method='POST' action='".editaccountEmail($conn)."'>
							<input type='hidden' name='c_id' value='".$id."'>
							<textarea name='update'>".$c_email."</textarea><br>
							<button type='submit' name='editEmail'>Edit</button>
					 	  </form>

					 	  <h3>Edit Username Name</h3>
						  <form class='forums' method='POST' action='".editaccountUsername($conn)."'>
							<input type='hidden' name='c_id' value='".$id."'>
							<textarea name='update'>".$c_username."</textarea><br>
							<button type='submit' name='editUsername'>Edit</button>
					 	  </form>



							<h3>Edit Region</h3>
						  <form class='forums' method='POST' action='".editaccountRegion($conn)."'>
							<input type='hidden' name='c_id' value='".$c_id."'>
							<textarea name='update'>".$c_region."</textarea><br>
							<button type='submit' name='editRegion'>Edit</button>
					 	  </form>
				

					 	   <div class='link'><button > <a  href='Account.php'>Go Back</a></button> </div>";

					}
				else{ echo "<h2>error trying to access unknown page.<h2>";}

			?>

		</div>

	</div>

<?php
	include_once 'Footer.php';	
?>


