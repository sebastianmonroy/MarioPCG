package dk.itu.mario.level;

import java.util.*;

import dk.itu.mario.MarioInterface.Constraints;
import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.engine.sprites.SpriteTemplate;
import dk.itu.mario.engine.sprites.Enemy;


public class MyLevel extends Level{
	//Store information about the level
	 public   int ENEMIES = 0; //the number of enemies the level contains
	 public   int BLOCKS_EMPTY = 0; // the number of empty blocks
	 public   int BLOCKS_COINS = 0; // the number of coin blocks
	 public   int BLOCKS_POWER = 0; // the number of power blocks
	 public   int COINS = 0; //These are the coins in boxes that Mario collect
	 
 
	private static Random levelSeedRandom = new Random();
		public static long lastSeed;

		Random random;

  
		private int difficulty;
		private int type;
		private int gaps;
		private int MIN_ENEMIES = 10;
		private int MAX_ENEMIES = 100;
		private int MIN_COINS = 10;
		private int MAX_COINS = 300;
		private int MIN_GAPS = 5;
		private int MAX_GAPS = 20;
		private int MIN_POWERS = 1;
		private int MAX_POWERS = 5;
		private int MIN_COINBLOCKS = 20;
		private int MAX_COINBLOCKS = 100;
		private int MAX_COMPLETION_TIME = 200;
		private int MIN_COMPLETION_TIME = MAX_COMPLETION_TIME-173;
		private double MIN_ENEMY_PROBABILITY = 0.1;
		private double MAX_ENEMY_PROBABILITY = 0.7;

		private double goombaProbability;
		private double turtleProbability;
		private double armoredProbability;

		private int MAX_HILL_ELEVATION_CHANGE;
		private int MIN_HILL_STRETCH;
		private int MAX_HILL_STRETCH;
		private double HILL_FREQUENCY;

		private int MAX_GROUND_ELEVATION_CHANGE;
		private int MIN_GROUND_STRETCH;
		private int MAX_GROUND_STRETCH;
		private double GROUND_GAP_FREQUENCY;

		private GamePlay GPM;
		private int[][] elevMap;
		private PlayerStyle playerType;

		private enum PlayerStyle {
			SPEEDRUNNER,
			KILLER,
			COLLECTOR,
			COMPLETIONIST
		}

		public MyLevel(int width, int height, long seed, int difficulty, int type, GamePlay PlayerMetrics)
		{
			super(width, height);
			

			if (PlayerMetrics != null) {
				this.GPM = PlayerMetrics;
			} else {
				this.GPM = new GamePlay();
				this.GPM = GPM.read("player.txt");
			}

			elevMap = new int[4][width];
			for (int i = 0; i < 4; i++){
				for (int j = 0; j < width; j++){
					elevMap[i][j] = height;
				}
			}

			creat(seed, difficulty, type);
		}

		private void setPlayerType(PlayerStyle ps) {
			// set player type by altering game parameters
			playerType = ps;

			switch (playerType) {
				case SPEEDRUNNER:
					GPM.coinsCollected = 0;
					GPM.totalCoins = 0;
					GPM.totalEnemies = 0;
					GPM.GoombasKilled = (int) (.30 * GPM.totalEnemies);
					GPM.RedTurtlesKilled = (int) (.30 * GPM.totalEnemies);
					GPM.GreenTurtlesKilled = (int) (.30 * GPM.totalEnemies);
					GPM.ArmoredTurtlesKilled = (int) (.10 * GPM.totalEnemies);
					GPM.completionTime = MIN_COMPLETION_TIME;
					break;
				case KILLER:
					GPM.coinsCollected = 20;
					GPM.totalCoins = 30;
					GPM.totalEnemies = MAX_ENEMIES;
					GPM.GoombasKilled = (int) (.30 * GPM.totalEnemies);
					GPM.RedTurtlesKilled = (int) (.30 * GPM.totalEnemies);
					GPM.GreenTurtlesKilled = (int) (.30 * GPM.totalEnemies);
					GPM.ArmoredTurtlesKilled = (int) (.10 * GPM.totalEnemies);
					GPM.completionTime = 2*(MAX_COMPLETION_TIME - MIN_COMPLETION_TIME)/3;
					break;
				case COLLECTOR:
					GPM.coinsCollected = MAX_COINS;
					GPM.totalCoins = MAX_COINS;
					GPM.totalEnemies = 0;
					GPM.GoombasKilled = (int) (.30 * GPM.totalEnemies);
					GPM.RedTurtlesKilled = (int) (.30 * GPM.totalEnemies);
					GPM.GreenTurtlesKilled = (int) (.30 * GPM.totalEnemies);
					GPM.ArmoredTurtlesKilled = (int) (.10 * GPM.totalEnemies);
					GPM.completionTime = 2*(MAX_COMPLETION_TIME - MIN_COMPLETION_TIME)/3;
					break;
				case COMPLETIONIST:
					GPM.coinsCollected = MAX_COINS;
					GPM.totalCoins = MAX_COINS;
					GPM.totalEnemies = MAX_ENEMIES;
					GPM.GoombasKilled = (int) (.30 * GPM.totalEnemies);
					GPM.RedTurtlesKilled = (int) (.30 * GPM.totalEnemies);
					GPM.GreenTurtlesKilled = (int) (.30 * GPM.totalEnemies);
					GPM.ArmoredTurtlesKilled = (int) (.10 * GPM.totalEnemies);
					GPM.completionTime = MAX_COMPLETION_TIME;
					break;
			}
		}

