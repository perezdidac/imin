<?php

// THIS METHOD RETURNS THE LIST OF EVENTS CREATED BY THE USER
// GIVEN ITS PRIVATE USER ID, AND ALSO RETURNS THE EVENTS IN
// WHICH THE USER PARTICIPATES. THE RESPONSE IS FORMATTED AS
// A JSON ARRAY OF EVENTS.

// Include database parameters and configuration parameters
include("database.php");
include("config.php");

// Get the GET parameters
$private_user_id = $_GET["privateUserId"];

// Check user id
if (isset($private_user_id))
{
	// Analyze the format of the private user id id
	if (strlen($private_user_id) == $private_user_len)
	{
		// Connect to the database
		mysql_connect($mysqlserver, $username, $password);
		$database_select = mysql_select_db($database);
		
		if ($database_select == true)
		{
			// Real escape strings
			$private_user_id = mysql_real_escape_string($private_user_id);
			
			// Find all the events created by the user
			//$query = "SELECT public_event_id, name, description, public_user_id, closed, final_datetime_proposal_id, final_location_proposal_id FROM events JOIN users AS u ON events.ref_user_id = u.user_id WHERE EXISTS ( SELECT 1 FROM responses AS r JOIN users AS ur ON ur.user_id = r.ref_user_id JOIN proposals ON proposals.proposal_id = r.ref_proposal_id WHERE proposals.ref_event_id = events.event_id AND ur.private_user_id = '$private_user_id' ) OR (private_user_id = '$private_user_id' AND events.ref_user_id = user_id)";
			$query = "SELECT public_event_id, name, description, public_user_id, closed, final_datetime_proposal_id, final_location_proposal_id, hash FROM events LEFT JOIN pictures AS pics ON pics.ref_event_id = events.event_id JOIN users AS u ON events.ref_user_id = u.user_id WHERE EXISTS ( SELECT 1 FROM responses AS r JOIN users AS ur ON ur.user_id = r.ref_user_id JOIN proposals ON proposals.proposal_id = r.ref_proposal_id WHERE proposals.ref_event_id = events.event_id AND ur.private_user_id = 'eWcL9kuLh0mRn5Vz' ) OR (private_user_id = 'eWcL9kuLh0mRn5Vz' AND events.ref_user_id = user_id)";
			$query_result = mysql_query($query);
			
			if ($query_result)
			{
				// Analyze the result and build the array
				$events = array();
				while ($row = mysql_fetch_array($query_result, MYSQL_ASSOC)) 
				{
					$events[] = $row;
				}
				
				// Once the events have been retrieved, get the proposals for that event
				$query = "SELECT public_event_id, public_proposal_id, proposal_data, proposal_type FROM events JOIN users AS u ON events.ref_user_id = u.user_id JOIN proposals AS p ON p.ref_event_id = event_id WHERE EXISTS ( SELECT 1 FROM responses AS r JOIN users AS ur ON ur.user_id = r.ref_user_id JOIN proposals ON proposals.proposal_id = r.ref_proposal_id WHERE proposals.ref_event_id = events.event_id AND ur.private_user_id = '$private_user_id' ) OR (private_user_id = '$private_user_id' AND events.ref_user_id = user_id)";
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
					$query = "SELECT public_proposal_id, public_user_id, user_name, response
					FROM responses
					  JOIN proposals AS pro ON ref_proposal_id = pro.proposal_id
					  JOIN ((SELECT DISTINCT event_id
									   FROM responses AS r
										 JOIN users AS ur ON ur.user_id = r.ref_user_id
										 JOIN proposals ON proposals.proposal_id = r.ref_proposal_id
										 JOIN events AS eve ON proposals.ref_event_id = eve.event_id
										 OR ur.user_id = eve.ref_user_id
										 WHERE ur.private_user_id = '$private_user_id') AS result_events) ON pro.ref_event_id = result_events.event_id
					  JOIN users AS usr ON ref_user_id = usr.user_id";
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
						$result = json_encode(array('error_code'=>$error_code_success, 'events'=>$events, 'proposals'=>$proposals, 'responses'=>$responses));
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
