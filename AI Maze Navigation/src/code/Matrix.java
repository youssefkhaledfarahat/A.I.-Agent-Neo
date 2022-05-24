package code;




import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import com.sun.management.OperatingSystemMXBean;


public class Matrix {
	//this method generates the grid
	public static String genGrid(){

		//the objects below are a mixture of the items used in 
		//the initState and the randomizing object used for randomizing stuff on the grid
		Random rand = new Random();
		String grid = "";	

		
		//grid initialization
		int M = rand.nextInt(11)+5;
		int N = rand.nextInt(11)+5;
		grid = grid + M + "," + N;
		boolean map[][] = new boolean[M][N];

		//carry number initialization
		int C = rand.nextInt(4)+1;
		grid = grid + ";" + C;
		
		//neo location  init
		int NeoX = rand.nextInt(M);
		int NeoY = rand.nextInt(N);
		grid = grid + ";" + NeoX + "," + NeoY;
		map[NeoX][NeoY] = true;

		
		//telephone loc init
		int TelephoneX = rand.nextInt(M);
		int TelephoneY = rand.nextInt(N);
		grid = grid + ";" + TelephoneX + "," + TelephoneY;
		map[TelephoneX][TelephoneY] = true;
		
		//random number of hostages pills agents and pads initialization + the current carryfor the init state
		int noHostages = rand.nextInt(8)+3;
		int noPills = rand.nextInt(noHostages)+1;
		int noAgents = rand.nextInt(M*N-2-noHostages-noPills);
		int noPads = rand.nextInt(M*N-2-noHostages-noPills-noAgents);
		int currentCarry = 0;
		
		
		//adding fundamentals: neo stats, saved hostages(0 in init state), hostages turned to agents (0 initially),
		//number of currently carried hostages (0), number of hostages, numbber of pills, number of agents. and leaving 
		//a place in the string for the carried hostages.

		
		//placing random agents on the grid and putting them in the initial state
		for(int i = 0; i<noAgents; i++){
			int AgentX = rand.nextInt(M);
			int AgentY = rand.nextInt(N);
			while(map[AgentX][AgentY]){
				AgentX = rand.nextInt(M);
				AgentY = rand.nextInt(N);
			}
			map[AgentX][AgentY] = true;
			if (i == 0){
				grid = grid + AgentX + "," + AgentY;
			}
			else{
				grid = grid + "," + AgentX + "," + AgentY;
			}
		}
		grid = grid + ";";
		
		//placing random pills on the grid and putting them in the initial state
		for(int i = 0; i<noPills; i++){
			int pillX = rand.nextInt(M);
			int pillY = rand.nextInt(N);
			while(map[pillX][pillY]){
				pillX = rand.nextInt(M);
				pillY = rand.nextInt(N);
			}
			map[pillX][pillY] = true;
			if(i == 0){
				grid = grid + pillX + "," + pillY;
			}
			else{
				grid = grid + ","+ pillX + "," + pillY;
			}
		}
		grid = grid + ";";
		
		
		//placing random jump pads on the map 
		for(int i = 0; i<noPads/2;i++){
			int startPadX = rand.nextInt(M);
			int startPadY = rand.nextInt(N);
			while(map[startPadX][startPadY]){
				startPadX = rand.nextInt(M);
				startPadY = rand.nextInt(N);
			}
			map[startPadX][startPadY] = true;
			int finishPadX = rand.nextInt(M);
			int finishPadY = rand.nextInt(N);
			while(map[finishPadX][finishPadY]){
				finishPadX = rand.nextInt(M);
				finishPadY = rand.nextInt(N);
			}
			map[finishPadX][finishPadY] = true;
			
			if(i == 0){
				grid = grid + startPadX + "," + startPadY + "," + finishPadX 
						+ "," + finishPadY + "," + finishPadX + "," 
						+ finishPadY + "," + startPadX + "," + startPadY;
			}
			else{
				grid = grid + ","+ startPadX + "," + startPadY + "," + finishPadX 
						+ "," + finishPadY + "," + finishPadX + "," 
						+ finishPadY + "," + startPadX + "," + startPadY;
			}


		}
		grid = grid + ";";
		
		
		
		//placing hostages randomly in the grid
		for(int i = 0; i<noHostages; i++){
			int HosX = rand.nextInt(M);
			int HosY = rand.nextInt(N);
			while(map[HosX][HosY]){
				HosX = rand.nextInt(M);
				HosY = rand.nextInt(N);
			}
			map[HosX][HosY] = true;
			
			int hosHealth = rand.nextInt(99)+1;
			if(i == 0){
				grid = grid +"," + HosX + "," + HosY + "," + hosHealth;
			}
			else{
				grid = grid +"," + HosX + "," + HosY + "," + hosHealth;
			}
			
		}
		return grid;
	}
	//this returns the inital state from the grid
	public static String initialize(String grid){
		String initialState = "";
		String[] gridArr = grid.split(";");
		int noHostage = gridArr[7].split(",").length/3;
		int noPills = gridArr[5].split(",").length/2;
		int noAgents = gridArr[4].split(",").length/2;
		String hostages = gridArr[7];
		String agents = gridArr[4];
		String pills = gridArr[5];
		initialState = initialState + gridArr[2] + ",0;" + "0;"+ "0;"+ "0;"+noHostage
				+";"+noPills+";"+noAgents+";"+";"+agents+";"
				+pills + ";"+ hostages + ";"+";"+"0;"+"0";
		
		return initialState;
	}
	public static String updateState(String grid,String prevState, String action){
		//0:neo
		//1:saved hostage number
		//2:hostages turned to agents number
		//3:currently carried hostages number
		//4:number of hostages
		//5:number of pills
		//6:number of agents
		//7:carried hostages
		//8:agents in
		//9:pills in
		//10:hostages in
		//11:hostages turned to agents
		//12:kills
		//13:deaths
		//the strings and string-arrays below are where we keep the fields we need to edit for the state
		String[] gridArr = grid.split(";");
		String[] arrayState = prevState.split(";");
		String[] agentKeeper = null;
		if(!arrayState[8].isEmpty()){
			agentKeeper = arrayState[8].split(",");
		}
		String[] HosKeeper = null;
		if(!arrayState[10].isEmpty()){
			HosKeeper = arrayState[10].split(",");
		}
		String [] carriedHostages = null;
		if(!arrayState[7].isEmpty()){
			carriedHostages  = arrayState[7].split(",");
		}
		
		String finalhostagentKeeper = "";
		String finalHostageKeeper = "";
		String[] finalHagKeepArr = null;
		if(!arrayState[11].isEmpty()){
			if(!arrayState[11].equals("none")){
				finalhostagentKeeper  = arrayState[11];
				finalHagKeepArr = arrayState[11].split(",");
			}
		}
		String finalCarried = "";
		String finalAgentKeeper = "";
		String newState = "";
		String pillLoc = "";
		String SurroundingAgents = "";
		String SurroundingHagents = "";
		short kills = Short.parseShort(arrayState[12]);
		short deaths = Short.parseShort(arrayState[13]);
		String[] neoKeep = arrayState[0].split(",");
		String[] pillKeep = arrayState[9].split(",");
		String[] jumpKeep = gridArr[6].split(",");
		short host = Short.parseShort(arrayState[4]);
		short hosta = Short.parseShort(arrayState[2]);
		String possibleJump = "";
		
		

		//the booleans below are what we use to check whether neo can perform the action or not.
		boolean checkUp = true;
		boolean checkDown = true;
		boolean checkLeft = true;
		boolean checkRight = true;
		boolean killAgent = false;
		boolean takePill = false;
		boolean jump = false;
		boolean pickup = false;
		boolean checkDrop = false;
		
		
		//these are the if conditions and loops where we perform the 
//		actual checks on whether the action is illegal or not for neo
		if(neoKeep[0].equals(gridArr[3].split(",")[0])
				&& neoKeep[1].equals(gridArr[3].split(",")[1]) 
				&& Integer.parseInt(arrayState[3])!=0){
			checkDrop = true;
		}
		if(agentKeeper != null){
			for(int i = 0; i < agentKeeper.length-1;i+=2){
				if(Integer.parseInt(neoKeep[1]) == Integer.parseInt(agentKeeper[i+1]) 
						&& Integer.parseInt(neoKeep[0])-1 == Integer.parseInt(agentKeeper[i]) && checkUp){
					checkUp = false;
					killAgent = true;
					if(SurroundingAgents.isEmpty()){
						SurroundingAgents = SurroundingAgents + agentKeeper[i]+","+agentKeeper[i+1];
					}
					else{
						SurroundingAgents = SurroundingAgents + "," + agentKeeper[i]+","+agentKeeper[i+1];
					}
				}
				if(Integer.parseInt(neoKeep[1]) == Integer.parseInt(agentKeeper[i+1]) 
						&& Integer.parseInt(neoKeep[0])+1 == Integer.parseInt(agentKeeper[i]) && checkDown){
					checkDown = false;
					killAgent = true;
					if(SurroundingAgents.isEmpty()){
						SurroundingAgents = SurroundingAgents + agentKeeper[i]+","+agentKeeper[i+1];
					}
					else{
						SurroundingAgents = SurroundingAgents + "," + agentKeeper[i]+","+agentKeeper[i+1];
					}
				}
				if(Integer.parseInt(neoKeep[1])+1 == Integer.parseInt(agentKeeper[i+1]) 
						&& Integer.parseInt(neoKeep[0]) == Integer.parseInt(agentKeeper[i]) && checkRight){
					checkRight = false;
					killAgent = true;
					if(SurroundingAgents.isEmpty()){
						SurroundingAgents = SurroundingAgents + agentKeeper[i]+","+agentKeeper[i+1];
					}
					else{
						SurroundingAgents = SurroundingAgents + "," + agentKeeper[i]+","+agentKeeper[i+1];
					}
				}
				if(Integer.parseInt(neoKeep[1])-1 == Integer.parseInt(agentKeeper[i+1]) 
						&& Integer.parseInt(neoKeep[0]) == Integer.parseInt(agentKeeper[i]) && checkLeft){
					checkLeft = false;
					killAgent = true;
					if(SurroundingAgents.isEmpty()){
						SurroundingAgents = SurroundingAgents + agentKeeper[i]+","+agentKeeper[i+1];
					}
					else{
						SurroundingAgents = SurroundingAgents + "," + agentKeeper[i]+","+agentKeeper[i+1];
					}
				}
			}
		}
		if(finalHagKeepArr != null){
			for(int i = 0; i < finalHagKeepArr.length-1;i+=2){
				if(Integer.parseInt(neoKeep[1]) == Integer.parseInt(finalHagKeepArr[i+1]) 
						&& Integer.parseInt(neoKeep[0])-1 == Integer.parseInt(finalHagKeepArr[i])){
					checkUp = false;
					killAgent = true;
					if(SurroundingHagents.isEmpty()){
						SurroundingHagents = SurroundingHagents + finalHagKeepArr[i]+","+finalHagKeepArr[i+1];
					}
					else{
						SurroundingHagents = SurroundingHagents + "," + finalHagKeepArr[i]+","+finalHagKeepArr[i+1];
					}
				}
				if(Integer.parseInt(neoKeep[1]) == Integer.parseInt(finalHagKeepArr[i+1]) 
						&& Integer.parseInt(neoKeep[0])+1 == Integer.parseInt(finalHagKeepArr[i])){
					checkDown = false;
					killAgent = true;
					if(SurroundingHagents.isEmpty()){
						SurroundingHagents = SurroundingHagents + finalHagKeepArr[i]+","+finalHagKeepArr[i+1];
					}
					else{
						SurroundingHagents = SurroundingHagents + "," + finalHagKeepArr[i]+","+finalHagKeepArr[i+1];
					}				}
				if(Integer.parseInt(neoKeep[1])+1 == Integer.parseInt(finalHagKeepArr[i+1]) 
						&& Integer.parseInt(neoKeep[0]) == Integer.parseInt(finalHagKeepArr[i])){
					checkRight = false;
					killAgent = true;
					if(SurroundingHagents.isEmpty()){
						SurroundingHagents = SurroundingHagents + finalHagKeepArr[i]+","+finalHagKeepArr[i+1];
					}
					else{
						SurroundingHagents = SurroundingHagents + "," + finalHagKeepArr[i]+","+finalHagKeepArr[i+1];
					}				}
				if(Integer.parseInt(neoKeep[1])-1 == Integer.parseInt(finalHagKeepArr[i+1]) 
						&& Integer.parseInt(neoKeep[0]) == Integer.parseInt(finalHagKeepArr[i])&& checkLeft){
					checkLeft = false;
					killAgent = true;
					if(SurroundingHagents.isEmpty()){
						SurroundingHagents = SurroundingHagents + finalHagKeepArr[i]+","+finalHagKeepArr[i+1];
					}
					else{
						SurroundingHagents = SurroundingHagents + "," + finalHagKeepArr[i]+","+finalHagKeepArr[i+1];
					}
				}
			}
		}
		if(HosKeeper != null){
			for(int i = 2; i < HosKeeper.length-1;i+=3){
				if(Integer.parseInt(neoKeep[1]) == Integer.parseInt(HosKeeper[i-1]) 
						&& Integer.parseInt(neoKeep[0])-1 == Integer.parseInt(HosKeeper[i-2])
						&& Integer.parseInt(HosKeeper[i]) >= 98){
					checkUp = false;
				}
				if(Integer.parseInt(neoKeep[1]) == Integer.parseInt(HosKeeper[i-1] ) 
						&& Integer.parseInt(neoKeep[0])+1 == Integer.parseInt(HosKeeper[i-2])
						&& Integer.parseInt(HosKeeper[i]) >= 98 ){
					checkDown = false;
				}
				if(Integer.parseInt(neoKeep[1])+1 == Integer.parseInt(HosKeeper[i-1]) 
						&& Integer.parseInt(neoKeep[0]) == Integer.parseInt(HosKeeper[i-2])
						&& Integer.parseInt(HosKeeper[i]) >= 98){
					checkRight = false;
				}
				if(Integer.parseInt(neoKeep[1])-1 == Integer.parseInt(HosKeeper[i-1]) 
						&& Integer.parseInt(neoKeep[0]) == Integer.parseInt(HosKeeper[i-2])
						&& Integer.parseInt(HosKeeper[i]) >= 98){
					checkLeft = false;
					}
			}
		}
		for(int i = 0; i < pillKeep.length-1;i+=2){
			if(Integer.parseInt(neoKeep[1]) == Integer.parseInt(pillKeep[i+1]) 
					&& Integer.parseInt(neoKeep[0]) == Integer.parseInt(pillKeep[i])){
				takePill = true;
				pillLoc = pillLoc + pillKeep[i] + "," +  pillKeep[i+1];
			}
		}
		if(HosKeeper != null)
			for(int i = 0; i < HosKeeper.length;i+=3){
				if(Integer.parseInt(neoKeep[1]) == Integer.parseInt(HosKeeper[i+1]) 
						&& Integer.parseInt(neoKeep[0]) == Integer.parseInt(HosKeeper[i])){
					if(Short.parseShort(gridArr[1]) >= Short.parseShort(arrayState[3])){
						pickup = true;
						if(Short.parseShort(HosKeeper[i+2]) >= 98){
							killAgent = false;
						}
					}
				}
			}

		for(int i = 0; i < jumpKeep.length-1;i+=4){
			if(neoKeep[0].equals(jumpKeep[i]) &&
					neoKeep[1].equals(jumpKeep[i+1])){
				jump = true;
				possibleJump = jumpKeep[i+2]+","+jumpKeep[i+3];
			}
		}
		if(neoKeep[2].equals("100")){
			return prevState;
		}
		
		//this switch is where we check what the string action is, 
		//and in each case we perform the +2 dmg on the 
		//carried hostages and the hostages on the map and the actions itself
		//the new state is returned in each case, with the edited values modified.
		switch(action){
		case("up"):
			if(Short.parseShort(neoKeep[0])-1 < 0){
				return prevState;
			}
			else if(!checkUp){
				return prevState;
			}
			else{
				short j = Short.parseShort(neoKeep[0]);
				j -= 1;
				if(HosKeeper != null)
				for(int i = 2; i < HosKeeper.length; i+=3){
					short numberDamage = Short.parseShort(HosKeeper[i]);
					numberDamage += 2;
					if(numberDamage >= 100){
						if(finalhostagentKeeper.equals("none")||finalhostagentKeeper.isEmpty()){
							finalhostagentKeeper = finalhostagentKeeper + HosKeeper[i-2] + "," + HosKeeper[i-1];
						}
						else{
							finalhostagentKeeper = finalhostagentKeeper +","+ HosKeeper[i-2]+"," + HosKeeper[i-1];
						}
						hosta += 1;
						host -= 1;
						deaths += 1;
					}
					else{
						if(finalHostageKeeper.isEmpty())
							finalHostageKeeper = finalHostageKeeper + HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
						else
							finalHostageKeeper = finalHostageKeeper +","+ HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
					}
				}
				
				if(carriedHostages != null){
					for (int i = 0; i < carriedHostages.length;i++){
					short numDam = Short.parseShort(carriedHostages[i]);
					numDam += 2;
					if(numDam == 100 || numDam == 101){
						deaths += 1;
					}
					if(finalCarried.isEmpty())
						finalCarried = finalCarried + numDam;
					else
						finalCarried = finalCarried + "," + numDam;
					}
				}

				newState = newState + j + "," + neoKeep[1]+","+neoKeep[2]
						+";"+arrayState[1]+";"+hosta+";"+arrayState[3]+";"+host+";"+arrayState[5]
						+";"+arrayState[6]+";"+finalCarried+";"+arrayState[8]+";"+arrayState[9]
						+";"  + finalHostageKeeper + ";" + finalhostagentKeeper + ";" + kills + ";" + deaths;
				return newState;
			}
		case("down"):
			if(Short.parseShort(neoKeep[0])+1 > Short.parseShort(gridArr[0].split(",")[0])-1){
				return prevState;
			}
			else if(!checkDown){
				return prevState;
			}
			else{
				short j = Short.parseShort(neoKeep[0]);
				j += 1;

				if(HosKeeper != null)
					for(int i = 2; i < HosKeeper.length; i+=3){
						short numberDamage = Short.parseShort(HosKeeper[i]);
						numberDamage += 2;
						if(numberDamage >= 100){
							if(finalhostagentKeeper.equals("none") || finalhostagentKeeper.isEmpty()){
								finalhostagentKeeper = finalhostagentKeeper + HosKeeper[i-2] + "," + HosKeeper[i-1];
							}
							else{
								finalhostagentKeeper = finalhostagentKeeper +","+ HosKeeper[i-2]+"," + HosKeeper[i-1];
							}
							hosta += 1;
							host -= 1;
							deaths += 1;
						}
						else{
							if(finalHostageKeeper.isEmpty())
								finalHostageKeeper = finalHostageKeeper + HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
							else
								finalHostageKeeper = finalHostageKeeper +","+ HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
						}
					}
					
				if(carriedHostages != null){
					for (int i = 0; i < carriedHostages.length;i++){
					short numDam = Short.parseShort(carriedHostages[i]);
					numDam += 2;
					if(numDam == 100 || numDam == 101){
						deaths += 1;
					}
					if(finalCarried.isEmpty())
						finalCarried = finalCarried + numDam;
					else
						finalCarried = finalCarried + "," + numDam;
					}
				}

				newState = newState + j + "," + neoKeep[1]+","+neoKeep[2]
						+";"+arrayState[1]+";"+hosta+";"+arrayState[3]+";"+host+";"+arrayState[5]
						+";"+arrayState[6]+";"+finalCarried+";"+arrayState[8]+";"+arrayState[9]
						+";"  +finalHostageKeeper +";" +  finalhostagentKeeper + ";" + kills + ";" + deaths;				
				return newState;
			}
		case("left"):
			if(Short.parseShort(neoKeep[1])-1 < 0){
				return prevState;
			}
			else if(!checkLeft){
				return prevState;
			}
			else{
				short j = Short.parseShort(neoKeep[1]);
				j -= 1;
				
				if(HosKeeper != null)
					for(int i = 2; i < HosKeeper.length; i+=3){
						short numberDamage = Short.parseShort(HosKeeper[i]);
						numberDamage += 2;
						if(numberDamage >= 100){
							if(finalhostagentKeeper.equals("none")||finalhostagentKeeper.isEmpty()){
								finalhostagentKeeper = finalhostagentKeeper + HosKeeper[i-2] + "," + HosKeeper[i-1];
							}
							else{
								finalhostagentKeeper = finalhostagentKeeper +","+ HosKeeper[i-2]+"," + HosKeeper[i-1];
							}
							hosta += 1;
							host -= 1;
							deaths += 1;
						}
						else{
							if(finalHostageKeeper.isEmpty())
								finalHostageKeeper = finalHostageKeeper + HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
							else
								finalHostageKeeper = finalHostageKeeper +","+ HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
						}
					}
					
				if(carriedHostages != null){
					for (int i = 0; i < carriedHostages.length;i++){
					short numDam = Short.parseShort(carriedHostages[i]);
					numDam += 2;
					if(numDam == 100 || numDam == 101){
						deaths += 1;
					}
					if(finalCarried.isEmpty())
						finalCarried = finalCarried + numDam;
					else{
						finalCarried = finalCarried + "," + numDam;
					}
					}
				}
				newState = newState + neoKeep[0] + "," + j +","+neoKeep[2]
						+";"+arrayState[1]+";"+hosta+";"+arrayState[3]+";"+host+";"+arrayState[5]
						+";"+arrayState[6]+";"+finalCarried+";"+arrayState[8]+";"+arrayState[9]
						+";"  +finalHostageKeeper +";" + finalhostagentKeeper + ";" + kills + ";" + deaths;
				
				return newState;
			}
		case("right"):
			if(Short.parseShort(neoKeep[1])+1 > Short.parseShort(gridArr[0].split(",")[1])-1){
				return prevState;
			}
			else if(!checkRight){
				return prevState;
			}
			else{
				short j = Short.parseShort(neoKeep[1]);
				j += 1;

				if(HosKeeper != null)
					for(int i = 2; i < HosKeeper.length; i+=3){
						short numberDamage = Short.parseShort(HosKeeper[i]);
						numberDamage += 2;
						if(numberDamage >= 100){
							if(finalhostagentKeeper.equals("none")|| finalhostagentKeeper.isEmpty()){
								finalhostagentKeeper = finalhostagentKeeper + HosKeeper[i-2] + "," + HosKeeper[i-1];
							}
							else{
								finalhostagentKeeper = finalhostagentKeeper +","+ HosKeeper[i-2]+"," + HosKeeper[i-1];
							}
							hosta += 1;
							host -= 1;
							deaths += 1;
						}
						else{
							if(finalHostageKeeper.isEmpty())
								finalHostageKeeper = finalHostageKeeper + HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
							else
								finalHostageKeeper = finalHostageKeeper +","+ HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
						}
					}
									
				if(carriedHostages != null){
					for (int i = 0; i < carriedHostages.length;i++){
					short numDam = Short.parseShort(carriedHostages[i]);
					numDam += 2;
					if(numDam == 100 || numDam == 101){
						deaths += 1;
					}
					if(finalCarried.isEmpty())
						finalCarried = finalCarried + numDam;
					else
						finalCarried = finalCarried + "," + numDam;
				}
				}
				newState = newState + neoKeep[0] + "," + j +","+neoKeep[2]
						+";"+arrayState[1]+";"+hosta+";"+arrayState[3]+";"+host+";"+arrayState[5]
						+";"+arrayState[6]+";"+finalCarried+";"+arrayState[8]+";"+arrayState[9]
						+";" + finalHostageKeeper+ ";" + finalhostagentKeeper + ";" + kills + ";" + deaths;
				
				return newState;
			}
		case("fly"):
			if(!jump){
				return prevState;
			}
			else{
				
				if(HosKeeper != null)
					for(int i = 2; i < HosKeeper.length; i+=3){
						short numberDamage = Short.parseShort(HosKeeper[i]);
						numberDamage += 2;
						if(numberDamage >= 100){
							if(finalhostagentKeeper.equals("none")||finalhostagentKeeper.isEmpty()){
								finalhostagentKeeper = finalhostagentKeeper + HosKeeper[i-2] + "," + HosKeeper[i-1];
							}
							else{
								finalhostagentKeeper = finalhostagentKeeper +","+ HosKeeper[i-2]+"," + HosKeeper[i-1];
							}
							hosta += 1;
							host -= 1;
							deaths += 1;
						}
						else{
							if(finalHostageKeeper.isEmpty())
								finalHostageKeeper = finalHostageKeeper + HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
							else
								finalHostageKeeper = finalHostageKeeper +","+ HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
						}
					}
									
				
				if(carriedHostages != null){
					for (int i = 0; i < carriedHostages.length;i++){
					short numDam = Short.parseShort(carriedHostages[i]);
					numDam += 2;
					if(numDam == 100 || numDam == 101){
						deaths += 1;
					}
					if(finalCarried.isEmpty())
						finalCarried = finalCarried + numDam;
					else
						finalCarried = finalCarried + "," + numDam;

					}
				}
				newState = possibleJump +","+neoKeep[2]
						+";"+arrayState[1]+";"+hosta+";"+arrayState[3]+";"+host+";"+arrayState[5]
						+";"+arrayState[6]+";"+finalCarried+";"+arrayState[8]+";"+arrayState[9]
						+";"  +finalHostageKeeper +";" + finalhostagentKeeper + ";" + kills + ";" + deaths;
				

				return newState;
			}
		case("takePill"):
			if(!takePill){
				return prevState;
			}
			else{
				short j = Short.parseShort(neoKeep[2]);
				j -= 20;
				if(j<0){
					j =0;
				}
				String pills = "";
				String finalPills  = "";
				for(int i = 0 ;i<pillKeep.length-1;i+=2){
					pills =pillKeep[i]+","+pillKeep[i+1];
					if(pills.equals(pillLoc)){
					}
					else{
						if(finalPills.isEmpty())
							finalPills	= finalPills + pillKeep[i]+","+pillKeep[i+1];
						else
							finalPills	= finalPills+"," +pillKeep[i]+","+pillKeep[i+1];
					}
				}
				arrayState[9] = finalPills;
				short jy =Short.parseShort(arrayState[5]);
                jy -=1;

				if(HosKeeper != null)
				for(int i = 2; i < HosKeeper.length; i+=3){
					short numberDamage = Short.parseShort(HosKeeper[i]);
					numberDamage += -18;
					if(numberDamage < 0){
						numberDamage = 0;
					}
					if(numberDamage >= 100){
						if(finalhostagentKeeper.isEmpty() || finalhostagentKeeper.contentEquals("none")) {
							finalhostagentKeeper = finalhostagentKeeper + HosKeeper[i-2]+"," + HosKeeper[i-1];
						}
						else {
							finalhostagentKeeper = finalhostagentKeeper +","+ HosKeeper[i-2]+"," + HosKeeper[i-1];
						}
						hosta += 1;
						host -= 1;
						deaths += 1;						
					}
					else{
						if(finalHostageKeeper.isEmpty())
							finalHostageKeeper = finalHostageKeeper + HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
						else
							finalHostageKeeper = finalHostageKeeper +","+ HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;						
					}
				}
				
				if(carriedHostages != null){
					for (int i = 0; i < carriedHostages.length;i++){
					short numDam = Short.parseShort(carriedHostages[i]);
					if(numDam <= 100){
						numDam -= 18;
					}
					else{
						numDam += 2;
					}
					if(finalCarried.isEmpty())
						finalCarried = finalCarried + numDam;
					else{
						finalCarried = finalCarried + "," + numDam;
					}
					}
				}
				newState = newState + neoKeep[0] + "," + neoKeep[1]+","+j
						+";"+arrayState[1]+";"+hosta+";"+arrayState[3]+";"+host+";" +jy
						+";"+arrayState[6]+";"+finalCarried+";"+arrayState[8]+";"+finalPills
						+";"  +finalHostageKeeper +";" + finalhostagentKeeper + ";" + kills + ";" + deaths;
				
				return newState;
			}
		case("carry"):
			if(!pickup){
				return prevState;
			}
			else{
				short jy =Short.parseShort(arrayState[3]);
                jy +=1;
                
                
                if(carriedHostages != null){
					for (int i = 0; i < carriedHostages.length;i++){
					short numDam = Short.parseShort(carriedHostages[i]);
					numDam += 2;
					if(numDam == (101)|| numDam == (100))
						deaths += 1;
					if(finalCarried.isEmpty())
						finalCarried = finalCarried + numDam;
					else
						finalCarried = finalCarried + "," + numDam;
					}
				}

				if(HosKeeper != null)
				for(int i = 2; i < HosKeeper.length; i+=3){
					short numberDamage = Short.parseShort(HosKeeper[i]);
					numberDamage += 2;

					if(numberDamage < 0){
						numberDamage = 0;
					}
					if(HosKeeper[i-2].equals(neoKeep[0]) && HosKeeper[i-1].equals(neoKeep[1])){
						if(finalCarried.isEmpty()){
							finalCarried = finalCarried + numberDamage;
						}
						else{
							finalCarried = finalCarried + "," + numberDamage;
						}
						if(numberDamage >=100) {
							deaths +=1;
						}
					}
					else{
					if(numberDamage >= 100){
						if(finalhostagentKeeper.isEmpty() || finalhostagentKeeper.equals("none"))
							finalhostagentKeeper = finalhostagentKeeper + HosKeeper[i-2]+"," + HosKeeper[i-1];
						else
							finalhostagentKeeper = finalhostagentKeeper +","+ HosKeeper[i-2]+"," + HosKeeper[i-1];
						hosta += 1;
						host -= 1;
						deaths += 1;
					}
					else{
						if(finalHostageKeeper.isEmpty())
								finalHostageKeeper = finalHostageKeeper + HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
						else
							finalHostageKeeper = finalHostageKeeper +","+ HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;						
					}
					}
				}
				newState = newState + neoKeep[0] + "," + neoKeep[1]+","+neoKeep[2]
						+";"+arrayState[1]+";"+hosta+";"+jy+";"+host+";"+arrayState[5]
						+";"+arrayState[6]+";"+finalCarried+";"+arrayState[8]+";"+arrayState[9]
						+";"  +finalHostageKeeper +";" + finalhostagentKeeper + ";" + kills + ";" + deaths;
						
				return newState;
			}
		case("kill"):
			if(!killAgent){
				return prevState;
			}
			else{
				if(carriedHostages != null){
				for (int i = 0; i < carriedHostages.length;i++){
					short numDam = Short.parseShort(carriedHostages[i]);
					numDam += 2;
					if(numDam == 100 || numDam == 101)
						deaths +=1;
					if(finalCarried.isEmpty())
						finalCarried = finalCarried + numDam;
					else
						finalCarried = finalCarried + "," + numDam;
				}
				}
				String surrAg[] = null;
				if(!SurroundingAgents.isEmpty()){
					surrAg = SurroundingAgents.split(",");
				}
				String surrHag[] = null;
				if(!SurroundingHagents.isEmpty()){
					surrHag = SurroundingHagents.split(",");
				}
				short dead = 0;
				short hagdead = 0;
				boolean[] checkToRemoveAgent;
				boolean[] checkToRemoveHagent;
				finalhostagentKeeper = "";
				if(surrAg != null && agentKeeper != null){
					checkToRemoveAgent = new boolean[agentKeeper.length/2];
					for(int i = 0; i < agentKeeper.length-1; i+=2){
						for(int j = 0; j < surrAg.length-1;j+=2){
							if(surrAg[j].equals(agentKeeper[i]) && surrAg[j+1].equals(agentKeeper[i+1])){
								checkToRemoveAgent[i/2] = true;
								dead += 1;
								kills +=1;
							}
						}
					}
					for(int i = 0; i < checkToRemoveAgent.length;i++){
						if(!checkToRemoveAgent[i]){
							if(finalAgentKeeper.isEmpty())
								finalAgentKeeper = finalAgentKeeper + agentKeeper[i*2]+","+agentKeeper[(i*2)+1];
							else
								finalAgentKeeper = finalAgentKeeper + "," + agentKeeper[i*2]+","+agentKeeper[(i*2)+1];
						}
					}
				}
				if(surrAg == null){
					finalAgentKeeper = arrayState[8];
				}
				if(surrHag != null && finalHagKeepArr != null){
					checkToRemoveHagent = new boolean[finalHagKeepArr.length/2];
					for(int i = 0; i < finalHagKeepArr.length-1; i+=2){
						for(int j = 0; j < surrHag.length-1;j+=2){
							if(surrHag[j].equals(finalHagKeepArr[i]) && surrHag[j+1].equals(finalHagKeepArr[i+1])){
								checkToRemoveHagent[i/2] = true;
								hagdead += 1;
								kills += 1;
							}
						}
					}
					for(int i = 0; i < checkToRemoveHagent.length;i++){
						if(!checkToRemoveHagent[i]){
							if(finalhostagentKeeper.isEmpty() || finalhostagentKeeper.equals("none"))
								finalhostagentKeeper = finalhostagentKeeper + finalHagKeepArr[i*2]+","+finalHagKeepArr[(i*2)+1];
							else
								finalhostagentKeeper = finalhostagentKeeper + "," + finalHagKeepArr[i*2]+","+finalHagKeepArr[(i*2)+1];
						}
					}

				}
				if(surrHag == null){
					finalhostagentKeeper = arrayState[11];
				}
				
					
				if(HosKeeper != null)
				for(int i = 2; i < HosKeeper.length; i+=3){
					short numberDamage = Short.parseShort(HosKeeper[i]);
					numberDamage += 2;
					if(numberDamage < 0){
						numberDamage = 0;
					}
					if(numberDamage >= 100){
						if(finalhostagentKeeper.isEmpty()){
							finalhostagentKeeper = finalhostagentKeeper + HosKeeper[i-2]+"," + HosKeeper[i-1];
						}
						else{
							finalhostagentKeeper = finalhostagentKeeper +","+ HosKeeper[i-2]+"," + HosKeeper[i-1];
						}
						hosta += 1;
						host -= 1;
						deaths += 1;
					}
					else{
						if(finalHostageKeeper.isEmpty())
							finalHostageKeeper = finalHostageKeeper + HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
						else
							finalHostageKeeper = finalHostageKeeper +","+ HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;						
					}
				}
				int finalDamage = Integer.parseInt(neoKeep[2]) +20;
				int finalAgents = Integer.parseInt(arrayState[6]) - dead;
				hosta = (short) (hosta - hagdead);
				
				newState = neoKeep[0]+","+neoKeep[1] +","+finalDamage
						+";"+arrayState[1]+";"+hosta+";"+arrayState[3]+";"+host+";"+arrayState[5]
						+";"+finalAgents+";"+finalCarried+";"+finalAgentKeeper+";"+arrayState[9]
						+";"  +finalHostageKeeper +";" + finalhostagentKeeper + ";" + kills + ";" + deaths;
				return newState;
				
			}
		case("drop"):
			if(!checkDrop){
				return prevState;
			}
			else{
				short jy =Short.parseShort(arrayState[3]);
                jy = 0;
                
                short savedThisTime = 0;
                short savedOnes = Short.parseShort(arrayState[1]);
                if(carriedHostages != null)
				for (int i = 0; i < carriedHostages.length;i++){
					short numDam = Short.parseShort(carriedHostages[i]);
					if(numDam <= 100){
						savedOnes +=1;
					}
					savedThisTime += 1;
				}
				
				if(HosKeeper != null)
				for(int i = 2; i < HosKeeper.length; i+=3){
					short numberDamage = Short.parseShort(HosKeeper[i]);
					numberDamage += 2;

					if(numberDamage >= 100){
						if(finalhostagentKeeper.isEmpty() || finalhostagentKeeper.equals("none"))
							finalhostagentKeeper = finalhostagentKeeper + HosKeeper[i-2]+"," + HosKeeper[i-1];
						else
							finalhostagentKeeper = finalhostagentKeeper + ","+ HosKeeper[i-2]+"," + HosKeeper[i-1];
						hosta += 1;
						host -= 1;
						deaths += 1;
					}
					else{
						if(finalHostageKeeper.isEmpty())
							finalHostageKeeper = finalHostageKeeper + HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;
						else
							finalHostageKeeper = finalHostageKeeper +","+ HosKeeper[i-2]+","+HosKeeper[i-1]+","+numberDamage;						
					}
				}
				host -= savedThisTime;
				newState = newState + neoKeep[0] + "," + neoKeep[1]+","+neoKeep[2]
						+";"+savedOnes+";"+hosta+";"+0+";"+host+";"+arrayState[5]
						+";"+arrayState[6]+";"+finalCarried+";"+arrayState[8]+";"+arrayState[9]
						+";"  +finalHostageKeeper + ";" + finalhostagentKeeper + ";" + kills + ";" + deaths;
						
				return newState;
			}
		}
		//if the action string is incorrect, we return the previous state
		return prevState;
	}
		
	


