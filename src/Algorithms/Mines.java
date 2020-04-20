/*
    Name: Eskioglou Maria
    AEM: 3237
    Email: eskioglou@csd.auth.gr
 */
package Algorithms;

import java.io.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Mines {
    //I created a helping function in order to depict the points better with variables the x and y.
    public static class Point {

        public int x;
        public int y;

        //The constructor: Initializing the variables.
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /*
        We are aware that the first two points are the coordinates of the source and the coordinates of the end(where the treasure is.).
        I add those points in the list and then delete them from the initial list.
        Then, we must find all points that are left and right of the first two points.
     */
    public static ArrayList<Point> quickHull(ArrayList<Point> points) {
        ArrayList<Point> convexHull1 = new ArrayList<Point>();
        ArrayList<Point> convexHull2 = new ArrayList<Point>();
        if (points.size() == 3)
            return (ArrayList) points.clone();

        Point A = points.get(0);
        Point B = points.get(1);
        convexHull1.add(A);
        convexHull1.add(B);
        convexHull2.add(A);
        convexHull2.add(B);
        points.remove(0);
        points.remove(1);

        ArrayList<Point> leftSet = new ArrayList<Point>();
        ArrayList<Point> rightSet = new ArrayList<Point>();

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (pointLocation(A, B, p) == -1)       // Βρίσκω τα σημεία που είναι αριστερά των δύο προηγούμενων σημείων
                leftSet.add(p);
            else if (pointLocation(A, B, p) == 1)   // Βρίσκω τα σημεία που είναι δεξιά των δύο προηγούμενων σημείων
                rightSet.add(p);
        }
        hullSet(A, B, rightSet, convexHull1);
        hullSet(A, B, leftSet, convexHull2);
        if(EuclideanLenghtofPath(convexHull1) > EuclideanLenghtofPath(convexHull2))
            return convexHull2;
        return convexHull1;
    }


    /*
        Returns the distance of a third point C from the line defined by Points A and B.
     */
    public static double distance(Point A, Point B, Point C) {
        int ABx = B.x - A.x;
        int ABy = B.y - A.y;
        double num = ABx * (A.y - C.y) - ABy * (A.x - C.x);
        if (num < 0)
            num = -num;
        return num;
    }

    public static void hullSet(Point A, Point B, ArrayList<Point> set,    // Τρέχει αναδρομικά το σύνολο των σημείων που βρίσκονται είτε αριστερά είτα δεξιά
                               ArrayList<Point> hull) {                     // από τα δύο αρχικά σημεία
        int insertPosition = hull.indexOf(B);
        if (set.size() == 0)
            return;
        if (set.size() == 1) {
            Point p = set.get(0);
            set.remove(p);
            hull.add(insertPosition, p);
            return;
        }
        double dist = Integer.MIN_VALUE;
        int furthestPoint = -1;
        for (int i = 0; i < set.size(); i++) {
            Point p = set.get(i);
            double distance = distance(A, B, p);
            if (distance > dist) {
                dist = distance;
                furthestPoint = i;
            }
        }
        Point P = set.get(furthestPoint);
        set.remove(furthestPoint);
        hull.add(insertPosition, P);


        ArrayList<Point> leftSetAP = new ArrayList<Point>();
        for (int i = 0; i < set.size(); i++) {
            Point M = set.get(i);
            if (pointLocation(A, P, M) == 1) {
                leftSetAP.add(M);
            }
        }

        ArrayList<Point> leftSetPB = new ArrayList<Point>();
        for (int i = 0; i < set.size(); i++) {
            Point M = set.get(i);
            if (pointLocation(P, B, M) == 1) {
                leftSetPB.add(M);
            }
        }
        hullSet(A, P, leftSetAP, hull);
        hullSet(P, B, leftSetPB, hull);
    }

    /*
        This method estimates if a point belongs on the left or on the right side of the points A and B.
     */
    public static int pointLocation(Point A, Point B, Point P) {
        int cp1 = (B.x - A.x) * (P.y - A.y) - (B.y - A.y) * (P.x - A.x);
        if (cp1 > 0)
            return 1;
        else if (cp1 == 0)
            return 0;
        else
            return -1;
    }

    /*
        This method estimates the euclidean length of the path.
     */
    public static double EuclideanLenghtofPath(ArrayList<Point> hull)
    {
        double length = 0;
        for (int i = 0; i < hull.size() - 1; i++) {
            length += Math.sqrt(Math.pow(hull.get(i).x - hull.get(i + 1).x, 2) + Math.pow(hull.get(i).y - hull.get(i + 1).y, 2));
        }
        return length;
    }


    /*
        Reading the file arg[0] as argument from the main.
        I read each line as a string and then split the two numbers and with parseInt, I transform the string into an int.
        Then, we create a Point with a and b as parameters, where a is the x coordinates and b is the y coordinates.
     */
    public static void main(String[] args) throws IOException {
        try {
            FileInputStream fstream = new FileInputStream("file.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            ArrayList<Point> listOfPoints = new ArrayList<>();
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] words = line.split(" ");
                int a = Integer.parseInt(words[0]);
                int b = Integer.parseInt(words[1]);
                Point point = new Point(a, b);
                listOfPoints.add(i, point);
                i++;
            }
            ArrayList<Point> finalPath;
            double minDistance;
            finalPath = quickHull(listOfPoints);
            minDistance = EuclideanLenghtofPath(finalPath);


            System.out.printf("The shortest distance is %.5f%n", minDistance);  // Στρογγυλοποιώ στο 5ο δεκαδικο ψηφίο
            System.out.println("The shortest path is: ");
            for (int j = 0; j < finalPath.size(); j++) {
                if (j != finalPath.size() - 1)
                    System.out.println("(" + finalPath.get(j).x + ", " + finalPath.get(j).y + ")" + "-->");
                else
                    System.out.println("(" + finalPath.get(j).x + ", " + finalPath.get(j).y + ")");
            }
        }
        catch(IOException ioException)
        {
            System.out.println("Error opening file. Terminating.");
            System.exit(1);
        }
        catch(NoSuchElementException elementException)
        {
            System.out.println("File improperly formed. Terminating");
        }
        catch(IllegalStateException stateException)
        {
            System.out.println("Error reading from file. Terminating.");
        }
    }
}