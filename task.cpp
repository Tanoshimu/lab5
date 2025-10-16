#include <iostream>
#include <vector>
#include <algorithm>
#include <climits>
#include <cmath>

using namespace std;

class TSP {
private:
    vector<vector<int>> graph;
    int n;

public:
    TSP(vector<vector<int>> distanceMatrix) {
        graph = distanceMatrix;
        n = graph.size();
    }

    // Метод полного перебора (Brute Force)
    int bruteForceTSP() {
        vector<int> cities;
        for (int i = 0; i < n; i++) {
            cities.push_back(i);
        }

        int minPath = INT_MAX;
        
        do {
            int currentPath = 0;
            for (int i = 0; i < n - 1; i++) {
                currentPath += graph[cities[i]][cities[i + 1]];
            }
            currentPath += graph[cities[n - 1]][cities[0]]; // возврат в начальную точку
            
            minPath = min(minPath, currentPath);
            
        } while (next_permutation(cities.begin() + 1, cities.end()));
        
        return minPath;
    }

    // Метод динамического программирования
    int dpTSP() {
        int totalStates = 1 << n;
        vector<vector<int>> dp(totalStates, vector<int>(n, INT_MAX));
        
        // Базовый случай: из начальной вершины в саму себя
        dp[1][0] = 0;
        
        for (int mask = 1; mask < totalStates; mask++) {
            for (int last = 0; last < n; last++) {
                if (dp[mask][last] == INT_MAX) continue;
                
                for (int nextCity = 0; nextCity < n; nextCity++) {
                    if (mask & (1 << nextCity)) continue; // город уже посещен
                    
                    int newMask = mask | (1 << nextCity);
                    dp[newMask][nextCity] = min(dp[newMask][nextCity], 
                                               dp[mask][last] + graph[last][nextCity]);
                }
            }
        }
        
        // Находим минимальный путь с возвратом в начальную точку
        int minPath = INT_MAX;
        int finalMask = (1 << n) - 1;
        for (int last = 1; last < n; last++) {
            if (dp[finalMask][last] != INT_MAX) {
                minPath = min(minPath, dp[finalMask][last] + graph[last][0]);
            }
        }
        
        return minPath;
    }

    // Жадный алгоритм (ближайший сосед)
    vector<int> nearestNeighborTSP() {
        vector<bool> visited(n, false);
        vector<int> path;
        
        int currentCity = 0;
        visited[currentCity] = true;
        path.push_back(currentCity);
        
        for (int i = 1; i < n; i++) {
            int nextCity = -1;
            int minDist = INT_MAX;
            
            for (int j = 0; j < n; j++) {
                if (!visited[j] && graph[currentCity][j] < minDist) {
                    minDist = graph[currentCity][j];
                    nextCity = j;
                }
            }
            
            currentCity = nextCity;
            visited[currentCity] = true;
            path.push_back(currentCity);
        }
        
        return path;
    }
    
    int calculatePathLength(const vector<int>& path) {
        int length = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            length += graph[path[i]][path[i + 1]];
        }
        length += graph[path.back()][path[0]]; // возврат в начало
        return length;
    }
};

// Пример использования
int main() {
    // Пример матрицы расстояний между 4 городами
    vector<vector<int>> distanceMatrix = {
        {0, 10, 15, 20},
        {10, 0, 35, 25},
        {15, 35, 0, 30},
        {20, 25, 30, 0}
    };
    
    TSP tsp(distanceMatrix);
    
    cout << "Матрица расстояний:" << endl;
    for (const auto& row : distanceMatrix) {
        for (int dist : row) {
            cout << dist << "\t";
        }
        cout << endl;
    }
    
    cout << "\nРезультаты:" << endl;
    
    // Жадный алгоритм
    vector<int> greedyPath = tsp.nearestNeighborTSP();
    cout << "Жадный алгоритм: ";
    for (int city : greedyPath) cout << city << " ";
    cout << "-> Длина: " << tsp.calculatePathLength(greedyPath) << endl;
    
    // Полный перебор (работает для небольших n)
    if (distanceMatrix.size() <= 10) {
        int bruteResult = tsp.bruteForceTSP();
        cout << "Полный перебор: " << bruteResult << endl;
    } else {
        cout << "Полный перебор: слишком много городов для перебора" << endl;
    }
    
    // Динамическое программирование
    int dpResult = tsp.dpTSP();
    cout << "Динамическое программирование: " << dpResult << endl;
    
    return 0;
}
