<?php

// THIS METHOD CREATES A NEW EVENT FROM ALL THE GIVEN
// DATA. THE RESPONSE IS FORMATTED AS A JSON OBJECT.

// Include database parameters and configuration parameters
include("database.php");
include("config.php");

// Get the POST parameters
$private_user_id = $_POST["privateUserId"];
$event_name = $_POST["eventName"];
$event_description = $_POST["eventDescription"];
$proposals = $_POST["proposals"];

// Check user id
if (isset($private_user_id) && isset($event_name) && isset($event_description) && isset($proposals))
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
			$event_name = mysql_real_escape_string($event_name);
			$event_description = mysql_real_escape_string($event_description);
			
			do
			{
				$generated = true;
				
				// Loop until there are no collisions
				$characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
				$public_event_id = '';
				for ($i = 0; $i < $public_event_len; $i++) {
					$public_event_id .= $characters[rand(0, strlen($characters) - 1)];
				}
				
				// Check if public event id already exists
				$query = "SELECT event_id FROM events WHERE public_event_id = '$public_event_id'";
				$query_result = mysql_query($query);
			
				if ($query_result)
				{
					// Analyze the result in order to find matches
					while ($row = mysql_fetch_array($query_result, MYSQL_ASSOC)) 
					{
						$generated = false;
					}
				}
				else
				{
					// Query error
					$result = json_encode(array('error_code'=>$error_code_query, 'error_text'=>$error_text_query));
				}
			} while ($generated == false);
			
			// Add the new event into the events table in the database
			$query = "INSERT INTO events (public_event_id, ref_user_id, name, description, closed) SELECT '$public_event_id', user_id, '$event_name', '$event_description', false FROM users WHERE private_user_id = '$private_user_id'";
			$query_result = mysql_query($query);
			
			if ($query_result)
			{
				// Now, let's insert the proposals for that event
				$json_proposals = (json_decode($proposals, true));
				
				// For each proposal
				foreach ($json_proposals as $json_proposal)
				{
					// Generate random public proposal id
					do
					{
						$generated = true;
						
						// Loop until there are no collisions
						$characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
						$public_proposal_id = '';
						for ($i = 0; $i < $public_proposal_len; $i++) {
							$public_proposal_id .= $characters[rand(0, strlen($characters) - 1)];
						}
						
						// Check if public event id already exists
						$query = "SELECT public_proposal_id FROM proposals WHERE public_proposal_id = '$public_proposal_id'";
						$query_result = mysql_query($query);
					
						if ($query_result)
						{
							// Analyze the result in order to find matches
							while ($row = mysql_fetch_array($query_result, MYSQL_ASSOC)) 
							{
								$generated = false;
							}
						}
						else
						{
							// Query error
							$result = json_encode(array('error_code'=>$error_code_query, 'error_text'=>$error_text_query));
						}
					} while ($generated == false);
					
					// Parse the data from the proposal
					$proposal_data = $json_proposal['data'];
					$proposal_type = intval($json_proposal['type']);
					
					// Real escape strings
					$proposal_data = mysql_real_escape_string($proposal_data);
					
					// Add the new proposal into the proposals table in the database
					$query = "INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_data, proposal_type) SELECT '$public_proposal_id', event_id, '$proposal_data', '$proposal_type' FROM events WHERE public_event_id = '$public_event_id'";
					$query_result = mysql_query($query);
					
					if ($query_result)
					{
						// Proposal added
					}
					else
					{
						// Proposal not added
					}
				}
				
				// Success, we have created the new event
				$result = json_encode(array('error_code'=>$error_code_success, 'public_event_id'=>$public_event_id));
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
