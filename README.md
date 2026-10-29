# Códigos do TCC

<<<<<<< HEAD
Este repositório reúne as implementações de código geradas por Inteligência Artificial utilizadas no Trabalho de Conclusão de Curso (TCC):
=======
Este repositório reúne os códigos utilizados na etapa experimental preliminar do Trabalho de Conclusão de Curso (TCC), destinada a comparar implementações de referência provenientes de um repositório público com implementações geradas por Inteligência Artificial. A autoria exclusivamente humana dos códigos de referência não pôde ser comprovada.
>>>>>>> de289c77085396c0fd2932fa464c2a3dc0ff8794

**Análise Comparativa da Qualidade de Software e Dívida Técnica em Códigos Gerados por LLMs e Desenvolvidos por Humanos.**

O estudo tem como objetivo comparar características de qualidade interna, manutenibilidade, dívida técnica e comportamento funcional entre implementações geradas por modelos de linguagem de grande porte (LLMs) e implementações de referência provenientes de repositórios públicos.

A análise utiliza o SonarQube para observar características estáticas do código e avaliações funcionais para investigar diferenças de comportamento.

**Este repositório contém somente os códigos do grupo de IA.** Os projetos públicos utilizados como referências comparativas são identificados e creditados neste documento, mas não integram a árvore atual do repositório.

## 1. Estrutura do repositório

```text
Codigos-TCC/
├── IA/
│   ├── CSharp/
│   ├── java/
│   ├── JavaScript/
│   ├── Python/
│   └── TypeScript/
├── docs/
│   ├── manifesto-rastreabilidade.md
│   └── PROMPTS.md
├── tests/
├── .gitignore
└── README.md
```

### Pasta `IA/`