	public static String solve(String grid, String type , boolean visualise) {
		Node root = new Node() ;
		root.state = initialize(grid);
	    boolean goal = false;
	    String goalSequence = "";
	    // Breadth first search
		if(type == "BF") {
		//to count nodes expanded
		int c  = 0; 
		 HashSet<String> hashset=new HashSet<String>();
		 hashset.add(root.state);
		int killGoal = 0 ;
		int deathGoal = 0;
		Queue<Node> q  = new LinkedList<>();
		q.add(root);
		while(!q.isEmpty()) {
			c++;
			Node current  =q.remove();
			String [] arrayState = current.state.split(";");
			goal = goalTest(current.state,grid);
			if(goal) {
				deathGoal =deathGoal+Integer.parseInt(arrayState[13]);
				killGoal = killGoal+Integer.parseInt(arrayState[12]);
				goalSequence = "";
				//node goal = node goal
				Node current2  = current;
				while(current2.parent != null) {
					if(goalSequence.contentEquals("")) {
						goalSequence = current2.operator ;
						current2 = current2.parent;
					}
					else {
						goalSequence = current2.operator +","+ goalSequence ;
						current2 = current2.parent;
					}
				}
				if(visualise) { 
					System.out.println(goalSequence+";"+deathGoal+";"+killGoal+";"+c);
				}
				return  goalSequence+";"+ deathGoal+";"+killGoal+";"+c;
			}
			else {	
				// nodes for 9 operators
				Node up = new Node() ;
				Node down = new Node() ;
				Node right = new Node() ;
				Node left = new Node() ;
				Node kill = new Node() ;
				Node drop = new Node() ;
				Node takePill = new Node() ;
				Node carry = new Node() ;
				Node fly = new Node() ;
				
				up.operator="up";
				down.operator="down";
				right.operator="right";
				left.operator="left";
				kill.operator="kill";
				drop.operator="drop";
				takePill.operator="takePill";
				carry.operator="carry";
				fly.operator="fly";
				
				up.parent = current;
				down.parent = current;
				right.parent = current;
				left.parent = current;
				kill.parent = current;
				drop.parent = current;
				takePill.parent = current;
				carry.parent = current;
				fly.parent = current;
							
				up.level = up.parent.level+1;
				down.level = down.parent.level+1;
				right.level = right.parent.level+1;
				left.level = left.parent.level+1;
				kill.level = kill.parent.level+1;
				drop.level = drop.parent.level+1;
				takePill.level = takePill.parent.level+1;
				carry.level = carry.parent.level+1;
				fly.level = fly.parent.level+1;
				
				// save in the hashset
				up.state= updateState(grid,current.state,"up");
				up.hashState = updateC(up);
				
				down.state = updateState(grid,current.state,"down");
				down.hashState = updateC(down);
				
				right.state = updateState(grid,current.state,"right");
				right.hashState = updateC(right);
				
				left.state = updateState(grid,current.state,"left");
				left.hashState = updateC(left);
				
				kill.state = updateState(grid,current.state,"kill");
				kill.hashState = updateC(kill);
				
				drop.state = updateState(grid,current.state,"drop");
				drop.hashState = updateC(drop);
				
				takePill.state = updateState(grid,current.state,"takePill");
				takePill.hashState = updateC(takePill);
				
				carry.state = updateState(grid,current.state,"carry");
				carry.hashState = updateC(carry);
				
				fly.state = updateState(grid,current.state,"fly");
				fly.hashState = updateC(fly);
				
				// if not in the hashset add in the queue
				if(!(hashset.contains(fly.hashState))){
					hashset.add(fly.hashState);
					q.add(fly);
				} 
				if(!(hashset.contains(up.hashState))){
					hashset.add(up.hashState);
					q.add(up);
				} 

				if(!(hashset.contains(right.hashState))){
					hashset.add(right.hashState);
					q.add(right);
				}
				if(!(hashset.contains(down.hashState))){
					hashset.add(down.hashState);
					q.add(down);
				}
				if(!(hashset.contains(left.hashState))){
					hashset.add(left.hashState);
					q.add(left);
				} 

				if(!(hashset.contains(carry.hashState))){ 
					hashset.add(carry.hashState);
					q.add(carry);
				}

				if(!(hashset.contains(kill.hashState))){
					hashset.add(kill.hashState);
					q.add(kill);
				}
				if(!(hashset.contains(takePill.hashState))){
					hashset.add(takePill.hashState);
					q.add(takePill);
				}
				if(!(hashset.contains(drop.hashState))){ 
					hashset.add(drop.hashState);
					q.add(drop);
				}
			}
		}
		}
		
		// Depth first search
		if(type == "DF") {
			//to count nodes expanded
			int c  = 0;
			 HashSet<String> hashset=new HashSet<String>();// state
			 hashset.add(root.state);
			int killGoal = 0 ;
			int deathGoal = 0;
			 Stack<Node> s= new Stack<>();
			s.add(root);
			while(!s.isEmpty()) {
				c++;
				Node current  =s.pop();
				String [] arrayState = current.state.split(";");
				goal = goalTest(current.state,grid);
				if(goal) {
					deathGoal =deathGoal+Integer.parseInt(arrayState[13]);
					killGoal = killGoal+Integer.parseInt(arrayState[12]);
					goalSequence = "";
					//node goal = node goal
					Node current2  = current;
					while(current2.parent != null) {
						if(goalSequence.contentEquals("")) {
							goalSequence = current2.operator ;
							current2 = current2.parent;
						}
						else {
							goalSequence = current2.operator +","+ goalSequence ;
							current2 = current2.parent;
						}
					}
					if(visualise) {
						System.out.println(goalSequence+";"+deathGoal+";"+killGoal+";"+c);
					}
					return  goalSequence+";"+ deathGoal+";"+killGoal+";"+c;
				}
				else {	
					// nodes for 9 operators
					Node up = new Node() ;
					Node down = new Node() ;
					Node right = new Node() ;
					Node left = new Node() ;
					Node kill = new Node() ;
					Node drop = new Node() ;
					Node takePill = new Node() ;
					Node carry = new Node() ;
					Node fly = new Node() ;
					
					up.operator="up";
					down.operator="down";
					right.operator="right";
					left.operator="left";
					kill.operator="kill";
					drop.operator="drop";
					takePill.operator="takePill";
					carry.operator="carry";
					fly.operator="fly";
					
					up.parent = current;
					down.parent = current;
					right.parent = current;
					left.parent = current;
					kill.parent = current;
					drop.parent = current;
					takePill.parent = current;
					carry.parent = current;
					fly.parent = current;
								
					up.level = up.parent.level+1;
					down.level = down.parent.level+1;
					right.level = right.parent.level+1;
					left.level = left.parent.level+1;
					kill.level = kill.parent.level+1;
					drop.level = drop.parent.level+1;
					takePill.level = takePill.parent.level+1;
					carry.level = carry.parent.level+1;
					fly.level = fly.parent.level+1;
					
					// save in the hashset
					down.state = updateState(grid,current.state,"down");
					down.hashState = updateC(down);
					
					
					up.state= updateState(grid,current.state,"up");
					up.hashState = updateC(up);
					

					
					right.state = updateState(grid,current.state,"right");
					right.hashState = updateC(right);
					
					left.state = updateState(grid,current.state,"left");
					left.hashState = updateC(left);
					
					kill.state = updateState(grid,current.state,"kill");
					kill.hashState = updateC(kill);
					
					drop.state = updateState(grid,current.state,"drop");
					drop.hashState = updateC(drop);
					
					takePill.state = updateState(grid,current.state,"takePill");
					takePill.hashState = updateC(takePill);
					
					carry.state = updateState(grid,current.state,"carry");
					carry.hashState = updateC(carry);
					
					fly.state = updateState(grid,current.state,"fly");
					fly.hashState = updateC(fly);
					
					// if not in the hashset add in the queue
					
					if(!(hashset.contains(fly.hashState))){
						hashset.add(fly.hashState);
						s.push(fly);
					} 
					if(!(hashset.contains(up.hashState))){
						hashset.add(up.hashState);
						s.push(up);
					} 

					if(!(hashset.contains(right.hashState))){
						hashset.add(right.hashState);
						s.push(right);
					}
					if(!(hashset.contains(down.hashState))){
						hashset.add(down.hashState);
						s.push(down);
					}
					if(!(hashset.contains(left.hashState))){
						hashset.add(left.hashState);
					s.push(left);
					} 

					if(!(hashset.contains(carry.hashState))){ 
						hashset.add(carry.hashState);
						s.push(carry);
					}

					if(!(hashset.contains(kill.hashState))){
						hashset.add(kill.hashState);
						s.push(kill);
					}
					if(!(hashset.contains(takePill.hashState))){
						hashset.add(takePill.hashState);
						s.push(takePill);
					}
					if(!(hashset.contains(drop.hashState))){ 
						hashset.add(drop.hashState);
						s.push(drop);
					}
				}
			}
		}
		
		// Iterative Deepning
		if(type == "ID") {
			HashSet<String> hashset=new HashSet<String>();// state
			hashset.add(root.state);
			//to count nodes expanded
			int c = 0;
			int limit =0;
			int killGoal = 0 ;
			int deathGoal = 0;
			Stack<Node> s  = new Stack<Node>();
			s.push(root);	
			while(!s.isEmpty()) {
				c++;
				Node current  =s.pop();
				if(current.level>limit) {
					limit=limit +1;
				}
				String [] arrayState = current.state.split(";");
				goal = goalTest(current.state,grid);
				if(goal) {
					deathGoal =deathGoal+Integer.parseInt(arrayState[13]);
					killGoal = killGoal+Integer.parseInt(arrayState[12]);
				goalSequence = "";
					Node current2  = current;
					while(current2.parent != null) {
						if(goalSequence.contentEquals("")) {
							goalSequence = current2.operator ;
							current2 = current2.parent;
						}
						else {
							goalSequence = current2.operator +","+ goalSequence ;
							//node goal = node goal
							current2 = current2.parent;
						}
					}
					if(visualise) {
						System.out.println(goalSequence+";"+deathGoal+";"+killGoal+";"+c);
					}
					return  goalSequence+";"+ deathGoal+";"+killGoal+";"+c*limit;
				}
				else {	 
					// check if the level of the node is less than the limit
					if(current.level<=limit) {
						// nodes for 9 operators
						Node up = new Node() ;
						Node down = new Node() ;
						Node right = new Node() ;
						Node left = new Node() ;
						Node kill = new Node() ;
						Node drop = new Node() ;
						Node takePill = new Node() ;
						Node carry = new Node() ;
						Node fly = new Node() ;
						
						up.operator="up";
						down.operator="down";
						right.operator="right";
						left.operator="left";
						kill.operator="kill";
						drop.operator="drop";
						takePill.operator="takePill";
						carry.operator="carry";
						fly.operator="fly";
						
						up.parent = current;
						down.parent = current;
						right.parent = current;
						left.parent = current;
						kill.parent = current;
						drop.parent = current;
						takePill.parent = current;
						carry.parent = current;
						fly.parent = current;
									
						up.level = up.parent.level+1;
						down.level = down.parent.level+1;
						right.level = right.parent.level+1;
						left.level = left.parent.level+1;
						kill.level = kill.parent.level+1;
						drop.level = drop.parent.level+1;
						takePill.level = takePill.parent.level+1;
						carry.level = carry.parent.level+1;
						fly.level = fly.parent.level+1;
						
						// save in the hashset
						
						down.state = updateState(grid,current.state,"down");
						down.hashState = updateC(down);
						
						
						up.state= updateState(grid,current.state,"up");
						up.hashState = updateC(up);
						

						
						right.state = updateState(grid,current.state,"right");
						right.hashState = updateC(right);
						
						left.state = updateState(grid,current.state,"left");
						left.hashState = updateC(left);
						
						kill.state = updateState(grid,current.state,"kill");
						kill.hashState = updateC(kill);
						
						drop.state = updateState(grid,current.state,"drop");
						drop.hashState = updateC(drop);
						
						takePill.state = updateState(grid,current.state,"takePill");
						takePill.hashState = updateC(takePill);
						
						carry.state = updateState(grid,current.state,"carry");
						carry.hashState = updateC(carry);
						
						fly.state = updateState(grid,current.state,"fly");
						fly.hashState = updateC(fly);
						
						// if not in the hashset add in the queue
						if(!(hashset.contains(fly.hashState))){
							hashset.add(fly.hashState);
							s.push(fly);
						} 
						if(!(hashset.contains(up.hashState))){
							hashset.add(up.hashState);
							s.push(up);
						} 

						if(!(hashset.contains(right.hashState))){
							hashset.add(right.hashState);
							s.push(right);
						}
						if(!(hashset.contains(down.hashState))){
							hashset.add(down.hashState);
							s.push(down);
						}
						if(!(hashset.contains(left.hashState))){
							hashset.add(left.hashState);
						s.push(left);
						} 

						if(!(hashset.contains(carry.hashState))){ 
							hashset.add(carry.hashState);
							s.push(carry);
						}

						if(!(hashset.contains(kill.hashState))){
							hashset.add(kill.hashState);
							s.push(kill);
						}
						if(!(hashset.contains(takePill.hashState))){
							hashset.add(takePill.hashState);
							s.push(takePill);
						}
						if(!(hashset.contains(drop.hashState))){ 
							hashset.add(drop.hashState);
							s.push(drop);
						}
					}
				}
				}
			}
		// uniform cost
		if(type == "UC") {
			 HashSet<String> hashset=new HashSet<String>();// state
			 hashset.add(root.state);
			//to count nodes expanded
			int c = 0;
			int killGoal = 0 ;
			int deathGoal = 0;
		   	Queue<Node> pq = new PriorityQueue<Node>();
		root.cost = costFunction(root.state);
		pq.add(root);
		while(!	pq.isEmpty()) {
			Node current  =pq.remove();
			String [] arrayState = current.state.split(";");
			goal = goalTest(current.state,grid);
			c++;
			if(goal) {			
				deathGoal =deathGoal+Integer.parseInt(arrayState[13]);
				killGoal = killGoal+Integer.parseInt(arrayState[12]);
				goalSequence = "";
				//node goal = node goal
				Node current2  = current;
				while(current2.parent != null) {
					if(goalSequence.contentEquals("")) {
						goalSequence = current2.operator ;
						current2 = current2.parent;
					}
					else {
						goalSequence = current2.operator +","+ goalSequence ;
						current2 = current2.parent;
					}
				}
				if(visualise) {
					System.out.println(goalSequence+";"+deathGoal+";"+killGoal+";"+c);
				}
				return  goalSequence+";"+deathGoal  +";"+killGoal+";"+c;
			}
			else {	
				// nodes for 9 operators
				Node up = new Node() ;
				Node down = new Node() ;
				Node right = new Node() ;
				Node left = new Node() ;
				Node kill = new Node() ;
				Node drop = new Node() ;
				Node takePill = new Node() ;
				Node carry = new Node() ;
				Node fly = new Node() ;
				
				up.operator="up";
				down.operator="down";
				right.operator="right";
				left.operator="left";
				kill.operator="kill";
				drop.operator="drop";
				takePill.operator="takePill";
				carry.operator="carry";
				fly.operator="fly";
				
				up.parent = current;
				down.parent = current;
				right.parent = current;
				left.parent = current;
				kill.parent = current;
				drop.parent = current;
				takePill.parent = current;
				carry.parent = current;
				fly.parent = current;
				
				
				up.level = up.parent.level+1;
				down.level = down.parent.level+1;
				right.level = right.parent.level+1;
				left.level = left.parent.level+1;
				kill.level = kill.parent.level+1;
				drop.level = drop.parent.level+1;
				takePill.level = takePill.parent.level+1;
				carry.level = carry.parent.level+1;
				fly.level = fly.parent.level+1;
				

				

				// save in the hashset
			    up.state= updateState(grid,current.state,"up");
				up.hashState = updateC(up);
				
				down.state = updateState(grid,current.state,"down");
				down.hashState = updateC(down);
				
				right.state = updateState(grid,current.state,"right");
				right.hashState = updateC(right);
				
				left.state = updateState(grid,current.state,"left");
				left.hashState = updateC(left);
				
				kill.state = updateState(grid,current.state,"kill");
				kill.hashState = updateC(kill);
				
				drop.state = updateState(grid,current.state,"drop");
				drop.hashState = updateC(drop);
				
				takePill.state = updateState(grid,current.state,"takePill");
				takePill.hashState = updateC(takePill);
				
				carry.state = updateState(grid,current.state,"carry");
				carry.hashState = updateC(carry);
				
				fly.state = updateState(grid,current.state,"fly");
				fly.hashState = updateC(fly);
				
				//calculate the cost function
			    up.cost = costFunction(up.state);
			    down.cost = costFunction(down.state);
			    right.cost =costFunction(right.state);
			    left.cost = costFunction(left.state);
			    kill.cost  = costFunction(kill.state);
			    drop.cost = costFunction(drop.state);
			    takePill.cost = costFunction(takePill.state);
			    carry.cost = costFunction(carry.state);
			    fly.cost = costFunction(fly.state);
							
			    
			 // if not in the hashset add in the queue
			    
				if(!(hashset.contains(fly.hashState))){
					hashset.add(fly.hashState);
					pq.add(fly);
				} 
				if(!(hashset.contains(up.hashState))){
					hashset.add(up.hashState);
					pq.add(up);
				} 

				if(!(hashset.contains(right.hashState))){
					hashset.add(right.hashState);
					pq.add(right);
				}
				if(!(hashset.contains(down.hashState))){
					hashset.add(down.hashState);
					pq.add(down);
				}
				if(!(hashset.contains(left.hashState))){
					hashset.add(left.hashState);
					pq.add(left);
				} 

				if(!(hashset.contains(carry.hashState))){ 
					hashset.add(carry.hashState);
					pq.add(carry);
				}

				if(!(hashset.contains(kill.hashState))){
					hashset.add(kill.hashState);
					pq.add(kill);
				}
				if(!(hashset.contains(takePill.hashState))){
					hashset.add(takePill.hashState);
					pq.add(takePill);
				}
				if(!(hashset.contains(drop.hashState))){ 
					hashset.add(drop.hashState);
					pq.add(drop);
				}
				}
		}
		}
		
	  // Greedy Search
		if(type == "GR1"||type == "GR2") {
			 HashSet<String> hashset=new HashSet<String>();// state
			 hashset.add(root.state);
				//to count nodes expanded
			 int c  = 0;
			int killGoal = 0 ;
			int deathGoal = 0;
		   	Queue<Node> pq = new PriorityQueue<Node>();
		// heuristic function for the goal
		root.cost = heuristicFunction(root.state,grid);
		pq.add(root);
		while(!	pq.isEmpty()) {
			Node current  =pq.remove();
			c++;
			String [] arrayState = current.state.split(";");
			goal = goalTest(current.state,grid);
			if(goal) {			
				deathGoal =deathGoal+Integer.parseInt(arrayState[13]);
				killGoal = killGoal+Integer.parseInt(arrayState[12]);
				goalSequence = "";
				Node current2  = current;
				while(current2.parent != null) {
					if(goalSequence.contentEquals("")) {
						goalSequence = current2.operator ;
						current2 = current2.parent;
					}
					else {
						goalSequence = current2.operator +","+ goalSequence ;
						current2 = current2.parent;
					}
				}
				if(visualise) {
					System.out.println(goalSequence+";"+deathGoal+";"+killGoal+";"+c);
				}
				return  goalSequence+";"+ deathGoal+";"+killGoal+";"+c;
			}
			else {	
				// nodes for 9 operators
				Node up = new Node() ;
				Node down = new Node() ;
				Node right = new Node() ;
				Node left = new Node() ;
				Node kill = new Node() ;
				Node drop = new Node() ;
				Node takePill = new Node() ;
				Node carry = new Node() ;
				Node fly = new Node() ;
				
				up.operator="up";
				down.operator="down";
				right.operator="right";
				left.operator="left";
				kill.operator="kill";
				drop.operator="drop";
				takePill.operator="takePill";
				carry.operator="carry";
				fly.operator="fly";
				
				up.parent = current;
				down.parent = current;
				right.parent = current;
				left.parent = current;
				kill.parent = current;
				drop.parent = current;
				takePill.parent = current;
				carry.parent = current;
				fly.parent = current;
				
				
				up.level = up.parent.level+1;
				down.level = down.parent.level+1;
				right.level = right.parent.level+1;
				left.level = left.parent.level+1;
				kill.level = kill.parent.level+1;
				drop.level = drop.parent.level+1;
				takePill.level = takePill.parent.level+1;
				carry.level = carry.parent.level+1;
				fly.level = fly.parent.level+1;
				
			
				

				// save in the hashset
			    up.state= updateState(grid,current.state,"up");
				up.hashState = updateC(up);
				
				down.state = updateState(grid,current.state,"down");
				down.hashState = updateC(down);
				
				right.state = updateState(grid,current.state,"right");
				right.hashState = updateC(right);
				
				left.state = updateState(grid,current.state,"left");
				left.hashState = updateC(left);
				
				kill.state = updateState(grid,current.state,"kill");
				kill.hashState = updateC(kill);
				
				drop.state = updateState(grid,current.state,"drop");
				drop.hashState = updateC(drop);
				
				takePill.state = updateState(grid,current.state,"takePill");
				takePill.hashState = updateC(takePill);
				
				carry.state = updateState(grid,current.state,"carry");
				carry.hashState = updateC(carry);
				
				fly.state = updateState(grid,current.state,"fly");
				fly.hashState = updateC(fly);
				
                 //  heuristic Function for greedy 1
				
				if(type=="GR1") {
				    up.cost = heuristicFunction(up.state,grid);
				    down.cost = heuristicFunction(down.state,grid);
				    right.cost =heuristicFunction(right.state,grid);
				    left.cost = heuristicFunction(left.state,grid);
				    kill.cost  = heuristicFunction(kill.state,grid);
				    drop.cost = heuristicFunction(drop.state,grid);
				    takePill.cost = heuristicFunction(takePill.state,grid);
				    carry.cost = heuristicFunction(carry.state,grid);
				    fly.cost = heuristicFunction(fly.state,grid);
				}
                //  heuristic Function 2 for greedy 2
				if(type=="GR2") {
				    up.cost = heuristicFunction2(up.state,grid);
				    down.cost = heuristicFunction2(down.state,grid);
				    right.cost =heuristicFunction2(right.state,grid);
				    left.cost = heuristicFunction2(left.state,grid);
				    kill.cost  = heuristicFunction2(kill.state,grid);
				    drop.cost = heuristicFunction2(drop.state,grid);
				    takePill.cost = heuristicFunction2(takePill.state,grid);
				    carry.cost = heuristicFunction2(carry.state,grid);
				    fly.cost = heuristicFunction2(fly.state,grid);
				}

				// if not in the hashset add in the queue		
				if(!(hashset.contains(fly.hashState))){
					hashset.add(fly.hashState);
					pq.add(fly);
				} 
				if(!(hashset.contains(up.hashState))){
					hashset.add(up.hashState);
					pq.add(up);
			} 

				if(!(hashset.contains(right.hashState))){
					hashset.add(right.hashState);
					pq.add(right);
				}

				if(!(hashset.contains(left.hashState))){
					hashset.add(left.hashState);
					pq.add(left);
				} 
				if(!(hashset.contains(carry.hashState))){ 
					hashset.add(carry.hashState);
					pq.add(carry);
				}
				if(!(hashset.contains(down.hashState))){
					hashset.add(down.hashState);
					pq.add(down);
				}
				if(!(hashset.contains(kill.hashState))){
					hashset.add(kill.hashState);
					pq.add(kill);
				}
				if(!(hashset.contains(drop.hashState))){ 
					hashset.add(drop.hashState);
					pq.add(drop);
				}
				if(!(hashset.contains(takePill.hashState))){
					hashset.add(takePill.hashState);
					pq.add(takePill);
				}
				}
		}
		}	
		// A* Search
		if(type == "AS1"||type == "AS2") {
			//to count nodes expanded
			int c = 0;
			 HashSet<String> hashset=new HashSet<String>();// state
				int killGoal = 0 ;
				int deathGoal = 0;
			 hashset.add(root.state);
		   	Queue<Node> pq = new PriorityQueue<Node>();
		root.cost = heuristicFunction(root.state, grid)+costFunction(root.state);
		pq.add(root);
		while(!	pq.isEmpty()) {
			Node current  =pq.remove();
			c++;
			String [] arrayState = current.state.split(";");
			goal = goalTest(current.state,grid);
			if(goal) {			
				deathGoal =deathGoal+Integer.parseInt(arrayState[13]);
				killGoal = killGoal+Integer.parseInt(arrayState[12]);
				goalSequence = "";
				Node current2  = current;
				while(current2.parent != null) {
					if(goalSequence.contentEquals("")) {
						goalSequence = current2.operator ;
						current2 = current2.parent;
					}
					else {
						goalSequence = current2.operator +","+ goalSequence ;
						current2 = current2.parent;
					}
				}
				if(visualise) {
					System.out.println(goalSequence+";"+deathGoal+";"+killGoal+";"+c);
				}
				return  goalSequence+";"+ deathGoal+";"+killGoal+";"+c;
			}
			else {	
				// nodes for 9 operators
				Node up = new Node() ;
				Node down = new Node() ;
				Node right = new Node() ;
				Node left = new Node() ;
				Node kill = new Node() ;
				Node drop = new Node() ;
				Node takePill = new Node() ;
				Node carry = new Node() ;
				Node fly = new Node() ;
				
				up.operator="up";
				down.operator="down";
				right.operator="right";
				left.operator="left";
				kill.operator="kill";
				drop.operator="drop";
				takePill.operator="takePill";
				carry.operator="carry";
				fly.operator="fly";
				
				up.parent = current;
				down.parent = current;
				right.parent = current;
				left.parent = current;
				kill.parent = current;
				drop.parent = current;
				takePill.parent = current;
				carry.parent = current;
				fly.parent = current;
				
				
				up.level = up.parent.level+1;
				down.level = down.parent.level+1;
				right.level = right.parent.level+1;
				left.level = left.parent.level+1;
				kill.level = kill.parent.level+1;
				drop.level = drop.parent.level+1;
				takePill.level = takePill.parent.level+1;
				carry.level = carry.parent.level+1;
				fly.level = fly.parent.level+1;
				
			
				
				// save in the hashset
			    up.state= updateState(grid,current.state,"up");
				up.hashState = updateC(up);
				
				down.state = updateState(grid,current.state,"down");
				down.hashState = updateC(down);
				
				right.state = updateState(grid,current.state,"right");
				right.hashState = updateC(right);
				
				left.state = updateState(grid,current.state,"left");
				left.hashState = updateC(left);
				
				kill.state = updateState(grid,current.state,"kill");
				kill.hashState = updateC(kill);
				
				drop.state = updateState(grid,current.state,"drop");
				drop.hashState = updateC(drop);
				
				takePill.state = updateState(grid,current.state,"takePill");
				takePill.hashState = updateC(takePill);
				
				carry.state = updateState(grid,current.state,"carry");
				carry.hashState = updateC(carry);
				
				fly.state = updateState(grid,current.state,"fly");
				fly.hashState = updateC(fly);
				// add heuristic function and cost function
				if(type=="AS1") {
				    up.cost = heuristicFunction(up.state,grid)+costFunction(up.state);
				    down.cost = heuristicFunction(down.state,grid)+costFunction(down.state);
				    right.cost =heuristicFunction(right.state,grid)+costFunction(right.state);
				    left.cost = heuristicFunction(left.state,grid)+ costFunction(left.state);
				    kill.cost  = heuristicFunction(kill.state,grid)+costFunction(kill.state);
				    drop.cost = heuristicFunction(drop.state,grid)+costFunction(drop.state);
				    takePill.cost = heuristicFunction(takePill.state,grid)+costFunction(takePill.state);
				    carry.cost = heuristicFunction(carry.state,grid)+ costFunction(carry.state);
				    fly.cost = heuristicFunction(fly.state,grid)+costFunction(fly.state);
				}
				// add heuristic function 2 and cost function
				if(type=="AS2") {
				    up.cost = heuristicFunction2(up.state,grid)+costFunction(up.state);
				    down.cost = heuristicFunction2(down.state,grid)+costFunction(down.state);
				    right.cost =heuristicFunction2(right.state,grid)+costFunction(right.state);
				    left.cost = heuristicFunction2(left.state,grid)+ costFunction(left.state);
				    kill.cost  = heuristicFunction2(kill.state,grid)+costFunction(kill.state);
				    drop.cost = heuristicFunction2(drop.state,grid)+costFunction(drop.state);
				    takePill.cost = heuristicFunction2(takePill.state,grid)+costFunction(takePill.state);
				    carry.cost = heuristicFunction2(carry.state,grid)+ costFunction(carry.state);
				    fly.cost = heuristicFunction2(fly.state,grid)+costFunction(fly.state);
				}

				// if not in the hashset add in the queue
							
					if(!(hashset.contains(up.hashState))){
						hashset.add(up.hashState);
						pq.add(up);
					}
					if(!(hashset.contains(down.hashState))){
						hashset.add(down.hashState);
						pq.add(down);
					}
					if(!(hashset.contains(left.hashState))){
						hashset.add(left.hashState);
						pq.add(left);
					}
					if(!(hashset.contains(right.hashState))){
						hashset.add(right.hashState);
						pq.add(right);
					}
					if(!(hashset.contains(kill.hashState))){
						hashset.add(kill.hashState);
						pq.add(kill);
					}
					if(!(hashset.contains(carry.hashState))){
						hashset.add(carry.hashState);
						pq.add(carry);
					}
					if(!(hashset.contains(drop.hashState))){
						hashset.add(drop.hashState);
						pq.add(drop);
					}
					if(!(hashset.contains(fly.hashState))){
						hashset.add(fly.hashState);
						pq.add(fly);
					}
					if(!(hashset.contains(takePill.hashState))){
						hashset.add(takePill.hashState);
						pq.add(takePill);
					}
				}
			
		}
		}
		
		
		
	return "No Solution";
	}
	public static int costFunction(String state) {
		//here we multiply the kills by a thousand and the deaths by 11000 and then add both
		String [] arrayState  = state.split(";");
		int cost =Integer.parseInt(arrayState[12])*1000+ Integer.parseInt(arrayState[13])*11000;
		return cost;
	}

