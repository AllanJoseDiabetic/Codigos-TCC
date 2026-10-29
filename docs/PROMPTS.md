## Prompt 01 Blog pessoal com autenticação

Produza uma implementação original da aplicação descrita abaixo em uma única resposta inicial. Use somente esta especificação. Não consulte web, repositórios, projetos conhecidos ou código de referência; não peça arquivos, links nem esclarecimentos. Não copie, transcreva ou traduza uma implementação existente.

Não use ferramentas, terminal, agentes, execução, compilação, instalação ou testes. Não gere suítes de testes, relatórios de testes ou resultados de análise. Não faça uma rodada separada de autorrevisão, autocorreção, patches ou alternativas; não apresente uma segunda versão. Esta restrição se refere ao procedimento observável e não exige expor nem eliminar raciocínio interno.

Mantenha exatamente as versões comprovadas do snapshot histórico humano que constam nesta especificação, incluindo linguagem, frameworks, bibliotecas e ferramentas quando declaradas. Não atualize, substitua ou acrescente dependências por conveniência. Versões não comprovadas devem permanecer identificadas como tal; qualquer escolha necessária é uma hipótese de compatibilidade, sem alegação de igualdade histórica.

Aplicação solicitada: Blog pessoal com autenticação.

### Ambiente e versões

Use Python e Flask, com Flask-SQLAlchemy, SQLAlchemy, Flask-Bcrypt, bcrypt, Flask-Login, Flask-Mail, Flask-WTF, WTForms, Pillow e itsdangerous. As versões exatas desses componentes, de Python, do gerenciador de pacotes e do SQLite não são comprováveis na especificação histórica. Não atribua números históricos a eles; declare qualquer versão necessária escolhida como hipótese de compatibilidade. As versões comprovadas da interface são Bootstrap 4.0.0, jQuery slim 3.2.1 e Popper.js 1.12.9; mantenha exatamente esses números. Use banco relacional configurável por URI, com SQLite como opção de execução local, e SMTP configurável com TLS.

### Funcionalidades e limites de escopo

1. Ofereça página inicial pública com posts do mais recente para o mais antigo, cinco por página; consulta individual com título, texto, autor e data; listagem paginada por autor e página informativa sobre o site.

2. Permita cadastro com nome de usuário único de 2 a 20 caracteres, e-mail único e válido, senha e confirmação. Ofereça login por e-mail e senha, opção de lembrar sessão e logout. Mostre mensagens de validação e de resultado das operações.

3. Usuários autenticados podem criar posts com título e conteúdo. Somente o autor pode editar ou excluir seu próprio post. Visitantes podem ler; ações restritas devem conduzir à autenticação ou apresentar a resposta de acesso negado cabível.

4. Permita atualizar nome de usuário, e-mail e imagem do próprio perfil, mantendo a unicidade dos campos. Aceite imagem JPG ou PNG e ofereça imagem padrão para contas sem foto.

5. Disponibilize solicitação de recuperação de senha por e-mail, link com token que expire e formulário de nova senha com confirmação. Credenciais SMTP e chave de aplicação devem ser configuráveis externamente, sem credenciais reais incorporadas.

6. Inclua páginas HTML próprias para acesso proibido, recurso inexistente e erro interno, com códigos HTTP 403, 404 e 500. Preserve navegação e mensagens entre os fluxos. Entregue persistência de usuários e posts e os recursos de apresentação necessários.

7. Contratos de navegação: / e /home; /about; /register; /login; /logout; /account; /user/{nome}; /post/new; /post/{id}; /post/{id}/update; /post/{id}/delete; /reset_password e /reset_password/{token}. Não acrescente comentários, curtidas, painel editorial ou funcionalidades comerciais.

### Formato da entrega e restrições

Entregue na mesma resposta uma árvore de arquivos e o conteúdo integral de cada arquivo necessário, identificado pelo caminho relativo, incluindo fontes, manifestos, configuração de exemplo sem segredos e recursos representáveis em texto. Use nomes neutros para o projeto. Não use reticências, trechos omitidos ou comentários que substituam funcionalidades. Não inclua nomes, links ou atribuições de projetos de referência.

Acrescente instruções curtas de preparação e uso destinadas ao pesquisador, sem executá-las. Liste as versões adotadas e diferencie valores comprovados das hipóteses. Não invente arquivos lock, hashes de dependências, arquivos binários ou logs que exigiriam resolução ou execução para existir. Bibliotecas de testes listadas nesta especificação podem constar do manifesto, mas não gere arquivos de testes.

Não alegue que compilou, iniciou, validou, testou ou obteve qualidade no SonarQube. Se algo não puder ser entregue ou se a especificação não determinar uma regra, registre a limitação honestamente na própria resposta. Não peça uma rodada posterior nem proponha correções futuras. A saída será preservada e avaliada exatamente como recebida.

---

## Prompt 02 Agenda de hábitos e acompanhamento diário

Produza uma implementação original da aplicação descrita abaixo em uma única resposta inicial. Use somente esta especificação. Não consulte web, repositórios, projetos conhecidos ou código de referência; não peça arquivos, links nem esclarecimentos. Não copie, transcreva ou traduza uma implementação existente.

Não use ferramentas, terminal, agentes, execução, compilação, instalação ou testes. Não gere suítes de testes, relatórios de testes ou resultados de análise. Não faça uma rodada separada de autorrevisão, autocorreção, patches ou alternativas; não apresente uma segunda versão. Esta restrição se refere ao procedimento observável e não exige expor nem eliminar raciocínio interno.

Mantenha exatamente as versões comprovadas do snapshot histórico humano que constam nesta especificação, incluindo linguagem, frameworks, bibliotecas e ferramentas quando declaradas. Não atualize, substitua ou acrescente dependências por conveniência. Versões não comprovadas devem permanecer identificadas como tal; qualquer escolha necessária é uma hipótese de compatibilidade, sem alegação de igualdade histórica.

Aplicação solicitada: Agenda de hábitos e acompanhamento diário.

### Ambiente e versões

