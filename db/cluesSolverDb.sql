-- person
-- - id
-- - first_name
-- - last_name
--
-- person_role
-- - person_id
-- - role_id
--
-- roles
-- - role_id
-- - role_name
-- - notes
-- rows: (administrator, student, teacher, guest)
--
-- person
-- 1,elise,kloster
-- 2,michael,kloster
-- 3,glynis,kloster
-- List<Roles> getRoles()
--
-- person_role
-- 1,2
-- 1,3
-- 2,4
-- 3,1
-- 3,2
-- 3,3
--
-- roles:
-- 1,administartor,full access
-- 2,teacher, some access
-- 3,student, limited access
-- 4,guest,view only
-- List<Person> getAdminstrators()
--
-- select p.first_name, p.last_name, r.role_name from person p, person_role pr, role r
-- p.id = pr.person_id
-- and pr.role_id = r.id
-- where r.role_name = 'Administrator'
-- ;
--
--
-- One to One
-- One to Many
-- Many to Many
drop table if exists player;
create table player(
	id integer primary key,
	name text,
	game_id integer,
	is_computer boolean,
	turn_order integer
);
drop table if exists player_hand;
drop table if exists player_card;
create table player_card(
	game_id integer,
	player_id integer,
	card_name text
);


drop table if exists card;
create table card(
	name text primary key,
	type text
);

drop table if exists guess;
create table guess(
	id integer primary key,
	game_id integer,
	guess_number integer,
	suspect text,
	room text,
	weapon text,
	guesser_name text,
	is_disproved boolean,
	disprove_player text,
	disprove_card text
);
drop table if exists game;
create table game(
	id integer primary key,
	winner text,
	secret_suspect text,
	secret_weapon text,
	secret_room text
	
);



-- insert into game (id, secret_suspect, secret_weapon,secret_room, winner) values (1, 'miss scarlet', 'knife', 'conservatory');
--
-- insert into player (id, game_id, name, turn_order) values (1,1, 'anne', 1);
-- insert into player (id, game_id, name, turn_order) values (2,1, 'ben', 2);
-- insert into player (id, game_id, name, turn_order) values (3,1, 'claire', 3);
--
-- INSERT INTO card (card_name, card_type) VALUES ('suspect', 'professor plum');
-- INSERT INTO card (card_name, card_type) VALUES ('suspect', 'miss scarlet');
-- INSERT INTO card (card_name, card_type) VALUES ('suspect', 'mrs white');
-- INSERT INTO card (card_name, card_type) VALUES ('suspect', 'miss peacock');
-- INSERT INTO card (card_name, card_type) VALUES ('suspect', 'col mustard');
-- INSERT INTO card (card_name, card_type) VALUES ('suspect', 'mr green');
--
-- INSERT INTO card (card_name, card_type) VALUES ( 'room', 'ballroom');
-- INSERT INTO card (card_name, card_type) VALUES ( 'room', 'library');
-- INSERT INTO card (card_name, card_type) VALUES ( 'room', 'kitchen');
-- INSERT INTO card (card_name, card_type) VALUES ( 'room', 'conservatory');
-- INSERT INTO card (card_name, card_type) VALUES ( 'room', 'billiard room');
-- INSERT INTO card (card_name, card_type) VALUES ( 'room', 'study');
-- INSERT INTO card (card_name, card_type) VALUES ( 'room', 'hall');
-- INSERT INTO card (card_name, card_type) VALUES ( 'room', 'dining room');
--
-- INSERT INTO card (card_name, card_type) VALUES ( 'weapon', 'wrench');
-- INSERT INTO card (card_name, card_type) VALUES ( 'weapon', 'revolver');
-- INSERT INTO card (card_name, card_type) VALUES ( 'weapon', 'rope');
-- INSERT INTO card (card_name, card_type) VALUES ( 'weapon', 'candlestick');
-- INSERT INTO card (card_name, card_type) VALUES ( 'weapon', 'knife');
-- INSERT INTO card (card_name, card_type) VALUES ( 'weapon', 'lead pipe');

.quit