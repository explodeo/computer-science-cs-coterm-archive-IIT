<?php
	
$dbServername = "localhost";
$dbUsername = "root";
$dbPassword = "";
$dbName = "cs425";

$conn = mysqli_connect($dbServername, $dbUsername, $dbPassword, $dbName); 

if (!$conn) {
	die("Connection Failed: ".mysqli_connect_error());
		}			