Use exatamente Python 3.8.5, asgiref 3.3.1, dj-database-url 0.5.0, Django 3.1.4, django-heroku 0.3.1, gunicorn 20.0.4, psycopg2 2.8.6, python-dotenv 0.15.0, pytz 2020.4, sqlparse 0.4.1 e whitenoise 5.2.0. Na interface, use Bootstrap 4.5.2, jQuery slim 3.5.1 e Popper.js 1.16.1. Há uso de Google Charts, mas sem versão numérica imutável comprovada. O servidor PostgreSQL, pip e o ambiente de hospedagem não têm versões exatas comprovadas; declare essa lacuna. Mantenha suporte à configuração de banco por URL e às variáveis externas de ambiente, sem exigir implantação pública.

### Funcionalidades e limites de escopo

1. Ofereça página pública de apresentação, cadastro, login, logout e menu pessoal autenticado. O cadastro recebe usuário, e-mail, senha e confirmação, com nome e sobrenome opcionais. Evite contas duplicadas e apresente mensagens de erro dos formulários.

2. Cada pessoa gerencia suas próprias categorias e tarefas. Uma tarefa tem nome, categoria, estado ativo e dias da semana previstos. Permita criar e editar esses dados, renomear categorias e excluir tarefas ou categorias com confirmação.

3. Excluir tarefa também remove seus registros; excluir categoria remove suas tarefas e registros. Desativar tarefa impede novos registros futuros, preservando o histórico anterior.

4. Mantenha registros por tarefa e data com os estados Complete, Incomplete, Ignored e Pending e campo de anotação. Nas consultas diária, semanal e mensal, crie os registros pertinentes aos dias previstos até a data corrente, sem duplicá-los. Novos registros começam Pending.

5. Permita alterar o registro do dia e editar dias da semana recente. Registros com mais de uma semana deixam de ser editáveis; pendências antigas são convertidas automaticamente para Incomplete. A regra deve considerar o fuso escolhido pela pessoa.

6. Ofereça visão diária, semanal, mensal por ano e mês, acesso a meses anteriores e resumo de progresso por tarefa. Mostre estatísticas em gráfico de colunas com Google Charts quando JavaScript estiver habilitado e em lista textual quando não estiver.

7. Permita alterar os dados do perfil e selecionar fuso horário, usando UTC inicialmente. A separação entre contas deve valer em consultas e alterações. Inclua páginas de autenticação, tarefas, registros, progresso, perfil, fuso e informações gerais; não acrescente redes sociais ou calendário externo.

8. Contratos de navegação: /tasks/add, /tasks/overview, /tasks/delete/task/{id}, /tasks/delete/category/{id}, /log/daily, /log/weekly, /log/weekly/edit/{dia}, /log/monthly/{ano}/{mes}, /log/past, /progress, /settings/profile e /settings/timezone, além de cadastro, login e logout.

### Formato da entrega e restrições

Entregue na mesma resposta uma árvore de arquivos e o conteúdo integral de cada arquivo necessário, identificado pelo caminho relativo, incluindo fontes, manifestos, configuração de exemplo sem segredos e recursos representáveis em texto. Use nomes neutros para o projeto. Não use reticências, trechos omitidos ou comentários que substituam funcionalidades. Não inclua nomes, links ou atribuições de projetos de referência.

Acrescente instruções curtas de preparação e uso destinadas ao pesquisador, sem executá-las. Liste as versões adotadas e diferencie valores comprovados das hipóteses. Não invente arquivos lock, hashes de dependências, arquivos binários ou logs que exigiriam resolução ou execução para existir. Bibliotecas de testes listadas nesta especificação podem constar do manifesto, mas não gere arquivos de testes.

Não alegue que compilou, iniciou, validou, testou ou obteve qualidade no SonarQube. Se algo não puder ser entregue ou se a especificação não determinar uma regra, registre a limitação honestamente na própria resposta. Não peça uma rodada posterior nem proponha correções futuras. A saída será preservada e avaliada exatamente como recebida.

---

## Prompt 03 API de usuários e pedidos comerciais

Produza uma implementação original da aplicação descrita abaixo em uma única resposta inicial. Use somente esta especificação. Não consulte web, repositórios, projetos conhecidos ou código de referência; não peça arquivos, links nem esclarecimentos. Não copie, transcreva ou traduza uma implementação existente.

Não use ferramentas, terminal, agentes, execução, compilação, instalação ou testes. Não gere suítes de testes, relatórios de testes ou resultados de análise. Não faça uma rodada separada de autorrevisão, autocorreção, patches ou alternativas; não apresente uma segunda versão. Esta restrição se refere ao procedimento observável e não exige expor nem eliminar raciocínio interno.

Mantenha exatamente as versões comprovadas do snapshot histórico humano que constam nesta especificação, incluindo linguagem, frameworks, bibliotecas e ferramentas quando declaradas. Não atualize, substitua ou acrescente dependências por conveniência. Versões não comprovadas devem permanecer identificadas como tal; qualquer escolha necessária é uma hipótese de compatibilidade, sem alegação de igualdade histórica.

Aplicação solicitada: API de usuários e pedidos comerciais.

### Ambiente e versões

Use Java 11, Spring Boot 2.1.7.RELEASE e Maven 3.6.0. Preserve o gerenciamento de versões do parent spring-boot-starter-parent 2.1.7.RELEASE para spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-test, H2, driver PostgreSQL e spring-boot-maven-plugin; não imponha overrides ou versões atuais. H2 e PostgreSQL são dependências de runtime. O patch do JDK 11 e a versão do servidor PostgreSQL não foram comprovados. Disponibilize perfil local test com H2 em memória, perfil de desenvolvimento e perfil prod com URL externa de PostgreSQL; o perfil padrão declarado é prod. Não confunda o perfil chamado test com autorização para gerar ou executar testes.

### Funcionalidades e limites de escopo

1. Ofereça API HTTP com respostas JSON para usuários, produtos, categorias e pedidos. Usuários possuem identificação, nome, e-mail, telefone e senha como dado do cadastro; não acrescente fluxo de login, emissão de tokens ou controle de acesso inexistente no escopo.

2. Usuários devem poder ser listados, consultados por identificador, cadastrados, alterados e excluídos. Cadastro retorna HTTP 201 com localização do recurso; exclusão bem-sucedida retorna 204. Dados inexistentes e violações de integridade devem produzir respostas de erro identificáveis.

