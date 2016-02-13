<?php

// THIS METHOD RETURNS A SINGLE EVENT GIVEN A PUBLIC
// EVENT ID. THE RESPONSE IS FORMATTED AS A JSON OBJECT.

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
			//$query = "SELECT public_event_id, name, description, public_user_id, closed, final_datetime_proposal_id, final_location_proposal_id FROM events, users WHERE events.public_event_id = '$public_event_id' AND users.user_id = events.ref_user_id";
			$query = "SELECT public_event_id, name, description, public_user_id, closed, final_datetime_proposal_id, final_location_proposal_id, hash FROM (events, users) LEFT JOIN pictures AS pics ON pics.ref_event_id = events.event_id WHERE events.public_event_id = '$public_event_id' AND users.user_id = events.ref_user_id";
			$query_result = mysql_query($query);
			
			if ($query_result)
			{
				// Analyze the result and build the object
				while ($row = mysql_fetch_array($query_result, MYSQL_ASSOC)) 
				{
					$event = $row;
				}
				
				// Once the events have been retrieved, get the proposals for that event
				$query = "SELECT public_event_id, public_proposal_id, proposal_data, proposal_type FROM events, proposals, users WHERE ref_event_id = event_id AND ref_user_id = user_id AND public_event_id = '$public_event_id'";
				$query_result = mysql_query($query);
				
				if ($query_result)
				{
					// Analyze the result and build the array
					$proposals = array();
					while ($row = mysql_fetch_array($query_result, MYSQL_ASSOC)) 
					{
						$proposals[] = $row;
					}
					
					// Once the proposals have been retrieved, get the responses for that event
					$query = "SELECT public_proposal_id, public_user_id, user_name, response FROM events, proposals, users, responses WHERE ref_event_id = event_id AND responses.ref_user_id = user_id AND ref_proposal_id = proposal_id AND public_event_id = '$public_event_id'";
					$query_result = mysql_query($query);
					
					if ($query_result)
					{
						// Analyze the result and build the array
						$responses = array();
						while ($row = mysql_fetch_array($query_result, MYSQL_ASSOC)) 
						{
							$responses[] = $row;
						}
						
						// Once the responses have been retrieved, we can build the final JSON structure and return it
						
						// Build the JSON result
						$result = json_encode(array('error_code'=>$error_code_success, 'event'=>$event, 'proposals'=>$proposals, 'responses'=>$responses));
					}
					else
					{
						// Query error
						$result = json_encode(array('error_code'=>$error_code_query, 'error_text'=>$error_text_query));
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