<<<<<<< HEAD
Contém implementações produzidas por IA, organizadas por linguagem de programação.
=======
Contém as implementações de referência provenientes do repositório TheAlgorithms/JavaScript. Embora esses códigos estejam disponíveis publicamente, não foi possível comprovar sua autoria exclusivamente humana. O nome da pasta `humano/` foi preservado por compatibilidade com a organização original dos arquivos do estudo piloto.
Esses arquivos foram adaptados a partir de implementações disponíveis no
repositório [TheAlgorithms/JavaScript](https://github.com/TheAlgorithms/JavaScript).
Os caminhos e os créditos específicos estão indicados na seção
[Atribuição e créditos](#atribuição-e-créditos).
>>>>>>> de289c77085396c0fd2932fa464c2a3dc0ff8794

Cada aplicação foi solicitada a partir de uma especificação funcional e tecnológica, com restrições de geração estabelecidas para a pesquisa.

### Pasta `docs/`

Contém documentação complementar dos experimentos:

- [`PROMPTS.md`](docs/PROMPTS.md): registro dos prompts utilizados na pesquisa.
- [`manifesto-rastreabilidade.md`](docs/manifesto-rastreabilidade.md): informações de rastreabilidade e hashes documentados na etapa preliminar.

Esses documentos foram iniciados durante os experimentos preliminares e podem não abranger integralmente a etapa ampliada.

### Pasta `tests/`

Contém materiais de avaliação funcional da etapa preliminar.

Como os códigos humanos foram retirados da árvore atual, testes que dependem dos caminhos anteriormente utilizados podem exigir acesso às referências externas para reprodução.

## 2. Etapa preliminar — Algoritmos JavaScript

A pesquisa começou com três algoritmos clássicos implementados em JavaScript.

| Identificação | Algoritmo | Grupo |
|---|---|---|
| H1 | Busca binária | Referência pública |
| IA1 | Busca binária | IA |
| H2 | Merge Sort | Referência pública |
| IA2 | Merge Sort | IA |
| H3 | Dijkstra | Referência pública |
| IA3 | Dijkstra | IA |

### Referências originais

As três implementações de referência foram selecionadas do projeto [TheAlgorithms/JavaScript](https://github.com/TheAlgorithms/JavaScript), mantido por seus colaboradores.

**Commit utilizado:** `5c39e87a9a31f279c60f830ad74a845e4788a517`

| Algoritmo | Código de referência no commit selecionado |
|---|---|
| Busca binária | [Search/BinarySearch.js](https://github.com/TheAlgorithms/JavaScript/blob/5c39e87a9a31f279c60f830ad74a845e4788a517/Search/BinarySearch.js) |
| Merge Sort | [Sorts/MergeSort.js](https://github.com/TheAlgorithms/JavaScript/blob/5c39e87a9a31f279c60f830ad74a845e4788a517/Sorts/MergeSort.js) |
| Dijkstra | [Graphs/Dijkstra.js](https://github.com/TheAlgorithms/JavaScript/blob/5c39e87a9a31f279c60f830ad74a845e4788a517/Graphs/Dijkstra.js) |

**Licença da referência:** GNU General Public License v3.0 (GPL-3.0).

[Consultar a licença original](https://github.com/TheAlgorithms/JavaScript/blob/5c39e87a9a31f279c60f830ad74a845e4788a517/LICENSE).

Acesso anteriormente registrado: 23 de outubro de 2026.

### Avaliação preliminar

A avaliação funcional preliminar utilizou o recurso nativo `node:test`, com registros associados a:

```bash
node --test tests/algorithms.test.js
```

Foram documentadas 29 execuções de teste, abrangendo diferentes casos de entrada.

Entre as situações consideradas estavam listas vazias, valores ausentes, valores duplicados, vértices inacessíveis e caminhos com custo superior a 10.000.

Foi identificada uma diferença funcional na implementação de referência H3 (Dijkstra), relacionada ao uso de 10.000 como valor inicial das distâncias.

A implementação original foi preservada, e o comportamento foi registrado como resultado experimental.

Esses resultados pertencem à etapa preliminar e não representam novas execuções após a reorganização do repositório.

## 3. Etapa ampliada — Aplicações completas

A pesquisa foi ampliada para incluir dez aplicações de software, abrangendo diferentes linguagens, frameworks, finalidades e níveis de complexidade.

Foram selecionados projetos de referência disponíveis publicamente. A partir de suas características funcionais e tecnológicas documentadas, foram elaboradas especificações para solicitar implementações independentes por IA.

### 3.1. Relação entre implementações de IA e referências públicas

| Nº | Implementação gerada por IA | Linguagem | Repositório público de referência |
|---|---|---|---|
| 1 | Flask Blog | Python | [CoreyMSchafer/code_snippets](https://github.com/CoreyMSchafer/code_snippets) |
| 2 | HabitLog | Python | [gonvalhector/tasksdaily](https://github.com/gonvalhector/tasksdaily) |
| 3 | TeamBugTracker | Python | [ThoshHub/CS50-Capstone](https://github.com/ThoshHub/CS50-Capstone) |
| 4 | Commerce API | Java | [acenelio/workshop-springboot2-jpa](https://github.com/acenelio/workshop-springboot2-jpa) |
| 5 | Turnboard | Java | [Raruto/IlMagnifico](https://github.com/Raruto/IlMagnifico) |
| 6 | SalesManagement | C# | [acenelio/workshop-asp-net-core-mvc](https://github.com/acenelio/workshop-asp-net-core-mvc) |
| 7 | CircuitWorkbench | C# | [snowl/CircuitSimulator](https://github.com/snowl/CircuitSimulator) |
| 8 | Movie Rental API | JavaScript | [mosh-hamedani/vidly-api-node](https://github.com/mosh-hamedani/vidly-api-node) |
| 9 | SpaceArcade | JavaScript | [straker/galaxian-canvas-game](https://github.com/straker/galaxian-canvas-game) |
| 10 | Survey NPS API | TypeScript | [rocketseat-education/nlw-04-nodejs](https://github.com/rocketseat-education/nlw-04-nodejs) |

Os códigos desses repositórios foram selecionados para compor o grupo de referência da pesquisa. A classificação como referência humana não significa que a autoria exclusivamente humana de todos os arquivos tenha sido comprovada.

### 3.2. Commits históricos utilizados

Para evitar que alterações posteriores nos repositórios originais modifiquem o objeto de comparação, foram registrados os identificadores dos commits selecionados.

**1. Flask Blog — Corey Schafer**

- Repositório: `CoreyMSchafer/code_snippets`
- Commit: `b6cff5b162e6d972c385f4554d010cdc17c64875`
- Diretório utilizado: `Python/Flask_Blog/12-Error-Pages`
- [Acessar o snapshot histórico](https://github.com/CoreyMSchafer/code_snippets/tree/b6cff5b162e6d972c385f4554d010cdc17c64875/Python/Flask_Blog/12-Error-Pages)

**2. TasksDaily — Hector Gonzalez**

- Repositório: `gonvalhector/tasksdaily`
- Commit: `d2b160bf97b8b370f4913e4d51f058ee58922ee3`
- [Acessar o snapshot histórico](https://github.com/gonvalhector/tasksdaily/tree/d2b160bf97b8b370f4913e4d51f058ee58922ee3)

**3. CS50 Capstone — Bug Tracker**

- Repositório: `ThoshHub/CS50-Capstone`
- Commit: `1ad41a8476d041ff3f525a587fbc8567126f456b`
- [Acessar o snapshot histórico](https://github.com/ThoshHub/CS50-Capstone/tree/1ad41a8476d041ff3f525a587fbc8567126f456b)

**4. Workshop Spring Boot/JPA — Nélio Alves**

- Repositório: `acenelio/workshop-springboot2-jpa`
- Commit: `4acdad5169c6453e9318d8a760e8880c1e5588f3`
- [Acessar o snapshot histórico](https://github.com/acenelio/workshop-springboot2-jpa/tree/4acdad5169c6453e9318d8a760e8880c1e5588f3)

**5. IlMagnifico — Raruto e colaboradores**

- Repositório: `Raruto/IlMagnifico`
- Commit: `4feb017bba928a0adc8159d3824e2839b6bddcbc`
- [Acessar o snapshot histórico](https://github.com/Raruto/IlMagnifico/tree/4feb017bba928a0adc8159d3824e2839b6bddcbc)

**6. Workshop ASP.NET Core MVC — Nélio Alves**

- Repositório: `acenelio/workshop-asp-net-core-mvc`
- Commit: `c3ee0ac621210dd0853f974bab4943907947f525`
- [Acessar o snapshot histórico](https://github.com/acenelio/workshop-asp-net-core-mvc/tree/c3ee0ac621210dd0853f974bab4943907947f525)

**7. CircuitSimulator — David Diaz**

- Repositório: `snowl/CircuitSimulator`
- Commit: `3113cd37b1684b15da4d011223b4ccadaed36db7`
- [Acessar o snapshot histórico](https://github.com/snowl/CircuitSimulator/tree/3113cd37b1684b15da4d011223b4ccadaed36db7)

**8. Vidly API — Mosh Hamedani**

- Repositório: `mosh-hamedani/vidly-api-node`
- Commit: `73aaf61f3aeb9b7462c1058cf86b4f36ea69d83c`
- [Acessar o snapshot histórico](https://github.com/mosh-hamedani/vidly-api-node/tree/73aaf61f3aeb9b7462c1058cf86b4f36ea69d83c)

**9. Galaxian — Steven Lambert**

- Repositório: `straker/galaxian-canvas-game`
- Commit: `2ae4c3e5a3bf37a9fba24491ec87abc192a069c4`
- Diretório utilizado: `part5`
- [Acessar o snapshot histórico](https://github.com/straker/galaxian-canvas-game/tree/2ae4c3e5a3bf37a9fba24491ec87abc192a069c4/part5)

**10. NPS-NLW — Rocketseat**

- Repositório: `rocketseat-education/nlw-04-nodejs`
- Commit: `d6ba81b93d7479045651554247b31db084cf9116`
- [Acessar o snapshot histórico](https://github.com/rocketseat-education/nlw-04-nodejs/tree/d6ba81b93d7479045651554247b31db084cf9116)

Os commits listados correspondem aos registros de seleção dos projetos. A associação entre implementação gerada e referência pública não implica reprodução literal de código nem comprova equivalência funcional.

## 4. Procedimento de geração por IA

As implementações foram solicitadas com prompts estruturados, contendo requisitos funcionais, restrições tecnológicas e regras para preservar a independência da geração.

O procedimento determinou:

1. Gerar código exclusivamente a partir da especificação fornecida.
2. Não consultar repositórios públicos durante a geração.
3. Não copiar, transcrever ou traduzir implementações existentes.
4. Preservar versões de dependências comprovadas.
5. Não substituir bibliotecas ou frameworks por conveniência.
6. Entregar uma única implementação inicial.
7. Não realizar execução, compilação ou testes durante a geração.
8. Não produzir correções ou versões alternativas na entrega inicial.
9. Identificar hipóteses de compatibilidade e limitações não determinadas.
10. Preservar a implementação original para avaliação posterior.

O registro dos prompts permite documentar as condições de geração das aplicações.

## 5. Preservação e rastreabilidade

As implementações produzidas por IA constituem o material experimental deste repositório.

Para preservar a integridade metodológica, problemas identificados posteriormente devem ser diferenciados conforme sua origem.

| Tipo de ocorrência | Interpretação |
|---|---|
<<<<<<< HEAD
| Falha de ambiente | Ferramenta, SDK, runtime ou banco ausente |
| Falha de compatibilidade | Incompatibilidade entre ferramentas e dependências |
| Erro de montagem | Arquivo copiado incorretamente ou no diretório errado |
| Defeito de implementação | Problema presente no código gerado originalmente |
| Falha funcional | Comportamento observado diferente do requisito |
=======
| H1 | Busca binária — referência |
| IA1 | Busca binária IA |
| H2 | Merge Sort — referência |
| IA2 | Merge Sort IA |
| H3 | Dijkstra — referência |
| IA3 | Dijkstra IA |
>>>>>>> de289c77085396c0fd2932fa464c2a3dc0ff8794

As informações de rastreabilidade devem permitir identificar os arquivos originais, eventuais intervenções posteriores e os resultados efetivamente obtidos.

<<<<<<< HEAD
Não devem ser atribuídos hashes ou resultados experimentais a arquivos sem a correspondente coleta ou verificação.
=======
No estado atual, são executados 12 cenários lógicos, totalizando 29 execuções individuais. A implementação de referência H3 (Dijkstra) apresenta falha no teste referente a caminho maior que `10.000`, pois inicializa as distâncias com esse valor e retorna `10.000` em vez de `12.001`. Esse resultado registra uma diferença funcional entre as implementações; o algoritmo original não foi alterado para fazer o teste passar.
>>>>>>> de289c77085396c0fd2932fa464c2a3dc0ff8794

## 6. Análise estática com SonarQube

A análise de qualidade utiliza o SonarQube, considerando métricas e problemas relacionados a:

- Manutenibilidade e code smells.
- Confiabilidade.
- Segurança.
- Complexidade.
- Duplicação de código.
- Esforço estimado de remediação.
- Dívida técnica.

Cada aplicação deve possuir escopo de análise apropriado à linguagem e ao projeto, evitando a inclusão acidental de arquivos de outros experimentos.

A existência de um projeto neste repositório não significa que sua análise tenha sido concluída.

Os resultados devem ser interpretados considerando diferenças de tamanho, linguagem, dependências e características funcionais.

## 7. Avaliação funcional

A avaliação funcional busca identificar diferenças de comportamento entre as implementações e os requisitos estabelecidos.

Na etapa preliminar, foram utilizados testes de algoritmos com casos lógicos equivalentes.

<<<<<<< HEAD
Na etapa ampliada, os critérios funcionais devem considerar as características de cada aplicação, como:
=======
Os links acima apontam para o commit `5c39e87a9a31f279c60f830ad74a845e4788a517` do repositório TheAlgorithms/JavaScript, identificado posteriormente para fins de verificação. Não foi possível comprovar que essa revisão corresponde exatamente à versão consultada durante a coleta original. Na comparação realizada, os arquivos H2 e H3 apresentaram conteúdo idêntico ao dessa revisão, enquanto H1 preservou as funções dos algoritmos, mas apresentou alterações em elementos externos a elas.
O repositório de origem informa que seus conteúdos são licenciados sob a **GNU General Public License v3.0
(GPL-3.0)**. Consulte o [arquivo de licença do projeto de
origem](https://github.com/TheAlgorithms/JavaScript/blob/5c39e87a9a31f279c60f830ad74a845e4788a517/LICENSE)
e mantenha esta atribuição ao redistribuir ou modificar os trechos derivados.
>>>>>>> de289c77085396c0fd2932fa464c2a3dc0ff8794

- Operações de API.
- Persistência de dados.
- Regras de negócio.
- Compilação e inicialização.
- Interações de interface.
- Comportamento de algoritmos e simulações.

Os testes automatizados cobrem cenários específicos e não garantem a ausência de todos os defeitos.

<<<<<<< HEAD
Quando uma aplicação não puder ser iniciada, o impedimento deve ser registrado sem inventar resultados para testes não realizados.

## 8. Direitos autorais e reconhecimento das fontes

Os repositórios públicos selecionados permanecem sob responsabilidade de seus respectivos autores e colaboradores.

As referências foram utilizadas para estabelecer o conjunto comparativo da pesquisa e orientar a elaboração de especificações funcionais e tecnológicas.

As implementações geradas por IA devem ser identificadas separadamente das implementações públicas de referência.

### 8.1. Créditos

Os autores e mantenedores dos projetos externos recebem os créditos correspondentes nas seções 2 e 3 deste documento, incluindo links para os respectivos repositórios e commits.

A manutenção desses registros permite identificar as fontes selecionadas mesmo quando seus códigos não estão armazenados neste repositório.

### 8.2. Licenças

Cada projeto público possui suas próprias condições de licenciamento.

A licença GPL-3.0 do TheAlgorithms/JavaScript foi indicada na seção preliminar. Outros projetos podem possuir licenças diferentes ou condições específicas de utilização.

Antes de redistribuir código ou arquivos derivados de uma referência pública, devem ser observadas as condições da licença correspondente, incluindo requisitos de atribuição, preservação de avisos de direitos autorais e eventuais obrigações adicionais.

A inclusão de créditos neste README não substitui o cumprimento dessas licenças.

### 8.3. Integridade acadêmica

As fontes são identificadas explicitamente para documentar a procedência dos projetos de referência.

A utilização de especificações elaboradas a partir de projetos existentes deve ser descrita na metodologia da pesquisa.

As implementações de IA não devem ser apresentadas como códigos escritos pelos autores dos projetos de referência, e os códigos de referência não devem ser apresentados como produções originais da IA.

A comparação entre os grupos deve manter clara a distinção entre fonte de referência, especificação fornecida ao modelo e implementação gerada.

## 9. Objetivo do repositório

O objetivo deste repositório é preservar e organizar as implementações produzidas por IA para a pesquisa de qualidade de software e dívida técnica.

O estudo não pretende demonstrar antecipadamente que uma abordagem seja universalmente superior à outra.

Busca-se produzir uma comparação fundamentada em evidências observáveis, considerando métricas estáticas, resultados funcionais, características das linguagens, diversidade dos projetos e limitações metodológicas.

**Este repositório corresponde ao grupo experimental de IA. Os projetos de referência utilizados na comparação são creditados e identificados pelos links históricos apresentados neste documento.**
=======
O objetivo não é estabelecer qual abordagem é universalmente melhor, mas
fornecer uma base reproduzível para observar diferenças de implementação,
legibilidade, complexidade e resultados de análise estática entre os dois
grupos de código.

## Convenção de cálculo dos hashes SHA-256

Os hashes SHA-256 registrados neste manifesto correspondem às cópias preservadas dos arquivos JavaScript com terminações de linha CRLF, utilizadas no ambiente Windows durante o registro dos artefatos.

Os arquivos recuperados diretamente do repositório GitHub podem apresentar terminações de linha LF. Essa diferença na representação dos arquivos altera os valores dos hashes SHA-256, mesmo quando não existem alterações nas instruções dos algoritmos.

Para reproduzir os hashes publicados, é necessário utilizar a mesma representação em bytes dos arquivos preservados, incluindo as terminações de linha CRLF.

Os hashes documentam a integridade das amostras preservadas, mas não comprovam, isoladamente, qual revisão do repositório TheAlgorithms/JavaScript foi consultada durante a coleta original.
>>>>>>> de289c77085396c0fd2932fa464c2a3dc0ff8794