3. Produtos têm nome, descrição, preço e endereço de imagem e podem pertencer a mais de uma categoria. Categorias têm identificação e nome. Disponibilize listagem e consulta individual de produtos e categorias.

4. Pedidos pertencem a um usuário e registram instante, estado, itens e eventual pagamento. Cada item associa produto, quantidade e preço praticado; exponha subtotal e total do pedido. O pagamento registra identificador e instante e está vinculado ao pedido.

5. Represente os estados aguardando pagamento, pago, enviado, entregue e cancelado. Disponibilize listagem e consulta individual de pedidos com seus itens e informações relacionadas, sem serializações recursivas intermináveis.

6. Contratos HTTP: GET /users e /users/{id}, POST /users, PUT e DELETE /users/{id}; GET /products e /products/{id}; GET /categories e /categories/{id}; GET /orders e /orders/{id}. Não implemente checkout, compra por interface gráfica, gateway financeiro ou CRUD adicional de pedidos, produtos e categorias.

7. Inclua dados fictícios para consulta no perfil local e instruções de seleção de perfil e configuração de banco. O conjunto precisa permitir observar relacionamentos, um pedido com vários itens e outro com pagamento, sem reproduzir registros pessoais ou textuais de qualquer fonte.

### Formato da entrega e restrições

Entregue na mesma resposta uma árvore de arquivos e o conteúdo integral de cada arquivo necessário, identificado pelo caminho relativo, incluindo fontes, manifestos, configuração de exemplo sem segredos e recursos representáveis em texto. Use nomes neutros para o projeto. Não use reticências, trechos omitidos ou comentários que substituam funcionalidades. Não inclua nomes, links ou atribuições de projetos de referência.

Acrescente instruções curtas de preparação e uso destinadas ao pesquisador, sem executá-las. Liste as versões adotadas e diferencie valores comprovados das hipóteses. Não invente arquivos lock, hashes de dependências, arquivos binários ou logs que exigiriam resolução ou execução para existir. Bibliotecas de testes listadas nesta especificação podem constar do manifesto, mas não gere arquivos de testes.

Não alegue que compilou, iniciou, validou, testou ou obteve qualidade no SonarQube. Se algo não puder ser entregue ou se a especificação não determinar uma regra, registre a limitação honestamente na própria resposta. Não peça uma rodada posterior nem proponha correções futuras. A saída será preservada e avaliada exatamente como recebida.

---

## Prompt 04 Jogo de tabuleiro por turnos em rede

Produza uma implementação original da aplicação descrita abaixo em uma única resposta inicial. Use somente esta especificação. Não consulte web, repositórios, projetos conhecidos ou código de referência; não peça arquivos, links nem esclarecimentos. Não copie, transcreva ou traduza uma implementação existente.

Não use ferramentas, terminal, agentes, execução, compilação, instalação ou testes. Não gere suítes de testes, relatórios de testes ou resultados de análise. Não faça uma rodada separada de autorrevisão, autocorreção, patches ou alternativas; não apresente uma segunda versão. Esta restrição se refere ao procedimento observável e não exige expor nem eliminar raciocínio interno.

Mantenha exatamente as versões comprovadas do snapshot histórico humano que constam nesta especificação, incluindo linguagem, frameworks, bibliotecas e ferramentas quando declaradas. Não atualize, substitua ou acrescente dependências por conveniência. Versões não comprovadas devem permanecer identificadas como tal; qualquer escolha necessária é uma hipótese de compatibilidade, sem alegação de igualdade histórica.

Aplicação solicitada: Jogo de tabuleiro por turnos em rede.

### Ambiente e versões

Use Java SE com recursos padrão de rede por sockets e RMI e interface desktop Swing/AWT, além de uma interface de linha de comando. Não substitua por aplicação web, JavaFX, framework de jogos ou serviço em nuvem. A configuração disponível referencia o ambiente Java sem versão e JUnit da família 4, sem patch. Não há versão exata comprovada de JDK, compilador, Eclipse ou ferramenta de build; não as apresente como históricas. Se precisar selecionar versão para o projeto, declare-a como hipótese, sem introduzir bibliotecas externas de produção não solicitadas. JUnit 4 pertence apenas à infraestrutura histórica de testes e não autoriza geração de testes.

### Funcionalidades e limites de escopo

1. Entregue servidor e cliente para jogo multijogador por turnos. O cliente deve permitir informar nome de jogador e escolher socket ou RMI, conectar a servidor já iniciado, aguardar a formação da partida e receber notificações de turno e ações de todos os participantes.

2. Disponibilize as mesmas operações de jogo em CLI e GUI Swing: consultar tabuleiro, própria área, áreas adversárias, cartas, recursos e pontos; selecionar um familiar disponível e uma área de ação; receber motivo de rejeição para ação não permitida. Somente o participante da vez executa ações de turno.

3. Represente familiares, moedas, madeira, pedra e servos; pontos de vitória, militares e de fé; cartas de território, edifício, personagem e empreendimento; custos, efeitos imediatos e efeitos permanentes. Não exponha apenas telas estáticas: as ações precisam alterar o estado compartilhado da partida.

4. Disponibilize mercado, colheita, produção, palácio do conselho e torres. No mercado, a pessoa escolhe área e familiar; na colheita e produção, escolhe área, familiar e efeitos permanentes aplicáveis às cartas correspondentes; no conselho, escolhe familiar e privilégio; nas torres, escolhe torre, andar, familiar e, quando houver, uma alternativa de custo.

5. Permita gastar servos para aumentar o valor de um familiar sem encerrar imediatamente o turno. Rejeite uso repetido de familiar, posição ocupada indevidamente, escolha inválida, valor insuficiente e recursos insuficientes, com mensagens compreensíveis.

6. Ao final de períodos, ofereça decisão de apoio à igreja condicionada aos pontos de fé, aplique os efeitos de excomunhão correspondentes e permita consultar seus efeitos. Ao término, calcule classificação por pontos, incluindo recursos, cartas e penalidades aplicáveis, e informe o resultado a todos.