		private int getNumEnemiesToSpawn() {
			int result;

			result = (int) (getKillPercentage() * GPM.totalEnemies * 4/3);
			
			if (result < MIN_ENEMIES) {
				result = MIN_ENEMIES;
			} else if (result > MAX_ENEMIES) {
				result = MAX_ENEMIES;
			}
			//System.out.println(GPM.totalEnemies + " // " + result);
			
			return result;
		}

		private int getNumCoinsToSpawn() {
			int result;

			result = (int) (getCoinPercentage() * GPM.totalCoins * 4/3);
			
			if (result < MIN_COINS) {
				result = MIN_COINS;
			} else if (result > MAX_COINS) {
				result = MAX_COINS;
			}
			System.out.println(GPM.totalCoins + " // " + result);

			return result;
		}

		private int getNumGapsToSpawn() {
			int result;

			result = 0;

			if (result < MIN_GAPS) {
				result = MIN_GAPS;
			} else if (result > MAX_GAPS) {
				result = MAX_GAPS;
			}

			return result;
		}

		private int getNumPowerToSpawn() {
			int result;

			result = (int) ((0.75 + 3 * (GPM.enemyKillByFire / getKillCount())) * GPM.totalpowerBlocks);

			if (result < MIN_POWERS) {
				result = MIN_POWERS;
			} else if (result > MAX_POWERS) {
				result = MAX_POWERS;
			}

			return result;
		}

		private int getNumCoinBlocksToSpawn() {
			int result;

			result = (int) (0.2f + getNumCoinsToSpawn());

			if (result < MIN_COINBLOCKS) {
				result = MIN_COINBLOCKS;
			} else if (result > MAX_COINBLOCKS) {
				result = MAX_COINBLOCKS;
			}

			return result;
		}

		private void calculateEnemyProbabilities() {
			goombaProbability = getGoombaPercentage();
			if (goombaProbability < MIN_ENEMY_PROBABILITY) {
				goombaProbability = MIN_ENEMY_PROBABILITY;
			} else if (goombaProbability > MAX_ENEMY_PROBABILITY) {
				goombaProbability = MAX_ENEMY_PROBABILITY;
			}

			turtleProbability = getTurtlePercentage();
			if (turtleProbability < MIN_ENEMY_PROBABILITY) {
				turtleProbability = MIN_ENEMY_PROBABILITY;
			} else if (turtleProbability > MAX_ENEMY_PROBABILITY) {
				turtleProbability = MAX_ENEMY_PROBABILITY;
			}

			armoredProbability = getArmoredPercentage();
			if (armoredProbability < MIN_ENEMY_PROBABILITY) {
				armoredProbability = MIN_ENEMY_PROBABILITY;
			} else if (armoredProbability > MAX_ENEMY_PROBABILITY) {
				armoredProbability = MAX_ENEMY_PROBABILITY;
			}

			double totalProbability = goombaProbability + turtleProbability + armoredProbability;

			goombaProbability = goombaProbability / totalProbability;
			turtleProbability = turtleProbability / totalProbability;
			armoredProbability = armoredProbability / totalProbability;
		}

		private double getGoombaPercentage() {
			if (getKillCount() <= 0) {
				// avoid divide by zero
				return 0;
			} else {
				return ((double) (GPM.GoombasKilled)) / ((double) (getKillCount()));
			}
		}

		private double getTurtlePercentage() {
			if (getKillCount() <= 0) {
				// avoid divide by zero
				return 0;
			} else {
				return ((double) (GPM.RedTurtlesKilled + GPM.GreenTurtlesKilled)) / ((double) (getKillCount()));
			}
		}

		private double getArmoredPercentage() {
			if (getKillCount() <= 0) {
				// avoid divide by zero
				return 0;
			} else {
				return  ((double) (GPM.ArmoredTurtlesKilled)) / ((double) (getKillCount()));
			}
		}

		private int getCannonCount() {
			return (int) (GPM.CannonBallKilled / 1.5);
		}

		private double getCoinPercentage() {
			if (GPM.totalCoins == 0) {
				return 0;
			} else {
				return  ((double) (GPM.coinsCollected)) / ((double) (GPM.totalCoins));
			}
		}

		private double getKillPercentage() {
			return (double) (getKillCount()) / ((double) (GPM.totalEnemies));
		}

		private int getKillCount() {
			return GPM.RedTurtlesKilled + GPM.GreenTurtlesKilled + GPM.ArmoredTurtlesKilled + GPM.GoombasKilled;
		}

		private double getBlocksPercentage() {
			return GPM.percentageBlocksDestroyed;
		}

		private double getRunPercentage() {
			return ((double) (GPM.timeSpentRunning)) / ((double) (GPM.completionTime));
		}

		private double getSpeed() {
			// returns 1 for fastest, 0 for slowest
			return  ((double) (MAX_COMPLETION_TIME - GPM.completionTime)) / ((double) (MAX_COMPLETION_TIME - MIN_COMPLETION_TIME));
		}

		private void calculateHillParameters() {
			double speed = getSpeed();

			MAX_HILL_ELEVATION_CHANGE = 1 + (int) (speed * 4);
			MIN_HILL_STRETCH = 2 + (int) ((1 - speed) * 5);
			MAX_HILL_STRETCH = MIN_HILL_STRETCH + (int) ((1 - speed) * 10);
			HILL_FREQUENCY = 0.2 + (1 - speed) * 0.5;
		}

		private void calculateGroundParameters() {
			double speed = getSpeed();

			MAX_GROUND_ELEVATION_CHANGE = 1 + (int) (speed * 3);
			MIN_GROUND_STRETCH = 2 + (int) ((1 - speed) * 5);
			MAX_GROUND_STRETCH = MIN_GROUND_STRETCH + (int) ((1 - speed) * 12);
			GROUND_GAP_FREQUENCY = 0.1 + (1 - speed) * 0.2;
		}

