<?php
	include_once 'Header.php';	
?>

	<div class="container">
		<div class="text">
			<?php
				if (!isset($_SESSION['c_id'])) {
					echo '<h2>Welcome to our online market!</h2>
							<p>The site is still under construction so feel free to take a look around and provide some feedback if possible! The core components have yet to be implemented, rest assured we will get them up and running as soon as possible.</p>';
			}else{
					echo '<h2>Congrats!</h2>
							<p>You managed to create an account and you are logged in, yay! Feel free to look around the site.</p>
							<h2>Future!</h2>
									<p> The idea behind this website is to incorporate a market system and adjustable content to display on your account page. If you have ever used a website like craigslist or facebook market then you are familiar with what I am planning on creating in terms of the market. Eventually I will implement the system with a content modifier.</p>

							<h3>Website Checklist!</h3>
							<ul class="checkmark"> 
								<li class="tick"> Create User account system with database</li>
								<li class="tick"> Create a login/logout system on header</li>
								<li class="tick"> Create User profile picture system</li>								
								<li class="tick"> Allow user to upload/delete picture</li>
								<li class="tick"> Fix styling bugs when logged in</li>
								<li class="tick"> Fix the title link bug in the system </li>														
								<li class="tick"> Forums that work between accounts</li>
								<li class="tick"> Forums that allow posting in the page</li>
								<li class="cross"> Fix Bugs: comments overlaying with header</li>
								<li class="cross"> User can update account and database</li>
								<li class="cross"> Forums that allow editing the post added</li>
								<li class="cross"> Forums that allow editing the post added</li>
								<li class="cross"> Forums with discussions and user posts</li>
								<li class="cross"> User can post stuff on their home page</li>
								<li class="cross"> User uploads market items for others to see</li>
								<li class="cross"> Users can order market items through categories</li>
								<li class="cross"> Create a pm system where users can communicate</li>
								<li class="cross"> Allow user to eventually edit what they can see</li>
								<li class="cross"> Fix styling for mobile site usage functionality</li>		
							</ul>';

					}

			?>

		</div>

	</div>

<?php
	include_once 'Footer.php';	
?>


