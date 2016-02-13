<?php

// THIS METHOD UPLOADS A PICTURE FOR A GIVEN EVENT.
// THE RESPONSE IS FORMATTED AS A JSON OBJECT.

// Include database parameters and configuration parameters
include("database.php");
include("config.php");

// Get the POST parameters
$private_user_id = $_POST["privateUserId"];
$public_event_id = $_POST["publicEventId"];
$picture = $_POST["picture"];
$hash = $_POST["hash"];

// Check user id
if (isset($private_user_id) && isset($public_event_id) && isset($picture))
{
	// Analyze the format of the private user id
	if (strlen($private_user_id) == $private_user_len)
	{
		// Connect to the database
		mysql_connect($mysqlserver, $username, $password);
		$database_select = mysql_select_db($database);
		
		if ($database_select == true)
		{
			// Real escape strings
			$private_user_id = mysql_real_escape_string($private_user_id);
			$public_event_id = mysql_real_escape_string($public_event_id);
			$picture = mysql_real_escape_string($picture);
			$hash = mysql_real_escape_string($hash);
			
			// Add the picture into the database
			$query = "INSERT INTO pictures (picture, hash, ref_event_id) SELECT '$picture', '$hash', event_id FROM events WHERE public_event_id = '$public_event_id'";
			$query_result = mysql_query($query);
			
			if ($query_result)
			{
				// Success, we have uploaded the picture
				$result = json_encode(array('error_code'=>$error_code_success));
			}
			else
			{
				// Query error
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
		// Invalid device id
		$result = json_encode(array('error_code'=>$error_code_device_id, 'error_text'=>$error_text_device_id));
	}
}
else 
{
	// Missing parameters
	$result = json_encode(array('error_code'=>$error_code_parameters, 'error_text'=>$error_text_parameters));
}

die($result);

?>
