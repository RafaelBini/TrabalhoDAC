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

select * from tb_usuario;


select * from tb_cidade
where id = 6;

insert into tb_usuario (login) values ('a');


select * from tb_processo;

select * from tb_fase;

delete from tb_fase;


select distinct p.id, p.dt_criacao, eu.nome,
case
when eu.id = promovente.advogado_id then 'Promovente'
when eu.id = promovida.advogado_id then 'Promovido'
end,
promovente.nome as Promovente, promovida.nome as Promovida
from tb_processo p
inner join tb_usuario promovente on p.parte_promovente_id = promovente.id
inner join tb_usuario promovida on p.parte_promovida_id = promovida.id
inner join tb_usuario eu on (promovente.advogado_id = 30 or promovida.advogado_id = 30)
where status <> 'Encerrado';

-- Processos Abertos
select p.id, p.dt_criacao as data, 
p.status, juiz.nome as juiz, 
case when eu.id = promovente.advogado_id then 'Promovente' when eu.id = promovida.advogado_id then 'Promovido' end as atuaçao,
case when eu.id = promovente.advogado_id then promovente.nome when eu.id = promovida.advogado_id then promovida.nome end as cliente, 
case when eu.id <> promovente.advogado_id then promovente.nome when eu.id <> promovida.advogado_id then promovida.nome end as parte_oposta
from tb_processo p
inner join tb_usuario promovente on p.parte_promovente_id = promovente.id
inner join tb_usuario promovida on p.parte_promovida_id = promovida.id
inner join tb_usuario juiz on p.juiz_id = juiz.id
inner join (select * from tb_usuario where id = 30) eu on (promovente.advogado_id = eu.id or promovida.advogado_id = eu.id)
where status <> 'Encerrado';

-- Processos encerrados
select p.id, p.dt_criacao as data, 
p.status, juiz.nome as juiz, 
case when eu.id = promovente.advogado_id then 'Promovente' when eu.id = promovida.advogado_id then 'Promovido' end as atuaçao,
case when eu.id = promovente.advogado_id then promovente.nome when eu.id = promovida.advogado_id then promovida.nome end as cliente, 
case when eu.id <> promovente.advogado_id then promovente.nome when eu.id <> promovida.advogado_id then promovida.nome end as parte_oposta,
case 
when (eu.id = promovente.advogado_id and p.vencedor = 'Promovente') or (eu.id = promovida.advogado_id and p.vencedor = 'Promovido') then 'Ganhei' 
when (eu.id = promovente.advogado_id and p.vencedor = 'Promovido') or (eu.id = promovida.advogado_id and p.vencedor = 'Promovente') then 'Perdi'
end as resultado
from tb_processo p
inner join tb_usuario promovente on p.parte_promovente_id = promovente.id
inner join tb_usuario promovida on p.parte_promovida_id = promovida.id
inner join tb_usuario juiz on p.juiz_id = juiz.id
inner join (select * from tb_usuario where id = 30) eu on (promovente.advogado_id = eu.id or promovida.advogado_id = eu.id)
where status = 'Encerrado';

-- Processos Parte
select p.id, p.dt_criacao as data, 
p.status, juiz.nome as juiz, 
case when eu.id = promovente.id then 'Promovente' when eu.id = promovida.id then 'Promovido' end as atuaçao,
case when eu.id <> promovente.id then promovente.nome when eu.id <> promovida.id then promovida.nome end as parte_oposta,
case 
when (eu.id = promovente.id and p.vencedor = 'Promovente') or (eu.id = promovida.id and p.vencedor = 'Promovido') then 'Ganhei' 
when (eu.id = promovente.id and p.vencedor = 'Promovido') or (eu.id = promovida.id and p.vencedor = 'Promovente') then 'Perdi'
end as resultado
from tb_processo p
inner join tb_usuario promovente on p.parte_promovente_id = promovente.id
inner join tb_usuario promovida on p.parte_promovida_id = promovida.id
inner join tb_usuario juiz on p.juiz_id = juiz.id
inner join (select * from tb_usuario where id = 32) eu on (promovente.id = eu.id or promovida.id = eu.id)
;


