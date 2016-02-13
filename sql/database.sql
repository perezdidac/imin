-- DATABASE
-- version: 0.2
-- language: mysql

-- CREATION OF DATABASE
CREATE DATABASE imin;

-- CREATION OF TABLES

-- Table users
CREATE TABLE users (
	user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	device_id VARCHAR(15),
	private_user_id VARCHAR(16),
	public_user_id VARCHAR(16),
	user_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table events
CREATE TABLE events (
	event_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	public_event_id VARCHAR(8),
	name VARCHAR(256),
	description VARCHAR(256),
	closed BOOLEAN,
	event_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	final_datetime_proposal_id VARCHAR(8),
	final_location_proposal_id VARCHAR(8),
	ref_user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE
);

-- Table proposals
CREATE TABLE proposals (
	proposal_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	public_proposal_id VARCHAR(8),
	proposal_data VARCHAR(32),
	proposal_type INT,
	ref_event_id INT NOT NULL REFERENCES events(event_id) ON DELETE CASCADE
);

-- Table responses
CREATE TABLE responses (
	response_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_name VARCHAR(32),
	response INT,
	ref_user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
	ref_proposal_id INT NOT NULL REFERENCES proposals(proposal_id) ON DELETE CASCADE
);

-- Table comments
CREATE TABLE comments (
	comment_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(32),
	comments TEXT,
	ref_user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE
);

-- Table pictures
CREATE TABLE pictures (
	picture_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	picture TEXT,
	hash TEXT,
	ref_event_id INT NOT NULL REFERENCES events(event_id) ON DELETE CASCADE
);

-- Table user_data
CREATE TABLE user_data (
	picture_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	picture TEXT,
	hash TEXT,
	user_name VARCHAR(32),
	ref_user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE
);

-- Avoid duplicates
ALTER TABLE proposals ADD CONSTRAINT uq_col UNIQUE (proposal_data, ref_event_id);
ALTER TABLE responses ADD CONSTRAINT uq_col UNIQUE (ref_user_id, ref_proposal_id);
ALTER TABLE pictures ADD CONSTRAINT uq_col UNIQUE (ref_event_id);
ALTER TABLE user_data ADD CONSTRAINT uq_col UNIQUE (ref_user_id);