	private static int heuristicFunction2(String state,String grid) {
		//  heuristic Function 2  is calculated by multiplying manhattan distance and the number of death
		String[] arrState = state.split(";");
		short deaths = Short.parseShort(arrState[13]);
		int actual=deaths+1;
		String[] neoPos = arrState[0].split(",");
		String[] gridArr = grid.split(",");
		String[] telephonepos = (gridArr[3].split(",")[0]).split(";");
		int manhatandistance= Math.abs(Integer.parseInt(neoPos[0])-Integer.parseInt(telephonepos[0]))+ 
				Math.abs(Integer.parseInt(neoPos[1]) - Integer.parseInt(telephonepos[1]));
		return actual * manhatandistance;
	}
	private static int heuristicFunction(String state,String grid) {
		//  heuristic Function 2  is calculated by multiplying manhattan distance and the cost function

		String[] arrState = state.split(";");
		short kills = Short.parseShort(arrState[12]);
		int killscost=kills*1000;
		short deaths = Short.parseShort(arrState[13]);
		int deathscost=kills*11000;
		int cost=deathscost+killscost;
		cost=cost/2;
		
		int actualkills=kills+1;
		int actualdeath=deaths+1;
		int actual=actualkills+actualdeath;
		String[] neoPos = arrState[0].split(",");
		String[] gridArr = grid.split(",");
		String[] telephonepos = (gridArr[3].split(",")[0]).split(";");
		int manhatandistance= Math.abs(Integer.parseInt(neoPos[0])-Integer.parseInt(telephonepos[0]))+ 
				Math.abs(Integer.parseInt(neoPos[1]) - Integer.parseInt(telephonepos[1]));
		if(manhatandistance==0){
			cost=cost*manhatandistance;
		}
		return cost;
	}
	public static void main(String [] args) {
		//the commented code below is the code we used for the examples in the report
		String grid0 = "5,5;2;3,4;1,2;0,3,1,4;2,3;4,4,0,2,0,2,4,4;2,2,91,2,4,62";
		String grid10 = "5,5;4;1,1;4,1;2,4,0,4,3,2,3,0,4,2,0,1,1,3,2,1;4,0,4,4,1,0;2,0,0,2,0,2,2,0;0,0,62,4,3,45,3,3,39,2,3,40";
//		  OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

//		solve(grid10,"BF",true);	
//		solve(grid10,"DF",true);
//		solve(grid10,"UC",true);
//		solve(grid10,"ID",true);
//		solve(grid10,"GR1",true);
//		solve(grid10,"GR2",true);
//		solve(grid10,"AS1",true);
//		solve(grid10,"AS2",true);
//		System.out.println();
//		solve(grid0,"BF",true);	
//		solve(grid0,"DF",true);
//		solve(grid0,"UC",true);
//		solve(grid0,"ID",true);
//		solve(grid0,"GR1",true);
//		solve(grid0,"GR2",true);
//		solve(grid0,"AS1",true);
//		solve(grid0,"AS2",true);
//		  for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
//			    method.setAccessible(true);
//			    if (method.getName().startsWith("get")
//			        && Modifier.isPublic(method.getModifiers())) {
//			            Object value;
//			        try {
//			            value = method.invoke(operatingSystemMXBean);
//			        } catch (Exception e) {
//			            value = e;
//			        } // try
//			        System.out.println(method.getName() + " = " + value);
//			    } // if
//			  } // for
			

	
	
}
public static boolean goalTest(String state, String grid){
	//0:neo
	//1:saved hostage number
	//2:hostages turned to agents number
	//3:currently carried hostages number
	//4:number of hostages
	//5:number of pills
	//6:number of agents
	//7:carried hostages
	//8:agents in
	//9:pills in
	//10:hostages in
	//11:hostages turned to agents
	//12:deaths
	//13:kills
	//in this method we test whether the state is a goal state or not,
	//we do this by comparing the values that affect the goal state
	//which are neo's location with respect to telephone booth,
	//number of remaining hostages and number of carried hostages,
	
	String[] arrState = state.split(";");
	String[] neoPos = arrState[0].split(",");
	String[] gridArr = grid.split(";");
	if(arrState[2].equals("0") && arrState[4].equals("0") && neoPos[0].equals(gridArr[3].split(",")[0]) 
			&& neoPos[1].equals(gridArr[3].split(",")[1]) && arrState[3].equals("0")){
		return true;
	}
	
	return false;
}
public static String updateC(Node up) {
	//in this method we change the values of the healths of
	//the carried hostages and the hostages on the field to
	//alive (less than 100) or dead (more than 100, the case is only for the carried hostages)
	//in order decrease the number of states in the hashset so that
	//neo will not keep alternating between two cells and add a lot of damage on
	//the hostages where each time we get a new state
	String [] arrayStateUP = up.state.split(";");
	  String stateReverted = "";
	String placehold =  "";
	String placehold2 =  "";
	if(arrayStateUP[7]!=null) {
		  String []	 deathAS = arrayStateUP[7].split(",");
		  for(int i = 0;i<deathAS.length;i++) {
			  if(!(deathAS[i].isEmpty())){
				  if(Short.parseShort(deathAS[i])>=100) {
					  if(i == deathAS.length-1) {
						  placehold = placehold + "dead" ;
					  }
					  else {
						  placehold = placehold + "dead," ;
					  }
				  }
				  else {
					  if(i == deathAS.length-1) {
						  placehold = placehold + "alive" ;
					  }
					  else {
						  placehold = placehold + "alive," ;
					  }
					  
				  }
			  }

		  }
		}
		arrayStateUP[7] = placehold;
		if(arrayStateUP[10]!=null) {	
			  String []	 killAS = arrayStateUP[10].split(",");
			  for(int i = 2;i<killAS.length;i+=3) {
				  if(!(killAS[i].isEmpty())){
					  if(Short.parseShort(killAS[i])<100) {
						  if(placehold2.isEmpty()) {
							  placehold2 = placehold2+killAS[i-2]+","+killAS[i-1]+ ",alive";
						  }
						  else {
							  placehold2 = placehold2+","+killAS[i-2]+","+killAS[i-1]+ ",alive";
						  }

					  }
				}  
			  }
			  arrayStateUP[10] = placehold2 ;
			  for(int i = 0;i< arrayStateUP.length;i++) {
				  if(stateReverted.isEmpty()) {
					  stateReverted = arrayStateUP[i];
				  }
				  else {
					  stateReverted = stateReverted +";"+arrayStateUP[i];
				  }
			  }
			}

	return stateReverted;	
}
}
