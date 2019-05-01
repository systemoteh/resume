-- Create data base
CREATE SCHEMA `resume` DEFAULT CHARACTER SET utf8 ;


-- Create table profile (main table)
CREATE TABLE `resume`.`profile` (
  `id` BIGINT(15) NOT NULL AUTO_INCREMENT,
  `uid` VARCHAR(100) NOT NULL,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `birth_day` TIMESTAMP(0) NULL,
  `country` VARCHAR(60) NULL,
  `city` VARCHAR(60) NULL,
  `objective` MEDIUMTEXT NULL,
  `summary` MEDIUMTEXT NULL,
  `small_photo` VARCHAR(255) NULL,
  `large_photo` VARCHAR(255) NULL,
  `info` MEDIUMTEXT NULL,
  `created` TIMESTAMP(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `completed` TINYINT(1) NULL,
  `skype` VARCHAR(255) NULL,
  `vkontakte` VARCHAR(255) NULL,
  `facebook` VARCHAR(255) NULL,
  `linkedin` VARCHAR(255) NULL,
  `github` VARCHAR(255) NULL,
  `stackoverflow` VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  UNIQUE INDEX `uid_UNIQUE` (`uid` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC),
  UNIQUE INDEX `phone_UNIQUE` (`phone` ASC));


-- Create table language
CREATE TABLE `resume`.`language` (
  `id` BIGINT(15) NOT NULL AUTO_INCREMENT,
  `profile_id` BIGINT(15) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `level` VARCHAR(50) NOT NULL,
  `type` VARCHAR(50) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_language__profile_id_idx` (`profile_id` ASC),
  CONSTRAINT `fk_language__profile_id`
    FOREIGN KEY (`profile_id`)
    REFERENCES `resume`.`profile` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);


-- Create table hobby
CREATE TABLE `resume`.`hobby` (
  `id` BIGINT(15) NOT NULL AUTO_INCREMENT,
  `profile_id` BIGINT(15) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_hobby__profile_id_idx` (`profile_id` ASC),
  CONSTRAINT `fk_hobby__profile_id`
    FOREIGN KEY (`profile_id`)
    REFERENCES `resume`.`profile` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);


-- Create table skill_category
CREATE TABLE `resume`.`skill_category` (
  `id` BIGINT(15) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `skill_category_UNIQUE` (`id` ASC));
 

-- Create table skill
CREATE TABLE `resume`.`skill` (
  `id` BIGINT(15) NOT NULL AUTO_INCREMENT,
  `profile_id` BIGINT(15) NOT NULL,
  `category_id` BIGINT(15) NOT NULL,
  `name` MEDIUMTEXT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_skill__profile_idx` (`profile_id` ASC),
  INDEX `fk_skill__skill_category_idx` (`category_id` ASC),
  CONSTRAINT `fk_skill__profile`
    FOREIGN KEY (`profile_id`)
    REFERENCES `resume`.`profile` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_skill__skill_category`
    FOREIGN KEY (`category_id`)
    REFERENCES `resume`.`skill_category` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
    
-- Create table 'practice'
CREATE TABLE `resume`.`practice` (
  `id` BIGINT(15) NOT NULL AUTO_INCREMENT,
  `profile_id` BIGINT(15) NOT NULL,
  `position` VARCHAR(100) NOT NULL,
  `company` VARCHAR(100) NOT NULL,
  `begin_date` TIMESTAMP(0) NOT NULL,
  `finish_date` TIMESTAMP(0) NULL,
  `responsibilities` MEDIUMTEXT NOT NULL,
  `demo_url` VARCHAR(255) NULL,
  `src_url` VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_practice__profile_id_idx` (`profile_id` ASC),
  CONSTRAINT `fk_practice__profile_id`
    FOREIGN KEY (`profile_id`)
    REFERENCES `resume`.`profile` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
    
-- Create table 'certificate'
CREATE TABLE `resume`.`certificate` (
  `id` BIGINT(15) NOT NULL AUTO_INCREMENT,
  `profile_id` BIGINT(15) NOT NULL,
  `large_url` VARCHAR(255) NOT NULL,
  `small_url` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_certificate__profile_id_idx` (`profile_id` ASC),
  CONSTRAINT `fk_certificate__profile_id`
    FOREIGN KEY (`profile_id`)
    REFERENCES `resume`.`profile` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
    
-- Create table 'course'
CREATE TABLE `resume`.`course` (
  `id` BIGINT(15) NOT NULL AUTO_INCREMENT,
  `profile_id` BIGINT(15) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `school` VARCHAR(100) NOT NULL,
  `finish_date` TIMESTAMP(0) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_course__profile_id_idx` (`profile_id` ASC),
  CONSTRAINT `fk_course__profile_id`
    FOREIGN KEY (`profile_id`)
    REFERENCES `resume`.`profile` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
    
    
-- Create table 'education'
CREATE TABLE `resume`.`education` (
  `id` BIGINT(15) NOT NULL AUTO_INCREMENT,
  `profile_id` BIGINT(15) NOT NULL,
  `summary` VARCHAR(100) NOT NULL,
  `begin_year` INT NOT NULL,
  `finish_year` INT NULL,
  `university` MEDIUMTEXT NULL,
  `faculty` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_education__profile_id_idx` (`profile_id` ASC),
  CONSTRAINT `fk_education__profile_id`
    FOREIGN KEY (`profile_id`)
    REFERENCES `resume`.`profile` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);


-- Create table 'profile_restore'
CREATE TABLE `profile_restore` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT,
  `token` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `token_UNIQUE` (`token`),
  CONSTRAINT `profile_restore_fk_profile_id` 
	FOREIGN KEY (`id`) 
	REFERENCES `profile` (`id`) 
	ON DELETE CASCADE 
	ON UPDATE CASCADE);
    
    
-- Create table 'persistent_logins'
CREATE TABLE `resume`.`persistent_logins` (
  `username` VARCHAR(100) NOT NULL,
  `series` VARCHAR(100) NOT NULL,
  `token` VARCHAR(100) NOT NULL,
  `last_used` TIMESTAMP(0) NOT NULL) 