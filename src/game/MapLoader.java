package game;

import java.awt.Point;
import java.io.*;
import java.util.ArrayList;

public class MapLoader {

    public static MapData load(String filePath, String mapName) {
        int lineCount = 0; // Track the line number for easier debugging
        int musicIndex = 0; // Declare this BEFORE the while loop starts

        // We use try-with-resources to ensure the file closes even if an error occurs
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                lineCount++; // --- AREA 1: Increment on every read ---
                line = line.trim();

                // 1. Find the exact [MapName] header
                if (line.equals("[" + mapName + "]")) {
                    System.out.println("DEBUG: Found header [" + mapName + "] at line " + lineCount);

                    int[][] grid = new int[22][22];
                    ArrayList<MapTrigger> triggers = new ArrayList<>();
                    ArrayList<MapData.SignData> signs = new ArrayList<>();
                    ArrayList<NPCData> npcs = new ArrayList<>();

                    // 2. Parse the 22x22 Grid immediately following the header
                    for (int r = 0; r < 22; r++) {
                        String rawLine = br.readLine();
                        lineCount++;
                        if (rawLine == null) break;

                        // Skip empty lines or comments within the grid
                        if (rawLine.trim().isEmpty() || rawLine.trim().startsWith("#")) {
                            r--; 
                            continue;
                        }

                        String[] tiles = rawLine.trim().split("[,\\s]+");
                        for (int c = 0; c < 22; c++) {
                            if (c < tiles.length) {
                                String val = tiles[c].trim();
                                grid[r][c] = val.matches("-?\\d+") ? Integer.parseInt(val) : tileNameToId(val);
                            }
                        }
                    }

                    // 3. Parse Metadata (TILE, TRIGGER, SIGN, NPC) until the next map or EOF
                    while ((line = br.readLine()) != null) {
                        lineCount++;
                        line = line.trim();

                        if (line.isEmpty() || line.startsWith("#")) continue;
                        if (line.startsWith("[")) break; // Stop if we hit a NEW map section

                        try {
                            if (line.startsWith("TILE:")) {
                                String[] p = line.replace("TILE:", "").trim().split(",");
                                if (p.length >= 3) {
                                    int tx = Integer.parseInt(p[0].trim());
                                    int ty = Integer.parseInt(p[1].trim());
                                    String val = p[2].trim();
                                    int id = val.matches("-?\\d+") ? Integer.parseInt(val) : tileNameToId(val);
                                    if (tx >= 0 && tx < 22 && ty >= 0 && ty < 22) grid[ty][tx] = id;
                                }
                            } 
                            else if (line.startsWith("MUSIC:")) {
                                musicIndex = Integer.parseInt(line.replace("MUSIC:", "").trim());
                            }
                            else if (line.startsWith("TRIGGER:")) {
                                String[] p = line.replace("TRIGGER:", "").trim().split(",");
                                if (p.length >= 5) {
                                    triggers.add(new MapTrigger(
                                        Integer.parseInt(p[0].trim()), 
                                        Integer.parseInt(p[1].trim()), 
                                        p[2].trim(), 
                                        new Point(Integer.parseInt(p[3].trim()), Integer.parseInt(p[4].trim()))
                                    ));
                                }
                            } 
                            else if (line.startsWith("SIGN:")) {
                                String[] p = line.replace("SIGN:", "").trim().split(",", 3);
                                if (p.length >= 3) {
                                    signs.add(new MapData.SignData(
                                        Integer.parseInt(p[0].trim()), 
                                        Integer.parseInt(p[1].trim()), 
                                        p[2].trim().split(";")
                                    ));
                                }
                            } 
                            else if (line.startsWith("NPC:")) {
                                // Extract data after "NPC:" and split into 6 specific parts
                                String dataPart = line.substring(line.indexOf(":") + 1).trim();
                                String[] p = dataPart.split(",", 6); 
                                
                                if (p.length >= 6) {
                                    String name = p[3].trim();
                                    int x = Integer.parseInt(p[0].trim());
                                    int y = Integer.parseInt(p[1].trim());
                                    
                                    String spriteVal = p[2].trim();
                                    int sprite = spriteVal.matches("-?\\d+") ? Integer.parseInt(spriteVal) : tileNameToId(spriteVal);
                                    
                                    boolean isDir = Boolean.parseBoolean(p[4].trim());
                                    String[] dialogue = p[5].trim().split(";");

                                    npcs.add(new NPCData(name, x, y, sprite, isDir, dialogue));
                                }
                            }
                        } catch (Exception e) {
                            // --- AREA 3: Catch the exact line that broke ---
                            System.err.println("CRITICAL ERROR in WorldData.txt at line " + lineCount);
                            System.err.println("Content: " + line);
                            throw e; // Re-throw so the game log shows the full trace
                        }
                    }

                    // 4. Construction: Return the MapData immediately once fully parsed
                    MapData finalData = new MapData(grid, triggers, signs, npcs);
                    finalData.setMapName(mapName);
                    finalData.setMusicIndex(musicIndex);
                    finalData.finalizeMap();
                    return finalData;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("MapLoader failed near line " + lineCount + " while looking for: " + mapName);            e.printStackTrace();
            
        }

        System.err.println("MapLoader Error: Map [" + mapName + "] not found in file.");
        return null;
    }

    private static int tileNameToId(String name) {
        switch (name.toUpperCase().trim()) {
            case "GRASS": return 0;
            case "WALL":  return 1;
            case "WOOD":  return 2;
            case "LOG":   return 3;
            case "WOOD_FLOOR":  return 4;            
            case "SAND":  return 8;
            case "WATER": return 9;
            case "DOOR":  return 10;
            case "SIGN":  return 11;
            case "TREE":  return 20;
            case "COBBLESTONE":  return 21;
            case "WOOD_ROOF":  return 22;
            case "WOOD_ROOF_LEFT": return 23;
            case "WOOD_ROOF_RIGHT": return 24;
            case "TREE BASE": return 25;
            case "TREE MIDDLE": return 26;
            case "TREE LEAVES": return 27;
            default:      return 99; // Missing Texture ID
        }
    }
}