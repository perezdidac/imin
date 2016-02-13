<?php

// THIS METHOD FILLS THE TABLE responses FOR THE GIVEN
// RESPONSES TO THE PROPOSALS OF A GIVEN EVENT FOR A
// GIVEN USER.

// Include database parameters and configuration parameters
include("database.php");
include("config.php");

// Get the POST parameters
$private_user_id = $_POST["privateUserId"];
$public_event_id = $_POST["publicEventId"];
$responses = $_POST["responses"];

// Check user id and event id
if (isset($private_user_id) && isset($public_event_id) && isset($responses))
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
				
				// Now, let's insert the responses for the given purpose
				$json_responses = (json_decode($responses, true));
				
				// For each response
				foreach ($json_responses as $json_response)
				{
					// Parse the data from the response
					$response_public_proposal_id = $json_response['publicProposalId'];
					$response_response_type = intval($json_response['responseType']);
					$response_name = $json_response['name'];
					
					// Real escape strings
					$response_public_proposal_id = mysql_real_escape_string($response_public_proposal_id);
					$response_name = mysql_real_escape_string($response_name);
					
					// Add the new response into the responses table in the database
					$query = "INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) SELECT user_id, proposal_id, '$response_name', '$response_response_type' FROM users, proposals WHERE private_user_id = '$private_user_id' AND public_proposal_id = '$response_public_proposal_id' ON DUPLICATE KEY UPDATE user_name = '$response_name', response = '$response_response_type'";
					$query_result = mysql_query($query);
					
					if ($query_result)
					{
						// Response added
					}
					else
					{
						// Response not added
					}
				}
				
				// Success, we have inserted all the responses to the event
				$result = json_encode(array('error_code'=>$error_code_success));
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
