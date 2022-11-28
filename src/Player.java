import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    static class Site {

        public final int siteId;
        public final int x;
        public final int y;
        public final int radius;

        Site(int siteId, int x, int y, int radius) {
            this.siteId = siteId;
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }

    enum UnitType {
        KNIGHT(80), ARCHER(100), GIANT(140);

        private int cost;

        UnitType(int cost) {
            this.cost = cost;
        }

        public static UnitType from(int unitType) {
            return unitType == 0 ? KNIGHT : unitType == 1 ? ARCHER : GIANT;
        }
    }

    enum OwnerType {
        PLAYER, ENEMY;

        public static OwnerType from(int owner) {
            return owner == 0 ? PLAYER : owner == 1 ? ENEMY : null;
        }
    }

    enum StructureType {
        BARRACKS, TOWER, NONE;

        public static StructureType from(int structureType) {
            return structureType == -1 ? NONE : structureType == 1 ? TOWER : BARRACKS;
        }
    }

    static class Structure {

        public final int siteId;
        public final StructureType structure;
        public final OwnerType owner;
        public final int ticks;
        public final UnitType unitType;

        Structure(int siteId, StructureType structure, OwnerType owner, int ticks, UnitType unitType) {
            this.siteId = siteId;
            this.structure = structure;
            this.owner = owner;
            this.ticks = ticks;
            this.unitType = unitType;
        }
    }

    static class Queen {

        public final int x;
        public final int y;
        public final int health;

        Queen(int x, int y, int health) {
            this.x = x;
            this.y = y;
            this.health = health;
        }
    }

    private static final Map<StructureType, Integer> STRUCTURE_LIMIT = new HashMap<>();
    private static final Map<UnitType, Integer> BARRACKS_RATIO = new HashMap<>();
    private static final Map<UnitType, Integer> UNIT_RATIO = new HashMap<>();

    static {
        STRUCTURE_LIMIT.put(StructureType.BARRACKS, 5);
        STRUCTURE_LIMIT.put(StructureType.TOWER, 3);
        BARRACKS_RATIO.put(UnitType.KNIGHT, 2);
        BARRACKS_RATIO.put(UnitType.ARCHER, 2);
        BARRACKS_RATIO.put(UnitType.GIANT, 1);
        UNIT_RATIO.put(UnitType.KNIGHT, 8);
        UNIT_RATIO.put(UnitType.ARCHER, 4);
        UNIT_RATIO.put(UnitType.GIANT, 1);
    }

    private static List<Site> sites = new ArrayList<>();
    private static Map<Integer, Site> siteMap = new HashMap<>();
    private static Site basePosition = null;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int numSites = in.nextInt();
        for (int i = 0; i < numSites; i++) {
            int siteId = in.nextInt();
            int x = in.nextInt();
            int y = in.nextInt();
            int radius = in.nextInt();
            Site site = new Site(siteId, x, y, radius);
            sites.add(site);
            siteMap.put(siteId, site);
        }

        // game loop
        while (true) {
            int gold = in.nextInt();
            int touchedSite = in.nextInt(); // -1 if none
            List<Structure> structures = new ArrayList<>();
            for (int i = 0; i < numSites; i++) {
                int siteId = in.nextInt();
                int ignore1 = in.nextInt(); // used in future leagues
                int ignore2 = in.nextInt(); // used in future leagues
                int structureType = in.nextInt(); // -1 = No structure, 2 = Barracks
                int owner = in.nextInt(); // -1 = No structure, 0 = Friendly, 1 = Enemy
                int param1 = in.nextInt(); // remain ticks, 0 = no work
                int param2 = in.nextInt(); // unit type, 0 for KNIGHT, 1 for ARCHER
                structures.add(new Structure(
                        siteId,
                        StructureType.from(structureType),
                        OwnerType.from(owner),
                        param1,
                        UnitType.from(param2)
                ));
            }
            List<Structure> myBarracks = structures.stream()
                                                   .filter(b -> b.structure == StructureType.BARRACKS && b.owner == OwnerType.PLAYER)
                                                   .collect(Collectors.toList());
            List<Structure> myTowers = structures.stream()
                                                 .filter(b -> b.structure == StructureType.TOWER && b.owner == OwnerType.PLAYER)
                                                 .collect(Collectors.toList());
            List<Structure> vacantBarracks = structures.stream()
                                                       .filter(b -> b.structure == StructureType.NONE || (b.structure == StructureType.BARRACKS && b.owner == OwnerType.ENEMY))
                                                       .collect(Collectors.toList());
            List<Structure> enemyBarracks = structures.stream()
                                                      .filter(b -> b.structure == StructureType.BARRACKS && b.owner == OwnerType.ENEMY)
                                                      .collect(Collectors.toList());
            List<Structure> readyBarracks = myBarracks.stream().filter(b -> b.ticks == 0).collect(Collectors.toList());

            int numUnits = in.nextInt();
            int archers = 0;
            int knights = 0;
            int giants = 0;
            Queen myQueen = null;
            Queen enemyQueen = null;
            for (int i = 0; i < numUnits; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int owner = in.nextInt(); // 0 = Friendly; 1 = Enemy
                int unitType = in.nextInt(); // -1 = QUEEN, 0 = KNIGHT, 1 = ARCHER
                int health = in.nextInt();
                if (owner == 0) {
                    if (unitType == -1) {
                        myQueen = new Queen(x, y, health);
                        if (basePosition == null) {
                            if (x < 300 || y < 300) {
                                basePosition = new Site(-1, 0, 0, 0);
                            } else {
                                basePosition = new Site(-1, 1920, 1000, 0);
                            }
                        }
                    } else {
                        int i1 = unitType == 0 ? knights++ : unitType == 1 ? archers++ : giants++;
                    }
                } else {
                    if (unitType == -1) {
                        enemyQueen = new Queen(x, y, health);
                    }
                }
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages..");
            System.err.println("My Barracks " + myBarracks.stream().map(b -> b.siteId).collect(Collectors.toList()));
            System.err.println("Vacant Barracks " + vacantBarracks.stream()
                                                                  .map(b -> b.siteId)
                                                                  .collect(Collectors.toList()));
            List<String> trainBarracks = new ArrayList<>();
            String command = "WAIT";

            Structure nearVacant = findNearestBarrack(vacantBarracks, myQueen);
            if (nearVacant != null) {
                System.err.println("Nearest barrack " + nearVacant.siteId);
                if (touchedSite == nearVacant.siteId) {
                    System.err.println("Touch target site!");
                    command = "BUILD ";
                    command += nearVacant.siteId;
                    command += " ";

                    String type = getNextStructure(myBarracks, myTowers);
                    command += type;
                } else if (myBarracks.size() + myTowers.size() < STRUCTURE_LIMIT.get(StructureType.TOWER) +
                        STRUCTURE_LIMIT.get(StructureType.BARRACKS)) {
                    command = "MOVE";
                    Site target = siteMap.get(nearVacant.siteId);
                    command += String.format(" %d %d", target.x, target.y);
                    System.err.println("Move to coords " + target.x + " " + target.y);
                } else {
                    System.err.printf("Goto base position %d %d", basePosition.x, basePosition.y);
                    command = "MOVE";
                    command += String.format(" %d %d", basePosition.x, basePosition.y);
                }
            }

            Comparator<Structure> comparator = new BarrackOrderComparator(myQueen, enemyQueen);
            List<Structure> notWorking = myBarracks.stream().filter(b -> b.ticks == 0).collect(Collectors.toList());
            System.err.println("Ready for order " + notWorking.stream()
                                                              .map(b -> b.siteId)
                                                              .collect(Collectors.toList()));
            if (notWorking.size() > 0) {
                double archerRatio = archers * 1.0 / UNIT_RATIO.get(UnitType.ARCHER);
                double knightRatio = knights * 1.0 / UNIT_RATIO.get(UnitType.KNIGHT);
                double giantRatio = giants * 1.0 / UNIT_RATIO.get(UnitType.GIANT);
                if (archerRatio < knightRatio && archerRatio < giantRatio) {
                    System.err.println("Try to order archer");
                    List<Structure> archerQueue = notWorking.stream()
                                                            .filter(b -> b.unitType == UnitType.ARCHER)
                                                            .sorted(comparator)
                                                            .collect(Collectors.toList());
                    if (archerQueue.size() > 0 && gold >= 100) {
                        Structure nearestToMe = archerQueue.get(0);
                        trainBarracks.add(" " + nearestToMe.siteId);
                        gold -= 100;
                        System.err.printf("Request %s for 100 gold from %d", nearestToMe.unitType, nearestToMe.siteId);
                    } else {
                        System.err.println("Not enough gold");
                    }
                } else if (knightRatio <= archerRatio && knightRatio <= giantRatio) {
                    System.err.println("Try to order knights");
                    List<Structure> knightsQueue = notWorking.stream()
                                                             .filter(b -> b.unitType == UnitType.KNIGHT)
                                                             .sorted(comparator)
                                                             .collect(Collectors.toList());
                    if (knightsQueue.size() > 0 && gold >= 80) {
                        Structure nearestToEnemy = knightsQueue.get(0);
                        trainBarracks.add(" " + nearestToEnemy.siteId);
                        gold -= 80;
                        System.err.printf("Request %s for 80 gold from %d", nearestToEnemy.unitType, nearestToEnemy.siteId);
                    } else {
                        System.err.println("Not enough gold");
                    }
                } else {
                    System.err.println("Try to order giants");
                    List<Structure> giantsQueue = notWorking.stream()
                                                             .filter(b -> b.unitType == UnitType.GIANT)
                                                             .sorted(comparator)
                                                             .collect(Collectors.toList());
                    if (giantsQueue.size() > 0 && gold >= 140) {
                        Structure nearestToEnemy = giantsQueue.get(0);
                        trainBarracks.add(" " + nearestToEnemy.siteId);
                        gold -= 140;
                        System.err.printf("Request %s for 140 gold from %d", nearestToEnemy.unitType, nearestToEnemy.siteId);
                    } else {
                        System.err.println("Not enough gold");
                    }
                }
            }

            // First line: A valid queen action
            // Second line: A set of training instructions
            System.out.println(command);
            System.out.println("TRAIN" + String.join("", trainBarracks));
        }
    }

    private static String getNextStructure(List<Structure> myBarracks, List<Structure> myTowers) {
        long archerBarracks = myBarracks.stream().filter(b -> b.unitType == UnitType.ARCHER).count();
        long knightBarracks = myBarracks.stream().filter(b -> b.unitType == UnitType.KNIGHT).count();
        long giantBarracks = myBarracks.stream().filter(b -> b.unitType == UnitType.GIANT).count();
        long towers = myTowers.size();
        if (archerBarracks + knightBarracks + giantBarracks < STRUCTURE_LIMIT.get(StructureType.BARRACKS)) {
            if (towers < STRUCTURE_LIMIT.get(StructureType.TOWER)) {
                if ((archerBarracks + knightBarracks + giantBarracks) * 1.0 / STRUCTURE_LIMIT.get(StructureType.BARRACKS)
                    <= towers * 1.0 / STRUCTURE_LIMIT.get(StructureType.TOWER)) {
                    double archers = archerBarracks * 1.0 / BARRACKS_RATIO.get(UnitType.ARCHER);
                    double knights = knightBarracks * 1.0 / BARRACKS_RATIO.get(UnitType.KNIGHT);
                    double giants = giantBarracks * 1.0 / BARRACKS_RATIO.get(UnitType.GIANT);
                    System.err.printf("Barracks ratio %f %f %f", archers, knights, giants);
                    if (knights <= archers && knights <= giants) {
                        return "BARRACKS-KNIGHT";
                    }
                    if (archers <= knights && archers <= giants) {
                        return "BARRACKS-ARCHER";
                    }
                    return "BARRACKS-GIANT";
                } else {
                    System.err.println("Towers are less than barracks");
                    return "TOWER";
                }
            } else {
                System.err.println("All towers are built");
                double archers = archerBarracks * 1.0 / BARRACKS_RATIO.get(UnitType.ARCHER);
                double knights = knightBarracks * 1.0 / BARRACKS_RATIO.get(UnitType.KNIGHT);
                double giants = giantBarracks * 1.0 / BARRACKS_RATIO.get(UnitType.GIANT);
                System.err.printf("Barracks ratio %f %f %f", archers, knights, giants);
                if (knights <= archers && knights <= giants) {
                    return "BARRACKS-KNIGHT";
                }
                if (archers <= knights && archers <= giants) {
                    return "BARRACKS-ARCHER";
                }
                return "BARRACKS-GIANT";
            }
        } else {
            System.err.println("All barracks are built");
            return "TOWER";
        }
    }

    private static Structure findNearestBarrack(List<Structure> vacantBarracks, Queen myQueen) {
        double distance = Integer.MAX_VALUE;
        Structure nearest = null;
        for (Structure barrack : vacantBarracks) {
            if (getDistance(siteMap.get(barrack.siteId), myQueen) < distance) {
                nearest = barrack;
                distance = getDistance(siteMap.get(barrack.siteId), myQueen);
            }
        }
        return nearest;
    }

    private static double getDistance(Site site, Queen unit) {
        return Math.sqrt((site.x - unit.x) * (site.x - unit.x) + (site.y - unit.y) * (site.y - unit.y));
    }

    private static class BarrackOrderComparator implements Comparator<Structure> {

        private final Queen myQueen;
        private final Queen enemyQueen;

        public BarrackOrderComparator(Queen myQueen, Queen enemyQueen) {
            this.myQueen = myQueen;
            this.enemyQueen = enemyQueen;
        }

        @Override
        public int compare(Structure o1, Structure o2) {
            if (o1.unitType == UnitType.ARCHER) {
                double d1 = getDistance(siteMap.get(o1.siteId), myQueen);
                double d2 = getDistance(siteMap.get(o2.siteId), myQueen);
                return Double.compare(d2, d1);
            } else {
                double d1 = getDistance(siteMap.get(o1.siteId), enemyQueen);
                double d2 = getDistance(siteMap.get(o2.siteId), enemyQueen);
                return Double.compare(d1, d2);
            }
        }
    }
}