7. Inclua chat entre participantes disponível fora da vez individual, atualização da área pessoal e adversária e indicação visual da ordem de turno. Na GUI, permita selecionar familiar e área por clique; na CLI, apresente opções textuais equivalentes.

8. Limite explícito desta especificação: ela não fornece o catálogo integral de cartas, valores de cada efeito, tabela de pontuação e todas as condições numéricas de formação e duração da partida. Não consulte fontes externas nem invente esses dados como se fossem requisitos confirmados. Entregue o máximo do escopo descrito em uma resposta e discrimine os pontos que dependem dessas regras ausentes. Não substitua silenciosamente o jogo por uma demonstração simplificada nem declare equivalência completa. Representações visuais próprias são permitidas; regras ou conteúdos ausentes devem continuar identificados como limitações.

### Formato da entrega e restrições

Entregue na mesma resposta uma árvore de arquivos e o conteúdo integral de cada arquivo necessário, identificado pelo caminho relativo, incluindo fontes, manifestos, configuração de exemplo sem segredos e recursos representáveis em texto. Use nomes neutros para o projeto. Não use reticências, trechos omitidos ou comentários que substituam funcionalidades. Não inclua nomes, links ou atribuições de projetos de referência.

Acrescente instruções curtas de preparação e uso destinadas ao pesquisador, sem executá-las. Liste as versões adotadas e diferencie valores comprovados das hipóteses. Não invente arquivos lock, hashes de dependências, arquivos binários ou logs que exigiriam resolução ou execução para existir. Bibliotecas de testes listadas nesta especificação podem constar do manifesto, mas não gere arquivos de testes.

Não alegue que compilou, iniciou, validou, testou ou obteve qualidade no SonarQube. Se algo não puder ser entregue ou se a especificação não determinar uma regra, registre a limitação honestamente na própria resposta. Não peça uma rodada posterior nem proponha correções futuras. A saída será preservada e avaliada exatamente como recebida.

---

## Prompt 05 Gestão de vendedores e consultas de vendas

Produza uma implementação original da aplicação descrita abaixo em uma única resposta inicial. Use somente esta especificação. Não consulte web, repositórios, projetos conhecidos ou código de referência; não peça arquivos, links nem esclarecimentos. Não copie, transcreva ou traduza uma implementação existente.

Não use ferramentas, terminal, agentes, execução, compilação, instalação ou testes. Não gere suítes de testes, relatórios de testes ou resultados de análise. Não faça uma rodada separada de autorrevisão, autocorreção, patches ou alternativas; não apresente uma segunda versão. Esta restrição se refere ao procedimento observável e não exige expor nem eliminar raciocínio interno.

Mantenha exatamente as versões comprovadas do snapshot histórico humano que constam nesta especificação, incluindo linguagem, frameworks, bibliotecas e ferramentas quando declaradas. Não atualize, substitua ou acrescente dependências por conveniência. Versões não comprovadas devem permanecer identificadas como tal; qualquer escolha necessária é uma hipótese de compatibilidade, sem alegação de igualdade histórica.

Aplicação solicitada: Gestão de vendedores e consultas de vendas.

### Ambiente e versões

Use C# e ASP.NET Core MVC com alvo netcoreapp2.1. Preserve Microsoft.AspNetCore.App sem versão explícita, Microsoft.VisualStudio.Web.CodeGeneration.Design 2.1.1 e Pomelo.EntityFrameworkCore.MySql 2.1.1. Use Entity Framework Core da cadeia compatível com esse alvo e MySQL como banco. Na interface, mantenha Bootstrap 3.3.7, jQuery 3.3.1, jQuery Validation 1.17.0 e jQuery Validation Unobtrusive 3.2.9. A versão de linguagem C#, o patch do SDK/runtime .NET Core, o servidor MySQL e ferramentas de restauração não foram fixados; não os declare comprovados. Não migre o alvo para .NET atual nem troque MySQL por outro produto.

### Funcionalidades e limites de escopo

1. Crie aplicação web MVC com páginas HTML e formulários para departamentos, vendedores e consultas de registros de venda. Inclua página inicial e navegação coerente entre esses módulos.

2. Departamento tem identificador e nome. Permita listar, ver detalhes, criar, editar e excluir, com confirmação de exclusão e tratamento de identificador ausente ou inexistente.

3. Vendedor tem identificador, nome, e-mail, data de nascimento, salário-base e departamento. Permita CRUD completo, seleção de departamento e validação dos campos. Exiba mensagens de erro no formulário e uma resposta controlada para operações que conflitem com registros relacionados.

4. Registro de venda tem identificador, data, valor, estado e vendedor associado. Represente estados pendente, faturado e cancelado. Inclua persistência e dados fictícios iniciais relacionados a departamentos e vendedores para viabilizar as consultas.

5. Ofereça busca simples de vendas por data mínima e máxima inclusivas, listagem dos registros encontrados e total dos valores. Ofereça busca agrupada por departamento, com registros e totais por grupo.

6. Use as áreas de navegação /Departments, /Sellers e /SalesRecords; disponibilize consultas SimpleSearch e GroupingSearch com minDate e maxDate. Se datas forem omitidas, use início do ano corrente até a data corrente. Documente o intervalo dos dados de exemplo para evitar interpretar lista vazia como falha.

7. Inclua configuração externa da conexão, mecanismo de criação/evolução do banco e instruções de preparação local. Não inclua autenticação, faturamento real, pagamento, estoque ou formulários adicionais de CRUD de vendas não descritos.

### Formato da entrega e restrições

Entregue na mesma resposta uma árvore de arquivos e o conteúdo integral de cada arquivo necessário, identificado pelo caminho relativo, incluindo fontes, manifestos, configuração de exemplo sem segredos e recursos representáveis em texto. Use nomes neutros para o projeto. Não use reticências, trechos omitidos ou comentários que substituam funcionalidades. Não inclua nomes, links ou atribuições de projetos de referência.

Acrescente instruções curtas de preparação e uso destinadas ao pesquisador, sem executá-las. Liste as versões adotadas e diferencie valores comprovados das hipóteses. Não invente arquivos lock, hashes de dependências, arquivos binários ou logs que exigiriam resolução ou execução para existir. Bibliotecas de testes listadas nesta especificação podem constar do manifesto, mas não gere arquivos de testes.

