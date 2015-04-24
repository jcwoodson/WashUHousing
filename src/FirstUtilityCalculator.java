import java.io.*;
import java.util.*;

public class FirstUtilityCalculator {

	public static final int NUM_TYPES = 40;
	public static final int NUM_STUDENTS = 1626;
	public static final int NUM_QUESTIONS = 7;
	public static final int[] NUM_SPACES = {27, 92, 32, 80, 18, 28, 48, 4, 46, 5, 24, 28, 2, 16, 8, 1, 6, 2, 6, 20, 32, 2, 8, 6, 16, 2, 69, 15, 318, 75, 4, 6, 54, 18, 15, 6, 312, 32, 168, 42};
	public static final boolean[] ON_CAMPUS = {true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true};
	public static final boolean[] ROUND_TWO = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false};
	public static final boolean[] SOUTH_FORTY = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, false, true, false, true, true, false, true};
	public static final boolean[] DOUBLE_ROOMS = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, true, false, true};
	public static final int[] SUITE_SIZES = {3, 4, 4, 5, 6, 7, 8, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 2, 2, 2, 3, 3, 4, 4, 4, 6};
	public static final int FRIEND_AVG = 100;
	public static final int FRIEND_VAR = 50;
	
	public static void main(String[] args) throws IOException {
		double personDistance = 0;
		double placeDistance = 0;
		for (int z = 0; z < 1000; z++) {
			Random r = new Random();
			Scanner sc = new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream("washu/washu-"+String.format("%03d",z)+".csv"))));
			int[] lottery = new int[NUM_STUDENTS];
			int[][] rankings = new int[NUM_STUDENTS][NUM_TYPES];
			int[][] questions = new int[NUM_STUDENTS][NUM_QUESTIONS];
			int[] friendCounts = new int[NUM_STUDENTS];
			ArrayList<ArrayList<Integer>> friends = new ArrayList<ArrayList<Integer>>();
			int[] prefRoom = new int[NUM_STUDENTS];
			int[] rooms = new int[NUM_STUDENTS];
			ArrayList<ArrayList<Integer>> suitemates = new ArrayList<ArrayList<Integer>>();
			int[] roommates = new int[NUM_STUDENTS];
			
			for (int i = 0; i < NUM_STUDENTS; i++) {
				friends.add(new ArrayList<Integer>());
				ArrayList<Integer> s = friends.get(i);
				String[] line = sc.nextLine().split(",");
				int j = 0;
				lottery[i] = Integer.parseInt(line[j++]);
				j++;
				for (int k = 0; k < NUM_TYPES; k++)
					rankings[i][k] = Integer.parseInt(line[j++]);
				j++;
				for (int k = 0; k < NUM_QUESTIONS; k++)
					questions[i][k] = Integer.parseInt(line[j++]);
				friendCounts[i] = Integer.parseInt(line[j++]);
				for (int k = 0; k < friendCounts[i]; k++)
					s.add(Integer.parseInt(line[j++]));
				prefRoom[i] = Integer.parseInt(line[j++]);
				suitemates.add(new ArrayList<Integer>());
			}
			
			sc = new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream("washu/random-"+String.format("%03d",z)+".csv"))));
			
			for (int i = 0; i < NUM_STUDENTS; i++) {
				String[] line = sc.nextLine().split(",");
				int j = 0;
				j++;
				rooms[i] = Integer.parseInt(line[j++]);
				j++;
				if (DOUBLE_ROOMS[rooms[i]])
					roommates[i] = Integer.parseInt(line[j++]);
				j++;
				for (int k = 0; k < SUITE_SIZES[rooms[i]]-1; k++)
					suitemates.get(i).add(Integer.parseInt(line[j++]));
			}
			
			double dist = 0;
			int numPeople = 0;
			
			for (int i = 0; i < NUM_STUDENTS; i++) {
				for (int j = 0; j < NUM_TYPES; j++) {
					if (rankings[i][j] == rooms[i])
						placeDistance += j;
				}
				if (SUITE_SIZES[rooms[i]] > 1) {
					numPeople++;
					int temp = 0;
					double tempBig = 0;
					int div = 0;
					if (DOUBLE_ROOMS[rooms[i]]) {
						int p = roommates[i];
						for (int j = 0; j < NUM_QUESTIONS; j++) {
							temp += (questions[i][j]-questions[p][j])*(questions[i][j]-questions[p][j]);
						}
						tempBig += Math.sqrt(temp)*2;
						div += 2;
					}
					for (int p : suitemates.get(i)) {
						temp = 0;
						for (int j = 0; j < NUM_QUESTIONS; j++) {
							temp += (questions[i][j]-questions[p][j])*(questions[i][j]-questions[p][j]);
						}
						tempBig += Math.sqrt(temp);
						div++;
					}
					dist += (double)tempBig/div;
				}
			}
			
			double avg = dist / numPeople;
			
			for (int i = 0; i < NUM_STUDENTS; i++) {
				if (SUITE_SIZES[rooms[i]] == 1) {
					numPeople++;
					dist += avg;
				}
			}
			
			personDistance += dist;
			System.out.println(z + "," + personDistance/NUM_STUDENTS/(z+1)+","+placeDistance/NUM_STUDENTS/(z+1));
		}
		StringBuilder output = new StringBuilder();
		output.append(personDistance/NUM_STUDENTS/1000+",");
		output.append(placeDistance/NUM_STUDENTS/1000);
		output.append("\n");
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("washu/raw-utility-averages.csv"), "utf-8"));
		bw.write(output.toString());
		bw.close();
	}
}
