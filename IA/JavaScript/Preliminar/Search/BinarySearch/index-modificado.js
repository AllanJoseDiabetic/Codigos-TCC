function binarySearch(sortedArray, targetValue) {
  let leftIndex = 0;
  let rightIndex = sortedArray.length - 1;

  while (leftIndex <= rightIndex) {
    const middleIndex = Math.floor((leftIndex + rightIndex) / 2);
    const middleValue = sortedArray[middleIndex];

    if (middleValue === targetValue) {
      return middleIndex;
    }

    if (middleValue < targetValue) {
      leftIndex = middleIndex + 1;
    } else {
      rightIndex = middleIndex - 1;
    }
  }

  return -1;
}

function main() {
  const numbers = [2, 5, 8, 12, 16, 23, 38, 56, 72, 91];
  const target = 23;

  const resultIndex = binarySearch(numbers, target);

  // Modificação: o resultado da busca passou a ser exibido no terminal.
  console.log("Índice encontrado:", resultIndex);
}

main();