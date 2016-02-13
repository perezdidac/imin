<?php

// THIS METHOD CLOSES OR REOPENS AN EVENT.

// Include database parameters and configuration parameters
include("database.php");
include("config.php");

// Get the POST parameters
$private_user_id = $_POST["privateUserId"];
$public_event_id = $_POST["publicEventId"];
$closed = $_POST["closed"];
$final_datetime_proposal_id = $_POST["finalDateTimeProposalId"];
$final_location_proposal_id = $_POST["finalLocationProposalId"];

// Check user id and event id
if (isset($private_user_id) && isset($public_event_id) && isset($closed))
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
				$closed = mysql_real_escape_string($closed);
				$final_datetime_proposal_id = mysql_real_escape_string($final_datetime_proposal_id);
				$final_location_proposal_id = mysql_real_escape_string($final_location_proposal_id);
				
				// Set the closed flag to the event
				$query = "UPDATE events, users SET closed = $closed, final_datetime_proposal_id = '$final_datetime_proposal_id', final_location_proposal_id = '$final_location_proposal_id' WHERE public_event_id = '$public_event_id' AND private_user_id = '$private_user_id' AND ref_user_id = user_id";
				$query_result = mysql_query($query);
				
				if ($query_result)
				{
					// Success, we have updated the closed flag of the event
					$result = json_encode(array('error_code'=>$error_code_success));
				}
				else
				{
					// Flag not modified
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
