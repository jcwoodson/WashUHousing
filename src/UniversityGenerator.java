import java.io.*;
import java.util.*;

public class UniversityGenerator {

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
			int[] weights = new int[NUM_TYPES];
			int[] cumulative = new int[NUM_TYPES];
			int total = 0;
			for (int i = 0; i < NUM_TYPES; i++) {
				int w = NUM_SPACES[i];
				if (SOUTH_FORTY[i])
					w *= 3;
				else if (ON_CAMPUS[i])
					w *= 2;
				weights[i] = w;
				if (i == 0)
					cumulative[i] = w;
				else
					cumulative[i] = cumulative[i-1] + w;
				total += w;
			}
			int[] lottery = new int[NUM_STUDENTS];
			int[][] rankings = new int[NUM_STUDENTS][NUM_TYPES];
			int[][] questions = new int[NUM_STUDENTS][NUM_QUESTIONS];
			int[] idealFriendCounts = new int[NUM_STUDENTS];
			int[] friendCounts = new int[NUM_STUDENTS];
			ArrayList<ArrayList<Integer>> friends = new ArrayList<ArrayList<Integer>>();
			int[] prefRoom = new int[NUM_STUDENTS];
			
			for (int i = 0; i < NUM_STUDENTS; i++) {
				lottery[i] = i+1;
				int[] c = new int[NUM_TYPES];
				int t = total;
				for (int j = 0; j < NUM_TYPES; j++) {
					c[j] = cumulative[j];
				}
				for (int j = 0; j < NUM_TYPES; j++) {
					int rn = r.nextInt(t)+1;
					int index = Arrays.binarySearch(c, rn);
					if (index < 0)
						index = -index-1;
					while (index > 0 && c[index] == c[index-1])
						index--;
					rankings[i][j] = index;
					t -= weights[index];
					for (int k = index; k < NUM_TYPES; k++) {
						c[k] -= weights[index];
					}
				}
				for (int j = 0; j < NUM_QUESTIONS; j++) {
					questions[i][j] = r.nextInt(5)+1;
				}
				idealFriendCounts[i] = r.nextInt(FRIEND_VAR*2+1)+FRIEND_AVG-FRIEND_VAR;
				prefRoom[i] = r.nextInt(101);
			}
			int[] cf = new int[NUM_STUDENTS];
			int tf = 0;
			for (int i = 0; i < NUM_STUDENTS; i++) {
				if (i == 0)
					cf[i] = idealFriendCounts[i];
				else
					cf[i] = cf[i-1] + idealFriendCounts[i];
				tf += idealFriendCounts[i];
				friends.add(new ArrayList<Integer>());
			}
			for (int i = NUM_STUDENTS-1; i >= 0; i--) {
				ArrayList<Integer> s = friends.get(i);
				while (tf > 1 && idealFriendCounts[i] > 0) {
					boolean numberAccepted = false;
					int rn = -1;
					int index = -1;
					int counter = 0;
					while (!numberAccepted && counter < 1000000) {
						rn = r.nextInt(tf);
						index = Arrays.binarySearch(cf, rn);
						if (index < 0)
							index = -index-1;
						while (index > 0 && cf[index] == cf[index-1])
							index--;
						if (index != i && !s.contains(index)) {
							int distSquared = 0;
							for (int j = 0; j < NUM_QUESTIONS; j++) {
								distSquared += Math.abs(questions[i][j]-questions[index][j]);
							}
							if (distSquared < 28 || r.nextBoolean())
								numberAccepted = true;
						}
						counter++;
					}
					if (counter >= 1000000)
						break;
					s.add(index);
					friends.get(index).add(i);
					
					idealFriendCounts[i]--;
					friendCounts[i]++;
					cf[i]--;
					idealFriendCounts[index]--;
					friendCounts[index]++;
					for (int j = index; j <= i; j++) {
						cf[j]--;
					}
					tf -= 2;
				}
			}
			
			StringBuilder output = new StringBuilder();
			for (int i = 0; i < NUM_STUDENTS; i++) {
				output.append(lottery[i]+",");
				output.append(NUM_TYPES+",");
				for (int j = 0; j < NUM_TYPES; j++)
					output.append(rankings[i][j]+",");
				output.append(NUM_QUESTIONS+",");
				for (int j = 0; j < NUM_QUESTIONS; j++)
					output.append(questions[i][j]+",");
				output.append(friendCounts[i]+",");
				for (int j = 0; j < friendCounts[i]; j++)
					output.append(friends.get(i).get(j)+",");
				output.append(prefRoom[i]);
				output.append("\n");
			}
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("washu/washu-"+String.format("%03d",z)+".csv"), "utf-8"));
			bw.write(output.toString());
			bw.close();
		}
	}
}
