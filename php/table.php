<?php

// Include database parameters and configuration parameters
include("database.php");
include("config.php");

// Get the GET parameters
$password_tables = $_GET["password"];
$table = $_GET["table"];

// Check event id
if (isset($password_tables) && isset($table))
{
	if ($password_tables == "albertalias")
	{
		// Connect to the database
		mysql_connect($mysqlserver, $username, $password);
		$database_select = mysql_select_db($database);

		// Real escape strings
		$table = mysql_real_escape_string($table);
		
		$query = "SELECT * FROM $table";
		$query_result = mysql_query($query);

		$fields_num = MYSQL_NUM_FIELDS($query_result);
		
		ECHO "<table border='1'><tr>";
		// printing table headers
		FOR($i=0; $i<$fields_num; $i++)
		{
			$field = MYSQL_FETCH_FIELD($query_result);
			ECHO "<td>{$field->name}</td>";
		}
		ECHO "</tr>\n";
		// printing table rows
		WHILE($row = MYSQL_FETCH_ROW($query_result))
		{
			ECHO "<tr>";
		 
			// $row is array... foreach( .. ) puts every element
			// of $row to $cell variable
			FOREACH($row AS $cell)
				ECHO "<td>$cell</td>";
		 
			ECHO "</tr>\n";
		}
		MYSQL_FREE_RESULT($query_result);

		// Close database connection
		mysql_close();
	}
	else
	{
		// Incorrect password
		$result = "Incorrect password";
	}
}
else
{
	// Missing parameters
	$result = "Missing parameters";
}

die($result);

?>
