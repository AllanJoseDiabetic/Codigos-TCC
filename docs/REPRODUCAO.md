# Estado de reprodução e inventário de evidências

## Escopo da auditoria histórica

Conferência documental realizada em 10 de novembro de 2026 sobre a árvore de `main` no commit [`b79adc0658e684c503434ab91aad946cc2b14f4f`](https://github.com/AllanJoseDiabetic/Codigos-TCC/tree/b79adc0658e684c503434ab91aad946cc2b14f4f). Esta seção é um retrato do commit examinado naquela ocasião, não uma descrição da árvore atual. A listagem recursiva retornou 7.720 entradas, sem truncamento. A verificação abrangeu os arquivos versionados dessa árvore, não outras branches, histórico completo, anexos, releases, artefatos de execução externos ou Google Drive.

Foram lidos o README, os prompts, o manifesto, a suíte preliminar e os 14 JSON encontrados. Nenhum experimento foi executado, nenhum resultado científico foi recalculado e nenhum hash experimental foi substituído. Os identificadores de commits são os já registrados ou retornados pelo GitHub nesta conferência.

## Testes preliminares

[`tests/algorithms.test.js`](../tests/algorithms.test.js) usa `fs.readFileSync` para carregar os arquivos abaixo antes de registrar os casos de teste. Todos os seis caminhos usados pelo carregador estão ausentes na árvore auditada:

| Identificador | Caminho usado pelo teste e pelo manifesto histórico | Situação / localização atual |
|---|---|---|
| H1 | `humano/JavaScript/BinarySearch/BinarySearch.js` | Ausente |
| H2 | `humano/JavaScript/MergeSort/MergeSort.js` | Ausente |
| H3 | `humano/JavaScript/Dijkstra/Dijkstra.js` | Ausente |
| IA1 | `IA/JavaScript/Search/BinarySearch/index.js` | `IA/JavaScript/Preliminar/Search/BinarySearch/index.js` |
| IA2 | `IA/JavaScript/MergeSort/MergeSort.js` | `IA/JavaScript/Preliminar/MergeSort/MergeSort.js` |
| IA3 | `IA/JavaScript/Dijkstra/DijkstraIA.js` | `IA/JavaScript/Preliminar/Dijkstra/DijkstraIA.js` |

O primeiro carregamento é o de H1. Assim, a leitura de código permite identificar que a ausência desse arquivo impede chegar aos cenários; isso não é uma nova medição experimental. A suíte não foi executada nesta revisão. O comando histórico `node --test tests/algorithms.test.js` não reproduz automaticamente a comparação com o conteúdo atual.

O código define 12 cenários lógicos e 29 testes individuais (5 × 3 de busca binária, 4 × 2 de Merge Sort e 3 × 2 de Dijkstra). O carregador adapta exports em memória para H2/H3 e usa `vm`; os caminhos públicos originais não substituem automaticamente as amostras locais. As variáveis `human*` e os identificadores H1/H2/H3 são nomenclatura histórica do grupo de referência, sem comprovação de autoria exclusivamente humana.

Para uma futura reprodução comparativa, será necessário localizar as amostras de referência efetivamente utilizadas, conferir sua procedência e representação em bytes, documentar eventuais adaptações do carregador e registrar o ambiente de execução. Isso exige trabalho separado: esta revisão não restaura referências nem altera testes ou códigos de IA. A revisão de TheAlgorithms indicada no README foi identificada posteriormente; não está comprovado que seja a revisão consultada na coleta original.

## Prompts e rastreabilidade

- [`PROMPTS.md`](PROMPTS.md) contém dez prompts da etapa ampliada; não contém os prompts dos três algoritmos preliminares. A ordem é explicitada na tabela introdutória. Os textos históricos foram mantidos integralmente.
- [`manifesto-rastreabilidade.md`](manifesto-rastreabilidade.md) contém seis entradas de algoritmos e cinco cópias modificadas da etapa preliminar. Os caminhos e os onze hashes históricos foram preservados. As três cópias modificadas de IA também estão sob `IA/JavaScript/Preliminar/`; as duas de referência estão ausentes.
- O manifesto não traz hashes das dez aplicações. Os prompts registram requisitos e restrições, mas não substituem registros de geração, ambiente, execução e coleta de métricas.

## Atualização posterior — resultados experimentais publicados

Após a auditoria histórica, foram publicados em [`resultados-sonarqube/`](resultados-sonarqube/) os relatórios das dez implementações de referência e das dez implementações de IA. O conjunto contém métricas, apontamentos identificados e resultados de Quality Gate por projeto, em exportações CSV e JSON, além da planilha consolidada [`Comparacao_SonarQube_10_pares.xlsx`](resultados-sonarqube/Comparacao_SonarQube_10_pares.xlsx).

Também foram publicados em [`resultados-teste-funcionais/`](resultados-teste-funcionais/) os resultados dos testes funcionais dos dez pares, nas pastas `par01_vidly` a `par10_nps`, com aprovações e reprovações registradas. O conjunto totaliza 672 resultados individuais, sendo 336 por grupo. A numeração das pastas é a numeração original da execução; a correspondência com os pares das tabelas deve ser estabelecida pelos nomes dos projetos, e não pela posição numérica. A tabela equivalente está no [README](../README.md#7-avaliação-funcional).

Esses arquivos tornam disponíveis as evidências experimentais e permitem conferir os registros e recalcular indicadores a partir das exportações. Isso não significa que os experimentos tenham sido reproduzidos nesta atualização, nem que todas as condições originais tenham sido recuperadas. A reprodução exata da execução continua limitada pelas versões de ferramentas e dependências, configurações, ambientes, escopos de análise, amostras e procedimentos que estavam disponíveis na coleta.

## CSV/JSON na auditoria histórica

**Na árvore do commit auditado historicamente, nenhum arquivo `.csv` foi encontrado. Foram encontrados 14 arquivos `.json`, listados integralmente abaixo. Nenhum deles continha relatório de análise estática ou resultado de teste funcional da etapa ampliada.** A classificação foi feita pela leitura do conteúdo, não apenas pelo nome.

| Arquivo versionado | Conteúdo / finalidade |
|---|---|
| [`IA/CSharp/SalesManagement/appsettings.json`](../IA/CSharp/SalesManagement/appsettings.json) | Configuração da aplicação (.NET) |
| [`IA/CSharp/SalesManagement/bin/Debug/netcoreapp2.1/SalesManagement.deps.json`](../IA/CSharp/SalesManagement/bin/Debug/netcoreapp2.1/SalesManagement.deps.json) | Manifesto de dependências de execução (.NET) |
| [`IA/CSharp/SalesManagement/bin/Debug/netcoreapp2.1/SalesManagement.runtimeconfig.dev.json`](../IA/CSharp/SalesManagement/bin/Debug/netcoreapp2.1/SalesManagement.runtimeconfig.dev.json) | Configuração de desenvolvimento do runtime (.NET) |
| [`IA/CSharp/SalesManagement/bin/Debug/netcoreapp2.1/SalesManagement.runtimeconfig.json`](../IA/CSharp/SalesManagement/bin/Debug/netcoreapp2.1/SalesManagement.runtimeconfig.json) | Configuração do runtime (.NET) |
| [`IA/CSharp/SalesManagement/obj/SalesManagement.csproj.nuget.dgspec.json`](../IA/CSharp/SalesManagement/obj/SalesManagement.csproj.nuget.dgspec.json) | Especificação de restauração NuGet |
| [`IA/CSharp/SalesManagement/obj/project.assets.json`](../IA/CSharp/SalesManagement/obj/project.assets.json) | Resolução de dependências NuGet |
| [`IA/JavaScript/movie-rental-api/config/custom-environment-variables.json`](../IA/JavaScript/movie-rental-api/config/custom-environment-variables.json) | Mapeamento de variáveis de ambiente |
| [`IA/JavaScript/movie-rental-api/config/default.json`](../IA/JavaScript/movie-rental-api/config/default.json) | Configuração da aplicação |
| [`IA/JavaScript/movie-rental-api/package.json`](../IA/JavaScript/movie-rental-api/package.json) | Manifesto de pacote e dependências Node.js |
| [`IA/Python/habitlog/.venv38/Lib/site-packages/dj_database_url-0.5.0.dist-info/metadata.json`](../IA/Python/habitlog/.venv38/Lib/site-packages/dj_database_url-0.5.0.dist-info/metadata.json) | Metadados de pacote Python |
| [`IA/Python/habitlog/.venv38/Lib/site-packages/django_heroku-0.3.1.dist-info/metadata.json`](../IA/Python/habitlog/.venv38/Lib/site-packages/django_heroku-0.3.1.dist-info/metadata.json) | Metadados de pacote Python |
| [`IA/Python/habitlog/.venv38/Lib/site-packages/pytz-2020.4.dist-info/metadata.json`](../IA/Python/habitlog/.venv38/Lib/site-packages/pytz-2020.4.dist-info/metadata.json) | Metadados de pacote Python |
| [`IA/TypeScript/survey-nps-api/package.json`](../IA/TypeScript/survey-nps-api/package.json) | Manifesto de pacote e dependências Node.js |
| [`IA/TypeScript/survey-nps-api/tsconfig.json`](../IA/TypeScript/survey-nps-api/tsconfig.json) | Configuração do compilador TypeScript |

Não havia CSV/JSON de resultados na raiz, em `docs/`, em `tests/` ou no restante da árvore auditada. Essa afirmação permanece válida apenas para o snapshot histórico; os resultados foram publicados posteriormente nos caminhos indicados acima. Arquivos de build e dependências não demonstram, por si só, sucesso de compilação, validação funcional ou qualidade no SonarQube.

Na auditoria histórica, permaneciam pendentes os relatórios coletados, sua associação às amostras analisadas, escopos de análise, versões efetivamente utilizadas e procedimentos/resultados funcionais. A publicação posterior fornece esses artefatos registrados, mas não preenche automaticamente todas essas lacunas metodológicas nem autoriza afirmar que as condições experimentais foram integralmente recuperadas.

## Integridade e segurança dos artefatos publicados

Foi feita uma verificação documental dos arquivos publicados, sem alteração ou exclusão. Não foram identificados tokens, credenciais, chaves privadas ou arquivos de configuração de segredo. Os seguintes itens são potencialmente desnecessários ou merecem revisão antes de uma distribuição mais ampla:

- logs de execução e instalação em todas as pastas funcionais, por poderem revelar detalhes locais do ambiente;
- [`par04_spring/execucao_20261029_201543/ambiente.txt`](resultados-teste-funcionais/par04_spring/execucao_20261029_201543/ambiente.txt) e os logs `java_*_stdout.log` do mesmo diretório, que contêm nome de usuário, nome de máquina e caminhos locais do ambiente de execução;
- [`par10_nps/execucao_20261029_225407/database_humano.sqlite`](resultados-teste-funcionais/par10_nps/execucao_20261029_225407/database_humano.sqlite) e [`database_IA.sqlite`](resultados-teste-funcionais/par10_nps/execucao_20261029_225407/database_IA.sqlite), que contêm dados de teste sintéticos, inclusive nomes e endereços `example.invalid`, e devem ser confirmados como adequados à publicação;
- binários de build, arquivos `WiredTiger*`/`.wt`, classes compiladas e o arquivo `diagnostico_origem.zip` de `par08_galaxian`, por ampliarem o conteúdo publicado além dos registros tabulares e dos logs necessários à conferência.

Esta lista é para revisão do responsável. Nenhum desses arquivos foi removido, renomeado ou modificado.

## Conferência das referências externas

Os 25 URLs distintos de GitHub preservados no README foram consultados com sucesso em 10 de novembro de 2026: 11 repositórios de origem, 10 snapshots históricos das aplicações (incluindo os diretórios de Flask Blog e Galaxian), 3 arquivos de algoritmos e a licença de TheAlgorithms/JavaScript.

Os créditos e os identificadores históricos dos dez projetos e dos três algoritmos foram mantidos no [README](../README.md). A disponibilidade dos links confirma acesso nesta data; não comprova autoria exclusivamente humana, identidade com a coleta original ou equivalência funcional entre os grupos. A ressalva histórica específica do commit de TheAlgorithms permanece explícita.
