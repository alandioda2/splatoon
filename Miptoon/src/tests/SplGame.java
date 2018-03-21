package me.alandioda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class SplGame {
	
	//play mode variables
	Map<Integer, Integer> schID = new HashMap<Integer, Integer>();
	Map<String, PlayerData> players = new HashMap<String, PlayerData>();
	List<Block> changedBlocks = new ArrayList<Block>();
	Miptoon.GameStage gameStage = Miptoon.GameStage.DISABED;
	int gameTime;
	int teamColor1;
	int teamColor2;
	
	
	//game display data
	Scoreboard gameBoard;
	Objective obj;
	Score website; // mipcraft.eu
	Team timeDispl; // Time: 7:00
	Team playersDispl; // ❤❤❤❤ ❤❤❤❤ display for team (who is alive and who isn't)
	
	//game save data
	String gamePath;
	Location Bound1;
	Location Bound2;
	Location Lobby;
	Location Team1;
	Location Team2;
}