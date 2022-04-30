import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Given a database of movies and actors, perform the functionality of the classic Kevin Bacon Game
 * @author - Andres Ibarra, Spring 2021, CS10 Problem Set 4
 */
public class BaconGame {
    private Map<Integer, String> actors;                //Map actorID -> actorName
    private Map<Integer, String> movies;                //Map movieID -> movieName
    private Graph<String, Set<String>> actorsToMovies;  //Graph: vertices = actor names, edges = Set{movies actors costar in}
    private String centerOfUniverse;                    //name of current center of universe

    public BaconGame() {
        actors = new HashMap<Integer, String>();
        movies = new HashMap<Integer, String>();
        actorsToMovies = new AdjacencyMapGraph<>();
        centerOfUniverse = "Kevin Bacon";               //default centerOfUniverse to Kevin Bacon

        createActorsMap("inputs/actors.txt");
        createMoviesMap("inputs/movies.txt");
        createActorsToMovies("inputs/movie-actors.txt");
    }

    /**
     * get the actorsToMovies graph instance variable
     * @return - actorsToMovies graph instance variable
     */
    public Graph<String, Set<String>> getActorsToMovies() {
        return actorsToMovies;
    }

    /**
     * get the name of the current center of the game universe
     * @return - name of current center of the game universe
     */
    public String getCenterOfUniverse () {
        return centerOfUniverse;
    }

    /**
     * set the center of the game universe to a new actor
     * @param newCenter - name of the new center of the game universe
     */
    public void setCenterOfUniverse(String newCenter) {
        centerOfUniverse = newCenter;
    }

