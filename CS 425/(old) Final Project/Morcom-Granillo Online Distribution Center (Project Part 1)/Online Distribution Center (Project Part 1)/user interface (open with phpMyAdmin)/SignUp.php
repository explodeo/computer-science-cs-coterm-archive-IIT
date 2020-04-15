<?php
	include_once 'Header.php';
?>

	<div class="container">
		<h2> Sign Up Test Page</h2>
			<div class="text">

				<form class="signup-form" action="includes/signup-inc.php" method="POST">
					<input type="text" name="first" placeholder="Firstname">
					<input type="text" name="last" placeholder="Lastname">
					<input type="text" name="email" placeholder="email">
					<input type="text" name="uid" placeholder="Username">
					<input type="password" name="pwd" pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}" placeholder="Password" title="Must contain at least one number and one uppercase and lowercase letter, and at least 8 or more characters">
					<p>Select the region that best suits:</p>
					<select name="region">
						<option value="North America">North America</option>
						<option value="Central America">Central America</option>
					    <option value="South America">South America</option>
					    <option value="Europe">Asia</option>
					    <option value="Africa">Africa</option>
					</select>
					<input type="submit" name="submit">
				</form>	
				
				<h4>Foot note</h4>
				<P>This page will be to test out a login system to learn more about html and web development. It will also be to learn about database systems and how PHP works.</P>

<?php
	include_once 'Footer.php';	
?>