<?php
	include_once 'Header.php';	
?>

	<div class="container">
		<div class="text">
			<?php
				date_default_timezone_set('America/Chicago');				
				include ( getcwd(). '/includes/comments-inc.php');
				include ( getcwd(). '/includes/dbh-inc.php');
				$id = $_SESSION['u_id'];

				if (isset($_SESSION['u_id'])) {
					$cid = $_POST['cid'];
					$uid = $_POST['uid'];
					$date = $_POST['date'];
					$message = $_POST['message'];
					
					echo "<h2>Comment editor 1.0!</h2>

						  <form class='forums' method='POST' action='".editComments($conn)."'>
							<input type='hidden' name='cid' value='".$cid."'>
							<input type='hidden' name='uid' value='".$uid."'>
							<input type='hidden' name='date' value='". date('Y/m/d H:i:s') ."'>
							<textarea name='message'>".$message."</textarea><br>
							<button type='submit' name='editSubmit'>Edit</button>
					 	  </form>";

					}
				else{ echo "<h2>error trying to access unknown page.<h2>";}

			?>

		</div>

	</div>

<?php
	include_once 'Footer.php';	
?>


