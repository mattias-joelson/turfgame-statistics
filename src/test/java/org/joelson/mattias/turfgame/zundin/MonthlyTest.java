package org.joelson.mattias.turfgame.zundin;

import org.joelson.mattias.turfgame.apiv4.Zone;
import org.joelson.mattias.turfgame.apiv4.ZonesTest;
import org.joelson.mattias.turfgame.statistics.Statistics;
import org.joelson.mattias.turfgame.statistics.StatisticsInitializer;
import org.joelson.mattias.turfgame.util.URLReaderTest;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MonthlyTest {

    private static final String OBEROFF = "Oberoff";
    private static final int ROUND = 113;

    @Test
    public void parseStatisticsPartFile() throws IOException {
        Monthly monthly = getPartMonthly();
        assertNotNull(monthly);
        assertEquals(OBEROFF, monthly.getUserName());
        assertEquals(ROUND, monthly.getRound());
        assertEquals(423, (monthly.getZones()).size());
        int takes = monthly.getZones().stream()
                .map(MonthlyZone::getTakes)
                .collect(Collectors.summingInt(Integer::intValue));
        assertEquals(872, takes);
        int sumPoints = monthly.getZones().stream()
                .map(MonthlyZone::getPoints)
                .collect(Collectors.summingInt(Integer::intValue));
        assertEquals(159616, sumPoints);
        long totalDuration = monthly.getZones().stream()
                .collect(Collectors.summingLong(zone -> zone.getTakes() * zone.getAverageDuration().toSeconds()));
        long averageDuration = totalDuration / takes;
        assertEquals(Duration.ofSeconds(13 * 3600 + 43 * 60 + 25), Duration.ofSeconds(averageDuration));
    }

    @Test
    public void testAddPartToStatistics() throws  IOException {
        Monthly monthly = getPartMonthly();
        Statistics statistics = StatisticsInitializer.initialize();
        Monthly.addToStatistics(monthly, statistics);
    }

    private static Monthly getPartMonthly() throws IOException {
        return readProperties("/monthly_oberoff_round113_part.html");
    }

    @Test
    public void parseStatisticsFile() throws IOException {
        Monthly monthly = getMonthly();
        assertNotNull(monthly);
        assertEquals(OBEROFF, monthly.getUserName());
        assertEquals(ROUND, monthly.getRound());
        assertEquals(256, (monthly.getZones()).size());
    }

    @Test
    public void testAddToStatistics() throws  IOException {
        Monthly monthly = getMonthly();
        Statistics statistics = StatisticsInitializer.initialize();
        Monthly.addToStatistics(monthly, statistics);
    }

    public static Monthly getMonthly() throws IOException {
        return readProperties("/monthly_oberoff_round114.html");
    }

    private static Monthly readProperties(String resource) throws IOException {
        return URLReaderTest.readProperties(resource, s -> Monthly.fromHTML(OBEROFF, ROUND, s));
    }

    @Test
    public void createYearly() throws IOException {
        File outFile = new File(URLReaderTest.class.getResource("/yearly.html").getFile());
        //File outFile = new File("/Users/mattias.joelson/src/turfgame-statistics/src/test/resources/yearly.html");
        try (FileOutputStream output = new FileOutputStream(outFile)) {
            File startFile = new File(URLReaderTest.class.getResource("/yearly_start.html").getFile());
            try (FileInputStream input = new FileInputStream(startFile)) {
                int ch;
                while ((ch = input.read()) != -1) {
                    output.write(ch);
                }
            }

            Monthly monthly = getMonthly();
            Map<String, Zone> zones = getZones();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output));
            boolean comma = false;
            for (MonthlyZone zone : monthly.getZones()) {
                Zone turfZone = zones.get(zone.getName());
                if (!turfZone.getRegion().getName().equals("Stockholm")) {
                    continue;
                }
                if (comma) {
                    writer.println(',');
                } else {
                    comma = true;
                }
                writer.println("\t\t{");
                writer.println("\t\t\t\"type\": \"Feature\",");
                writer.println("\t\t\t\"geometry\": {");
                writer.println("\t\t\t\t\"type\": \"Point\",");
                writer.println("\t\t\t\t\"coordinates\": [");
                writer.println("\t\t\t\t\t" + turfZone.getLongitude() + ',');
                writer.println("\t\t\t\t\t" + turfZone.getLatitude());
                writer.println("\t\t\t\t]");
                writer.println("\t\t\t},");
                writer.println("\t\t\t\"properties\": {");
                writer.println("\t\t\t\t\"title\": \"" + zone.getName() + "\",");
                writer.println("\t\t\t\t\"count\": " + (zone.getTakes() + zone.getAssists()));
                writer.println("\t\t\t}");
                writer.print("\t\t}");
            }
            writer.println();
            writer.flush();

            File endFile = new File(URLReaderTest.class.getResource("/yearly_end.html").getFile());
            try (FileInputStream input = new FileInputStream(endFile)) {
                int ch;
                while ((ch = input.read()) != -1) {
                    output.write(ch);
                }
            }
        }
    }

    private Map<String,Zone> getZones() throws IOException {
        List<Zone> z = ZonesTest.getAllZones();
        Map<String, Zone> zones = new HashMap<>(z.size());
        for (Zone zone : z) {
            zones.put(zone.getName(), zone);
        }
        return zones;
    }
}

