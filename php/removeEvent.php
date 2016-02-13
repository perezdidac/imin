<?php

// THIS METHOD REMOVES AN EVENT FROM THE LIST IN THE CASE
// THE GIVEN USER CREATED THE EVENT. THE RESPONSE IS
// FORMATTED AS A JSON OBJECT.

// Include database parameters and configuration parameters
include("database.php");
include("config.php");

// Get the GET parameters
$private_user_id = $_GET["privateUserId"];
$public_event_id = $_GET["publicEventId"];

// Check user id and event id
if (isset($private_user_id) && isset($public_event_id))
{
	// Analyze the format of the private id
	if (strlen($private_user_id) == $private_user_len)
	{
		// Analyze the format of the public event id
		if (strlen($public_event_id) == $public_event_len)
		{
			// Connect to the database
			mysql_connect($mysqlserver, $username, $password);
			$database_select = mysql_select_db($database);
			
			if ($database_select == true)
			{
				// Real escape strings
				$private_user_id = mysql_real_escape_string($private_user_id);
				$public_event_id = mysql_real_escape_string($public_event_id);
				
				// Remove the event from the database, if exists
				$query = "DELETE events FROM events JOIN users WHERE events.public_event_id = '$public_event_id' AND events.ref_user_id = users.user_id AND users.private_user_id = '$private_user_id'";
				$query_result = mysql_query($query);
				
				if ($query_result)
				{
					// We don't mind if the event has been removed or not, so
					// notify a success error code and everything will go well
					
					// Build the JSON result
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
			// Invalid public event id
			$result = json_encode(array('error_code'=>$error_code_public_event_id, 'error_text'=>$error_text_public_event_id));
		}
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
