<?php

// THIS METHOD RETURNS THE PICTURE OF A GIVEN EVENT.
// THE RESPONSE IS FORMATTED AS A JSON OBJECT.

// Include database parameters and configuration parameters
include("database.php");
include("config.php");

// Get the GET parameters
$public_event_id = $_GET["publicEventId"];

// Check event id
if (isset($public_event_id))
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
			$public_event_id = mysql_real_escape_string($public_event_id);
			
			// Find the corresponding event
			$query = "SELECT picture FROM pictures, events WHERE events.public_event_id = '$public_event_id' AND ref_event_id = event_id";
			$query_result = mysql_query($query);
			
			if ($query_result)
			{
				while ($row = mysql_fetch_array($query_result, MYSQL_ASSOC)) 
				{
					$picture = $row;
				}
				
				if ($picture)
				{
					// Build the JSON result
					$result = json_encode(array('error_code'=>$error_code_success, 'picture'=>$picture));
				}
				else
				{
					// Query error
					$result = json_encode(array('error_code'=>$error_code_picture, 'error_text'=>$error_text_picture));
				}
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