		public void creat(long seed, int difficulty, int type)
		{
			this.type = type;
			this.difficulty = difficulty;

			lastSeed = seed;
			random = new Random(seed);

			/*buildCompleteGround(4, 2, 12, 5);
			buildHills(1, 5 , 2 , 10 , 2);
			buildHills(2, 5 , 2 , 10 , 2);
			buildHills(3, 5 , 2 , 10 , 2);*/

			setPlayerType(PlayerStyle.COLLECTOR);

			buildDependentGround();
			buildDependentHills(1);
			buildDependentHills(2);
			buildDependentHills(3);

			//placePipes(0.1f);
			//placeCannons(0.05f);

			buildEmptyBlocks(100);

			buildDependentCoins();
			buildDependentEnemies();



	        //set the end piece
	        int floor = height - 1 - random.nextInt(4);
	        int length = width - 64;
	        xExit = length + 8;
	        yExit = floor;


	        // fills the end piece
	        for (int x = length; x < width; x++)
	        {
	            for (int y = 0; y < height; y++)
	            {
	                if (y >= floor)
	                {
	                	elevMap[0][x] = floor;
	                    setBlock(x, y, GROUND);
	                }
	            }
	        }
	        


	        if (type == LevelInterface.TYPE_CASTLE || type == LevelInterface.TYPE_UNDERGROUND)
	        {
	            int ceiling = 0;
	            int run = 0;
	            for (int x = 0; x < width; x++)
	            {
	                if (run-- <= 0 && x > 4)
	                {
	                    ceiling = random.nextInt(4);
	                    run = random.nextInt(4) + 4;
	                }
	                for (int y = 0; y < height; y++)
	                {
	                    if ((x > 4 && y <= ceiling) || x < 1)
	                    {
	                        setBlock(x, y, GROUND);
	                    }
	                }
	            }
	        }

	        fixWalls();

	    }	    

	    private void buildCompleteGround(int maxElevationChange, int minFlatStretch, int maxFlatStretch, float gapFrequency){
	    	int maxElevation = height - 8;
	    	int length = 0;

	    	//starting position
	        buildGround(0, 10, height -1);
	        length += 10;
	        
	        int curElevation = height -1;
	        while (length < width - 64)
	        {
	        	
	        	if (getBlock(length-1, height - 1) != 0 && random.nextFloat() <= gapFrequency) {
	        		//make a gap
	        		length += 1 + random.nextInt(4);
	        	} else {
	        		//create more ground
		        	int stretch  = minFlatStretch + random.nextInt(maxFlatStretch - minFlatStretch + 1);
		        	if (length + stretch > width - 64) 
		        		stretch = width - 64 - length;
		        	int elevationChange = random.nextInt(maxElevationChange + 1);
		        	//int elevationChange = maxElevationChange;

		        	if (random.nextInt(2) == 0 || curElevation < maxElevation + maxElevationChange)
		        		curElevation += elevationChange;
		        	else 
		        		curElevation -= elevationChange;

		        	if (curElevation > height - 1) 
		        		curElevation = height - 1;
		        	else if (curElevation < maxElevation) 
		        		curElevation = maxElevation;

		        	buildGround(length, stretch, curElevation);
		        	for (int t = 0; t<4; t++){
		        		for (int x = length; x < length + stretch; x++){
							elevMap[t][x] = curElevation;
						}
					}

					length += stretch;
				}
			}
		}

		private void buildDependentGround() {
			calculateGroundParameters();
			buildCompleteGround(MAX_GROUND_ELEVATION_CHANGE, MIN_GROUND_STRETCH, MAX_GROUND_STRETCH, (float) GROUND_GAP_FREQUENCY);
		}

		private void buildCoins(int numCoinsToSpawn) {
			// create list of all possible locations for a coin to be spawned
			List<int[]> possibleLocations = new ArrayList<int[]>();
			for (int x = 10; x <= elevMap[0].length-64; x++) {
				for (int j = 0; j < elevMap.length; j++) {
					int y = elevMap[j][x];
					if (y > 0 && y < height) {
						for (int k = 2; k <= 5; k++) {
							if (y-k < 0 || getBlock(x, y-k) == Level.HILL_FILL || getBlock(x, y-k) == Level.HILL_LEFT || getBlock(x, y-k) == Level.HILL_RIGHT || getBlock(x, y-k) == Level.HILL_TOP_LEFT || getBlock(x, y-k) == Level.HILL_TOP_RIGHT || getBlock(x, y-k) == Level.HILL_TOP) {
								// don't bother searching for more possible locations if you find a hill above this platform
								break;
							} else if (getBlock(x,y-k) == 0 || getBlock(x,y-k) == BLOCK_EMPTY) {
								// potential coin location found! add to list.
								int[] loc = {x, y-k};
								possibleLocations.add(loc);
							} 
						}
					}
				}
			}

			//System.out.println(numCoinsToSpawn + " // " + possibleLocations.size());
			// randomly select possible spawn locations from the list, spawn the coins, remove the used spawn locations from the list
			int coinsLeft = numCoinsToSpawn;
			while (possibleLocations.size() > 0 && coinsLeft > 0) {
				int i = random.nextInt(possibleLocations.size());
				int[] loc = possibleLocations.get(i);
				if (getBlock(loc[0], loc[1]) == BLOCK_EMPTY) {
					buildBlockCoin(loc[0], loc[1]);
				} else {
					buildCoin(loc[0], loc[1]);
				}

				possibleLocations.remove(i);
				coinsLeft--;
				//System.out.print(coinsLeft + " // ");
			}
		}

		private void buildDependentCoins() {
			buildCoins(getNumCoinsToSpawn());
		}

		private void buildCoin(int x, int y) {
			setBlock(x, y, Level.COIN);
		}

