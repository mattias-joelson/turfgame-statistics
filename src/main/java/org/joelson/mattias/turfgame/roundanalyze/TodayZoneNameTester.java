package org.joelson.mattias.turfgame.roundanalyze;

import org.joelson.mattias.turfgame.apiv4.Zone;
import org.joelson.mattias.turfgame.apiv4.Zones;
import org.joelson.mattias.turfgame.util.ZoneUtil;
import org.joelson.mattias.turfgame.zundin.Today;
import org.joelson.mattias.turfgame.zundin.TodayZone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class TodayZoneNameTester {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage:\n\t" + TodayZoneNameTester.class.getName() + " zonefile.json todayfile.html");
            return;
        }
        List<Zone> zoneList = readZoneFile(args[0]);
        Map<String, Zone> zoneMap = ZoneUtil.toNameMap(zoneList);
        for (int i = 1; i < args.length; i += 1) {
            Today today = readTodayFile(args[i]);
            compareToday(args[i], zoneMap, today);
        }
    }

    private static List<Zone> readZoneFile(String zonesFilename) throws IOException {
        return Zones.fromJSON(Files.readString(Path.of(zonesFilename)));
    }

    private static Today readTodayFile(String todayFilename) throws IOException {
        String[] filenameParts = todayFilename.split("[.]");
        String username = filenameParts[filenameParts.length - 3];
        String dateString = filenameParts[filenameParts.length - 2];
        return Today.fromHTML(username, dateString, Files.readString(Path.of(todayFilename)));
    }

    private static void compareToday(String todayFilename, Map<String, Zone> zoneMap, Today today) {
        System.out.println(todayFilename);
        for (TodayZone todayZone : today.getZones()) {
            Zone zone = zoneMap.get(todayZone.getZoneName());
            if (zone == null) {
                throw new NullPointerException(todayFilename + " - " + todayZone.getZoneName());
            }
        }
    }
}
