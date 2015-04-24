import java.io.*;
import java.util.*;

public class RandomHousingAssigner {

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
			ArrayList<ArrayList<Integer>> spaces = new ArrayList<ArrayList<Integer>>();
			
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
			
			int[] numSpacesLeft = new int[NUM_TYPES];
			int[] c = new int[NUM_TYPES];
			int t = 0;
			for (int i = 0; i < NUM_TYPES; i++) {
				numSpacesLeft[i] = NUM_SPACES[i];
				if (i == 0)
					c[i] = NUM_SPACES[i];
				else
					c[i] = c[i-1] + NUM_SPACES[i];
				t += NUM_SPACES[i];
				spaces.add(new ArrayList<Integer>());
			}
			
			for (int i = 0; i < NUM_STUDENTS; i++) {
				int rn = r.nextInt(t)+1;
				int index = Arrays.binarySearch(c, rn);
				if (index < 0)
					index = -index-1;
				while (index > 0 && c[index] == c[index-1])
					index--;
				rooms[i] = index;
				t--;
				for (int j = index; j < NUM_TYPES; j++)
					c[j]--;
				spaces.get(index).add(i);
			}
			
			ArrayList<Integer> peopleLeft = new ArrayList<Integer>();
			for (int i = 0; i < NUM_TYPES; i++) {
				while (spaces.get(i).size()%SUITE_SIZES[i] > 0) {
					int person = spaces.get(i).remove(0);
					numSpacesLeft[i]++;
					rooms[person] = -1;
					t++;
					for (int j = i; j < NUM_TYPES; j++)
						c[j]++;
					peopleLeft.add(person);
				}
			}
			
			while (peopleLeft.size() > 0) {
				for (int i = 0; i < NUM_TYPES; i++) {
					if (SUITE_SIZES[i] == 1 && spaces.get(i).size() > 0) {
						int person = spaces.get(i).remove(0);
						numSpacesLeft[i]++;
						rooms[person] = -1;
						t++;
						for (int j = i; j < NUM_TYPES; j++)
							c[j]++;
						peopleLeft.add(person);
					}
				}
				while (peopleLeft.size() > 0) {
					int i = peopleLeft.remove(0);
					int rn = r.nextInt(t)+1;
					int index = Arrays.binarySearch(c, rn);
					if (index < 0)
						index = -index-1;
					while (index > 0 && c[index] == c[index-1])
						index--;
					rooms[i] = index;
					t--;
					for (int j = index; j < NUM_TYPES; j++)
						c[j]--;
					spaces.get(index).add(i);
				}
				for (int i = 0; i < NUM_TYPES; i++) {
					while (spaces.get(i).size()%SUITE_SIZES[i] > 0) {
						int person = spaces.get(i).remove(0);
						numSpacesLeft[i]++;
						rooms[person] = -1;
						t++;
						for (int j = i; j < NUM_TYPES; j++)
							c[j]++;
						peopleLeft.add(person);
					}
				}
			}
			
			for (int i = 0; i < NUM_TYPES; i++) {
				while (spaces.get(i).size() > 0) {
					int p = spaces.get(i).remove(0);
					for (int j = 1; j < SUITE_SIZES[i]; j++) {
						int index = r.nextInt(spaces.get(i).size());
						suitemates.get(p).add(spaces.get(i).get(index));
						spaces.get(i).remove(index);
						if (DOUBLE_ROOMS[i] && j%2==1) {
							if (j == 1) {
								roommates[p] = suitemates.get(p).get(0);
								roommates[suitemates.get(p).get(0)] = p;
							} else {
								roommates[suitemates.get(p).get(j-1)] = suitemates.get(p).get(j-2);
								roommates[suitemates.get(p).get(j-2)] = suitemates.get(p).get(j-1);
							}
						}
					}
					for (int s : suitemates.get(p)) {
						suitemates.get(s).add(p);
						for (int s2 : suitemates.get(p)) {
							if (s2 != s)
								suitemates.get(s).add(s2);
						}
					}
				}
			}
			
			StringBuilder output = new StringBuilder();
			for (int i = 0; i < NUM_STUDENTS; i++) {
				output.append(lottery[i]+",");
				output.append(rooms[i]+",");
				output.append((DOUBLE_ROOMS[rooms[i]]?1:0)+",");
				if (DOUBLE_ROOMS[rooms[i]])
					output.append(roommates[i]+",");
				output.append((SUITE_SIZES[rooms[i]]-1)+",");
				for (int j = 0; j < SUITE_SIZES[rooms[i]]-1; j++)
					output.append(suitemates.get(i).get(j)+",");
				output.deleteCharAt(output.length()-1);
				output.append("\n");
			}
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("washu/random-"+String.format("%03d",z)+".csv"), "utf-8"));
			bw.write(output.toString());
			bw.close();
		}
	}
}
