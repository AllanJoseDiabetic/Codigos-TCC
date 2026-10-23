const assert = require('node:assert/strict')
const fs = require('node:fs')
const path = require('node:path')
const test = require('node:test')
const vm = require('node:vm')

const root = path.resolve(__dirname, '..')

function normalizeValue(value) {
  if (Array.isArray(value)) return Array.from(value, normalizeValue)
  if (value !== null && typeof value === 'object') {
    return Object.fromEntries(
      Object.entries(value).map(([key, item]) => [key, normalizeValue(item)])
    )
  }
  return value
}

function assertSameValue(actual, expected) {
  assert.deepEqual(normalizeValue(actual), expected)
}

function loadFunctions(relativePath, functionNames, replacements = []) {
  let source = fs.readFileSync(path.join(root, relativePath), 'utf8')

  for (const [from, to] of replacements) {
    source = source.replaceAll(from, to)
  }

  source += `\nglobalThis.__testExports = { ${functionNames.join(', ')} }\n`

  const context = {
    console: { log() {} },
    Infinity
  }
  vm.createContext(context)
  vm.runInContext(source, context, { filename: relativePath })

  return context.__testExports
}

const humanBinarySearch = loadFunctions(
  'humano/JavaScript/BinarySearch/BinarySearch.js',
  ['binarySearchIterative', 'binarySearchRecursive']
)

const aiBinarySearch = loadFunctions(
  'IA/JavaScript/Search/BinarySearch/index.js',
  ['binarySearch']
)

const humanMergeSort = loadFunctions(
  'humano/JavaScript/MergeSort/MergeSort.js',
  ['mergeSort'],
  [['export function', 'function']]
)

const aiMergeSort = loadFunctions(
  'IA/JavaScript/MergeSort/MergeSort.js',
  ['mergeSort']
)

const humanDijkstra = loadFunctions(
  'humano/JavaScript/Dijkstra/Dijkstra.js',
  ['createGraph', 'djikstra'],
  [['export { createGraph, djikstra }', '']]
)

const aiDijkstra = loadFunctions(
  'IA/JavaScript/Dijkstra/DijkstraIA.js',
  ['dijkstra']
)

const binarySearchCases = [
  { name: 'vetor vazio', numbers: [], target: 4, expected: -1 },
  { name: 'um elemento encontrado', numbers: [4], target: 4, expected: 0 },
  { name: 'primeiro índice', numbers: [1, 3, 5, 7], target: 1, expected: 0 },
  { name: 'último índice', numbers: [1, 3, 5, 7], target: 7, expected: 3 },
  { name: 'valor ausente', numbers: [1, 3, 5, 7], target: 4, expected: -1 }
]

for (const binaryCase of binarySearchCases) {
  test(`H1 - Busca binária iterativa: ${binaryCase.name}`, () => {
    assert.equal(
      humanBinarySearch.binarySearchIterative(binaryCase.numbers, binaryCase.target),
      binaryCase.expected
    )
  })

  test(`H1 - Busca binária recursiva: ${binaryCase.name}`, () => {
    assert.equal(
      humanBinarySearch.binarySearchRecursive(binaryCase.numbers, binaryCase.target),
      binaryCase.expected
    )
  })

  test(`IA1 - Busca binária: ${binaryCase.name}`, () => {
    assert.equal(
      aiBinarySearch.binarySearch(binaryCase.numbers, binaryCase.target),
      binaryCase.expected
    )
  })
}

const mergeSortCases = [
  { name: 'vetor vazio', numbers: [], expected: [] },
  { name: 'um elemento', numbers: [4], expected: [4] },
  {
    name: 'negativos e duplicados',
    numbers: [3, -1, 2, -1, 0],
    expected: [-1, -1, 0, 2, 3]
  },
  {
    name: 'vetor invertido',
    numbers: [5, 4, 3, 2, 1],
    expected: [1, 2, 3, 4, 5]
  }
]

for (const mergeCase of mergeSortCases) {
  test(`H2 - Merge Sort: ${mergeCase.name}`, () => {
    assertSameValue(humanMergeSort.mergeSort(mergeCase.numbers), mergeCase.expected)
  })

  test(`IA2 - Merge Sort: ${mergeCase.name}`, () => {
    assertSameValue(aiMergeSort.mergeSort(mergeCase.numbers), mergeCase.expected)
  })
}

function createHumanGraph(vertexCount, edges) {
  return humanDijkstra.createGraph(vertexCount, edges)
}

function createAiGraph(vertexCount, edges) {
  const graph = Object.fromEntries(
    Array.from({ length: vertexCount }, (_, vertex) => [String(vertex), []])
  )

  for (const [from, to, weight] of edges) {
    graph[from].push({ vertex: String(to), weight })
    graph[to].push({ vertex: String(from), weight })
  }

  return graph
}

function expectedHumanDistances(distances) {
  return distances.map((distance) => (distance === Infinity ? 10000 : distance))
}

function expectedAiDistances(distances) {
  return Object.fromEntries(
    distances.map((distance, vertex) => [String(vertex), distance])
  )
}

const dijkstraCases = [
  {
    name: 'grafo comum',
    vertexCount: 4,
    edges: [
      [0, 1, 4],
      [1, 2, 3],
      [0, 2, 10]
    ],
    distances: [0, 4, 7, Infinity]
  },
  {
    name: 'vértice inacessível',
    vertexCount: 3,
    edges: [[0, 1, 2]],
    distances: [0, 2, Infinity]
  },
  {
    name: 'caminho maior que 10.000',
    vertexCount: 3,
    edges: [
      [0, 1, 6000],
      [1, 2, 6001]
    ],
    distances: [0, 6000, 12001]
  }
]

for (const dijkstraCase of dijkstraCases) {
  test(`H3 - Dijkstra: ${dijkstraCase.name}`, () => {
    const graph = createHumanGraph(dijkstraCase.vertexCount, dijkstraCase.edges)
    const expected = expectedHumanDistances(dijkstraCase.distances)

    assertSameValue(
      humanDijkstra
        .djikstra(graph, dijkstraCase.vertexCount, 0)
        .map(([distance]) => distance),
      expected
    )
  })

  test(`IA3 - Dijkstra: ${dijkstraCase.name}`, () => {
    const graph = createAiGraph(dijkstraCase.vertexCount, dijkstraCase.edges)
    const expected = expectedAiDistances(dijkstraCase.distances)

    assertSameValue(aiDijkstra.dijkstra(graph, '0'), expected)
  })
}
