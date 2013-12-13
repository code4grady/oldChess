use chess;

drop table MOVE_SUMMARY;
drop table GAME_Move;
drop table GAME;
drop table TEST_ME;


create table MOVE_SUMMARY
(
	move			char(255) 	NOT NULL 	PRIMARY KEY,
	wins 			INT,
	losses 			INT,
	stalemates		INT,
	sum_move_number 	INT,
	sum_move_total 		INT
);


create table GAME
(
	game_id	 		VARCHAR(20) 	NOT NULL 	PRIMARY KEY,
	color 			VARCHAR(5),
	direction		VARCHAR(5),
	time 			TIMESTAMP,
	result 			VARCHAR(10)
);


create table GAME_Move
(
	move_desc		char(255) 	NOT NULL,
	game_id	 		VARCHAR(20) 	NOT NULL,
	piece_name 		VARCHAR(15),
	piece_direction 	VARCHAR(15),
	opponent_piece		VARCHAR(15),
	move_number 		INT,
	move_point		VARCHAR(5),

    	FOREIGN KEY (game_id) REFERENCES game(game_id) 
);


create table TEST_ME
(
	name	char(1) not null PRIMARY KEY
);
insert into TEST_ME values (1);




