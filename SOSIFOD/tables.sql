create table tb_usuario(
id serial,
login varchar(256),
senha varchar(256),
tipo varchar(256),
cpf varchar(256),
nome varchar(256),
email varchar(256),
Constraint pkUser primary key(id),
Constraint uklogin unique(login),
Constraint ukCpf unique(cpf),
Constraint ukemail unique(email));

/*create table tb_oficial(
idOficial serial,
cpf bigint,
nome varchar(256),
email varchar(256),
idUsuario integer,
Constraint pkOficial primary key(idOficial, idUsuario),
Constraint ukCpf unique(cpf),
Constraint ukemail unique(email),
Constraint fkOficial foreign key(idUsuario) references tb_usuarioSOSIFOD(id));*/

-- drop table tb_usuario;
-- drop table tb_oficial;
-- drop table tb_intimacao;

create table tb_intimacao(
id_intimacao serial,
dt_intimacao timestamp,
cpf_intimado varchar(256),
nome_intimado varchar(256),
endereco_intimado varchar(256),
dt_execucao timestamp,
status varchar(20),
id_oficial integer,
num_processo integer,
Constraint pkIntimacao primary key(id_intimacao, id_oficial, num_processo),
Constraint fkOficial foreign key(id_oficial) references tb_usuario(id));

------------------------------------------------------------------------
insert into tb_usuario values(default, 'teste', '698dc19d489c4e4db73e28a713eab07b', 'Admin', '12345678900', 'Administrador Teste', 'admin@gmail.com');

select * from tb_usuario;

insert into tb_intimacao values(default, '2020-02-11 16:30:47', '301.311.180-13', 'João Sorrisão', 'Rua dos Teste, 3, Curitiba, PR', '2020-12-15 16:30:47', 'Efetuada', 2, 12345);

insert into tb_intimacao values(default, '2020-02-11 16:30:47', '103.325.760-51', 'Joana Guimarães', 'Rua dos Exemplos, 50, Curitiba, PR', now(), 'Efetuada', 2, 23456);

insert into tb_intimacao(id_intimacao, dt_intimacao, cpf_intimado, nome_intimado, endereco_intimado, status, id_oficial, num_processo) values(default, now(), '412.048.550-13', 'Nando Moura', 'Rua dos Oficiais, 444, Curitiba, PR', 'Não efetuada', 2, 56789);

select * from tb_intimacao order by id_intimacao;

delete from tb_intimacao where id_intimacao = 3;