create table tb_estado (
	id serial,
	nome varchar(256),
	abreviatura varchar(5),
	constraint pkEstado primary key (id)
);

create table tb_cidade (
	id serial,
	nome varchar(256),
	estado_id int,
	constraint pkCidade primary key (id),
	constraint fkCidadeEstado foreign key (estado_id) references tb_estado(id)
);

create table tb_usuario (
	id serial,
	login varchar(256),
	senha varchar(256),
	tipo varchar(256),
	nome varchar(256),
	cpf bigint,
	email varchar(256),
	rua varchar(256),
	numero int,
	cidade_id int,
	advogado_id int,
	constraint pkUsuario primary key (id),
	constraint fkUsuarioCidade foreign key (cidade_id) references tb_cidade (id),
	constraint fkUsuarioAdvogado foreign key (advogado_id) references tb_usuario(id),
	constraint ukUsuario unique (login),
	constraint ukUsuario2 unique (cpf)
);

----------------------------------------------------------------------------------------------------------------

create table tb_processo (
	id serial,
	dt_criacao date,
	status varchar (256),
	vencedor varchar (256),
	juiz_id int,
	parte_promovente_id int,
	parte_promovida_id int,
	constraint pkProcesso primary key (id),
	constraint fkProcessoJuiz foreign key (juiz_id) references tb_usuario(id),
	constraint fkProcessoPartePromovente foreign key (parte_promovente_id) references tb_usuario(id),
	constraint fkProcessoPartePromovida foreign key (parte_promovida_id) references tb_usuario(id)
);

create table tb_fase (
	id serial,
	processo_id int,
	criador_id int,
	dt_criacao timestamp,
	titulo varchar(256),
	descricao varchar(512),
	caminho_arquivo varchar(512),
	tipo varchar(256),
	resposta varchar(256),
	justificativa_resposta varchar(512),
	constraint pkFase primary key (id),
	constraint fkFaseProcesso foreign key (processo_id) references tb_processo(id),
	constraint fkFaseCriador foreign key (criador_id) references tb_usuario(id)
);

----------------------------------------------------------------------------------------------------------

select * from tb_usuario

delete from tb_usuario
where nome not like '%Moro%'

select * from tb_cidade
where id = 6

insert into tb_usuario (login) values ('a')


select * from tb_processo

select * from tb_fase

