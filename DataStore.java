import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Reads and writes timetable and booking data in CSV files (folder "data"). */
public class DataStore {
    private final Path dir, trainFile, bookingFile;
    private final List<Train> trains = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    public DataStore(String folder) {
        dir = Paths.get(folder);
        trainFile = dir.resolve("trains.csv");
        bookingFile = dir.resolve("bookings.csv");
    }

    public void load() throws IOException {
        Files.createDirectories(dir);
        if (!Files.exists(trainFile)) writeDefaultTrains();
        if (!Files.exists(bookingFile)) Files.write(bookingFile, Collections.singletonList(Booking.HEADER));
        trains.clear();
        bookings.clear();
        for (String line : Files.readAllLines(trainFile)) {
            if (line.isBlank() || line.startsWith("TrainNo")) continue;
            trains.add(Train.fromCsv(line));
        }
        for (String line : Files.readAllLines(bookingFile)) {
            if (line.isBlank() || line.startsWith("Ref")) continue;
            bookings.add(Booking.fromCsv(line));
        }
    }

    public void saveBookings() throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(Booking.HEADER);
        for (Booking b : bookings) lines.add(b.toCsv());
        Files.write(bookingFile, lines);
    }

    /** Sample timetable. Edit trains.csv to match the current schedule on railway.gov.lk. */
    private void writeDefaultTrains() throws IOException {
        List<String> l = new ArrayList<>();
        l.add("TrainNo,Name,Route,Departure,Arrival,Carriages,SeatsPerCarriage");
        l.add("4077,Yal Devi,Colombo - Kankesanthurai,05:45,13:45,8,56");
        l.add("4017,Uttara Devi,Colombo - Kankesanthurai,06:45,15:15,10,48");
        l.add("8057,Galu Kumari,Colombo - Galle,06:55,09:40,6,60");
        l.add("8059,Ruhunu Kumari,Colombo - Galle,15:35,18:20,7,52");
        l.add("1005,Podi Menike,Colombo - Kandy,05:55,09:05,9,64");
        l.add("1015,Intercity Express,Colombo - Kandy,07:00,10:10,6,56");
        l.add("1021,Udarata Menike,Colombo - Kandy,08:30,12:20,8,60");
        Files.write(trainFile, l);
    }

    public List<Train> trains() { return trains; }

    public List<String> routes() {
        Set<String> r = new LinkedHashSet<>();
        for (Train t : trains) r.add(t.route);
        return new ArrayList<>(r);
    }

    public List<Train> trainsOnRoute(String route) {
        List<Train> out = new ArrayList<>();
        for (Train t : trains) if (t.route.equals(route)) out.add(t);
        return out;
    }

    /** Seat keys ("carriage-seat") already booked on a train and date, optionally ignoring one booking. */
    public Set<String> bookedSeats(String trainNo, String date, String excludeRef) {
        Set<String> s = new HashSet<>();
        for (Booking b : bookings)
            if (b.trainNo.equals(trainNo) && b.date.equals(date) && !b.ref.equals(excludeRef))
                s.add(b.seatKey());
        return s;
    }

    public int seatCountForId(String trainNo, String date, String idNo, String excludeRef) {
        int n = 0;
        for (Booking b : bookings)
            if (b.trainNo.equals(trainNo) && b.date.equals(date)
                    && b.idNo.equalsIgnoreCase(idNo) && !b.ref.equals(excludeRef)) n++;
        return n;
    }

    public Map<String, List<Booking>> bookingsByRef(String trainNo, String date) {
        Map<String, List<Booking>> m = new LinkedHashMap<>();
        for (Booking b : bookings)
            if (b.trainNo.equals(trainNo) && b.date.equals(date))
                m.computeIfAbsent(b.ref, k -> new ArrayList<>()).add(b);
        return m;
    }

    public String nextRef() {
        int max = 0;
        for (Booking b : bookings) {
            try { max = Math.max(max, Integer.parseInt(b.ref.substring(2))); }
            catch (NumberFormatException ignored) { }
        }
        return String.format("BK%05d", max + 1);
    }

    public void add(List<Booking> list) { bookings.addAll(list); }

    public void removeRef(String ref) { bookings.removeIf(b -> b.ref.equals(ref)); }
}