		private void buildEnemies(int numEnemiesToSpawn) {
			// create list of all possible locations for an enemy to be spawned
			List<int[]> possibleLocations = new ArrayList<int[]>();
			for (int x = 10; x <= elevMap[0].length-64; x++) {
				for (int j = 0; j < elevMap.length; j++) {
					int y = elevMap[j][x] - 1;
					if (y >= 0) {
						if (getBlock(x,y) == 0) {
							// potential enemy location found! add to list.
							int[] loc = {x, y};
							possibleLocations.add(loc);
						}
					}
				}
			}

			calculateEnemyProbabilities();

			//System.out.println(numEnemiesToSpawn + " // " + possibleLocations.size());
			// randomly select possible spawn locations from the list, spawn the enemies, remove the used spawn locations from the list
			int enemiesLeft = numEnemiesToSpawn;
			while (possibleLocations.size() > 0 && enemiesLeft > 0) {
				int i = random.nextInt(possibleLocations.size());
				int[] loc = possibleLocations.get(i);
				float choose = random.nextFloat();
				if (choose <= goombaProbability) {
					buildGoomba(loc[0], loc[1]);
				} else if (choose <= goombaProbability + turtleProbability) {
					buildTurtle(loc[0], loc[1]);
				} else {
					buildArmoredTurtle(loc[0], loc[1]);
				}
				possibleLocations.remove(i);
				enemiesLeft--;
			}
		}

		private void buildDependentEnemies() {
			buildEnemies(getNumEnemiesToSpawn());
		}

		private void buildHills(int passNo, int maxElevationChange, int minFlatStretch, int maxFlatStretch, float frequency){
			int length = 10;

			while (length < width - 64)
			{
				//make a hill
				//System.out.println(maxFlatStretch - minFlatStretch + 1);
		   		int stretch  =  minFlatStretch + random.nextInt(maxFlatStretch - minFlatStretch + 1);
		   		//System.out.println(maxElevationChange + 1);
		   		int elevationChange = 2 + random.nextInt(maxElevationChange + 1);
				int localMaxElevation = height;

				for (int x = length; x < length + stretch; x++){
					if (elevMap[passNo - 1][x] < localMaxElevation)
						localMaxElevation = elevMap[passNo - 1][x];
				}

				if (random.nextFloat() <= frequency && localMaxElevation < height && localMaxElevation - elevationChange > 2){
					buildHill(length, localMaxElevation - elevationChange, length + stretch - 1, height);
					for (int i = passNo; i < 4; i++){
						for (int x = length; x < length + stretch; x++){
							elevMap[i][x] = localMaxElevation - elevationChange;
						}
					}
				}

				length += stretch + 2;
			}
		}

		private void buildDependentHills(int passNo) {
			calculateHillParameters();
			buildHills(passNo, MAX_HILL_ELEVATION_CHANGE, MIN_HILL_STRETCH, MAX_HILL_STRETCH, (float) HILL_FREQUENCY);
		}
	    
	    private void placePipes(float frequency){
	    	int length = 0;
	    	
			//System.out.println();

	    	for (int x = 10; x < width-64; x+=2){

	    		if (random.nextFloat() <= frequency){
	    			if (elevMap[0][x] == elevMap[0][x+1] && elevMap[0][x] < height){
	    				//System.out.println(height);
	    				//System.out.println(elevMap[0][x] + ", " + elevMap[0][x+1]);
	    				int pipeHeight = 2 + random.nextInt(3);
	    				if (elevMap[3][x-1] - (elevMap[0][x] - pipeHeight) < 5){
	    					buildPipe(x, elevMap[0][x] - pipeHeight, elevMap[0][x]-1, false);
	    					elevMap[0][x]   = elevMap[0][x]     - pipeHeight;
    						elevMap[0][x+1] = elevMap[0][x + 1] - pipeHeight;
	    					for (int i = 1; i < 4; i++){
	    						elevMap[i][x]   = Math.min(elevMap[i][x],     elevMap[0][x]);
	    						elevMap[i][x+1] = Math.min(elevMap[i][x + 1], elevMap[0][x + 1]);
	    					}
	    				}
	    			}
	    		}
	    	}
	    }
	    
	    private void placeCannons(float frequency){
	    	int tier = random.nextInt(4);
	    	//tier = 1;
	    		
	    	for (int x = 10; x < width-64; x+=1){
		    	//System.out.println(elevMap[1][x]);

	    		if (random.nextFloat() <= frequency){
	    			if (elevMap[tier][x] < height && 
	    				getBlock(x,elevMap[tier][x]) != Level.TUBE_TOP_LEFT && 
	    				getBlock(x,elevMap[tier][x]) != Level.TUBE_TOP_RIGHT){
	    				if (elevMap[3][x-1] - (elevMap[tier][x] - 2) < 5){

	    					buildCannon(x,elevMap[tier][x]-2, elevMap[tier][x] - 1);
	    				}
	    				
	    			}
	    		}
	    	}
	    }

		private void buildHill(int xi, int yi, int xf, int yf) {
			// xi : left edge x-coordinate
			// yi : top edge y-coordinate
			// xf : right edge x-coordinate
			// yf : bottom edge y-coordinate
			for (int x = xi; x <= xf; x++) {
				for (int y = yi; y <= yf; y++) {
					if (getBlock(x, y) == Level.BLOCK_EMPTY || getBlock(x,y) == 0) {
						if (x == xi && y == yi) {
							setBlock(x, y, Level.HILL_TOP_LEFT);
						} else if (x == xf && y == yi) {
							setBlock(x, y, Level.HILL_TOP_RIGHT);
						} else if (x == xi) {
							setBlock(x, y, Level.HILL_LEFT);
						} else if (x == xf) {
							setBlock(x, y, Level.HILL_RIGHT);
						} else if (y == yi) {
							setBlock(x, y, Level.HILL_TOP);
						} else {
							setBlock(x, y, Level.HILL_FILL);
						}
					} else if (getBlock(x,y) == Level.HILL_TOP_LEFT) {
						setBlock(x, y, Level.HILL_TOP_LEFT_IN);
					} else if (getBlock(x,y) == Level.HILL_TOP_RIGHT) {
						setBlock(x, y, Level.HILL_TOP_RIGHT_IN);
					}
				}
			}
		}

