# Estado de reprodução e inventário de evidências

## Escopo da conferência

Conferência documental realizada em 10 de novembro de 2026 sobre a árvore de `main` no commit [`b79adc0658e684c503434ab91aad946cc2b14f4f`](https://github.com/AllanJoseDiabetic/Codigos-TCC/tree/b79adc0658e684c503434ab91aad946cc2b14f4f). A listagem recursiva retornou 7.720 entradas, sem truncamento. A verificação abrange os arquivos versionados dessa árvore, não outras branches, histórico completo, anexos, releases, artefatos de execução externos ou Google Drive.

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

## CSV/JSON efetivamente disponíveis

**Nenhum arquivo `.csv` foi encontrado. Foram encontrados 14 arquivos `.json`, listados integralmente abaixo. Nenhum deles contém relatório de análise estática ou resultado de teste funcional da etapa ampliada.** A classificação foi feita pela leitura do conteúdo, não apenas pelo nome.

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

Não há CSV/JSON de resultados na raiz, em `docs/`, em `tests/` ou no restante da árvore auditada. Arquivos de build e dependências não demonstram, por si só, sucesso de compilação, validação funcional ou qualidade no SonarQube. A ausência de relatórios versionados não permite concluir que as análises nunca tenham sido realizadas; apenas impede conferir seus resultados por esses artefatos neste snapshot.

Para completar a documentação da etapa ampliada, permanecem pendentes os relatórios efetivamente coletados, sua associação às amostras analisadas, escopos de análise, versões efetivamente utilizadas e procedimentos/resultados funcionais. Não foram criados nomes presumidos de relatórios, números de versões, resultados ou hashes para preencher essas lacunas.

## Conferência das referências externas

Os 25 URLs distintos de GitHub preservados no README foram consultados com sucesso em 10 de novembro de 2026: 11 repositórios de origem, 10 snapshots históricos das aplicações (incluindo os diretórios de Flask Blog e Galaxian), 3 arquivos de algoritmos e a licença de TheAlgorithms/JavaScript.

Os créditos e os identificadores históricos dos dez projetos e dos três algoritmos foram mantidos no [README](../README.md). A disponibilidade dos links confirma acesso nesta data; não comprova autoria exclusivamente humana, identidade com a coleta original ou equivalência funcional entre os grupos. A ressalva histórica específica do commit de TheAlgorithms permanece explícita.
