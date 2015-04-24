import java.io.*;
import java.util.*;

public class CurrentHousingAssigner {

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
			ArrayList<ArrayList<Integer>> groups = new ArrayList<ArrayList<Integer>>();
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
				groups.add(new ArrayList<Integer>());
			}
			
			boolean[] inGroup = new boolean[NUM_STUDENTS];
			boolean[] groupLeader = new boolean[NUM_STUDENTS];
			int[] spaceAvailable = new int[NUM_TYPES];
			for (int i = 0; i < NUM_TYPES; i++) {
				spaceAvailable[i] = NUM_SPACES[i];
			}
			
			for (int i = 0; i < NUM_STUDENTS-NUM_STUDENTS/300*100*2; i++) {
				int topRanked = -1;
				for (int j = 0; j < NUM_TYPES; j++) {
					if (ROUND_TWO[rankings[i][j]]) {
						topRanked = rankings[i][j];
						break;
					}
				}
				if (!inGroup[i] && SUITE_SIZES[topRanked] != 1) {
					int totAvail = 0;
					ArrayList<Integer> availableFriends = new ArrayList<Integer>();
					for (int f : friends.get(i)) {
						if (!inGroup[f] && f < NUM_STUDENTS-NUM_STUDENTS/300*100){
							if (f < NUM_STUDENTS-NUM_STUDENTS/300*100*2) {
								totAvail++;
								availableFriends.add(f);
							} else {
								for (int j = 0; j < 5; j++) {
									if (rankings[f][j] == topRanked) {
										totAvail++;
										availableFriends.add(f);
									}
								}
							}
						}
					}
					if (totAvail < SUITE_SIZES[topRanked]-1)
						continue;
					groupLeader[i] = true;
					inGroup[i] = true;
					for (int j = 0; j < SUITE_SIZES[topRanked]-1; j++) {
						double bestUtil = Double.MAX_VALUE;
						int best = -1;
						for (int f : availableFriends) {
							if (!inGroup[f]) {
								double util = f/(NUM_STUDENTS/2);
								for (int k = 0; k < NUM_TYPES; k++) {
									if (rankings[f][k] == topRanked)
										util += k/PLACE_AVG;
								}
								double temp = 0;
								for (int k = 0; k < NUM_QUESTIONS; k++) {
									temp += (questions[i][k]-questions[f][k])*(questions[i][k]-questions[f][k]);
								}
								util += Math.sqrt(temp)/PERSON_AVG;
								if (util < bestUtil) {
									bestUtil = util;
									best = f;
								}
							}
						}
						inGroup[best] = true;
						groups.get(i).add(best);
					}
				}
			}
			for (int i = NUM_STUDENTS-NUM_STUDENTS/300*100*2; i < NUM_STUDENTS-NUM_STUDENTS/300*100; i++) {
				int topRanked = -1;
				for (int j = 0; j < 5; j++) {
					if (ROUND_TWO[rankings[i][j]]) {
						topRanked = rankings[i][j];
						break;
					}
				}
				if (topRanked == -1)
					continue;
				if (!inGroup[i] && SUITE_SIZES[topRanked] != 1) {
					int totAvail = 0;
					ArrayList<Integer> availableFriends = new ArrayList<Integer>();
					for (int f : friends.get(i)) {
						if (!inGroup[f] && f < NUM_STUDENTS-NUM_STUDENTS/300*100){
							if (f < NUM_STUDENTS-NUM_STUDENTS/300*100*2) {
								totAvail++;
								availableFriends.add(f);
							} else {
								for (int j = 0; j < 5; j++) {
									if (rankings[f][j] == topRanked) {
										totAvail++;
										availableFriends.add(f);
									}
								}
							}
						}
					}
					if (totAvail < SUITE_SIZES[topRanked]-1)
						continue;
					groupLeader[i] = true;
					inGroup[i] = true;
					for (int j = 0; j < SUITE_SIZES[topRanked]-1; j++) {
						double bestUtil = Double.MAX_VALUE;
						int best = -1;
						for (int f : availableFriends) {
							if (!inGroup[f]) {
								double util = f/(NUM_STUDENTS/2);
								for (int k = 0; k < NUM_TYPES; k++) {
									if (rankings[f][k] == topRanked)
										util += k/PLACE_AVG;
								}
								double temp = 0;
								for (int k = 0; k < NUM_QUESTIONS; k++) {
									temp += (questions[i][k]-questions[f][k])*(questions[i][k]-questions[f][k]);
								}
								util += Math.sqrt(temp)/PERSON_AVG;
								if (util < bestUtil) {
									bestUtil = util;
									best = f;
								}
							}
						}
						inGroup[best] = true;
						groups.get(i).add(best);
					}
				}
			}
			ArrayList<Integer> g = new ArrayList<Integer>();
			while (true) {
				double bestLottery = Double.MAX_VALUE;
				int best = -1;
				for (int i = 0; i < NUM_STUDENTS; i++) {
					if (groupLeader[i]) {
						int groupTotal = i;
						for (int p : groups.get(i))
							groupTotal += p;
						double lotto = (double)groupTotal/(groups.get(i).size()+1);
						if (lotto < bestLottery) {
							bestLottery = lotto;
							best = i;
						}
					}
				}
				if (best == -1)
					break;
				groupLeader[best] = false;
				g.add(best);
			}
			
			for (int leader : g) {
				int size = groups.get(leader).size()+1;
				boolean roomFound = false;
				for (int i = 0; i < NUM_TYPES; i++) {
					int room = rankings[leader][i];
					if (ROUND_TWO[room] && SUITE_SIZES[room] == size && spaceAvailable[room] > 0) {
						spaceAvailable[room] -= size;
						rooms[leader] = room;
						for (int f : groups.get(leader)) {
							rooms[f] = room;
							suitemates.get(leader).add(f);
							suitemates.get(f).add(leader);
							for (int f2 : groups.get(leader)) {
								if (f2 != f) {
									suitemates.get(f).add(f2);
								}
							}
						}
						if (DOUBLE_ROOMS[room]) {
							ArrayList<Integer> group = new ArrayList<Integer>();
							group.add(leader);
							for (int f : groups.get(leader)) {
								group.add(f);
							}
							while (group.size() > 0) {
								int p = group.remove(0);
								double bestUtil = Double.MAX_VALUE;
								int best = -1;
								for (int p2 : group) {
									double temp = 0;
									for (int k = 0; k < NUM_QUESTIONS; k++) {
										temp += (questions[p][k]-questions[p2][k])*(questions[p][k]-questions[p2][k]);
									}
									temp = Math.sqrt(temp);
									if (temp < bestUtil) {
										bestUtil = temp;
										best = p2;
									}
								}
								group.remove(new Integer(best));
								roommates[p] = best;
								roommates[best] = p;
							}
						}
						roomFound = true;
						break;
					}
				}
				if (!roomFound) {
					inGroup[leader] = false;
					for (int f : groups.get(leader))
						inGroup[f] = false;
					while (groups.get(leader).size() > 0)
						groups.get(leader).remove(0);
				}
			}
			
			for (int i = NUM_STUDENTS-1; i >= 0; i--) {
				int topRanked = -1;
				for (int j = 0; j < NUM_TYPES; j++) {
					if (!ROUND_TWO[rankings[i][j]]) {
						topRanked = rankings[i][j];
						break;
					}
				}
				if (!inGroup[i] && SUITE_SIZES[topRanked] != 1) {
					int totAvail = 0;
					ArrayList<Integer> availableFriends = new ArrayList<Integer>();
					for (int f : friends.get(i)) {
						if (!inGroup[f]){
							totAvail++;
							availableFriends.add(f);
						}
					}
					if (totAvail < SUITE_SIZES[topRanked]-1)
						continue;
					groupLeader[i] = true;
					inGroup[i] = true;
					for (int j = 0; j < SUITE_SIZES[topRanked]-1; j++) {
						double bestUtil = Double.MAX_VALUE;
						int best = -1;
						for (int f : availableFriends) {
							if (!inGroup[f]) {
								double util = (NUM_STUDENTS-f)/(NUM_STUDENTS/2);
								for (int k = 0; k < NUM_TYPES; k++) {
									if (rankings[f][k] == topRanked)
										util += k/PLACE_AVG;
								}
								double temp = 0;
								for (int k = 0; k < NUM_QUESTIONS; k++) {
									temp += (questions[i][k]-questions[f][k])*(questions[i][k]-questions[f][k]);
								}
								util += Math.sqrt(temp)/PERSON_AVG;
								if (util < bestUtil) {
									bestUtil = util;
									best = f;
								}
							}
						}
						inGroup[best] = true;
						groups.get(i).add(best);
					}
				}
			}
			g = new ArrayList<Integer>();
			while (true) {
				double bestLottery = 0;
				int best = -1;
				for (int i = 0; i < NUM_STUDENTS; i++) {
					if (groupLeader[i]) {
						int groupTotal = i;
						for (int p : groups.get(i))
							groupTotal += p;
						double lotto = (double)groupTotal/(groups.get(i).size()+1);
						if (lotto > bestLottery) {
							bestLottery = lotto;
							best = i;
						}
					}
				}
				if (best == -1)
					break;
				groupLeader[best] = false;
				g.add(best);
			}
			
			for (int leader : g) {
				int size = groups.get(leader).size()+1;
				boolean roomFound = false;
				for (int i = 0; i < NUM_TYPES; i++) {
					int room = rankings[leader][i];
					if (!ROUND_TWO[room] && SUITE_SIZES[room] == size && spaceAvailable[room] > 0) {
						spaceAvailable[room] -= size;
						rooms[leader] = room;
						for (int f : groups.get(leader)) {
							rooms[f] = room;
							suitemates.get(leader).add(f);
							suitemates.get(f).add(leader);
							for (int f2 : groups.get(leader)) {
								if (f2 != f) {
									suitemates.get(f).add(f2);
								}
							}
						}
						if (DOUBLE_ROOMS[room]) {
							ArrayList<Integer> group = new ArrayList<Integer>();
							group.add(leader);
							for (int f : groups.get(leader)) {
								group.add(f);
							}
							while (group.size() > 0) {
								int p = group.remove(0);
								double bestUtil = Double.MAX_VALUE;
								int best = -1;
								for (int p2 : group) {
									double temp = 0;
									for (int k = 0; k < NUM_QUESTIONS; k++) {
										temp += (questions[p][k]-questions[p2][k])*(questions[p][k]-questions[p2][k]);
									}
									temp = Math.sqrt(temp);
									if (temp < bestUtil) {
										bestUtil = temp;
										best = p2;
									}
								}
								group.remove(new Integer(best));
								roommates[p] = best;
								roommates[best] = p;
							}
						}
						roomFound = true;
						break;
					}
				}
				if (!roomFound) {
					inGroup[leader] = false;
					for (int f : groups.get(leader))
						inGroup[f] = false;
					while (groups.get(leader).size() > 0)
						groups.get(leader).remove(0);
				}
			}
			
			ArrayList<ArrayList<Integer>> peopleAssigned = new ArrayList<ArrayList<Integer>>();
			int spacesLeftThree = 0;
			int studentsLeft = 0;
			for (int i = 0; i < NUM_STUDENTS; i++) {
				if (!inGroup[i])
					studentsLeft++;
			}
			for (int i = 0; i < NUM_TYPES; i++) {
				peopleAssigned.add(new ArrayList<Integer>());
				if (!ROUND_TWO[i])
					spacesLeftThree += spaceAvailable[i];
			}
			for (int i = 0; i < NUM_TYPES; i++) {
				if (spacesLeftThree == 0)
					break;
				for (int j = NUM_STUDENTS-1; j >= NUM_STUDENTS-NUM_STUDENTS/300*100; j--) {
					if (spacesLeftThree == 0)
						break;
					if (!inGroup[j] && !ROUND_TWO[rankings[j][i]] && spaceAvailable[rankings[j][i]] > 0) {
						spaceAvailable[rankings[j][i]]--;
						spacesLeftThree--;
						rooms[j] = rankings[j][i];
						peopleAssigned.get(rooms[j]).add(j);
						inGroup[j] = true;
						studentsLeft--;
					}
				}
			}
			for (int i = 0; i < NUM_TYPES; i++) {
				if (spacesLeftThree == 0)
					break;
				for (int j = NUM_STUDENTS-NUM_STUDENTS/300*100-1; j >= NUM_STUDENTS-NUM_STUDENTS/300*100*2; j--) {
					if (spacesLeftThree == 0)
						break;
					if (!inGroup[j] && !ROUND_TWO[rankings[j][i]] && spaceAvailable[rankings[j][i]] > 0) {
						spaceAvailable[rankings[j][i]]--;
						spacesLeftThree--;
						rooms[j] = rankings[j][i];
						peopleAssigned.get(rooms[j]).add(j);
						inGroup[j] = true;
						studentsLeft--;
					}
				}
			}
			for (int i = 0; i < NUM_TYPES; i++) {
				for (int j = 0; j < NUM_STUDENTS; j++) {
					if (!inGroup[j] && spaceAvailable[rankings[j][i]] > 0 && studentsLeft >= SUITE_SIZES[rankings[j][i]]) {
						spaceAvailable[rankings[j][i]]--;
						rooms[j] = rankings[j][i];
						peopleAssigned.get(rooms[j]).add(j);
						inGroup[j] = true;
						studentsLeft--;
						int spacesLeftSuite = SUITE_SIZES[rooms[j]]-1;
						for (int m = 0; m < NUM_TYPES; m++) {
							if (spacesLeftSuite == 0)
								break;
							for (int n = 0; n < NUM_STUDENTS; n++) {
								if (spacesLeftSuite == 0)
									break;
								if (!inGroup[n] && rankings[n][m] == rooms[j] && spaceAvailable[rankings[n][m]] > 0) {
									spaceAvailable[rankings[n][m]]--;
									rooms[n] = rankings[n][m];
									peopleAssigned.get(rooms[n]).add(n);
									inGroup[n] = true;
									studentsLeft--;
									spacesLeftSuite--;
								}
							}
						}
					}
				}
			}
			for (int i = 0; i < NUM_TYPES; i++) {
				for (int p : peopleAssigned.get(i)) {
					studentsLeft++;
				}
			}
			for (int i = 0; i < NUM_TYPES; i++) {
				if (SUITE_SIZES[i] == 1) {
					studentsLeft -= peopleAssigned.get(i).size();
					while (peopleAssigned.get(i).size() > 0)
						peopleAssigned.get(i).remove(0);
				}
				ArrayList<ArrayList<Integer>> suites = new ArrayList<ArrayList<Integer>>();
				for (int j = 0; j < peopleAssigned.get(i).size()/SUITE_SIZES[i]; j++) {
					suites.add(new ArrayList<Integer>());
				}
				for (int j = 0; j < suites.size(); j++) {
					double bestUtil = Double.MAX_VALUE;
					int best1 = -1;
					int best2 = -1;
					for (int p1 : peopleAssigned.get(i)) {
						for (int p2 : peopleAssigned.get(i)) {
							if (p1 != p2) {
								double temp = 0;
								for (int k = 0; k < NUM_QUESTIONS; k++) {
									temp += (questions[p1][k]-questions[p2][k])*(questions[p1][k]-questions[p2][k]);
								}
								temp = Math.sqrt(temp);
								if (temp < bestUtil) {
									bestUtil = temp;
									best1 = p1;
									best2 = p2;
								}
							}
						}
					}
					suites.get(j).add(best1);
					suites.get(j).add(best2);
					peopleAssigned.get(i).remove(new Integer(best1));
					peopleAssigned.get(i).remove(new Integer(best2));
				}
				if (DOUBLE_ROOMS[i]) {
					while (peopleAssigned.get(i).size() > 0) {
						for (int j = 0; j < suites.size(); j++) {
							double bestUtil = Double.MAX_VALUE;
							int best = -1;
							for (int p : peopleAssigned.get(i)) {
								double tempBig = 0;
								for (int p2 : suites.get(j)) {
									double temp = 0;
									for (int k = 0; k < NUM_QUESTIONS; k++) {
										temp += (questions[p][k]-questions[p2][k])*(questions[p][k]-questions[p2][k]);
									}
									tempBig += Math.sqrt(temp);
								}
								if (tempBig < bestUtil) {
									bestUtil = tempBig;
									best = p;
								}
							}
							suites.get(j).add(best);
							peopleAssigned.get(i).remove(new Integer(best));
						}
						for (int j = 0; j < suites.size(); j++) {
							double bestUtil = Double.MAX_VALUE;
							int best = -1;
							for (int p : peopleAssigned.get(i)) {
								double temp = 0;
								for (int k = 0; k < NUM_QUESTIONS; k++) {
									temp += (questions[p][k]-questions[suites.get(j).get(suites.get(j).size()-1)][k])*(questions[p][k]-questions[suites.get(j).get(suites.get(j).size()-1)][k]);
								}
								temp = Math.sqrt(temp);
								if (temp < bestUtil) {
									bestUtil = temp;
									best = p;
								}
							}
							suites.get(j).add(best);
							peopleAssigned.get(i).remove(new Integer(best));
						}
					}
				} else {
					while (peopleAssigned.get(i).size() > 0) {
						for (int j = 0; j < suites.size(); j++) {
							double bestUtil = Double.MAX_VALUE;
							int best = -1;
							for (int p : peopleAssigned.get(i)) {
								double tempBig = 0;
								for (int p2 : suites.get(j)) {
									double temp = 0;
									for (int k = 0; k < NUM_QUESTIONS; k++) {
										temp += (questions[p][k]-questions[p2][k])*(questions[p][k]-questions[p2][k]);
									}
									tempBig += Math.sqrt(temp);
								}
								if (tempBig < bestUtil) {
									bestUtil = tempBig;
									best = p;
								}
							}
							suites.get(j).add(best);
							peopleAssigned.get(i).remove(new Integer(best));
						}
					}
				}
				for (ArrayList<Integer> s : suites) {
					for (int p1 : s) {
						for (int p2 : s) {
							if (p1 != p2) {
								suitemates.get(p1).add(p2);
							}
						}
					}
					if (DOUBLE_ROOMS[i]) {
						for (int j = 0; j < s.size(); j+=2) {
							roommates[s.get(j)] = s.get(j+1);
							roommates[s.get(j+1)] = s.get(j);
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
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("washu/current-"+String.format("%03d",z)+".csv"), "utf-8"));
			bw.write(output.toString());
			bw.close();
		}
	}
}