		private void buildHill(int xi, int yi, int xf) {
			// automatically fill hill to bottom of map
			this.buildHill(xi, yi, xf, height-1);
		}

		private void buildPipe(int xi, int yi, int yf, boolean flower) {
			// xi : left edge x-coordinate
			// yi : top edge y-coordinate
			// yf : bottom edge y-coordinate
			// flower : spawn enemy flower in tube?
			int xf = xi + 1;
			for (int x = xi; x <= xf; x++) {
				for (int y = yi; y <= yf; y++) {
					if (x == xi && y == yi) {
						setBlock(x, y, Level.TUBE_TOP_LEFT);
					} else if (x == xf && y == yi) {
						setBlock(x, y, Level.TUBE_TOP_RIGHT);
					} else if (x == xi) {
						setBlock(x, y, Level.TUBE_SIDE_LEFT);
					} else if (x == xf) {
						setBlock(x, y, Level.TUBE_SIDE_RIGHT);
					}
				}
			}

			if (flower) {
				buildJumpFlower(xi, yi);
			}
		}

		private void buildCannon(int xi, int yi, int yf) {
			// xi : left edge x-coordinate
			// yi : top edge y-coordinate
			// yf : bottom edge y-coordinate
			int x = xi;

			byte CANNON_TOP = (byte) (14 + 0 * 16);
			byte CANNON_BASE = (byte) (14 + 1 * 16);
			byte CANNON_FILL = (byte) (14 + 2 * 16);
			for (int y = yi; y <= yf; y++) {
				if (y == yi) {
					setBlock(x, y, CANNON_TOP);
				} else if (y == yi+1) {
					setBlock(x, y, CANNON_BASE);
				} else {
					setBlock(x, y, CANNON_FILL);
				}
			}
		}

		private void buildEmptyBlocks(int numBlocksToSpawn) {
			// create list of all possible locations for a block to be spawned
			List<int[]> possibleLocations = new ArrayList<int[]>();
			for (int x = 10; x <= elevMap[0].length-64; x++) {
				for (int j = 0; j < elevMap.length; j++) {
					int y = elevMap[j][x] - 3;
					if (y >= 0) {
						if (getBlock(x-1,y) != TUBE_TOP_LEFT && getBlock(x-1,y) != TUBE_TOP_RIGHT && getBlock(x-1,y) != BLOCK_EMPTY
						&& getBlock(x,y) != TUBE_TOP_LEFT && getBlock(x,y) != TUBE_TOP_RIGHT && getBlock(x-1,y) != BLOCK_EMPTY
						&& getBlock(x+1,y) != TUBE_TOP_LEFT && getBlock(x+1,y) != TUBE_TOP_RIGHT && getBlock(x-1,y) != BLOCK_EMPTY) {
							// potential block location found! add to list.
							int[] loc = {x, y};
							possibleLocations.add(loc);
						}
					}
				}
			}

			//System.out.println(numEnemiesToSpawn + " // " + possibleLocations.size());
			// randomly select possible spawn locations from the list, spawn the enemies, remove the used spawn locations from the list
			int blocksLeft = numBlocksToSpawn;
			while (possibleLocations.size() > 0 && blocksLeft > 2) {
				int i = random.nextInt(possibleLocations.size());
				int[] loc = possibleLocations.get(i);

				buildBlockEmpty(loc[0] - 1, loc[1]);		possibleLocations.remove(new int[]{loc[0] - 1, loc[1]});
				buildBlockEmpty(loc[0]	  , loc[1]);		possibleLocations.remove(new int[]{loc[0]	 , loc[1]});
				buildBlockEmpty(loc[0] + 1, loc[1]);		possibleLocations.remove(new int[]{loc[0] + 1, loc[1]});
				blocksLeft -= 3;

				int sectionLeft = random.nextInt(3);
				int offset = 2;
				while (sectionLeft > 0) {
					if (getBlock(loc[0] - offset, loc[1]) != TUBE_TOP_LEFT && getBlock(loc[0] - offset,loc[1]) != TUBE_TOP_RIGHT) {
						buildBlockEmpty(loc[0] - offset, loc[1]);		possibleLocations.remove(new int[]{loc[0] - offset, loc[1]});
						blocksLeft--;
						sectionLeft--;
					} else if (getBlock(loc[0] + offset, loc[1]) != TUBE_TOP_LEFT && getBlock(loc[0] + offset,loc[1]) != TUBE_TOP_RIGHT) {
						buildBlockEmpty(loc[0] + offset, loc[1]);		possibleLocations.remove(new int[]{loc[0] + offset, loc[1]});
						blocksLeft--;
						sectionLeft--;
						offset++;
					}
				}

				
				blocksLeft--;
			}
		}

		private void buildBlockEmpty(int x, int y) {
			setBlock(x, y, Level.BLOCK_EMPTY);
		}

		private void buildBlockCoin(int x, int y) {
			setBlock(x, y, Level.BLOCK_COIN);
		}

		private void buildBlockPower(int x, int y) {
			setBlock(x, y, Level.BLOCK_POWERUP);
		}

