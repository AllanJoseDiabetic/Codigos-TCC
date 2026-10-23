# Manifesto de rastreabilidade

Este documento registra os hashes SHA-256 dos arquivos JavaScript utilizados
nas análises estáticas do TCC. Os identificadores H1, H2, H3, IA1, IA2 e IA3
correspondem às implementações avaliadas.

| Identificador | Implementação | Arquivo | SHA-256 |
|---|---|---|---|
| H1 | Busca binária referência | `humano/JavaScript/BinarySearch/BinarySearch.js` | `1AF7E588C951EFC0298BAF564EA4CB0F5CA3A0EB7C7495DD5AE9BCE6BD5171F3` |
| IA1 | Busca binária IA | `IA/JavaScript/Search/BinarySearch/index.js` | `486BF9721D3BD978F2C82A41D152DE910F5BAE368501CCAF43EC705D3EBDC412` |
| H2 | Merge Sort referência | `humano/JavaScript/MergeSort/MergeSort.js` | `251A580321F3B36E32E20547E309F9AA0DCE30DF75B7D8E7D73D2083949F6B20` |
| IA2 | Merge Sort IA | `IA/JavaScript/MergeSort/MergeSort.js` | `AAE9C4DE98BE870F8527410355672F853C8C012B52600BAE43EF1072F6C9FCDF` |
| H3 | Dijkstra referência | `humano/JavaScript/Dijkstra/Dijkstra.js` | `F8BC24163D174146834F04447CF0C51860227BDB2683C68994A4F1B4D0F0DB7E` |
| IA3 | Dijkstra IA | `IA/JavaScript/Dijkstra/DijkstraIA.js` | `F5745D166767BE4BBD7838669900402276DC945C2AAF9B66B1F7FB0C149C6A46` |

Os hashes identificam o conteúdo dos arquivos preservados após as análises
estáticas. As cópias com o sufixo `-modificado` foram criadas posteriormente
para permitir a execução visível dos exemplos e não fazem parte deste
manifesto.
