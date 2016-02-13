<?php
	$debug = true;
	
	// Values
	$device_id_len_min = 15;
	$device_id_len_max = 16;
	$private_user_len = 16;
	$public_user_len = 16;
	$public_event_len = 8;
	$public_proposal_len = 8;
	
	// Error codes
	$error_code_success = 0;
	$error_code_database = 1;
	$error_code_query = 2;
	$error_code_parameters = 3;
	$error_code_device_id = 4;
	$error_code_private_user_id = 5;
	$error_code_public_user_id = 6;
	$error_code_public_event_id = 7;
	$error_code_picture = 8;
	
	// Error texts
	$error_text_database = 'Unable to connect to the database';
	$error_text_query = 'Query error';
	$error_text_parameters = 'Missing parameters';
	$error_text_device_id = 'Invalid device id';
	$error_text_private_user_id = 'Invalid private user id';
	$error_text_public_user_id = 'Invalid public user id';
	$error_text_public_event_id = 'Invalid public event id';
	$error_text_picture = 'No event picture';
	
?> 