		private void buildBlocksEmpty(int xi, int yi, int xf, int yf) {
			// xi : left edge x-coordinate
			// yi : top edge y-coordinate
			// xf : right edge x-coordinate
			// yf : bottom edge y-coordinate
			for (int x = xi; x <= xf; x++) {
				for (int y = yi; y <= yf; y++) {
					buildBlockEmpty(x, y);
				}
			}
		}

		private void buildRock(int x, int y) {
			setBlock(x, y, Level.ROCK);
		}

		private void buildGoomba(int x, int y, boolean winged) {
			setSpriteTemplate(x, y, new SpriteTemplate(SpriteTemplate.GOOMPA, winged));
			ENEMIES++;
		}

		private void buildGoomba(int x, int y) {
			this.buildGoomba(x, y, random.nextInt(4) == 3);
		}

		private void buildGreenTurtle(int x, int y, boolean winged) {
			setSpriteTemplate(x, y, new SpriteTemplate(SpriteTemplate.GREEN_TURTLE, winged));
			ENEMIES++;
		}

		private void buildRedTurtle(int x, int y, boolean winged) {
			setSpriteTemplate(x, y, new SpriteTemplate(SpriteTemplate.RED_TURTLE, winged));
			ENEMIES++;
		}

		private void buildArmoredTurtle(int x, int y, boolean winged) {
			setSpriteTemplate(x, y, new SpriteTemplate(SpriteTemplate.ARMORED_TURTLE, winged));
			ENEMIES++;
		}

		private void buildArmoredTurtle(int x, int y) {
			this.buildArmoredTurtle(x, y, random.nextInt(5) == 0);
		}

		private void buildTurtle(int x, int y) {
			if (random.nextBoolean()) {
				buildGreenTurtle(x, y, random.nextInt(4) == 0);
			} else {
				buildRedTurtle(x, y, random.nextInt(4) == 0);
			}
			ENEMIES++;
		}

		private void buildChompFlower(int x, int y) {
			setSpriteTemplate(x, y, new SpriteTemplate(SpriteTemplate.CHOMP_FLOWER, false));
			ENEMIES++;
		}

		private void buildJumpFlower(int x, int y) {
			setSpriteTemplate(x, y, new SpriteTemplate(SpriteTemplate.JUMP_FLOWER, false));
			ENEMIES++;
		}


		private int buildJump(int xo, int maxLength)
		{	gaps++;
			//jl: jump length
			//js: the number of blocks that are available at either side for free
			int js = random.nextInt(4) + 2;
			int jl = random.nextInt(2) + 2;
			int length = js * 2 + jl;

			boolean hasStairs = random.nextInt(3) == 0;

			int floor = height - 1 - random.nextInt(4);
		  //run from the start x position, for the whole length
			for (int x = xo; x < xo + length; x++)
			{
				if (x < xo + js || x > xo + length - js - 1)
				{
					//run for all y's since we need to paint blocks upward
					for (int y = 0; y < height; y++)
					{	//paint ground up until the floor
						if (y >= floor)
						{
							setBlock(x, y, GROUND);
						}
					  //if it is above ground, start making stairs of rocks
						else if (hasStairs)
						{	//LEFT SIDE
							if (x < xo + js)
							{ //we need to max it out and level because it wont
							  //paint ground correctly unless two bricks are side by side
								if (y >= floor - (x - xo) + 1)
								{
									setBlock(x, y, ROCK);
								}
							}
							else
							{ //RIGHT SIDE
								if (y >= floor - ((xo + length) - x) + 2)
								{
									setBlock(x, y, ROCK);
								}
							}
						}
					}
				}
			}

			return length;
		}

		/*private void buildCannon(int xo, int floorHeight, int cannonHeight) {
			if (height < 2)		height = 2;

			int yo = floorHeight + cannonHeight - random.nextInt(cannonHeight);

			for (int y = yo; y <= yo + cannonHeight; y++) {
				if (y == cannonHeight) {
					setBlock(xo, y, (byte) (14 + 0 * 16));
				} else if (y == cannonHeight + 1) {
					setBlock(xo, y, (byte) (14 + 1 * 16));
				} else {
					setBlock(xo, y, (byte) (14 + 2 * 16));
				}	
			}
		}

		private int buildCannons(int xo, int maxLength) {
			int length = random.nextInt(10) + 2;
			if (length > maxLength) length = maxLength;

			int floor = height - 1 - random.nextInt(4);
			int xCannon = xo + 1 + random.nextInt(4);
			for (int x = xo; x < xo + length; x++)
			{
				if (x > xCannon)
				{
					xCannon += 2 + random.nextInt(4);
				}
				if (xCannon == xo + length - 1) xCannon += 10;
				int cannonHeight = floor - random.nextInt(4) - 1;

				for (int y = 0; y < height; y++)
				{
					if (y >= floor)
					{
						setBlock(x, y, GROUND);
					}
					else
					{
						if (x == xCannon && y >= cannonHeight)
						{
							if (y == cannonHeight)
							{
								setBlock(x, y, (byte) (14 + 0 * 16));
							}
							else if (y == cannonHeight + 1)
							{
								setBlock(x, y, (byte) (14 + 1 * 16));
							}
							else
							{
								setBlock(x, y, (byte) (14 + 2 * 16));
							}
						}
					}
				}
			}

			return length;
		}*/

