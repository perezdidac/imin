<?php

// THIS METHOD RETURNS BOTH PRIVATE AND PUBLIC USER ID STRINGS
// GIVEN A DEVICE ID (IMEI). IF DEVICE ID IS NOT REGISTERED,
// A RANDOM PUBLIC AND PRIVATE ID STRINGS ARE GENERATED AND
// RETURNED IN JSON FORMAT.

// Include database parameters and configuration parameters
include("database.php");
include("config.php");

// Get the GET parameters
$device_id = $_GET["deviceId"];

// Check device id
if (isset($device_id))
{
	// Analyze the format of the device id
	if (strlen($device_id) >= $device_id_len_min && strlen($device_id) <= $device_id_len_max)
	{
		// Connect to the database
		mysql_connect($mysqlserver, $username, $password);
		$database_select = mysql_select_db($database);
		
		if ($database_select == true)
		{
			// Real escape strings
			$device_id = mysql_real_escape_string($device_id);
			
			// Look for the device id in the database
			$query = "SELECT private_user_id, public_user_id FROM users WHERE device_id = '$device_id'";
			$query_result = mysql_query($query);
			
			if ($query_result)
			{
				// Analyze the result in order to know if there is an associated
				// user id for the given device id
				$array = array();
				$found = false;
				while ($row = mysql_fetch_array($query_result, MYSQL_ASSOC)) 
				{
					$array[] = $row;
					$found = true;
				}
				
				if ($found)
				{
					// Load the data
					$private_user_id = $array[0]['private_user_id'];
					$public_user_id = $array[0]['public_user_id'];
					
					// User already exists, return the user ids
					$result = json_encode(array('error_code'=>$error_code_success, 'private_user_id'=>$private_user_id, 'public_user_id'=>$public_user_id));
				}
				else
				{
					// First time the device uses the system, so we must generate
					// both private and public user id numbers avoiding collisions
					do
					{
						$generated = true;
						
						// Loop until there are no collisions
						$characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
						$private_user_id = '';
						for ($i = 0; $i < $private_user_len; $i++) {
							$private_user_id .= $characters[rand(0, strlen($characters) - 1)];
						}
						
						$public_user_id = '';
						for ($i = 0; $i < $public_user_len; $i++) {
							$public_user_id .= $characters[rand(0, strlen($characters) - 1)];
						}
						
						// Check if both private and public user ids already exist
						$query = "SELECT user_id FROM users WHERE private_user_id = '$private_user_id' OR public_user_id = '$public_user_id'";
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
					
					// Add the new user into the users table in the database
					$query = "INSERT INTO users (device_id, private_user_id, public_user_id) VALUES ('$device_id', '$private_user_id', '$public_user_id')";
					$query_result = mysql_query($query);
					
					if ($query_result)
					{
						// Success, we have created the new user
						$result = json_encode(array('error_code'=>$error_code_success, 'private_user_id'=>$private_user_id, 'public_user_id'=>$public_user_id));
					}
					else
					{
						// Query error
						$result = json_encode(array('error_code'=>$error_code_query, 'error_text'=>$error_text_query));
					}
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
