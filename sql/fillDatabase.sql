-- DATABASE
-- version: 0.1
-- language: mysql

-- Create some users
INSERT INTO users (device_id, private_user_id, public_user_id) VALUES ("359901048048876", "lk0X3lOQl972U7pB", "04C9kleL6Od13N23"); -- user 1
INSERT INTO users (device_id, private_user_id, public_user_id) VALUES ("353723050222603", "thisIsAPrivate01", "thisIsAPublicId1"); -- user 2
INSERT INTO users (device_id, private_user_id, public_user_id) VALUES ("qwertasdfgzxvbb", "thisIsAPrivate02", "thisIsAPublicId2"); -- user 3

-- Create some events
INSERT INTO events (public_event_id, ref_user_id, name, description, closed) VALUES ("02345678", 1, "Evento 1", "Hecho por mí", false); -- event 1
INSERT INTO events (public_event_id, ref_user_id, name, description, closed, final_proposal_id) VALUES ("12345678", 1, "Evento 2", "Hecho por mí", true, "aaddaadd"); -- event 2
INSERT INTO events (public_event_id, ref_user_id, name, description, closed) VALUES ("42345678", 2, "Evento 3", "Hecho por David", false); -- event 3
INSERT INTO events (public_event_id, ref_user_id, name, description, closed) VALUES ("52345678", 2, "Evento 4", "Hecho por David", false); -- event 4
INSERT INTO events (public_event_id, ref_user_id, name, description, closed) VALUES ("52345678", 3, "Evento 5", "Hecho por Juan", false); -- event 5
INSERT INTO events (public_event_id, ref_user_id, name, description, closed) VALUES ("52345678", 1, "Evento 6", "Hecho por mí pero sin votos", false); -- event 6

-- Create proposals
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("abcdabcd", 1, "230120132200", "Marto", 0); -- proposal 1
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("aaaaaaaa", 1, "260120132200", "Marto", 0); -- proposal 2
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("bbbbbbbb", 1, "230120132200", "Barna", 0); -- proposal 3
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("cccccccc", 1, "260120132200", "Barna", 0); -- proposal 4
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("qrqrqrqr", 2, "230120132200", "Marto", 0); -- proposal 5
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("wtwtwtwt", 2, "260120132200", "Marto", 0); -- proposal 6
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("aaddaadd", 2, "230120132200", "Barna", 0); -- proposal 7
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("75757575", 2, "260120132200", "Barna", 0); -- proposal 8
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("qaswqasw", 3, "230120132200", "Marto", 0); -- proposal 9
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("rfgtrfgt", 3, "260120132200", "Marto", 0); -- proposal 10
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("mnbvmnvv", 3, "230120132200", "Barna", 0); -- proposal 11
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("mdhmhmgh", 3, "260120132200", "Barna", 0); -- proposal 12
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("proposal", 4, "230120132200", "Marto", 0); -- proposal 13
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("pripupol", 4, "260120132200", "Marto", 0); -- proposal 14
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("capuccho", 4, "230120132200", "Barna", 0); -- proposal 15
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("ameefbgn", 4, "260120132200", "Barna", 0); -- proposal 16
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("12331221", 5, "230120132200", "Marto", 0); -- proposal 17
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("11112312", 5, "260120132200", "Marto", 0); -- proposal 18
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("11111241", 5, "230120132200", "Barna", 0); -- proposal 19
INSERT INTO proposals (public_proposal_id, ref_event_id, proposal_date, proposal_location, proposal_type) VALUES ("b5gzzzzz", 5, "260120132200", "Barna", 0); -- proposal 20

-- Create responses
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (1, 1, "Didac", 0);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (1, 2, "Didac", 0);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (1, 3, "Didac", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (1, 4, "Didac", 0);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (1, 5, "Didac", 0);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (1, 6, "Didac", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (1, 7, "Didac", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (1, 8, "Didac", 2);

INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (1, 13, "Didac", 0);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (1, 14, "Didac", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (1, 15, "Didac", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (1, 16, "Didac", 2);

INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (2, 1, "Mundo", 0);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (2, 2, "Mundo", 0);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (2, 3, "Mundo", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (2, 4, "Mundo", 0);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (2, 5, "Mundo", 0);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (2, 6, "Mundo", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (2, 7, "Mundo", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (2, 8, "Mundo", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (2, 9, "Mundo", 0);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (2, 10, "Mundo", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (2, 11, "Mundo", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (2, 12, "Mundo", 2);

INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (3, 1, "Juan", 0);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (3, 2, "Juan", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (3, 3, "Juan", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (3, 4, "Juan", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (3, 9, "Juan", 0);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (3, 10, "Juan", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (3, 11, "Juan", 2);
INSERT INTO responses (ref_user_id, ref_proposal_id, user_name, response) VALUES (3, 12, "Juan", 2);



