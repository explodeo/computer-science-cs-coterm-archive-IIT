<?php
	include_once 'Header.php';	
?>

	<div class="container">
		<div class="text">
			<?php
				include ( getcwd(). '/includes/comments-inc.php');
				include ( getcwd(). '/includes/dbh-inc.php');

				if (isset($_SESSION['c_id'])) {
					echo "<h2>Market 1.0!</h2>
							<p>Welcome to the Market! Here you can post things and see what others are selling. We are planning to implement more features, so just bare with us while we get everything up and running.<p>					

								<form class='market' method='POST' action='".setProducts($conn)."'>


								<form class='signup-form' action='includes/signup-inc.php' method='POST'>
								<input type='text' name='productName' placeholder='Product name'>
								<input type='text' name='description' placeholder='Description'>
								<input type='hidden' name='cid' value='".$_SESSION['c_id']."'>
								<input type='text' name='price' placeholder='Price'>
								<input type='text' name='quantity' placeholder='Quantity'>
								<input type='text' name='category' placeholder='Category'>							
								<button type='submit' name='commentSubmit'>Comment</button>
								</form>
								";




					getProducts($conn);

					}

				else{ echo "<h2>error trying to access unknown page.<h2>";}

			?>

		</div>

	</div>

<?php
	include_once 'Footer.php';	
?>


