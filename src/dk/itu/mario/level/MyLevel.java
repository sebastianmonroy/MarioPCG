package dk.itu.mario.level;

import java.util.Random;

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
		private int MAX_COINS = 100;
		private int MIN_GAPS = 5;
		private int MAX_GAPS = 20;
		private int MIN_POWERS = 1;
		private int MAX_POWERS = 5;
		private int MIN_COINBLOCKS = 20;
		private int MAX_COINBLOCKS = 100;
		private int MIN_COMPLETION_TIME = 200-173;
		private int MAX_COMPLETION_TIME = 200;
		private GamePlay GPM;


		public int getNumEnemiesToSpawn() {
			int result;

			result = (int) (getKillPercentage() * GPM.totalEnemies * 2);

			if (result < MIN_ENEMIES) {
				result = MIN_ENEMIES;
			} else if (result > MAX_ENEMIES) {
				result = MAX_ENEMIES;
			}

			return result;
		}

		public int getNumCoinsToSpawn() {
			int result;

			result = (int) (getCoinPercentage() * GPM.totalCoins * 2);

			if (result < MIN_COINS) {
				result = MIN_COINS;
			} else if (result > MAX_COINS) {
				result = MAX_COINS;
			}

			return result;
		}

		public int getNumGapsToSpawn() {
			int result;

			result = 0;

			if (result < MIN_GAPS) {
				result = MIN_GAPS;
			} else if (result > MAX_GAPS) {
				result = MAX_GAPS;
			}

			return result;
		}

		public int getNumPowerToSpawn() {
			int result;

			result = (int) ((0.75 + 3 * (GPM.enemyKillByFire / getKillCount())) * GPM.totalpowerBlocks);

			if (result < MIN_POWERS) {
				result = MIN_POWERS;
			} else if (result > MAX_POWERS) {
				result = MAX_POWERS;
			}

			return result;
		}

		public int getNumCoinBlocksToSpawn() {
			int result;

			result = (int) (0.2f + getNumCoinsToSpawn());

			if (result < MIN_COINBLOCKS) {
				result = MIN_COINBLOCKS;
			} else if (result > MAX_COINBLOCKS) {
				result = MAX_COINBLOCKS;
			}

			return result;
		}

		public double getProbabilityOfTurtles() {
			return (GPM.RedTurtlesKilled + GPM.GreenTurtlesKilled) / getKillCount();
		}

		public double getProbabilityOfGoombas() {
			return GPM.GoombasKilled / getKillCount();
		}

		public double getProbabilityOfCannons() {
			return (GPM.CannonBallKilled / 1.5) / getKillCount();
		}

		/*public PlayerType getPlayerType() {
			double coinThreshold;	// min percentage of coins to be considered "high"
			double killThreshold;	// min percentage of kills to be considered "high"
			double jumpThreshold;	// min number of aimless jumps to be considered "high"
			double runThreshold;	// min percentage of running to be considered "high"


		}*/

		public double getCoinPercentage() {
			return (double) (GPM.coinsCollected / GPM.totalCoins);
		}

		public double getKillPercentage() {
			return (double) (getKillCount() / GPM.totalEnemies);
		}

		public int getKillCount() {
			return GPM.RedTurtlesKilled + GPM.GreenTurtlesKilled + GPM.ArmoredTurtlesKilled + GPM.GoombasKilled + GPM.CannonBallKilled + GPM.JumpFlowersKilled + GPM.ChompFlowersKilled;
		}

		public double getBlocksPercentage() {
			return GPM.percentageBlocksDestroyed;
		}

		public double getRunPercentage() {
			return (double) (GPM.timeSpentRunning / GPM.completionTime);
		}

		/*public int getAimlessJumpCount() {
			return GPM.aimlessJumps;
		}*/






		public MyLevel(int width, int height)
	    {
			super(width, height);
	    }


		public MyLevel(int width, int height, long seed, int difficulty, int type, GamePlay playerMetrics)
	    {
	        this(width, height);
	        creat(seed, difficulty, type);
	        this.GPM = playerMetrics;
	    }

	    public void creat(long seed, int difficulty, int type)
	    {
	        this.type = type;
	        this.difficulty = difficulty;

	        lastSeed = seed;
	        random = new Random(seed);

			buildCompleteGround(4, 2, 12, 5);

			buildHill(9, height-7, 14, height-2);
			buildPipe(10, height-4, height-2);
			buildCannon(13, height-5, height-2);

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

	    private void buildCompleteGround(int maxElevationChange, int minFlatStretch, int maxFlatStretch, int gapFrequency){
	    	int maxElevation = height - 8;
	    	int length = 0;

	    	//starting position
	        buildGround(0, 10, height -1);
	        length += 10;
	        
	        int curElevation = height -1;
	        while (length < width - 64)
	        {
	        	//make a gap
	        	if (getBlock(length-1, height - 1) != 0 && random.nextInt(gapFrequency) == 0){
	        		length += 1 + random.nextInt(4);
	        	}

	        	//create more ground
	        	else{
	        	int stretch  = minFlatStretch + random.nextInt(maxFlatStretch - minFlatStretch);
	        	int elevationChange = random.nextInt(maxElevationChange);
	        	//int elevationChange = maxElevationChange;
	        	if (random.nextInt(2) == 0 || curElevation < maxElevation + maxElevationChange)
	        		curElevation += elevationChange;
	        	else curElevation -= elevationChange;

	        	if (curElevation > height - 1) curElevation = height - 1;
	        	else if (curElevation < maxElevation) curElevation = maxElevation;


	        	buildGround(length, stretch, curElevation);

				length += stretch;
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
						if (getBlock(x, y) == Level.HILL_TOP_LEFT) {
							setBlock(x, y, Level.HILL_TOP_LEFT_IN);
						} else if (getBlock(x, y) == Level.HILL_TOP_RIGHT) {
							setBlock(x, y, Level.HILL_TOP_RIGHT_IN);
						} else {
	    					setBlock(x, y, Level.HILL_FILL);
	    				}
	    			}
	    		}
	    	}
	    }

	    private void buildHills(int maxElevationChange, int maxFlatStretch, int frequency){

	    }

	    private void buildPipe(int xi, int yi, int yf) {
	    	// xi : left edge x-coordinate
	    	// yi : top edge y-coordinate
	    	// yf : bottom edge y-coordinate
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

	    private int buildCannons(int xo, int maxLength)
	    {
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

	    private int buildHillStraight(int xo, int maxLength)
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
	            if (random.nextInt(35) < difficulty + 1)
	            {
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
	            }
	        }
	    }

	    private int buildTubes(int xo, int maxLength)
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
	    }

	    private int buildGround(int xo, int length, int elevation)
	    {

	        int floor = elevation;

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

	        return length;
	    }

	    private void decorate(int xStart, int xLength, int floor)
	    {
	    	//if its at the very top, just return
	        if (floor < 1)
	        	return;

	        //        boolean coins = random.nextInt(3) == 0;
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
	    }

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