Não alegue que compilou, iniciou, validou, testou ou obteve qualidade no SonarQube. Se algo não puder ser entregue ou se a especificação não determinar uma regra, registre a limitação honestamente na própria resposta. Não peça uma rodada posterior nem proponha correções futuras. A saída será preservada e avaliada exatamente como recebida.

---

## Prompt 06 Simulador gráfico de circuitos lógicos

Produza uma implementação original da aplicação descrita abaixo em uma única resposta inicial. Use somente esta especificação. Não consulte web, repositórios, projetos conhecidos ou código de referência; não peça arquivos, links nem esclarecimentos. Não copie, transcreva ou traduza uma implementação existente.

Não use ferramentas, terminal, agentes, execução, compilação, instalação ou testes. Não gere suítes de testes, relatórios de testes ou resultados de análise. Não faça uma rodada separada de autorrevisão, autocorreção, patches ou alternativas; não apresente uma segunda versão. Esta restrição se refere ao procedimento observável e não exige expor nem eliminar raciocínio interno.

Mantenha exatamente as versões comprovadas do snapshot histórico humano que constam nesta especificação, incluindo linguagem, frameworks, bibliotecas e ferramentas quando declaradas. Não atualize, substitua ou acrescente dependências por conveniência. Versões não comprovadas devem permanecer identificadas como tal; qualquer escolha necessária é uma hipótese de compatibilidade, sem alegação de igualdade histórica.

Aplicação solicitada: Simulador gráfico de circuitos lógicos.

### Ambiente e versões

Use C#, WPF/XAML e .NET Framework 4.5 no Windows, com projeto no formato MSBuild histórico que declara ToolsVersion 14.0. Use referências da plataforma System, System.Core, System.Data, System.Drawing, System.Windows.Forms, System.Xml, System.Xml.Linq, System.Data.DataSetExtensions, Microsoft.CSharp, System.Xaml, WindowsBase, PresentationCore e PresentationFramework. Não substitua por .NET Core, .NET moderno, WinForms como interface principal ou aplicação web. A versão da linguagem C#, edição/build do Visual Studio e compilador efetivamente usados não estão comprovados. O projeto histórico de testes referencia MSTest legado, mas isso não autoriza gerar ou executar testes.

### Funcionalidades e limites de escopo

1. Forneça janela desktop com área de desenho de circuito e paleta de componentes. Permita inserir e mover componentes e conectar saídas a entradas por fios visíveis, mantendo a posição dos fios coerente com a movimentação.

2. Inclua portas AND e OR de duas entradas e NOT de uma entrada, com sinais booleanos. Inclua entradas, saídas e junção para distribuir um sinal. As alterações devem propagar conforme a simulação avança.

3. Inclua fonte de nível alto, interruptor liga/desliga, clock, LED e visor de oito bits que apresente valor decimal, hexadecimal e binário. Ofereça entrada por teclado numérico compatível com esse conjunto de componentes.

4. Inclua flip-flops D e JK com memória de estado e acionamento pelo clock. O D deve armazenar o valor da entrada na transição pertinente; o JK deve permitir manter, ativar, desativar e alternar conforme suas entradas e o clock.

5. Disponibilize execução contínua, pausa/retomada e avanço por um passo. Permita ajustar intervalo entre passos de 10 a 2000 milissegundos, com valor inicial de 100 milissegundos, e mostre o intervalo na janela.

6. Diferencie visualmente estados lógicos e conexões para permitir observar os efeitos do circuito. O LED deve distinguir desligado e ligado; o visor deve atualizar as três representações do mesmo valor.

7. Entregue aplicação iniciável com XAML, fontes e recursos necessários. Ícones próprios simples são suficientes. Não acrescente simulação elétrica analógica, SPICE, resistores/capacitores, colaboração em rede ou persistência de circuitos sem requisito expresso.

### Formato da entrega e restrições

Entregue na mesma resposta uma árvore de arquivos e o conteúdo integral de cada arquivo necessário, identificado pelo caminho relativo, incluindo fontes, manifestos, configuração de exemplo sem segredos e recursos representáveis em texto. Use nomes neutros para o projeto. Não use reticências, trechos omitidos ou comentários que substituam funcionalidades. Não inclua nomes, links ou atribuições de projetos de referência.

Acrescente instruções curtas de preparação e uso destinadas ao pesquisador, sem executá-las. Liste as versões adotadas e diferencie valores comprovados das hipóteses. Não invente arquivos lock, hashes de dependências, arquivos binários ou logs que exigiriam resolução ou execução para existir. Bibliotecas de testes listadas nesta especificação podem constar do manifesto, mas não gere arquivos de testes.

Não alegue que compilou, iniciou, validou, testou ou obteve qualidade no SonarQube. Se algo não puder ser entregue ou se a especificação não determinar uma regra, registre a limitação honestamente na própria resposta. Não peça uma rodada posterior nem proponha correções futuras. A saída será preservada e avaliada exatamente como recebida.

---

## Prompt 07 API de locadora de filmes

Produza uma implementação original da aplicação descrita abaixo em uma única resposta inicial. Use somente esta especificação. Não consulte web, repositórios, projetos conhecidos ou código de referência; não peça arquivos, links nem esclarecimentos. Não copie, transcreva ou traduza uma implementação existente.

Não use ferramentas, terminal, agentes, execução, compilação, instalação ou testes. Não gere suítes de testes, relatórios de testes ou resultados de análise. Não faça uma rodada separada de autorrevisão, autocorreção, patches ou alternativas; não apresente uma segunda versão. Esta restrição se refere ao procedimento observável e não exige expor nem eliminar raciocínio interno.

Mantenha exatamente as versões comprovadas do snapshot histórico humano que constam nesta especificação, incluindo linguagem, frameworks, bibliotecas e ferramentas quando declaradas. Não atualize, substitua ou acrescente dependências por conveniência. Versões não comprovadas devem permanecer identificadas como tal; qualquer escolha necessária é uma hipótese de compatibilidade, sem alegação de igualdade histórica.

