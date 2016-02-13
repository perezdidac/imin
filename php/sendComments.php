<?php

// THIS METHOD CLOSES OR REOPENS AN EVENT.

// Include database parameters and configuration parameters
include("database.php");
include("config.php");

// Get the POST parameters
$private_user_id = $_POST["privateUserId"];
$name = $_POST["name"];
$comments = $_POST["comments"];

// Check fields
if (isset($private_user_id) && isset($name) && isset($comments))
{
	// Analyze the format of the private id
	if (strlen($private_user_id) == $private_user_len)
	{
		// Connect to the database
		mysql_connect($mysqlserver, $username, $password);
		$database_select = mysql_select_db($database);
		
		if ($database_select == true)
		{
			// Real escape strings
			$private_user_id = mysql_real_escape_string($private_user_id);
			$name = mysql_real_escape_string($name);
			$comments = mysql_real_escape_string($comments);
			
			// Save the comments
			$query = "INSERT INTO comments (name, comments, ref_user_id) SELECT '$name', '$comments', user_id FROM users WHERE private_user_id = '$private_user_id'";
			$query_result = mysql_query($query);
			
			if ($query_result)
			{
				// Success, comments inserted
				$result = json_encode(array('error_code'=>$error_code_success));
			}
			else
			{
				// Error while executing the query
				$result = json_encode(array('error_code'=>$error_code_query, 'error_text'=>$error_text_query));
			}
		}
		else
		{
			$result = json_encode(array('error_code'=>$error_code_database, 'error_text'=>$error_text_database));
		}
			
		// Close database connection
		mysql_close();
	}
	else
	{
		// Invalid private user id
		$result = json_encode(array('error_code'=>$error_code_private_user_id, 'error_text'=>$error_text_private_user_id));
	}
}
else 
{
	// Missing parameters
	$result = json_encode(array('error_code'=>$error_code_parameters, 'error_text'=>$error_text_parameters));
}

die($result);

?>
