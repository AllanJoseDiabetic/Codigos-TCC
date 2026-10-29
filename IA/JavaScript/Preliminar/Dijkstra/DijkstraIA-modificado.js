class PriorityQueue {
  constructor() {
    this.items = [];
  }

  enqueue(vertex, priority) {
    this.items.push({ vertex, priority });
    this.items.sort((a, b) => a.priority - b.priority);
  }

  dequeue() {
    return this.items.shift();
  }

  isEmpty() {
    return this.items.length === 0;
  }
}

function initializeDistances(graph, startVertex) {
  const distances = {};

  for (const vertex in graph) {
    distances[vertex] = Infinity;
  }

  distances[startVertex] = 0;

  return distances;
}

function dijkstra(graph, startVertex) {
  const distances = initializeDistances(graph, startVertex);
  const visited = new Set();
  const queue = new PriorityQueue();

  queue.enqueue(startVertex, 0);

  while (!queue.isEmpty()) {
    const { vertex: currentVertex } = queue.dequeue();

    if (visited.has(currentVertex)) {
      continue;
    }

    visited.add(currentVertex);

    for (const neighbor of graph[currentVertex]) {
      const { vertex: neighborVertex, weight } = neighbor;
      const newDistance = distances[currentVertex] + weight;

      if (newDistance < distances[neighborVertex]) {
        distances[neighborVertex] = newDistance;
        queue.enqueue(neighborVertex, newDistance);
      }
    }
  }

  return distances;
}

function main(graph, startVertex) {
  // Modificação: o resultado do algoritmo passou a ser exibido no terminal.
  const distances = dijkstra(graph, startVertex);
  console.log("Menores distâncias:", distances);
}

const graph = {
  A: [
    { vertex: "B", weight: 4 },
    { vertex: "C", weight: 2 }
  ],
  B: [
    { vertex: "C", weight: 1 },
    { vertex: "D", weight: 5 }
  ],
  C: [
    { vertex: "D", weight: 8 },
    { vertex: "E", weight: 10 }
  ],
  D: [
    { vertex: "E", weight: 2 }
  ],
  E: []
};

main(graph, "A");