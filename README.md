#SIJOGA - Sistema Jurídico On-line para Grandes Administrações

Vocês deverão desenvolver um sistema de Controle de Processos Jurídicos. Este sistema possui vários usuários com os seguintes perfis:
●	Juiz
●	Advogado
●	Parte
O sistema mantém Processos, onde cada processo é composto por várias fases. Cada processo é atribuído a somente um Juiz. Cada processo possui duas partes (Promovente e Promovido), e cada parte é representada por um advogado. Um juiz pode estar atribuído a vários processos. As partes podem estar em vários processos. Um advogado pode representar várias partes.
As partes só podem consultar os dados gerais de seus processos (tanto como promovente como promovido) e os dados de cada uma das fases.
Os advogados podem criar novas fases, anexando documentos. As fases possuem dois tipos: As informativas e as deliberativas. As informativas não necessitam de intervenção do juiz. As deliberativas trancam o processo para qualquer movimentação até que o juiz dê andamento. As fases possuem uma sequência no processo, data e hora de criação, o advogado criador, um título, uma descrição e (opcionalmente) um arquivo pdf anexo.
A resposta do juiz em qualquer fase pode ser:
●	Pedido Aceito: o processo volta a aceitar novas fases
●	Pedido Negado: o processo volta a aceitar novas fases, deve ser dada uma justificativa
●	Intimação: uma intimação no sistema SOSIFOD é gerada e uma fase é gerada automaticamente, com dados da intimação. O Juiz escolhe qual oficial de justiça fará a intimação. Quando a intimação é executada, uma outra fase é automaticamente criada com os dados da execução da intimação
●	Encerramento: a qualquer momento o juiz pode encerrar o processo, indicando um parecer, não aceitam-se mais fases. Também deve haver um estado (status) indicando quem venceu, o promovido ou o promovente
	Todos os usuários devem possuir login e senha. Na tela principal, tem-se:
●	Para o perfil JUIZ: uma lista com todos os processos no qual está alocado e, em vermelho, os que estão em fase deliberativa
●	Para o perfil ADVOGADO: uma lista com todos os processos no qual está alocado, indicando se sua parte é Promovente ou Promovido. Em vermelho os processos que estão em fase deliberativa
●	Para o perfil PARTE: uma lista com todos os seus processos em aberto, indicando se é promovido ou promovente. 
	Somente advogados podem criar processos, indicando seu cliente (automaticamente como promovente) e uma outra parte (automaticamente como promovido). As partes devem estar cadastradas no sistema, se não, o advogado é responsável por fazê-lo.
	Ao ser criado um novo processo, um juiz é automaticamente atrelado à ele, e será o juiz que possui menos processos em aberto. Se houver empate, qualquer destes pode ser atrelado.
	Um advogado pode consultar seus processos pelos seguintes critérios:
●	Todos os seus processos
●	Todos os seus processos em aberto
●	Todos os seus processos já encerrados
●	Todos os seus processos em que foi promovido
●	Todos os seus processos em que foi promovente
●	Todos os seus processos que ganhou ou perdeu como promovido
●	Todos os seus processos que ganhou ou perdeu como promovente
	Também pode tirar relatório em PDF das seguintes consultas:
●	Processos em aberto, que foram criados entre um intervalo de datas
●	Processos já encerrados
	Uma parte também pode tirar um relatório em PDF de todos os seus processos, contendo as informações básicas do processo, em que estado está, se estiver encerrado se venceu ou não, se foi promovido ou promovente.

#SOSIFOD - Sistema Online Sobre Informações Factíveis de Oficiais de justiça Do paraná

Este é um sistema de controle de oficial de justiça e sua alocação para executar intimações. Os dados básicos são:
●	Oficial de Justiça: CPF, Nome, e-mail
●	Intimação: Data/Hora da intimação, CPF do intimado, Nome do intimado, Endereço do intimado, Data/hora da execução da intimação, Estado se a intimação foi efetuada ou não, Oficial de Justiça alocado, processo
	Há um perfil administrador que cadastra os oficiais de Justiça e mantém (crud) as intimações. Uma intimação pode ser feita automaticamente através do sistema SIJOGA. Neste caso, quando a intimação é executada, o sistema SOSIFOD automaticamente sinaliza o sistema SIJOGA sobre a execução da intimação.
	Há também um perfil Oficial de Justiça, cujos usuários são cadastrados pelo administrador. O oficial de justiça tem acesso a todas intimações alocadas para ele e pode indicar a execução dela.

Requisitos não-funcionais

Toda e qualquer suposição, que não esteja definida aqui e que a equipe faça, deve ser devidamente documentada e entregue em um arquivo .PDF que acompanha o trabalho.
Os requisitos não-funcionais mínimos são:

●	A comunicação entre os sistemas deve ser feita através de Web Services tipo REST, obrigatoriamente;
●	Senhas devem ser criptografadas;
●	O leiaute deve seguir preceitos de usabilidade e ergonomia, usar o ErgoList como direcionador (http://www.labiutil.inf.ufsc.br/ergolist/);
●	O leiaute deve ser agradável;
●	Validação de campos deve ser implementada em duas etapas - cliente com Javascript e servidor em Java;
●	Todas as datas e valores monetários devem ser entrados e mostrados no formato brasileiro;
●	Todos os campos que tiverem formatação devem possuir máscara;
●	Todas as datas deverão ser entradas através de calendários;
●	Todos os relatórios devem ser feitos em PDF (usar JasperStudio);
●	O banco de dados deve estar normalizado apropriadamente.