		/*private int buildHillStraight(int xo, int maxLength)
		{
			int length = random.nextInt(10) + 10;
			if (length > maxLength) length = maxLength;

			int floor = height - 1 - random.nextInt(4);
			for (int x = xo; x < xo + length; x++)
			{
				for (int y = 0; y < height; y++)
				{
					if (y >= floor)
					{
						setBlock(x, y, GROUND);
					}
				}
			}

			addEnemyLine(xo + 1, xo + length - 1, floor - 1);

			int h = floor;

			boolean keepGoing = true;

			boolean[] occupied = new boolean[length];
			while (keepGoing)
			{
				h = h - 2 - random.nextInt(3);

				if (h <= 0)
				{
					keepGoing = false;
				}
				else
				{
					int l = random.nextInt(5) + 3;
					int xxo = random.nextInt(length - l - 2) + xo + 1;

					if (occupied[xxo - xo] || occupied[xxo - xo + l] || occupied[xxo - xo - 1] || occupied[xxo - xo + l + 1])
					{
						keepGoing = false;
					}
					else
					{
						occupied[xxo - xo] = true;
						occupied[xxo - xo + l] = true;
						addEnemyLine(xxo, xxo + l, h - 1);
						if (random.nextInt(4) == 0)
						{
							decorate(xxo - 1, xxo + l + 1, h);
							keepGoing = false;
						}
						for (int x = xxo; x < xxo + l; x++)
						{
							for (int y = h; y < floor; y++)
							{
								int xx = 5;
								if (x == xxo) xx = 4;
								if (x == xxo + l - 1) xx = 6;
								int yy = 9;
								if (y == h) yy = 8;

								if (getBlock(x, y) == 0)
								{
									setBlock(x, y, (byte) (xx + yy * 16));
								}
								else
								{
									if (getBlock(x, y) == HILL_TOP_LEFT) setBlock(x, y, HILL_TOP_LEFT_IN);
									if (getBlock(x, y) == HILL_TOP_RIGHT) setBlock(x, y, HILL_TOP_RIGHT_IN);
								}
							}
						}
					}
				}
			}

			return length;
		}

		private void addEnemyLine(int x0, int x1, int y)
		{
			for (int x = x0; x < x1; x++)
			{
				//if (random.nextInt(35) < difficulty + 1)
				//{
					int type = random.nextInt(4);

					if (difficulty < 1)
					{
						type = Enemy.ENEMY_GOOMBA;
					}
					else if (difficulty < 3)
					{
						type = random.nextInt(3);
					}

					setSpriteTemplate(x, y, new SpriteTemplate(type, random.nextInt(35) < difficulty));
					ENEMIES++;
				//}
			}
		}*/

		/*private int buildTubes(int xo, int maxLength)
		{
			int length = random.nextInt(10) + 5;
			if (length > maxLength) length = maxLength;

			int floor = height - 1 - random.nextInt(4);
			int xTube = xo + 1 + random.nextInt(4);
			int tubeHeight = floor - random.nextInt(2) - 2;
			for (int x = xo; x < xo + length; x++)
			{
				if (x > xTube + 1)
				{
					xTube += 3 + random.nextInt(4);
					tubeHeight = floor - random.nextInt(2) - 2;
				}
				if (xTube >= xo + length - 2) xTube += 10;

				if (x == xTube && random.nextInt(11) < difficulty + 1)
				{
					setSpriteTemplate(x, tubeHeight, new SpriteTemplate(Enemy.ENEMY_FLOWER, false));
					ENEMIES++;
				}

				for (int y = 0; y < height; y++)
				{
					if (y >= floor)
					{
						setBlock(x, y,GROUND);

					}
					else
					{
						if ((x == xTube || x == xTube + 1) && y >= tubeHeight)
						{
							int xPic = 10 + x - xTube;

							if (y == tubeHeight)
							{
								//tube top
								setBlock(x, y, (byte) (xPic + 0 * 16));
							}
							else
							{
								//tube side
								setBlock(x, y, (byte) (xPic + 1 * 16));
							}
						}
					}
				}
			}

			return length;
		}

		private int buildStraight(int xo, int maxLength, boolean safe)
		{
			int length = random.nextInt(10) + 2;

			if (safe)
				length = 10 + random.nextInt(5);

			if (length > maxLength)
				length = maxLength;

			int floor = height - 1 - random.nextInt(4);

			//runs from the specified x position to the length of the segment
			for (int x = xo; x < xo + length; x++)
			{
				for (int y = 0; y < height; y++)
				{
					if (y >= floor)
					{
						setBlock(x, y, GROUND);
					}
				}
			}

			if (!safe)
			{
				if (length > 5)
				{
					decorate(xo, xo + length, floor);
				}
			}

			return length;
		}*/

		private int buildGround(int xo, int length, int elevation)
		{
			//elevation = height-1;


			//runs from the specified x position to the length of the segment
			for (int x = xo; x < xo + length; x++)
			{

				for (int y = 0; y < height; y++)
				{
					if (y >= elevation)
					{
						setBlock(x, y, GROUND);
					}
				}
			}

			return length;
		}

		/*private void decorate(int xStart, int xLength, int floor)
		{
			//if its at the very top, just return
			if (floor < 1)
				return;

			//		boolean coins = random.nextInt(3) == 0;
			boolean rocks = true;

			//add an enemy line above the box
			addEnemyLine(xStart + 1, xLength - 1, floor - 1);

			int s = random.nextInt(4);
			int e = random.nextInt(4);

			if (floor - 2 > 0){
				if ((xLength - 1 - e) - (xStart + 1 + s) > 1){
					for(int x = xStart + 1 + s; x < xLength - 1 - e; x++){
						setBlock(x, floor - 2, COIN);
						COINS++;
					}
				}
			}

			s = random.nextInt(4);
			e = random.nextInt(4);
			
			//this fills the set of blocks and the hidden objects inside them
			if (floor - 4 > 0)
			{
				if ((xLength - 1 - e) - (xStart + 1 + s) > 2)
				{
					for (int x = xStart + 1 + s; x < xLength - 1 - e; x++)
					{
						if (rocks)
						{
							if (x != xStart + 1 && x != xLength - 2 && random.nextInt(3) == 0)
							{
								if (random.nextInt(4) == 0)
								{
									setBlock(x, floor - 4, BLOCK_POWERUP);
									BLOCKS_POWER++;
								}
								else
								{	//the fills a block with a hidden coin
									setBlock(x, floor - 4, BLOCK_COIN);
									BLOCKS_COINS++;
								}
							}
							else if (random.nextInt(4) == 0)
							{
								if (random.nextInt(4) == 0)
								{
									setBlock(x, floor - 4, (byte) (2 + 1 * 16));
								}
								else
								{
									setBlock(x, floor - 4, (byte) (1 + 1 * 16));
								}
							}
							else
							{
								setBlock(x, floor - 4, BLOCK_EMPTY);
								BLOCKS_EMPTY++;
							}
						}
					}
				}
			}
		}*/

