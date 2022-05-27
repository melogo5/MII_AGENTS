package com.company;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Admin on 21.04.2018.
 */
public class FindWay {
    static ArrayList<Integer> dijkstra(int start, int end, int vNum, int [][] graph) {
        int INF = Integer.MAX_VALUE / 2;
        boolean[] used = new boolean [vNum]; // массив пометок
        int[] dist = new int [vNum]; // массив расстояния. dist[v] = минимальное_расстояние(start, v)
        int[] previous = new int [vNum];

        for (int i=0; i<vNum; i++)
        {
            previous[i]=-1;
        }
        Arrays.fill(dist, INF); // устанаавливаем расстояние до всех вершин INF
        dist[start] = 0; // для начальной вершины положим 0
        for (;;) {
            int v = -1;
            for (int nv = 0; nv < vNum; nv++) // перебираем вершины
                if (!used[nv] && dist[nv] < INF && (v == -1 || dist[v] > dist[nv])) // выбираем самую близкую непомеченную вершину
                    v = nv;
            if (v == -1) break; // ближайшая вершина не найдена
            used[v] = true; // помечаем ее
            for (int nv = 0; nv < vNum; nv++)
                if (!used[nv] && graph[v][nv] < INF) // для всех непомеченных смежных
                {
                    if (dist[nv]>dist[v] + graph[v][nv])
                    {
                        dist[nv] = dist[v] + graph[v][nv];
                        previous[nv] = v;
                    }

                }
        }


        ArrayList<Integer> way = new ArrayList<Integer>();
        ArrayList<Integer> govnofix = new ArrayList<Integer>();
        if (dist[end]>0 && dist[end]<INF)
        {
            govnofix.add(end);
            int t = previous[end];
            govnofix.add(t);
            while(t != start)
            {
                t = previous[t];
                govnofix.add(t);
            }
        }
        else
            govnofix.add(-1);

        for (int i = govnofix.size() - 1; i >= 0; --i)
            way.add(govnofix.get(i));
            /*
            System.out.println(dist.length);
            for (int i =0; i< dist.length; i=i+1)
            System.out.println(dist[i] + "\t" + previous[i]);
            System.out.println();

            for (int i=0; i<way.size(); i++)
            {
                System.out.println(way.get(i));
            }*/
        return way;
    }

    public static int getLength(int[][] info, ArrayList<Integer> route) {
        int sum = 0;
        for(int i = 0; i < route.size() - 1; i++) {
            int from = route.get(i);
            int to = route.get(i+1);
            sum += info[from][to];
        }
        return sum;
    }

    public static void sortRoutes(ArrayList<String> data) {
        for (int min = 0; min < data.size() - 1; min++) {
            int least = min;
            for (int j = min + 1; j < data.size(); j++) {
                if (Integer.parseInt(data.get(j).split(":")[1]) < Integer.parseInt(data.get(least).split(":")[1])) {
                    least = j;
                }
            }
            if (least != min) {
                String tmp = data.get(min);
                data.set(min,data.get(least));
                data.set(least,tmp);
            }
        }
    }

    public static boolean check(String a, String b)
    {
        /*
        public static boolean check(String a, String b)
        {
            if (a.length()>b.length() && a.contains(b))
            return true;
            if (a.length()<=b.length() && b.contains(a))
            return true;
            return false;
        }
*/
        int index;

        if(a.length() > b.length())
            index = a.indexOf(b);
        else
            index = b.indexOf(a);

        if(index != -1)
            return true;
        else
            return false;

    }

    public static void main(String[] args) {
        // TODO code application logic here
        int INF = Integer.MAX_VALUE / 2; // "Бесконечность"
        int vNum = 5; // количество вершин
        int[][] graph = new int[vNum][vNum]; // матрица смежности
        int start = 0, // начальная вершина
                end = 4;    // конечная верщина
        //int[][] graph = new int[][] {{INF,5,INF},{5,INF,7},{INF,7,INF}};

        graph[0][0] = INF ;
        graph[0][1] = 1;
        graph[0][2] = 4;
        graph[0][3] = INF;
        graph[0][4] = 5;

        graph[1][0] = INF;
        graph[1][1] = INF;
        graph[1][2] = INF;
        graph[1][3] = INF;
        graph[1][4] = 2;

        graph[2][0] = INF;
        graph[2][1] = INF;
        graph[2][2] = INF;
        graph[2][3] = 6;
        graph[2][4] = INF;

        graph[3][0] = INF;
        graph[3][1] = INF;
        graph[3][2] = INF;
        graph[3][3] = INF;
        graph[3][4] = INF;

        graph[4][0] = INF;
        graph[4][1] = INF;
        graph[4][2] = INF;
        graph[4][3] = 3;
        graph[4][4] = INF;


     /* Алгоритм Дейкстры за O(V^2) */

        ArrayList<Integer> rez = dijkstra(start, end, vNum, graph); // начальная вершина (нумерация с 0), конечная вершина, число вершин, матрица связности

        for (int i=0; i<rez.size(); i++)
        {
            System.out.println(rez.get(i));
        }

    }
}
