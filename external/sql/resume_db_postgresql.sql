-- Create database 'resume'
CREATE DATABASE resume WITH OWNER = resume ENCODING = 'UTF8';
	
-- Create tables: profile, language, hobby, skill_category, skill, practice, certificate, course, education, profile_restore, persistent_logins. 
  	
-- Create table 'profile' (main table)
CREATE TABLE public.profile (
  id BIGINT NOT NULL,
  uid VARCHAR(100) NOT NULL UNIQUE,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100) UNIQUE,
  phone VARCHAR(20) UNIQUE,
  birth_day TIMESTAMP(0) WITHOUT TIME ZONE,
  country VARCHAR(100),
  city VARCHAR(100),
  objective TEXT,
  summary TEXT,
  large_photo VARCHAR(255),
  small_photo VARCHAR(255),
  info TEXT,
  created TIMESTAMP(0) WITHOUT TIME ZONE DEFAULT now() NOT NULL,
  completed BOOLEAN NOT NULL,
  skype VARCHAR(255),
  vkontakte VARCHAR(255),
  facebook VARCHAR(255),
  linkedin VARCHAR(255),
  github VARCHAR(255),
  stackoverflow VARCHAR(255),
  PRIMARY KEY(id)
) 
WITH (oids = false);

-- Create table 'language'
CREATE TABLE public.language (
  id BIGINT NOT NULL,
  profile_id BIGINT NOT NULL,
  name VARCHAR(50) NOT NULL,
  level VARCHAR(50) NOT NULL,
  type VARCHAR(50) NOT NULL,
  CONSTRAINT language_pkey PRIMARY KEY(id),
  CONSTRAINT language_fk_profile_id FOREIGN KEY (profile_id)
    REFERENCES public.profile(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    NOT DEFERRABLE
) 
WITH (oids = false);


-- Create table 'hobby'
CREATE TABLE public.hobby (
  id BIGINT NOT NULL,
  profile_id BIGINT NOT NULL,
  name VARCHAR(50) NOT NULL,
  CONSTRAINT hobby_pkey PRIMARY KEY(id),
  CONSTRAINT hobby_fk_profile_id FOREIGN KEY (profile_id)
    REFERENCES public.profile(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    NOT DEFERRABLE
) 
WITH (oids = false);


-- Create table 'skill_category'
CREATE TABLE public.skill_category (
  id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  PRIMARY KEY(id)
) 
WITH (oids = false);


-- Create table 'skill'
CREATE TABLE public.skill (
  id BIGINT NOT NULL,
  profile_id BIGINT NOT NULL,
  skill_category_id BIGINT NOT NULL,
  name TEXT NOT NULL,
  CONSTRAINT skill_pkey PRIMARY KEY(id),
  CONSTRAINT skill_fk_profile_id FOREIGN KEY (profile_id)
    REFERENCES public.profile(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    NOT DEFERRABLE,
  CONSTRAINT skill_fk_skill_category_id FOREIGN KEY (skill_category_id)
    REFERENCES public.skill_category(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    NOT DEFERRABLE
) 
WITH (oids = false);


-- Create table 'practice'
CREATE TABLE public.practice (
  id BIGINT NOT NULL,
  profile_id BIGINT NOT NULL,
  "position" VARCHAR(100) NOT NULL,
  company VARCHAR(100) NOT NULL,
  begin_date TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
  finish_date TIMESTAMP(0) WITHOUT TIME ZONE,
  responsibilities TEXT NOT NULL,
  demo_url VARCHAR(255),
  src_url VARCHAR(255),
  CONSTRAINT practice_pkey PRIMARY KEY(id),
  CONSTRAINT practice_fk_profile_id FOREIGN KEY (profile_id)
    REFERENCES public.profile(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    NOT DEFERRABLE
) 
WITH (oids = false);


-- Create table 'certificate'
CREATE TABLE public.certificate (
  id BIGINT NOT NULL,
  profile_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  large_url VARCHAR(255) NOT NULL,
  small_url VARCHAR(255) NOT NULL,
  CONSTRAINT certificate_pkey PRIMARY KEY(id),
  CONSTRAINT certificate_fk_profile_id FOREIGN KEY (profile_id)
    REFERENCES public.profile(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    NOT DEFERRABLE
) 
WITH (oids = false);


-- Create table 'course'
CREATE TABLE public.course (
  id BIGINT NOT NULL,
  profile_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  school VARCHAR(100) NOT NULL,
  finish_date TIMESTAMP(0) WITHOUT TIME ZONE,
  CONSTRAINT course_pkey PRIMARY KEY(id),
  CONSTRAINT course_fk_profile_id FOREIGN KEY (profile_id)
    REFERENCES public.profile(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    NOT DEFERRABLE
) 
WITH (oids = false);


-- Create table 'education'
CREATE TABLE public.education (
  id BIGINT NOT NULL,
  profile_id BIGINT NOT NULL,
  summary VARCHAR(100) NOT NULL,
  begin_year INTEGER NOT NULL,
  finish_year INTEGER,
  university TEXT NOT NULL,
  faculty VARCHAR(255) NOT NULL,
  CONSTRAINT education_pkey PRIMARY KEY(id),
  CONSTRAINT education_fk_profile_id FOREIGN KEY (profile_id)
    REFERENCES public.profile(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    NOT DEFERRABLE
) 
WITH (oids = false);

  
-- Create table 'profile_restore'  
CREATE TABLE public.profile_restore (
  id BIGINT NOT NULL,
  token VARCHAR(255) NOT NULL,
  CONSTRAINT profile_restore_pkey PRIMARY KEY(id),
  CONSTRAINT profile_restore_token_key UNIQUE(token),
  CONSTRAINT profile_restore_fk_profile_id FOREIGN KEY (id)
    REFERENCES public.profile(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    NOT DEFERRABLE
) 
WITH (oids = false);


-- Create table 'persistent_logins'
CREATE TABLE public.persistent_logins (
  username VARCHAR(100) NOT NULL,
  series VARCHAR(100) NOT NULL,
  token VARCHAR(100) NOT NULL,
  last_used TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL
) 
WITH (oids = false);


-- Create sequences for column 'id' each table 

CREATE SEQUENCE public.profile_seq
  INCREMENT 1 MINVALUE 1
  MAXVALUE 9223372036854775807 START 1
  CACHE 1;
  
CREATE SEQUENCE public.language_seq
  INCREMENT 1 MINVALUE 1
  MAXVALUE 9223372036854775807 START 1
  CACHE 1;

CREATE SEQUENCE public.hobby_seq
  INCREMENT 1 MINVALUE 1
  MAXVALUE 9223372036854775807 START 1
  CACHE 1;
 
CREATE SEQUENCE public.skill_category_seq
  INCREMENT 1 MINVALUE 1
  MAXVALUE 9223372036854775807 START 1
  CACHE 1;
  
CREATE SEQUENCE public.skill_seq
  INCREMENT 1 MINVALUE 1
  MAXVALUE 9223372036854775807 START 1
  CACHE 1;
  
CREATE SEQUENCE public.practice_seq
  INCREMENT 1 MINVALUE 1
  MAXVALUE 9223372036854775807 START 1
  CACHE 1;

  
CREATE SEQUENCE public.certificate_seq
  INCREMENT 1 MINVALUE 1
  MAXVALUE 9223372036854775807 START 1
  CACHE 1;
  
CREATE SEQUENCE public.course_seq
  INCREMENT 1 MINVALUE 1
  MAXVALUE 9223372036854775807 START 1
  CACHE 1;

CREATE SEQUENCE public.education_seq
  INCREMENT 1 MINVALUE 1
  MAXVALUE 9223372036854775807 START 1
  CACHE 1;

CREATE SEQUENCE public.profile_restore_seq
  INCREMENT 1 MINVALUE 1
  MAXVALUE 9223372036854775807 START 1
  CACHE 1;
 
 -- Indices will be created automatically
 
 -- End script 