		private void fixWalls()
		{
			boolean[][] blockMap = new boolean[width + 1][height + 1];

			for (int x = 0; x < width + 1; x++)
			{
				for (int y = 0; y < height + 1; y++)
				{
					int blocks = 0;
					for (int xx = x - 1; xx < x + 1; xx++)
					{
						for (int yy = y - 1; yy < y + 1; yy++)
						{
							if (getBlockCapped(xx, yy) == GROUND){
								blocks++;
							}
						}
					}
					blockMap[x][y] = blocks == 4;
				}
			}
			blockify(this, blockMap, width + 1, height + 1);
		}

		private void blockify(Level level, boolean[][] blocks, int width, int height){
			int to = 0;
			if (type == LevelInterface.TYPE_CASTLE)
			{
				to = 4 * 2;
			}
			else if (type == LevelInterface.TYPE_UNDERGROUND)
			{
				to = 4 * 3;
			}

			boolean[][] b = new boolean[2][2];

			for (int x = 0; x < width; x++)
			{
				for (int y = 0; y < height; y++)
				{
					for (int xx = x; xx <= x + 1; xx++)
					{
						for (int yy = y; yy <= y + 1; yy++)
						{
							int _xx = xx;
							int _yy = yy;
							if (_xx < 0) _xx = 0;
							if (_yy < 0) _yy = 0;
							if (_xx > width - 1) _xx = width - 1;
							if (_yy > height - 1) _yy = height - 1;
							b[xx - x][yy - y] = blocks[_xx][_yy];
						}
					}

					if (b[0][0] == b[1][0] && b[0][1] == b[1][1])
					{
						if (b[0][0] == b[0][1])
						{
							if (b[0][0])
							{
								level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
							}
							else
							{
								// KEEP OLD BLOCK!
							}
						}
						else
						{
							if (b[0][0])
							{
								//down grass top?
								level.setBlock(x, y, (byte) (1 + 10 * 16 + to));
							}
							else
							{
								//up grass top
								level.setBlock(x, y, (byte) (1 + 8 * 16 + to));
							}
						}
					}
					else if (b[0][0] == b[0][1] && b[1][0] == b[1][1])
					{
						if (b[0][0])
						{
							//right grass top
							level.setBlock(x, y, (byte) (2 + 9 * 16 + to));
						}
						else
						{
							//left grass top
							level.setBlock(x, y, (byte) (0 + 9 * 16 + to));
						}
					}
					else if (b[0][0] == b[1][1] && b[0][1] == b[1][0])
					{
						level.setBlock(x, y, (byte) (1 + 9 * 16 + to));
					}
					else if (b[0][0] == b[1][0])
					{
						if (b[0][0])
						{
							if (b[0][1])
							{
								level.setBlock(x, y, (byte) (3 + 10 * 16 + to));
							}
							else
							{
								level.setBlock(x, y, (byte) (3 + 11 * 16 + to));
							}
						}
						else
						{
							if (b[0][1])
							{
								//right up grass top
								level.setBlock(x, y, (byte) (2 + 8 * 16 + to));
							}
							else
							{
								//left up grass top
								level.setBlock(x, y, (byte) (0 + 8 * 16 + to));
							}
						}
					}
					else if (b[0][1] == b[1][1])
					{
						if (b[0][1])
						{
							if (b[0][0])
							{
								//left pocket grass
								level.setBlock(x, y, (byte) (3 + 9 * 16 + to));
							}
							else
							{
								//right pocket grass
								level.setBlock(x, y, (byte) (3 + 8 * 16 + to));
							}
						}
						else
						{
							if (b[0][0])
							{
								level.setBlock(x, y, (byte) (2 + 10 * 16 + to));
							}
							else
							{
								level.setBlock(x, y, (byte) (0 + 10 * 16 + to));
							}
						}
					}
					else
					{
						level.setBlock(x, y, (byte) (0 + 1 * 16 + to));
					}
				}
			}
		}
		
		public RandomLevel clone() throws CloneNotSupportedException {

			RandomLevel clone=new RandomLevel(width, height);

			clone.xExit = xExit;
			clone.yExit = yExit;
			byte[][] map = getMap();
			SpriteTemplate[][] st = getSpriteTemplate();
			
			for (int i = 0; i < map.length; i++)
				for (int j = 0; j < map[i].length; j++) {
					clone.setBlock(i, j, map[i][j]);
					clone.setSpriteTemplate(i, j, st[i][j]);
			}
			clone.BLOCKS_COINS = BLOCKS_COINS;
			clone.BLOCKS_EMPTY = BLOCKS_EMPTY;
			clone.BLOCKS_POWER = BLOCKS_POWER;
			clone.ENEMIES = ENEMIES;
			clone.COINS = COINS;
			
			return clone;

		  }


}
