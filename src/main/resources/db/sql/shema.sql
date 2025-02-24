USE servlet_tre;

CREATE TABLE
    IF NOT EXISTS Utenti (
        ID INT PRIMARY KEY AUTO_INCREMENT,
        Nome VARCHAR(50) NOT NULL,
        Cognome VARCHAR(50) NOT NULL,
        Email VARCHAR(100) UNIQUE NOT NULL,
        Telefono VARCHAR(15) UNIQUE NOT NULL
    );

CREATE TABLE
    IF NOT EXISTS Credenziali (
        ID INT PRIMARY KEY AUTO_INCREMENT,
        Username VARCHAR(50) UNIQUE NOT NULL,
        Passwd VARCHAR(255) NOT NULL,
        UserID INT NOT NULL,
        FOREIGN KEY (UserID) REFERENCES Utenti (id)
    );