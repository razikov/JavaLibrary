--
-- Скрипт сгенерирован Devart dbForge Studio for MySQL, Версия 6.3.358.0
-- Домашняя страница продукта: http://www.devart.com/ru/dbforge/mysql/studio
-- Дата скрипта: 30.12.2015 9:51:49
-- Версия сервера: 5.6.20
-- Версия клиента: 4.1
--


-- 
-- Отключение внешних ключей
-- 
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- 
-- Установить режим SQL (SQL mode)
-- 
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 
-- Установка кодировки, с использованием которой клиент будет посылать запросы на сервер
--
SET NAMES 'utf8';

-- 
-- Установка базы данных по умолчанию
--
USE library;

--
-- Описание для таблицы book
--
DROP TABLE IF EXISTS book;
CREATE TABLE book (
  id INT(11) NOT NULL,
  author VARCHAR(255) DEFAULT NULL,
  title VARCHAR(255) DEFAULT NULL,
  isbn VARCHAR(255) DEFAULT NULL,
  genre VARCHAR(255) DEFAULT NULL,
  age INT(11) DEFAULT NULL,
  quantity INT(11) DEFAULT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB
CHARACTER SET utf8
COLLATE utf8_general_ci;

--
-- Описание для таблицы reader
--
DROP TABLE IF EXISTS reader;
CREATE TABLE reader (
  id INT(11) NOT NULL,
  fName VARCHAR(255) DEFAULT NULL,
  lName VARCHAR(255) DEFAULT NULL,
  sName VARCHAR(255) DEFAULT NULL,
  age INT(11) DEFAULT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB
CHARACTER SET utf8
COLLATE utf8_general_ci;

--
-- Описание для таблицы usingt
--
DROP TABLE IF EXISTS usingt;
CREATE TABLE usingt (
  id INT(11) NOT NULL,
  idBook INT(11) DEFAULT NULL,
  idReader INT(11) DEFAULT NULL,
  dateReturn DATE DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT FK_g9hwu317ovg5emxy9dhep8s3b FOREIGN KEY (idBook)
    REFERENCES book(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_kctn8ppy2yhv67f9m0l9lajs8 FOREIGN KEY (idReader)
    REFERENCES reader(id) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE = INNODB
CHARACTER SET utf8
COLLATE utf8_general_ci;

-- 
-- Восстановить предыдущий режим SQL (SQL mode)
-- 
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;

-- 
-- Включение внешних ключей
-- 
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;