Aplicação solicitada: API de locadora de filmes.

### Ambiente e versões

Use JavaScript no backend Node.js, Express e MongoDB. Use exatamente as versões resolvidas relacionadas a seguir, sem carets ou intervalos na declaração das dependências diretas. O runtime Node.js, npm e o servidor MongoDB não têm versões exatas comprovadas; a versão 1 do formato de lock não identifica um npm exato. Não adote uma versão atual como se fosse histórica e não substitua bcrypt por bcryptjs nem MongoDB por outro banco. Mantenha Jest e Supertest apenas como dependências de desenvolvimento declaradas, sem criar testes.

Produção: bcrypt 1.0.3; config 1.29.4; cors 2.8.4; express 4.16.2; express-async-errors 2.1.0; fawn 2.1.5; joi 13.4.0; joi-objectid 2.0.0; jsonwebtoken 8.1.1; lodash 4.17.10; moment 2.20.1; mongoose 5.0.2; winston 2.4.0; winston-mongodb 3.0.0.

Desenvolvimento: jest 22.2.2; supertest 3.0.0.

### Funcionalidades e limites de escopo

1. Forneça API JSON sob /api, sem frontend. Permita cadastrar usuário com nome, e-mail e senha, rejeitar e-mail duplicado, autenticar por e-mail/senha, emitir JWT e consultar o próprio perfil. Receba o token no cabeçalho x-auth-token; a chave é configurável externamente.

2. Gêneros têm nome. Disponibilize listagem, consulta por identificador, criação, edição e exclusão. Leitura de gêneros é pública; escrita exige autenticação; exclusão exige também perfil administrador.

3. Clientes têm nome, telefone e indicador de associação especial. Disponibilize CRUD de clientes com autenticação e validação dos dados.

4. Filmes têm título, gênero, quantidade em estoque e diária de locação. Disponibilize CRUD, verificando a existência do gênero. Leitura é pública; criação/edição exige autenticação; exclusão exige administrador.

5. Permita listar e consultar locações e criar locação para cliente e filme existentes. Registre cliente, filme, data de retirada e diária contratada; recuse filme sem estoque e reduza uma unidade quando uma locação é registrada.

6. Permita devolver filme de uma locação por customerId e movieId. Localize locação correspondente, registre data da devolução e valor calculado conforme dias e diária e reponha o estoque. Recuse locação inexistente ou já devolvida.

7. Contratos: POST /api/users, GET /api/users/me, POST /api/auth; CRUD /api/genres, /api/customers e /api/movies; GET e POST /api/rentals, GET /api/rentals/{id}; POST /api/returns. Use respostas de validação, autenticação, autorização e inexistência coerentes com HTTP 400, 401, 403 e 404.

8. Inclua configuração externa do banco, chave JWT e porta, porta local padrão 3900, tratamento de erros e registros de diagnóstico. Pode incluir dados fictícios iniciais. Não acrescente reservas, cobranças reais, recomendações ou interface web.

### Formato da entrega e restrições

Entregue na mesma resposta uma árvore de arquivos e o conteúdo integral de cada arquivo necessário, identificado pelo caminho relativo, incluindo fontes, manifestos, configuração de exemplo sem segredos e recursos representáveis em texto. Use nomes neutros para o projeto. Não use reticências, trechos omitidos ou comentários que substituam funcionalidades. Não inclua nomes, links ou atribuições de projetos de referência.

Acrescente instruções curtas de preparação e uso destinadas ao pesquisador, sem executá-las. Liste as versões adotadas e diferencie valores comprovados das hipóteses. Não invente arquivos lock, hashes de dependências, arquivos binários ou logs que exigiriam resolução ou execução para existir. Bibliotecas de testes listadas nesta especificação podem constar do manifesto, mas não gere arquivos de testes.

Não alegue que compilou, iniciou, validou, testou ou obteve qualidade no SonarQube. Se algo não puder ser entregue ou se a especificação não determinar uma regra, registre a limitação honestamente na própria resposta. Não peça uma rodada posterior nem proponha correções futuras. A saída será preservada e avaliada exatamente como recebida.

---

## Prompt 08 Jogo de nave espacial no navegador

Produza uma implementação original da aplicação descrita abaixo em uma única resposta inicial. Use somente esta especificação. Não consulte web, repositórios, projetos conhecidos ou código de referência; não peça arquivos, links nem esclarecimentos. Não copie, transcreva ou traduza uma implementação existente.

Não use ferramentas, terminal, agentes, execução, compilação, instalação ou testes. Não gere suítes de testes, relatórios de testes ou resultados de análise. Não faça uma rodada separada de autorrevisão, autocorreção, patches ou alternativas; não apresente uma segunda versão. Esta restrição se refere ao procedimento observável e não exige expor nem eliminar raciocínio interno.

Mantenha exatamente as versões comprovadas do snapshot histórico humano que constam nesta especificação, incluindo linguagem, frameworks, bibliotecas e ferramentas quando declaradas. Não atualize, substitua ou acrescente dependências por conveniência. Versões não comprovadas devem permanecer identificadas como tal; qualquer escolha necessária é uma hipótese de compatibilidade, sem alegação de igualdade histórica.

Aplicação solicitada: Jogo de nave espacial no navegador.

### Ambiente e versões

Use HTML5, CSS e JavaScript executado diretamente no navegador, com Canvas 2D e áudio HTML5. Não use Node.js como backend, npm, bundler ou biblioteca externa de jogos. Não há versão ECMAScript, navegador ou ferramenta de build numericamente fixada na especificação histórica. Não invente uma versão histórica; declare requisitos de navegador como compatibilidade assumida. A área jogável é de 600 por 360 pixels. Forneça recursos visuais e sonoros próprios ou representações equivalentes sem consultar ou copiar arquivos externos.

### Funcionalidades e limites de escopo

1. Apresente tela de carregamento, área de jogo, pontuação visível e estado de fim de jogo com opção de reiniciar. O fundo espacial deve se mover durante a partida.

2. Permita controlar a nave do jogador com as quatro setas do teclado, dentro dos limites da área permitida, e disparar com a barra de espaço. A nave atua na faixa inferior do cenário; não transforme o jogo em navegação livre por um mapa.

