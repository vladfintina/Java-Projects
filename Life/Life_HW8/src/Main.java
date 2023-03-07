import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Main extends Thread {

    static int cores = Runtime.getRuntime().availableProcessors();
    static int N = 0;
    static int nrOfSteps = 0;
    static char[][] myMatrix;

    static char[][] newMatrix;

    static List<Thread> myThreads = new ArrayList<>();

    private CountDownLatch countDownLatch;

    private final int startIndex;
    private final int endIndex;

    public Main(int startIndex, int endIndex, CountDownLatch countDownLatch)
    {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run()
    {
        newLine(startIndex, endIndex);
        countDownLatch.countDown();
    }

    public static void main(String[] args) {

        BufferedReader myReader = new BufferedReader( new InputStreamReader(System.in));
        try{
            //File myObj = new File("file.txt");
            //BufferedReader myReader
              //      = new BufferedReader(new FileReader(myObj));

               String firstLine = myReader.readLine();
            //String firstLine = myReader.readLine();
            String[] firstLineNumbers = firstLine.split(" ");
            N = Integer.parseInt(firstLineNumbers[0]);
            nrOfSteps = Integer.parseInt(firstLineNumbers[1]);
            myMatrix = new char[N+2][N+2];
            newMatrix = new char[N+1][N+1];
            String line;
            for(int i = 1; i <=N ; i++)
            {
                //line = myReader.readLine();
                line = myReader.readLine();
                for(int j = 1; j <= N;j++)
                    myMatrix[i][j] = line.charAt(j-1);
            }
            int chunkSize = (N + cores - 1) / cores; // divide by threads rounded up.

            /*for(int i = 0; i <=N+1 ; i++)
            {
                for(int j = 0; j <= N+1;j++)
                    System.out.print(myMatrix[i][j]);
                System.out.println();
            }*/
            for(int k = 0;k < nrOfSteps;k++)
            {
                //create a border of the matrix to calculate easier the neighbours
                //System.out.println("k=" + k);
                for(int i = 1; i <= N ; i++)
                {
                    myMatrix[i][0] = myMatrix[i][N];//bordering left side
                    myMatrix[i][N+1] = myMatrix[i][1];//bordering right side
                    myMatrix[0][i] = myMatrix[N][i];//bordering upside
                    myMatrix[N+1][i] = myMatrix[1][i];//bordering downside

                    //corners
                    myMatrix[0][0] = myMatrix[N][N]; //left up corner
                    myMatrix[0][N+1] = myMatrix[N][1];//right up corner
                    myMatrix[N+1][0] = myMatrix[1][N];//left down corner
                    myMatrix[N+1][N+1] = myMatrix[1][1];//right down corner
                }
                //end of bordering
                CountDownLatch countDownLatch = new CountDownLatch(cores);
                myThreads.clear();
                for(int i = 0; i < cores ;i++)
                {
                    int start = i * chunkSize + 1;
                    int end = Math.min(start + chunkSize, N);
                    if(start < N) {
                        Thread t = new Main(start, end, countDownLatch);
                        myThreads.add(t);
                    }
                }
                for(int i = myThreads.size(); i<cores;i++)
                    countDownLatch.countDown();
                //System.out.println(countDownLatch.getCount());
                //System.out.println(cores);
                for (Thread thread : myThreads) {
                    thread.start();
                }
                countDownLatch.await();
                /*for(int i = 1; i <=N ; i++)
                {
                    for(int j = 1; j <= N;j++)
                        System.out.print(newMatrix[i][j]);
                    System.out.println();
                }*/
                for(int i = 1; i <=N ; i++)
                    for(int j = 1; j <= N;j++)
                        myMatrix[i][j] = newMatrix[i][j];

            }
        }
        catch (Exception exception)
        {
            System.out.println(exception.getMessage());
        }
        for(int i = 1; i <=N ; i++)
            {
                for(int j = 1; j <= N;j++)
                    System.out.print(myMatrix[i][j]);
                System.out.println();
            }
    }

    static public void newLine(int startIndex, int endIndex) {
        //threads divided the lines
        //int rows = endIndex - startIndex;
        if (startIndex != 1)
        {
            startIndex++;
        }
        //else
           // rows++;
        //char[][] result = new char[rows][N];
        //char North, East, West, South, NV, NE, SE, SV;
        //System.out.println("start:" + startIndex + " end:" + endIndex);
        for (int i = startIndex; i <= endIndex; i++) {
            for (int j = 1; j <= N; j++) {
                int aliveNeighbours = 0;
                if( myMatrix[i][j+1] == 'X')//E
                    aliveNeighbours++;
                if(myMatrix[i+1][j+1] == 'X')//SE
                    aliveNeighbours++;
                if(myMatrix[i+1][j] == 'X')//S
                    aliveNeighbours++;
                if(myMatrix[i+1][j-1] == 'X')//SV
                    aliveNeighbours++;
                if(myMatrix[i][j-1] == 'X')//V
                    aliveNeighbours++;
                if(myMatrix[i-1][j-1] =='X')//NV
                    aliveNeighbours++;
                if(myMatrix[i-1][j] == 'X')//N
                    aliveNeighbours++;
                if(myMatrix[i-1][j+1] == 'X')//NE
                    aliveNeighbours++;
                //verify if current cell is dead or alive
                if(myMatrix[i][j] == 'X') {
                    if (aliveNeighbours != 2 && aliveNeighbours != 3)
                        newMatrix[i][j] = '_';//result[i - startIndex][j] = '_';
                    else
                        newMatrix[i][j] = 'X';//result[i - startIndex][j] = 'x';
                }
                else {
                    if (aliveNeighbours == 3)
                        newMatrix[i][j] = 'X';//result[i - startIndex][j] = 'x';
                    else
                        newMatrix[i][j] = '_';
                }
            }
        }
       //return result;
    }
}