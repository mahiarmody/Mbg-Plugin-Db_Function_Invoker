--    Copyright 2014 Mahiar Mody
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.


-- Created by Mahiar Mody for creating and filling the test tables with data that the Db Function Invoker plugin will use while testing.

drop table UserTutorial if exists;
drop table UserBlog if exists;
drop table UserPhotos if exists;
drop table UsersToSkills if exists;
drop table UserSkills if exists;
drop table Users if exists;


create table Users
(
	user_id INTEGER GENERATED ALWAYS AS IDENTITY(START WITH 1) not null,
	login varchar(50) not null,
	first_name varchar(100),
	last_name varchar(100) not null,
	last_name_sdx char(4) not null,
	ignoredCol varchar(5), --this column must be ignored
	password varchar(128) not null,
	is_active boolean default 'TRUE' not null,
	create_date timestamp default current_timestamp,
	constraint Users__user_id__PK PRIMARY KEY(user_id),
	constraint Users__login__UQ UNIQUE (login)
);

create table UserSkills
(
	skill_id smallInt GENERATED ALWAYS AS IDENTITY(START WITH 1) not null,
	skill varchar(20),
	is_active boolean default 'true' not null,
	constraint UserRole__skill_id__PK Primary Key (skill_id)
);

create table UsersToSkills
(
	user_id integer not null ,
	skill_id smallInt not null,
	level varchar(10) default 'Unknown',
	constraint UsersToSkills__user_id__skill_id__PK Primary Key(user_id, skill_id),
	constraint UsersToSkills__user_id__FK Foreign key (user_id) References Users(user_id),
	constraint UsersToSkills__skill_id__FK Foreign Key (skill_id) References UserSkills(skill_id)
);

create table UserPhotos
(
	user_id integer not null,
	photo_title varchar(50),
	photo BLOB,
	photo_type varchar(10),
	constraint UserPhotos__user_id__FK Foreign Key (user_id) References Users(user_id)
);

create table UserTutorial
(
	user_tutorial_id int not null,
	user_id integer not null,
	title varchar(100),
	summary CLOB,
	narrative CLOB,
	video BLOB,
	video_type varchar(25),
	constraint UserTutorial__user_tutorial_id__PK Primary Key(user_tutorial_id),
	constraint UserTutorial__user_id__FK Foreign Key(user_id) references Users(user_id)
);

create table UserBlog
(
	user_id integer not null,
	blog_text CLOB,
	title varchar(40),
	constraint UserBlog__user_id__FK Foreign Key(user_id) references Users(user_id)
);


drop function md5 if exists;

CREATE FUNCTION md5(VARCHAR(128), VARCHAR(10))
RETURNS VARCHAR(256)
LANGUAGE JAVA
DETERMINISTIC
NO SQL
EXTERNAL NAME 'CLASSPATH:org.hsqldb.lib.MD5.encode';


insert into Users(login, first_name, last_name, last_name_sdx, password) values ('mmody', 'Mahiar', 'Mody', soundex('Mody'), md5('!@#$', 'US-ASCII'));
insert into UserSkills(skill) values ('Java');
insert into UserSkills(skill) values ('PostgreSQL');
insert into UserSkills(skill) values ('MyBatis');
insert into UsersToSkills(user_id, skill_id, level) values (1,1, 'Excellent');
insert into UserPhotos(user_id, photo_title, photo, photo_type) values (1,
	concat(substr('ImgHead Shot Mahiar', 4, length('ImgHead Shot Mahiar')), '-PHOTO'), X'0x440x540x33', 'png');

insert into UserTutorial(user_tutorial_id, user_id, title, summary, narrative, video, video_type)
	values (1, 1, 'MyBatis Generator Plugins',
		concat(left(convert('This is a large summary', SQL_CLOB), 20), '...'),
		convert('The narrative goes here', SQL_CLOB), X'0x440x540x33', 'flac');

insert into UserBlog(user_id, blog_text, title) values (1, convert('bla bla bla', SQL_CLOB), 'Db function invoker Plugin Usage');