3. Gere inimigos em ondas, com movimentação e disparos contra a nave. Quando uma onda for eliminada, comece outra. Projéteis e alvos devem interagir por colisão.

4. Ao destruir um inimigo, some dez pontos. Colisão fatal da nave com inimigo ou projétil adversário encerra a partida. Interrompa a ação nesse estado e permita reiniciar com pontuação e cenário reinicializados.

5. Inclua áudio de disparo, explosão, música de fundo e fim de jogo, com carregamento coerente com a inicialização. Se o navegador exigir interação para iniciar áudio, documente essa limitação sem alegar verificação em navegador.

6. Entregue os arquivos de entrada HTML, estilo, lógica e recursos indispensáveis de forma que não dependam de imagens ou sons que o usuário ainda teria de buscar. Se um recurso não puder ser representado na resposta, declare a ausência; não alegue que ele foi criado.

7. O escopo é apenas essa versão final descrita do jogo: não entregue etapas intermediárias, ranking remoto, contas de usuário, loja, multiplayer ou uma coleção de jogos.

### Formato da entrega e restrições

Entregue na mesma resposta uma árvore de arquivos e o conteúdo integral de cada arquivo necessário, identificado pelo caminho relativo, incluindo fontes, manifestos, configuração de exemplo sem segredos e recursos representáveis em texto. Use nomes neutros para o projeto. Não use reticências, trechos omitidos ou comentários que substituam funcionalidades. Não inclua nomes, links ou atribuições de projetos de referência.

Acrescente instruções curtas de preparação e uso destinadas ao pesquisador, sem executá-las. Liste as versões adotadas e diferencie valores comprovados das hipóteses. Não invente arquivos lock, hashes de dependências, arquivos binários ou logs que exigiriam resolução ou execução para existir. Bibliotecas de testes listadas nesta especificação podem constar do manifesto, mas não gere arquivos de testes.

Não alegue que compilou, iniciou, validou, testou ou obteve qualidade no SonarQube. Se algo não puder ser entregue ou se a especificação não determinar uma regra, registre a limitação honestamente na própria resposta. Não peça uma rodada posterior nem proponha correções futuras. A saída será preservada e avaliada exatamente como recebida.

---

## Prompt 09 Rastreamento de defeitos por equipe

Produza uma implementação original da aplicação descrita abaixo em uma única resposta inicial. Use somente esta especificação. Não consulte web, repositórios, projetos conhecidos ou código de referência; não peça arquivos, links nem esclarecimentos. Não copie, transcreva ou traduza uma implementação existente.

Não use ferramentas, terminal, agentes, execução, compilação, instalação ou testes. Não gere suítes de testes, relatórios de testes ou resultados de análise. Não faça uma rodada separada de autorrevisão, autocorreção, patches ou alternativas; não apresente uma segunda versão. Esta restrição se refere ao procedimento observável e não exige expor nem eliminar raciocínio interno.

Mantenha exatamente as versões comprovadas do snapshot histórico humano que constam nesta especificação, incluindo linguagem, frameworks, bibliotecas e ferramentas quando declaradas. Não atualize, substitua ou acrescente dependências por conveniência. Versões não comprovadas devem permanecer identificadas como tal; qualquer escolha necessária é uma hipótese de compatibilidade, sem alegação de igualdade histórica.

Aplicação solicitada: Rastreamento de defeitos por equipe.

### Ambiente e versões

Use Python com Django e SQLite, HTML/CSS e JavaScript. Adote Django 3.1.5 como alvo documental: há registro dessa versão no cabeçalho de criação do projeto, mas não um manifesto que prove a versão efetivamente instalada. Preserve React e ReactDOM da família 17 para as listagens, Babel Standalone da família 6 para JSX no navegador e Bootstrap 4.4.1. Os patches de React, ReactDOM e Babel não são comprovados, pois os endereços disponíveis fixam apenas as famílias. Python, pip e SQLite também não têm versão exata comprovada. Não acrescente pipeline npm/bundler sem necessidade e não declare hipóteses como fatos históricos.

### Funcionalidades e limites de escopo

1. Crie aplicação web para pessoas que acompanham defeitos de software em uma organização ou equipe. Ofereça cadastro com usuário, e-mail, senha e confirmação, login, logout e navegação autenticada.

2. Cada pessoa está associada a uma organização. A listagem principal mostra os defeitos da organização; outra listagem mostra apenas aqueles atribuídos à pessoa autenticada. Inclua contagens de defeitos da organização e de responsabilidade pessoal.

3. Defeito tem título, descrição com passos de reprodução, estimativa em horas, responsável técnico, categoria, severidade, organização e estado ativo/concluído. Categorias: funcional, desempenho, usabilidade, compatibilidade, segurança e outra. Severidades: baixa, média, alta e crítica.

4. Permita criar defeito, consultar detalhes, editar os dados e marcar como concluído. A conclusão altera o estado ativo. Ofereça formulários, mensagens de erro e navegação entre lista, detalhe, edição e criação.

5. As listagens por organização e por pessoa devem usar React. Páginas de detalhe, criação e edição usam JavaScript para consumir respostas JSON e atualizar os elementos necessários. Não implemente apenas uma API sem as páginas.

6. Disponibilize páginas sob /bugtracker/ e contratos de consulta buglistmessages, userbuglistmessages, bugdetailcontent/{id}, usermessagecount e completebug/{id}, além das páginas de lista, detalhe, criação, edição e autenticação. Os dados consultados devem respeitar a organização e o usuário do fluxo.

7. Inclua persistência e migrações para usuários, organizações e defeitos, e descreva uma forma local de preparar uma organização e suas associações sem criar um novo módulo de administração de organizações. Não inclua anexos, comentários, notificações, integrações de repositório ou planejamento de sprints.

### Formato da entrega e restrições

Entregue na mesma resposta uma árvore de arquivos e o conteúdo integral de cada arquivo necessário, identificado pelo caminho relativo, incluindo fontes, manifestos, configuração de exemplo sem segredos e recursos representáveis em texto. Use nomes neutros para o projeto. Não use reticências, trechos omitidos ou comentários que substituam funcionalidades. Não inclua nomes, links ou atribuições de projetos de referência.