    /**
     * put all the key/value pairs in the actors map
     * @param fileName - file of actor ID's and actor names
     */
    public void createActorsMap(String fileName) {
        BufferedReader input;

        try{
            input = new BufferedReader(new FileReader(fileName));
        }
        catch (FileNotFoundException e) {
            System.err.println("Cannot open file. \n" + e.getMessage());
            return;
        }

        try{
            String line;
            while ((line=input.readLine()) != null) {
                String[] s = line.split("\\|");
                actors.put(Integer.valueOf(s[0]), s[1]); //add into actors map
                actorsToMovies.insertVertex(s[1]); //add in all the vertices into actorsToMovies
            }
        }
        catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }
        finally {
            try {
                input.close();
            }
            catch (IOException e) {
                System.err.println("Cannot close file.\n" + e.getMessage());
            }
        }
    }

    /**
     * put all the key/value pairs in the movies map
     * @param fileName - file of movie ID's and movie names
     */
    public void createMoviesMap(String fileName) {
        BufferedReader input;

        try{
            input = new BufferedReader(new FileReader(fileName));
        }
        catch (FileNotFoundException e) {
            System.err.println("Cannot open file. \n" + e.getMessage());
            return;
        }

        try{
            String line;
            while ((line=input.readLine()) != null) {
                String[] s = line.split("\\|");
                movies.put(Integer.valueOf(s[0]), s[1]); //add values the movies map
            }
        }
        catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }
        finally {
            try {
                input.close();
            }
            catch (IOException e) {
                System.err.println("Cannot close file.\n" + e.getMessage());
            }
        }
    }

    /**
     * add all edges to the actorsToMovies graph
     * @param fileName - file with actor ID's and the ID's of the movies they costar in
     */
    public void createActorsToMovies(String fileName) {
        BufferedReader input;

        try{
            input = new BufferedReader(new FileReader(fileName));
        }
        catch (FileNotFoundException e) {
            System.err.println("Cannot open file. \n" + e.getMessage());
            return;
        }

        try{
            String line;
            int thisMovieId;
            ArrayList<String> actorsInSameMovie;
            line = input.readLine();
            while (true) {
                actorsInSameMovie = new ArrayList<String>(); //create empty arrayList for the actors int eh same movie
                if (line == null) break; //if this is the last line, exit while loop

                thisMovieId = Integer.parseInt(line.split("\\|")[0]); //get this movie's ID number
                while (Integer.parseInt(line.split("\\|")[0]) == thisMovieId) { //while the line we are on is of the movie - exit when we've moved onto another movie
                    int actorIdInMovie = Integer.parseInt(line.split("\\|")[1]); //get the actor Id
                    actorsInSameMovie.add(actors.get(actorIdInMovie)); //add the actor into teh arrayList
                    line = input.readLine(); //move onto the next line
                    if (line == null) break;
                }

                for (int i = 0; i < actorsInSameMovie.size(); i++) {
                    for (int j = 0; j < actorsInSameMovie.size(); j++) { //loop through every possible combination
                        if (i != j) { //don't add an edge between an actor and themself
                            if (!actorsToMovies.hasEdge(actorsInSameMovie.get(i), actorsInSameMovie.get(j))){
                                Set<String> temp = new HashSet<String>();   //create new set to use as the edge
                                temp.add(movies.get(thisMovieId));     //add this movie name into the edge set
                                actorsToMovies.insertUndirected(actorsInSameMovie.get(i), actorsInSameMovie.get(j), temp);
                            }
                            else { //if that edge already exists
                                actorsToMovies.getLabel(actorsInSameMovie.get(i), actorsInSameMovie.get(j)).add(movies.get(thisMovieId));
                                actorsToMovies.getLabel(actorsInSameMovie.get(j), actorsInSameMovie.get(i)).add(movies.get(thisMovieId)); //directed edge behind the scenes, so update both
                            }
                        }
                    }
                }
            }

        }
        catch (IOException e) {
            System.err.println("IO error while reading.\n" + e.getMessage());
        }
        finally {
            try {
                input.close();
            }
            catch (IOException e) {
                System.err.println("Cannot close file.\n" + e.getMessage());
            }
        }
    }

    /**
     * Helper function to display the instructions for the Kevin Bacon game
     * @return - instructions for the Kevin Bacon game
     */
    public static String getInstructions() {
        String s = "The commands are as such:\nc <#>: list top (positive number) or bottom (negative) <#> centers of " +
                "the universe, sorted by average separation\nd <low> <high>: list actors sorted by degree, with degree" +
                " between low and high\ni: list actors with infinite separation from the current center\np <name>: " +
                "find path from <name> to current center of the universe\ns <low> <high>: list actors sorted by " +
                "non-infinite separation from the current center, with separation between low and high\nu <name>: " +
                "make <name> the center of the universe\nq: quit game";
        return s;
    }

    /**
     * test case 1 - the sample test graph provided
     */
    public static void test1() {
        System.out.println("Test case 1: Hardcode the vertices of the test graph and test GraphLibrary");
        Graph<String, Set<String>> g = new AdjacencyMapGraph<String, Set<String>>();
        g.insertVertex("Kevin Bacon");
        g.insertVertex("Alice");
        g.insertVertex("Bob");
        g.insertVertex("Charlie");
        g.insertVertex("Dartmouth (Earl thereof)");
        g.insertVertex("Nobody");
        g.insertVertex("Nobody's friend");

        Set<String> s1 = new HashSet<>();
        s1.add("A Movie");
        s1.add("E Movie");
        g.insertUndirected("Kevin Bacon", "Alice", s1);
        Set<String> s2 = new HashSet<>();
        s2.add("A Movie");
        g.insertUndirected("Kevin Bacon", "Bob", s2);
        g.insertUndirected("Bob", "Alice", s2);
        Set<String> s3 = new HashSet<>();
        s3.add("D Movie");
        g.insertUndirected("Alice", "Charlie", s3);
        Set<String> s4 = new HashSet<>();
        s4.add("C Movie");
        g.insertUndirected("Bob", "Charlie", s4);
        Set<String> s5 = new HashSet<>();
        s5.add("B Movie");
        g.insertUndirected("Dartmouth (Earl thereof)", "Charlie", s5);
        Set<String> s6 = new HashSet<>();
        s6.add("F Movie");
        g.insertUndirected("Nobody", "Nobody's friend", s6);

        Graph<String, Set<String>> baconTestTree = GraphLibrary.bfs(g, "Kevin Bacon");
        System.out.println(baconTestTree);
        List<String> pathBaconDart = GraphLibrary.getPath(baconTestTree, "Dartmouth (Earl thereof)");
        System.out.println(pathBaconDart);
        System.out.println(GraphLibrary.missingVertices(g, baconTestTree));
        double avSeparation = GraphLibrary.averageSeparation(baconTestTree, "Kevin Bacon");
        System.out.println(avSeparation);
    }

    /**
     * test case 2 - boundary case where there are no edges
     */
    public static void test2() {
        System.out.println("Test case 2: hard code in graph to test boundary case where there are no edges");
        Graph<String, Set<String>> g = new AdjacencyMapGraph<String, Set<String>>();
        g.insertVertex("Kevin Bacon");
        g.insertVertex("Alice");
        g.insertVertex("Bob");
        g.insertVertex("Charlie");
        g.insertVertex("Dartmouth (Earl thereof)");
        g.insertVertex("Nobody");
        g.insertVertex("Nobody's friend");

        Graph<String, Set<String>> baconTestTree = GraphLibrary.bfs(g, "Kevin Bacon");
        System.out.println(baconTestTree);
        List<String> pathBaconDart = GraphLibrary.getPath(baconTestTree, "Dartmouth (Earl thereof)");
        System.out.println(pathBaconDart);
        System.out.println(GraphLibrary.missingVertices(g, baconTestTree));
        double avSeparation = GraphLibrary.averageSeparation(baconTestTree, "Kevin Bacon");
        System.out.println(avSeparation);
    }

    /**
     * test case 3 - boundary case where there are no vertices
     */
    public static void test3() {
        System.out.println("Test case 3: hard code in graph to test boundary case where there are no vertices");
        Graph<String, Set<String>> g = new AdjacencyMapGraph<String, Set<String>>();

        Graph<String, Set<String>> baconTestTree = GraphLibrary.bfs(g, "Kevin Bacon");
        System.out.println(baconTestTree);
        List<String> pathBaconDart = GraphLibrary.getPath(baconTestTree, "Dartmouth (Earl thereof)");
        System.out.println(pathBaconDart);
        System.out.println(GraphLibrary.missingVertices(g, baconTestTree));
        double avSeparation = GraphLibrary.averageSeparation(baconTestTree, "Kevin Bacon");
        System.out.println(avSeparation);
    }

    public static void main(String[] args) {
        BaconGame game = new BaconGame();
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to the Kevin Bacon game!");
        System.out.println("Note: The center of the universe is autoset to be Kevin Bacon");

        while (true) {
            System.out.println("\n" + BaconGame.getInstructions());
            System.out.println("\nWrite your command here:");

            String line = scan.nextLine();
            char command = line.charAt(0);

            //list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
            if (command == 'c') {
                int number = Integer.parseInt(line.split(" ")[1]);
                int posNumber = Math.abs(number);
                Map<String, Double> avSepMap = GraphLibrary.avSepMap(game.getActorsToMovies()); //map actor -> average Separation
                //create sorted increasing list of best centers of universe
                ArrayList<String> sortedVertices = new ArrayList<String>();
                for (String vertex: avSepMap.keySet()) { //loop through every actor in avSepMap
                    if (GraphLibrary.bfs(game.getActorsToMovies(), "Kevin Bacon").hasVertex(vertex)) { //only include actors who are in the Kevin Bacon universe
                        sortedVertices.add(vertex);
                    }
                }
                sortedVertices.sort((e1, e2) -> Double.compare(avSepMap.get(e2), avSepMap.get(e1)));

                //take bottom or top <#> elements
                if (number < 0) { //if asking for the bottom <#> centers of universe
                    System.out.println("The bottom " + posNumber + " centers of universe are:");
                    for (int i = 0; i < posNumber; i++) {
                        System.out.println(sortedVertices.get(i) + " has an average separation of " +
                                avSepMap.get(sortedVertices.get(i)));
                    }
                }
                else if (number > 0) { //if asking for top <#> centers of universe
                    System.out.println("The top " + posNumber + " centers of universe are:");
                    for (int i = sortedVertices.size()-1; i > sortedVertices.size()-posNumber-1; i--) {
                        System.out.println(sortedVertices.get(i) + " has an average separation of " +
                                avSepMap.get(sortedVertices.get(i)));
                    }
                }
                else {
                    System.out.println("Your input was 0. Nothing can be calculated");
                }
            }


            //list actors sorted by degree, with degree between low and high
            else if (command == 'd') {
                int low = Integer.parseInt(line.split(" ")[1]);
                int high = Integer.parseInt(line.split(" ")[2]);
                class inDegreeComparator implements Comparator<String> {
                    public int compare(String v1, String v2) { //comparator using the actor's degree's
                        return game.getActorsToMovies().inDegree(v1) - game.getActorsToMovies().inDegree(v2);
                    }
                }
                Comparator<String> inDegCompare = new inDegreeComparator();
                ArrayList<String> sortedVertices = new ArrayList<String>();
                for (String vertex: game.getActorsToMovies().vertices()) {
                    if (game.getActorsToMovies().inDegree(vertex) >= low && game.getActorsToMovies().inDegree(vertex) <= high) {
                        sortedVertices.add(vertex); //add all vertices with degrees between low and high
                    }
                }
                sortedVertices.sort(inDegCompare);
                for (int i = 0; i < sortedVertices.size(); i++) {
                    System.out.println(sortedVertices.get(i) + " has a degree of " + game.getActorsToMovies().inDegree(sortedVertices.get(i)));
                }
            }


            //list actors with infinite separation from the current center
            else if (command == 'i') {
                Set<String> actorsAtInfinite = GraphLibrary.missingVertices(game.getActorsToMovies(),
                        GraphLibrary.bfs(game.getActorsToMovies(), game.getCenterOfUniverse()));
                System.out.println("The actors with infinite separation from the current center, " + game.getCenterOfUniverse() + ", are:");
                for (String actor: actorsAtInfinite) {
                    System.out.println(actor);
                }
            }


            //find path from <name> to current center of the universe
            else if (command == 'p') {
                String end = line.substring(2);
                List<String> path = GraphLibrary.getPath(GraphLibrary.bfs(game.getActorsToMovies(), game.getCenterOfUniverse()), end);
                if (path.size() == 0) {
                    System.out.println(end + "'s " + game.getCenterOfUniverse() + " number is infinite; there is no path");
                }
                else {
                    System.out.println(end + "'s " + game.getCenterOfUniverse() + " number is " + (path.size()-1));
                    for (int i = path.size()-1; i > 0; i--) { //loop through in reverse order (ending with the centerOfUniverse)
                        System.out.println(path.get(i) + " appeared in " + game.getActorsToMovies().getLabel(path.get(i),
                                path.get(i-1)) + " with " + path.get(i-1));
                    }
                }

            }


            //list actors sorted by non-infinite separation from the current center, with separation between low and high
            else if (command == 's') {
                int low = Integer.parseInt(line.split(" ")[1]);
                int high = Integer.parseInt(line.split(" ")[2]);
                class separationComparator implements Comparator<String> {  //compare two vertices by their separation
                    public int compare(String v1, String v2) {
                        return GraphLibrary.getPath(GraphLibrary.bfs(game.getActorsToMovies(), game.getCenterOfUniverse()),
                                v1).size() - GraphLibrary.getPath(GraphLibrary.bfs(game.getActorsToMovies(),
                                game.getCenterOfUniverse()), v2).size();
                    }
                }
                Comparator<String> sepCompare = new separationComparator();
                ArrayList<String> sortedVertices = new ArrayList<String>();
                for (String vertex: game.getActorsToMovies().vertices()) { //loop through every vertex
                    if (GraphLibrary.getPath(GraphLibrary.bfs(game.getActorsToMovies(), game.getCenterOfUniverse()), vertex).size()-1
                            >= low && GraphLibrary.getPath(GraphLibrary.bfs(game.getActorsToMovies(), game.getCenterOfUniverse()),
                            vertex).size()-1 <= high & GraphLibrary.getPath(GraphLibrary.bfs(game.getActorsToMovies(),
                            game.getCenterOfUniverse()), vertex).size() != 0) { //if separation is non-infinite and between low and high
                        sortedVertices.add(vertex);
                    }
                }
                sortedVertices.sort(sepCompare);
                for (int i = 0; i < sortedVertices.size(); i++) {
                    System.out.println(sortedVertices.get(i) + " has a " + game.getCenterOfUniverse() + " number of "
                            + (GraphLibrary.getPath(GraphLibrary.bfs(game.getActorsToMovies(), game.getCenterOfUniverse()),
                            sortedVertices.get(i)).size()-1));
                }
            }


            //make <name> the center of the universe
            else if (command == 'u') {
                game.setCenterOfUniverse(line.substring(2));
                int numConnected = GraphLibrary.bfs(game.getActorsToMovies(), game.getCenterOfUniverse()).numVertices()-1; //all actors they can reach
                int totalNum = game.getActorsToMovies().numVertices()-1; //total number of actors in actors-> movies database
                double avSeparation = GraphLibrary.averageSeparation(GraphLibrary.bfs(game.getActorsToMovies(),
                        game.getCenterOfUniverse()), game.getCenterOfUniverse());
                System.out.println("The new center of the Universe is " + game.getCenterOfUniverse() + ", connected to "
                        + numConnected + "/" + totalNum + " actors with an average separation of " + avSeparation);
            }


            //quit game
            else if (command == 'q') {
                System.out.println("Thank you for playing!");
                break;
            }


            else {
                System.out.println("Invalid input\n");
            }
        }

        test1();
        test2();
        test3();

    } //end of main
} //end of class
