DROP DATABASE IF EXISTS TrojanTrader;
CREATE DATABASE TrojanTrader;

USE TrojanTrader;

CREATE TABLE userInfo (
	username VARCHAR(50) PRIMARY KEY NOT NULL,
    pass VARCHAR(50) NOT NULL,
	balance double NOT NULL
);

CREATE TABLE transactionsByUser(
	transactionID int PRIMARY KEY NOT NULL auto_increment,
    username VARCHAR(50),
    stockname VARCHAR(50) NOT NULL,
	quantity int NOT NULL,
    foreign key (username) references userinfo(username)
);

CREATE TABLE allTransactions(
	transactionID int PRIMARY KEY NOT NULL auto_increment,
    username VARCHAR(50),
    stockname VARCHAR(50) NOT NULL,
	purchasePrice VARCHAR(50) NOT NULL,
    timeOfTransaction VARCHAR(50) NOT NULL,
    quantity int NOT NULL,
    foreign key (username) references userinfo(username)
);

CREATE TABLE stockPrices(
	stockname VARCHAR(50) PRIMARY KEY NOT NULL,
    price double NOT NULL,
	24hourprice double NOT NULL,
    openingprice double NOT NULL
);

INSERT INTO stockPrices(stockname, price, 24hourprice, openingprice) values('AAPL', 100.0, 0.0, 0.0);
INSERT INTO stockPrices(stockname, price, 24hourprice, openingprice) values('TSLA', 90.0, 0.0, 0.0);
INSERT INTO stockPrices(stockname, price, 24hourprice, openingprice) values('MSFT', 80.0, 0.0, 0.0);
INSERT INTO stockPrices(stockname, price, 24hourprice, openingprice) values('AMZN', 70.0, 0.0, 0.0);
-- INSERT INTO stockPrices(stockname, price, 24hourprice, openingprice) values('BINANCE:BTCUSDT', 0.0, 0.0, 0.0);

