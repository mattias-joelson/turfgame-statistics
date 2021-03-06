package org.joelson.mattias.turfgame.warded;

import org.joelson.mattias.turfgame.apiv4.Zone;
import org.joelson.mattias.turfgame.apiv4.ZonesTest;
import org.joelson.mattias.turfgame.lundkvist.MunicipalityTest;
import org.joelson.mattias.turfgame.util.KMLWriter;
import org.joelson.mattias.turfgame.util.URLReaderTest;
import org.joelson.mattias.turfgame.zundin.Monthly;
import org.joelson.mattias.turfgame.zundin.MonthlyTest;
import org.joelson.mattias.turfgame.zundin.MonthlyZone;
import org.junit.Test;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HeatmapTest {
    
    public static final int TAKES_ENTRIES = HeatmapCategories.VIOLET.getTakes() + 1;
    
    private static Boolean apply(String s) {
        return Boolean.TRUE;
    }
    
    private enum WardedCategories {
        UNTAKEN(0),
        GREEN(1),
        YELLOW(2),
        ORANGE(11),
        RED(21),
        VIOLET(51);
    
        private final int takes;
    
        WardedCategories(int takes) {
            this.takes = takes;
        }
    
        public int getTakes() {
            return takes;
        }
    }
    
    private enum HeatmapCategories {
        UNTAKEN(WardedCategories.UNTAKEN),
        GREEN(WardedCategories.GREEN),
        YELLOW(WardedCategories.YELLOW),
        ORANGE(WardedCategories.ORANGE),
        RED_21(WardedCategories.RED),
        RED_27(WardedCategories.RED, 27),
        RED_33(WardedCategories.RED, 33),
        RED_39(WardedCategories.RED, 39),
        RED_45(WardedCategories.RED, 45),
        VIOLET(WardedCategories.VIOLET);
    
        private final WardedCategories category;
        private final int takes;
    
        HeatmapCategories(WardedCategories category) {
            this(category, category.getTakes());
        }
    
        HeatmapCategories(WardedCategories category, int takes) {
            this.category = category;
            this.takes = takes;
        }
    
        public WardedCategories getCategory() {
            return category;
        }
    
        public int getTakes() {
            return takes;
        }
    }
    
    @Test
    public void danderydHeatmap() throws IOException {
        municipalityHeatmap("danderyd_heatmap.kml", readTakenZones(), MunicipalityTest.getDanderydZones());
    }

    @Test
    public void solnaHeatmap() throws IOException {
        municipalityHeatmap("solna_heatmap.kml", readTakenZones(), MunicipalityTest.getSolnaZones());
    }

    @Test
    public void sundbybergHeatmap() throws IOException {
        municipalityHeatmap("sundbyberg_heatmap.kml", readTakenZones(), MunicipalityTest.getSundbybergZones());
    }
    
    @Test
    public void leifonsSolnaHeatmap() throws IOException {
        municipalityHeatmap("leifons_solna_heatmap.kml", readLeifonsTakenZones(), MunicipalityTest.getLeifonsSolnaZones());
    }

    @Test
    public void leifonsSundbybergHeatmap() throws IOException {
        municipalityHeatmap("leifons_sundbyberg_heatmap.kml", readLeifonsTakenZones(), MunicipalityTest.getLeifonsSundbybergZones());
    }
    
    @Test
    public void monthlyHeatmap() throws IOException {
        Monthly monthly = MonthlyTest.getMonthly();
        Map<String, Integer> takenZones = monthly.getZones().stream()
                .collect(Collectors.toMap(MonthlyZone::getName, monthlyZone -> monthlyZone.getTakes() + monthlyZone.getAssists()));
        Map<String, Boolean> municipalityZones = takenZones.keySet().stream().collect(Collectors.toMap(Function.identity(), HeatmapTest::apply));
        municipalityHeatmap("monthlyHeatmap.kml", takenZones, municipalityZones);
    }
    
    @Test
    public void monthlySolnaHeatmap() throws IOException {
        Monthly monthly = MonthlyTest.getMonthly();
        Map<String, Integer> takenZones = monthly.getZones().stream()
                .collect(Collectors.toMap(MonthlyZone::getName, monthlyZone -> monthlyZone.getTakes() + monthlyZone.getAssists()));
        Map<String, Boolean> municipalityZones = MunicipalityTest.getSolnaZones();
        municipalityHeatmap("monthlySolnaHeatmap.kml", takenZones, municipalityZones);
    }

    private void municipalityHeatmap(String filename, Map<String, Integer> takenZones, Map<String, Boolean> municipalityZones) throws IOException {
        List<Zone> allZones = ZonesTest.getAllZones();
    
        Map<Zone, Integer>[] zoneMaps = new Map[TAKES_ENTRIES];
        initZoneMaps(zoneMaps, HeatmapCategories.UNTAKEN);
        initZoneMaps(zoneMaps, HeatmapCategories.GREEN);
        initZoneMaps(zoneMaps, HeatmapCategories.YELLOW, HeatmapCategories.ORANGE);
        initZoneMaps(zoneMaps, HeatmapCategories.ORANGE, HeatmapCategories.RED_21);
        initZoneMaps(zoneMaps, HeatmapCategories.RED_21, HeatmapCategories.RED_27);
        initZoneMaps(zoneMaps, HeatmapCategories.RED_27, HeatmapCategories.RED_33);
        initZoneMaps(zoneMaps, HeatmapCategories.RED_33, HeatmapCategories.RED_39);
        initZoneMaps(zoneMaps, HeatmapCategories.RED_39, HeatmapCategories.RED_45);
        initZoneMaps(zoneMaps, HeatmapCategories.RED_45, HeatmapCategories.VIOLET);
        initZoneMaps(zoneMaps, HeatmapCategories.VIOLET);
        int[] zoneTakes = new int[TAKES_ENTRIES];
    
        for (String zoneName : municipalityZones.keySet()) {
            int takes = 0;
            if (takenZones.containsKey(zoneName)) {
                takes = takenZones.get(zoneName);
            }
            for (Zone zone : allZones) {
                if (zone.getName().equals(zoneName)) {
                    int cappedTakes = Math.min(takes, 51);
                    zoneMaps[cappedTakes].put(zone, takes);
                    zoneTakes[cappedTakes] += 1;
                    break;
                }
            }
        }
    
        int toOrange = countTakes(zoneTakes, WardedCategories.ORANGE);
        int toOrangeZones = countZones(zoneTakes, WardedCategories.ORANGE);
        int toRed = countTakes(zoneTakes, WardedCategories.RED);
        int toRedZones = countZones(zoneTakes, WardedCategories.RED);
        int toViolet = countTakes(zoneTakes, WardedCategories.VIOLET);
        int toVioletZones = countZones(zoneTakes, WardedCategories.VIOLET);

        KMLWriter out = new KMLWriter(filename);
        writeHeatmapFolder(out, zoneMaps[HeatmapCategories.UNTAKEN.getTakes()], "untaken");
        writeHeatmapFolder(out, zoneMaps[HeatmapCategories.GREEN.getTakes()], "green");
        writeHeatmapFolder(out, zoneMaps[HeatmapCategories.YELLOW.getTakes()], "yellow");
        writeHeatmapFolder(out, zoneMaps[HeatmapCategories.ORANGE.getTakes()], "orange");
        writeHeatmapFolder(out, zoneMaps[HeatmapCategories.RED_21.getTakes()], "red 21-26");
        writeHeatmapFolder(out, zoneMaps[HeatmapCategories.RED_27.getTakes()], "red 27-32");
        writeHeatmapFolder(out, zoneMaps[HeatmapCategories.RED_33.getTakes()], "red 33-38");
        writeHeatmapFolder(out, zoneMaps[HeatmapCategories.RED_39.getTakes()], "red 39-44");
        writeHeatmapFolder(out, zoneMaps[HeatmapCategories.RED_45.getTakes()], "red 45-50");
        writeHeatmapFolder(out, zoneMaps[WardedCategories.VIOLET.getTakes()], "violet");
        out.close();
        
        int[][] zoneCountArray = IntStream.range(0, zoneTakes.length).mapToObj(i -> new int[] { i, zoneTakes[i]}).sorted(Comparator.comparingInt(a -> a[1])).toArray(int[][]::new);
        int max = zoneCountArray[51][1];
        if (max % 5 != 0) {
            max = ((max / 5) + 1) * 5;
        }
        int nextToMax = zoneCountArray[50][1];
        if (nextToMax % 5 != 0) {
            nextToMax = ((nextToMax / 5) + 1) * 5;
        }
        for (int i = max; i > 0; i -= 1) {
            if (i % 5 == 0) {
                if (i + 5 == max && i > nextToMax + 5) {
                    System.out.println(String.format("   : %" + (zoneCountArray[51][0] + 1) + "s", ":"));
                    i = nextToMax;
                }
                if (i >= 10) {
                    System.out.print(i + " + ");
                } else {
                    System.out.print(" " + i + " + ");
                }
            } else {
                System.out.print("   | ");
            }
            for (int c = 0; c < zoneTakes.length; c += 1) {
                System.out.print((zoneTakes[c] >= i) ? "*" : " ");
            }
            System.out.println();
        }
        System.out.println("   +-+----+----+----+----+----+----+----+----+----+----+-");
        System.out.println("     0    5   10   15   20   25   30   35   40   45   50");

        System.out.println("Municipality:    " + filename);
        System.out.println("Takes to orange: " + toOrange + " (" + toOrangeZones + " zones)");
        System.out.println("Takes to red:    " + toRed + " (" + toRedZones + " zones)");
        System.out.println("Takes to violet: " + toViolet + " (" + toVioletZones + " zones)");
    }
    
    private void initZoneMaps(Map<Zone, Integer>[] zoneMaps, HeatmapCategories category) {
        initZoneMaps(zoneMaps, category.takes);
    }
    
    private void initZoneMaps(Map<Zone, Integer>[] zoneMaps, HeatmapCategories category, HeatmapCategories nextCategory) {
        initZoneMaps(zoneMaps, IntStream.range(category.getTakes(), nextCategory.getTakes()).toArray());
    }
    
    private void initZoneMaps(Map<Zone, Integer>[] zoneMaps, int... takes) {
        Map<Zone, Integer> map = new HashMap<>();
        for (int take : takes) {
            zoneMaps[take] = map;
        }
    }
    
    private void writeHeatmapFolder(KMLWriter out, Map<Zone, Integer> zoneCounts, String folderName) {
        if (zoneCounts.isEmpty()) {
            return;
        }
        out.writeFolder(folderName);
        zoneCounts.entrySet().stream()
                .sorted(getEntryComparator())
                .forEach(zoneCountEntry -> out.writePlacemark(String.format("%d - %s", zoneCountEntry.getValue(), zoneCountEntry.getKey().getName()),
                        "", zoneCountEntry.getKey().getLongitude(), zoneCountEntry.getKey().getLatitude()));
    }
    
    private Comparator<Entry<Zone, Integer>> getEntryComparator() {
        return (o1, o2) -> {
            int countDiff = o1.getValue() - o2.getValue();
            if (countDiff != 0) {
                return  countDiff;
            }
            return o1.getKey().getName().compareTo(o2.getKey().getName());
        };
    }

    private int countZones(int[] zoneTakes, WardedCategories limit) {
        return IntStream.range(0, limit.getTakes())
                .map(i -> zoneTakes[i])
                .sum();
    }
    
    private int countTakes(int[] zoneTakes, WardedCategories limit) {
        int limitTakes = limit.getTakes();
        return IntStream.range(0, limitTakes)
                .map(i -> (limitTakes - i) * zoneTakes[i])
                .sum();
    }
    
    public static Map<String, Integer> readTakenZones() throws IOException {
        return URLReaderTest.readProperties("/warded.unique.php.html", TakenZones::fromHTML);
    }

    public static Map<String, Integer> readLeifonsTakenZones() throws IOException {
        return URLReaderTest.readProperties("/Leifons-sthlm-unique.php.html", TakenZones::fromHTML);
    }
}
