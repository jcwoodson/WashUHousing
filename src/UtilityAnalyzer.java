import java.io.*;
import java.util.*;

public class UtilityAnalyzer {

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
	public static final double PLACE_AVG = 8.795842558425583;
	public static final double PERSON_AVG = 5.155534955382459;
	
	public static void main(String[] args) throws IOException {
		double[][] array = new double[9][NUM_STUDENTS*1000];
		for (int z = 0; z < 1000; z++) {
			Random r = new Random();
			Scanner sc = new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream("washu/utility-"+String.format("%03d",z)+".csv"))));
			for (int i = 0; i < NUM_STUDENTS; i++) {
				String[] line = sc.nextLine().split(",");
				int j = 0;
				j++;
				for (int k = 0; k < 9; k++) {
					array[k][z*NUM_STUDENTS+i] = Double.parseDouble(line[j++]);
				}
			}
			System.out.println(z);
		}
		int[][][] wins = new int[3][3][3];
		for (int i = 0; i < NUM_STUDENTS*1000; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					for (int l = 0; l < 3; l++) {
						if (array[j*3+l][i] > array[k*3+l][i])
							wins[j][k][l]++;
					}
				}
			}
		}
		for (int i = 0; i < 9; i++) {
			Arrays.sort(array[i]);
		}
		
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					output.append(wins[k][i][j]+",");
				}
			}
			output.deleteCharAt(output.length()-1);
			output.append("\n");
		}
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("washu/analysis-abtest.csv"), "utf-8"));
		bw.write(output.toString());
		bw.close();
		
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("washu/analysis-sorted.csv"), "utf-8"));
		for (int i = 0; i < NUM_STUDENTS*1000; i++) {
			if(i%271==135) {
				output = new StringBuilder();
				for (int j = 0; j < 9; j++) {
					output.append(array[j][i]+",");
				}
				output.deleteCharAt(output.length()-1);
				output.append("\n");
				bw.write(output.toString());
			}
			if (i%1000==0)
				System.out.println(i);
		}
		bw.close();
	}
}
