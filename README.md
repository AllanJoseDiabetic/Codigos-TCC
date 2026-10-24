# Códigos do TCC

Este repositório reúne os códigos utilizados na etapa experimental preliminar do Trabalho de Conclusão de Curso (TCC), destinada a comparar implementações de referência provenientes de um repositório público com implementações geradas por Inteligência Artificial. A autoria exclusivamente humana dos códigos de referência não pôde ser comprovada.

Os algoritmos analisados são:

- **Busca binária (Binary Search)**;
- **Algoritmo de Dijkstra**;
- **Merge Sort**.

## Organização do projeto

```text
.
├── humano/
│   └── JavaScript/
│       ├── BinarySearch/
│       ├── Dijkstra/
│       └── MergeSort/
└── IA/
    └── JavaScript/
        ├── Search/BinarySearch/
        ├── Dijkstra/
        └── MergeSort/
```

### `humano/`

Contém as implementações de referência provenientes do repositório TheAlgorithms/JavaScript. Embora esses códigos estejam disponíveis publicamente, não foi possível comprovar sua autoria exclusivamente humana. O nome da pasta `humano/` foi preservado por compatibilidade com a organização original dos arquivos do estudo piloto.
Esses arquivos foram adaptados a partir de implementações disponíveis no
repositório [TheAlgorithms/JavaScript](https://github.com/TheAlgorithms/JavaScript).
Os caminhos e os créditos específicos estão indicados na seção
[Atribuição e créditos](#atribuição-e-créditos).

### `IA/`

Contém as implementações utilizadas como referência de código gerado com
inteligência artificial. Cada algoritmo possui seu próprio diretório e,
quando aplicável, um arquivo de configuração do SonarQube para análise
estática.

## Descrição dos algoritmos

### Busca binária

Procura um valor em uma lista ordenada dividindo repetidamente o intervalo de
busca ao meio. A implementação em `humano/` apresenta versões iterativa e
recursiva; a implementação em `IA/` apresenta uma versão iterativa.

**Complexidade esperada:** `O(log n)` no caso médio e no pior caso, desde que a
lista esteja ordenada.

### Algoritmo de Dijkstra

Calcula as menores distâncias entre um vértice de origem e os demais vértices
de um grafo ponderado. O algoritmo pressupõe que as arestas não tenham pesos
negativos.

**Complexidade:** depende da estrutura utilizada para selecionar o próximo
vértice. As implementações deste projeto utilizam estruturas simples, sem uma
fila de prioridade baseada em heap.

### Merge Sort

Ordena uma lista pelo método de divisão e conquista: divide a lista em
sublistas, ordena cada parte recursivamente e depois intercala as partes
ordenadas.

**Complexidade:** `O(n log n)` em tempo e `O(n)` em espaço auxiliar.

## Como executar

Os arquivos JavaScript podem ser executados individualmente com Node.js:

```bash
node humano/JavaScript/BinarySearch/BinarySearch.js
node IA/JavaScript/Search/BinarySearch/index.js
```

Os arquivos de Dijkstra e Merge Sort da pasta `IA/` possuem exemplos de uso
no próprio arquivo. As implementações da pasta `humano/` podem ser importadas
ou executadas conforme seus respectivos módulos JavaScript.

### Testes automatizados

Os testes funcionais ficam em `tests/algorithms.test.js` e utilizam o recurso
nativo `node:test`. Para executá-los:

```bash
node --test tests/algorithms.test.js
```

Os testes são identificados da seguinte forma:

| Identificação | Implementação |
|---|---|
| H1 | Busca binária — referência |
| IA1 | Busca binária IA |
| H2 | Merge Sort — referência |
| IA2 | Merge Sort IA |
| H3 | Dijkstra — referência |
| IA3 | Dijkstra IA |

Os testes verificam o resultado dos algoritmos sem alterar os arquivos
originais. Os pares recebem os mesmos casos lógicos, com adaptação apenas da
representação dos dados quando as implementações usam formatos diferentes.
Também são testados vetores vazios, valores únicos, valores ausentes,
duplicados, vértices inacessíveis e caminhos com custo superior a `10.000`.

No estado atual, são executados 12 cenários lógicos, totalizando 29 execuções individuais. A implementação de referência H3 (Dijkstra) apresenta falha no teste referente a caminho maior que `10.000`, pois inicializa as distâncias com esse valor e retorna `10.000` em vez de `12.001`. Esse resultado registra uma diferença funcional entre as implementações; o algoritmo original não foi alterado para fazer o teste passar.

As cópias com o sufixo `-modificado` servem apenas para registrar as
alterações de saída feitas para visualizar os resultados no terminal.

Os hashes SHA-256 dos arquivos utilizados nas análises estão registrados no
[manifesto de rastreabilidade](docs/manifesto-rastreabilidade.md).

## Análise estática

Os diretórios dos algoritmos possuem arquivos `sonar-project.properties` para
configuração de análises no SonarQube. Os tokens e demais credenciais devem
ser fornecidos por variáveis ou pela configuração local do ambiente; não
publique credenciais reais no repositório.

## Atribuição e créditos

As implementações da pasta `humano/` foram baseadas nos seguintes arquivos do
projeto **The Algorithms - JavaScript**:

| Algoritmo | Arquivo de origem |
|---|---|
| Busca binária | [`Search/BinarySearch.js`](https://github.com/TheAlgorithms/JavaScript/blob/5c39e87a9a31f279c60f830ad74a845e4788a517/Search/BinarySearch.js) |
| Dijkstra | [`Graphs/Dijkstra.js`](https://github.com/TheAlgorithms/JavaScript/blob/5c39e87a9a31f279c60f830ad74a845e4788a517/Graphs/Dijkstra.js) |
| Merge Sort | [`Sorts/MergeSort.js`](https://github.com/TheAlgorithms/JavaScript/blob/5c39e87a9a31f279c60f830ad74a845e4788a517/Sorts/MergeSort.js) |

Crédito ao projeto e aos seus colaboradores:

> TheAlgorithms/JavaScript. *JavaScript Repository of TheAlgorithms, which
> implements various algorithms and data structures in JavaScript.* Disponível
> em: <https://github.com/TheAlgorithms/JavaScript>. Acesso em: 23 out. 2026.

Os links acima apontam para o commit `5c39e87a9a31f279c60f830ad74a845e4788a517` do repositório TheAlgorithms/JavaScript, identificado posteriormente para fins de verificação. Não foi possível comprovar que essa revisão corresponde exatamente à versão consultada durante a coleta original. Na comparação realizada, os arquivos H2 e H3 apresentaram conteúdo idêntico ao dessa revisão, enquanto H1 preservou as funções dos algoritmos, mas apresentou alterações em elementos externos a elas.
O repositório de origem informa que seus conteúdos são licenciados sob a **GNU General Public License v3.0
(GPL-3.0)**. Consulte o [arquivo de licença do projeto de
origem](https://github.com/TheAlgorithms/JavaScript/blob/5c39e87a9a31f279c60f830ad74a845e4788a517/LICENSE)
e mantenha esta atribuição ao redistribuir ou modificar os trechos derivados.

As implementações da pasta `IA/` fazem parte deste TCC e devem ser
consideradas separadamente das implementações de terceiros referenciadas
acima.

## Objetivo do repositório

O objetivo não é estabelecer qual abordagem é universalmente melhor, mas
fornecer uma base reproduzível para observar diferenças de implementação,
legibilidade, complexidade e resultados de análise estática entre os dois
grupos de código.

## Convenção de cálculo dos hashes SHA-256

Os hashes SHA-256 registrados neste manifesto correspondem às cópias preservadas dos arquivos JavaScript com terminações de linha CRLF, utilizadas no ambiente Windows durante o registro dos artefatos.

Os arquivos recuperados diretamente do repositório GitHub podem apresentar terminações de linha LF. Essa diferença na representação dos arquivos altera os valores dos hashes SHA-256, mesmo quando não existem alterações nas instruções dos algoritmos.

Para reproduzir os hashes publicados, é necessário utilizar a mesma representação em bytes dos arquivos preservados, incluindo as terminações de linha CRLF.

Os hashes documentam a integridade das amostras preservadas, mas não comprovam, isoladamente, qual revisão do repositório TheAlgorithms/JavaScript foi consultada durante a coleta original.