Acrescente instruções curtas de preparação e uso destinadas ao pesquisador, sem executá-las. Liste as versões adotadas e diferencie valores comprovados das hipóteses. Não invente arquivos lock, hashes de dependências, arquivos binários ou logs que exigiriam resolução ou execução para existir. Bibliotecas de testes listadas nesta especificação podem constar do manifesto, mas não gere arquivos de testes.

Não alegue que compilou, iniciou, validou, testou ou obteve qualidade no SonarQube. Se algo não puder ser entregue ou se a especificação não determinar uma regra, registre a limitação honestamente na própria resposta. Não peça uma rodada posterior nem proponha correções futuras. A saída será preservada e avaliada exatamente como recebida.

---

## Prompt 10 API de pesquisas de satisfação e NPS

Produza uma implementação original da aplicação descrita abaixo em uma única resposta inicial. Use somente esta especificação. Não consulte web, repositórios, projetos conhecidos ou código de referência; não peça arquivos, links nem esclarecimentos. Não copie, transcreva ou traduza uma implementação existente.

Não use ferramentas, terminal, agentes, execução, compilação, instalação ou testes. Não gere suítes de testes, relatórios de testes ou resultados de análise. Não faça uma rodada separada de autorrevisão, autocorreção, patches ou alternativas; não apresente uma segunda versão. Esta restrição se refere ao procedimento observável e não exige expor nem eliminar raciocínio interno.

Mantenha exatamente as versões comprovadas do snapshot histórico humano que constam nesta especificação, incluindo linguagem, frameworks, bibliotecas e ferramentas quando declaradas. Não atualize, substitua ou acrescente dependências por conveniência. Versões não comprovadas devem permanecer identificadas como tal; qualquer escolha necessária é uma hipótese de compatibilidade, sem alegação de igualdade histórica.

Aplicação solicitada: API de pesquisas de satisfação e NPS.

### Ambiente e versões

Use TypeScript no backend Node.js, Express, TypeORM e SQLite. Mantenha exatamente as versões resolvidas listadas abaixo. Preserve target es5, module commonjs, strict false, strictPropertyInitialization false, esModuleInterop true, experimentalDecorators true, emitDecoratorMetadata true, skipLibCheck true e forceConsistentCasingInFileNames true. Node.js, Yarn e a engine SQLite não têm versões exatas comprovadas; sqlite3 5.0.2 é a versão do pacote e não deve ser tratada como versão do banco. As dependências de testes permanecem apenas declaradas, sem criação ou execução de testes.

Produção: express 4.17.1; express-async-errors 3.1.1; handlebars 4.7.7; nodemailer 6.4.18; reflect-metadata 0.1.13; sqlite3 5.0.2; typeorm 0.2.31; uuid 8.3.2; yup 0.32.9.

Desenvolvimento: @types/express 4.17.11; @types/jest 26.0.20; @types/nodemailer 6.4.0; @types/supertest 2.0.10; @types/uuid 8.3.0; jest 26.6.3; supertest 6.1.3; ts-jest 26.5.1; ts-node-dev 1.1.1; typescript 4.1.5.

### Funcionalidades e limites de escopo

1. Ofereça API JSON com cadastro de usuário por nome e e-mail. Rejeite e-mail duplicado; cadastro bem-sucedido retorna HTTP 201 e duplicidade retorna 400. Não acrescente autenticação ou emissão de tokens.

2. Permita criar pesquisa com título e descrição e listar pesquisas cadastradas. A criação retorna HTTP 201 e identificador. Persista usuários, pesquisas e associações de convite/resposta em SQLite.

3. Permita enviar convite por e-mail para usuário existente responder a pesquisa existente. O convite deve relacionar usuário e pesquisa, ter identificador próprio e guardar resposta inicialmente ausente. Envio deve usar Nodemailer e template HTML com Handlebars; permita serviço de e-mail de desenvolvimento configurável.

4. O e-mail contém opções de nota de zero a dez, com link que identifique o convite. Ao abrir o link, registre o valor na associação correspondente. Diferencie convite pendente de respondido e reporte identificador inválido sem inventar uma resposta.

5. Calcule NPS por pesquisa somente com convites respondidos: detratores de zero a seis, neutros de sete a oito e promotores de nove a dez. Aplique 100 vezes a diferença entre quantidades de promotores e detratores dividida pelo total de respostas. Retorne as contagens, total e NPS com até duas casas decimais.

6. Contratos HTTP: POST /users; POST e GET /surveys; POST /sendMail com email e survey_id; GET /answers/{value} com identificador do convite no parâmetro u; GET /nps/{survey_id}. Porta local de referência: 3333.

7. Inclua migrações, configuração do banco, variáveis de e-mail e instruções de preparação. O comportamento para pesquisa sem respostas não está suficientemente especificado: declare a convenção adotada, sem apresentá-la como histórica. Não acrescente dashboard, importação em massa, agendamento ou análise estatística além do NPS.

### Formato da entrega e restrições

Entregue na mesma resposta uma árvore de arquivos e o conteúdo integral de cada arquivo necessário, identificado pelo caminho relativo, incluindo fontes, manifestos, configuração de exemplo sem segredos e recursos representáveis em texto. Use nomes neutros para o projeto. Não use reticências, trechos omitidos ou comentários que substituam funcionalidades. Não inclua nomes, links ou atribuições de projetos de referência.

Acrescente instruções curtas de preparação e uso destinadas ao pesquisador, sem executá-las. Liste as versões adotadas e diferencie valores comprovados das hipóteses. Não invente arquivos lock, hashes de dependências, arquivos binários ou logs que exigiriam resolução ou execução para existir. Bibliotecas de testes listadas nesta especificação podem constar do manifesto, mas não gere arquivos de testes.

Não alegue que compilou, iniciou, validou, testou ou obteve qualidade no SonarQube. Se algo não puder ser entregue ou se a especificação não determinar uma regra, registre a limitação honestamente na própria resposta. Não peça uma rodada posterior nem proponha correções futuras. A saída será preservada e avaliada exatamente como recebida.
