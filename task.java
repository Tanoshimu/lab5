import java.util.*;

public class TSP {
    private int[][] graph;
    private int n;
    
    public TSP(int[][] distanceMatrix) {
        this.graph = distanceMatrix;
        this.n = distanceMatrix.length;
    }
    
    // Метод полного перебора
    public int bruteForceTSP() {
        List<Integer> cities = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            cities.add(i);
        }
        
        int minPath = Integer.MAX_VALUE;
        
        do {
            int currentPath = 0;
            for (int i = 0; i < n - 1; i++) {
                currentPath += graph[cities.get(i)][cities.get(i + 1)];
            }
            currentPath += graph[cities.get(n - 1)][cities.get(0)];
            
            minPath = Math.min(minPath, currentPath);
            
        } while (nextPermutation(cities));
        
        return minPath;
    }
    
    // Генерация следующей перестановки
    private boolean nextPermutation(List<Integer> array) {
        // Пропускаем первый элемент (начальный город фиксирован)
        int i = array.size() - 1;
        while (i > 1 && array.get(i - 1) >= array.get(i)) {
            i--;
        }
        
        if (i <= 1) {
            return false;
        }
        
        int j = array.size() - 1;
        while (array.get(j) <= array.get(i - 1)) {
            j--;
        }
        
        Collections.swap(array, i - 1, j);
        
        j = array.size() - 1;
        while (i < j) {
            Collections.swap(array, i, j);
            i++;
            j--;
        }
        
        return true;
    }
    
    // Метод динамического программирования
    public int dpTSP() {
        int totalStates = 1 << n;
        int[][] dp = new int[totalStates][n];
        
        // Инициализация
        for (int i = 0; i < totalStates; i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE);
        }
        dp[1][0] = 0;
        
        for (int mask = 1; mask < totalStates; mask++) {
            for (int last = 0; last < n; last++) {
                if (dp[mask][last] == Integer.MAX_VALUE) continue;
                
                for (int nextCity = 0; nextCity < n; nextCity++) {
                    if ((mask & (1 << nextCity)) != 0) continue;
                    
                    int newMask = mask | (1 << nextCity);
                    int newCost = dp[mask][last] + graph[last][nextCity];
                    
                    if (newCost < dp[newMask][nextCity]) {
                        dp[newMask][nextCity] = newCost;
                    }
                }
            }
        }
        
        int minPath = Integer.MAX_VALUE;
        int finalMask = (1 << n) - 1;
        for (int last = 1; last < n; last++) {
            if (dp[finalMask][last] != Integer.MAX_VALUE) {
                minPath = Math.min(minPath, dp[finalMask][last] + graph[last][0]);
            }
        }
        
        return minPath;
    }
    
    // Жадный алгоритм
    public List<Integer> nearestNeighborTSP() {
        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();
        
        int currentCity = 0;
        visited[currentCity] = true;
        path.add(currentCity);
        
        for (int i = 1; i < n; i++) {
            int nextCity = -1;
            int minDist = Integer.MAX_VALUE;
            
            for (int j = 0; j < n; j++) {
                if (!visited[j] && graph[currentCity][j] < minDist) {
                    minDist = graph[currentCity][j];
                    nextCity = j;
                }
            }
            
            currentCity = nextCity;
            visited[currentCity] = true;
            path.add(currentCity);
        }
        
        return path;
    }
    
    public int calculatePathLength(List<Integer> path) {
        int length = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            length += graph[path.get(i)][path.get(i + 1)];
        }
        length += graph[path.get(path.size() - 1)][path.get(0)];
        return length;
    }
    
    // Дополнительный метод: 2-opt локальный поиск
    public List<Integer> twoOptTSP(List<Integer> initialPath) {
        List<Integer> bestPath = new ArrayList<>(initialPath);
        int bestLength = calculatePathLength(bestPath);
        boolean improved = true;
        
        while (improved) {
            improved = false;
            
            for (int i = 1; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    List<Integer> newPath = twoOptSwap(bestPath, i, j);
                    int newLength = calculatePathLength(newPath);
                    
                    if (newLength < bestLength) {
                        bestPath = newPath;
                        bestLength = newLength;
                        improved = true;
                    }
                }
            }
        }
        
        return bestPath;
    }
    
    private List<Integer> twoOptSwap(List<Integer> path, int i, int j) {
        List<Integer> newPath = new ArrayList<>();
        
        // 1. Берем путь от 0 до i-1
        for (int k = 0; k <= i - 1; k++) {
            newPath.add(path.get(k));
        }
        
        // 2. Берем путь от i до j в обратном порядке
        for (int k = j; k >= i; k--) {
            newPath.add(path.get(k));
        }
        
        // 3. Берем путь от j+1 до конца
        for (int k = j + 1; k < path.size(); k++) {
            newPath.add(path.get(k));
        }
        
        return newPath;
    }
    
    public static void main(String[] args) {
        // Пример матрицы расстояний
        int[][] distanceMatrix = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        
        TSP tsp = new TSP(distanceMatrix);
        
        System.out.println("Матрица расстояний:");
        for (int[] row : distanceMatrix) {
            for (int dist : row) {
                System.out.print(dist + "\t");
            }
            System.out.println();
        }
        
        System.out.println("\nРезультаты:");
        
        // Жадный алгоритм
        List<Integer> greedyPath = tsp.nearestNeighborTSP();
        System.out.println("Жадный алгоритм: " + greedyPath + 
                          " -> Длина: " + tsp.calculatePathLength(greedyPath));
        
        // 2-opt улучшение
        List<Integer> twoOptPath = tsp.twoOptTSP(greedyPath);
        System.out.println("2-opt улучшение: " + twoOptPath + 
                          " -> Длина: " + tsp.calculatePathLength(twoOptPath));
        
        // Полный перебор
        if (distanceMatrix.length <= 10) {
            int bruteResult = tsp.bruteForceTSP();
            System.out.println("Полный перебор: " + bruteResult);
        } else {
            System.out.println("Полный перебор: слишком много городов для перебора");
        }
        
        // Динамическое программирование
        int dpResult = tsp.dpTSP();
        System.out.println("Динамическое программирование: " + dpResult);
    }
}
