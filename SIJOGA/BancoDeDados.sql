CREATE TABLE usuario
(
cpf bigint NOT NULL,
senha character varying(256),
nome character varying(256) NOT NULL,
tipo integer NOT NULL,
CONSTRAINT pkusuario PRIMARY KEY (cpf),
CONSTRAINT fkTipoUsuario FOREIGN KEY (tipo) references tipo(id)
)

CREATE TABLE tipo
(
id int,
nome varchar(256),
constraint pkTipo primary key (id)
)
