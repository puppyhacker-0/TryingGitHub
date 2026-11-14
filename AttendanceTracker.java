import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AttendanceTracker {
    private final List<Person> roster;
    private final Set<Person> rosterSet;
    private final Map<Person, AttendanceRecord> presentRecords;

    public AttendanceTracker(List<Person> roster) {
        this.roster = new ArrayList<>();
        this.rosterSet = new LinkedHashSet<>();
        this.presentRecords = new LinkedHashMap<>();

        if (roster != null) {
            for (Person person : roster) {
                if (person != null && rosterSet.add(person)) {
                    this.roster.add(person);
                }
            }
        }
    }

    public void markPresent(Person p) {
        if (p == null || !rosterSet.contains(p) || presentRecords.containsKey(p)) {
            return;
        }

        Person rosterPerson = null;
        for (Person person : roster) {
            if (person.equals(p)) {
                rosterPerson = person;
                break;
            }
        }

        if (rosterPerson != null) {
            presentRecords.put(rosterPerson, new AttendanceRecord(rosterPerson, LocalDateTime.now(), true));
        }
    }

    public List<Person> getPresent() {
        return new ArrayList<>(presentRecords.keySet());
    }

    public List<Person> getAbsent() {
        List<Person> absent = new ArrayList<>();
        for (Person person : roster) {
            if (!presentRecords.containsKey(person)) {
                absent.add(person);
            }
        }
        return absent;
    }

    public List<AttendanceRecord> getRecords() {
        List<AttendanceRecord> records = new ArrayList<>(presentRecords.values());
        for (Person person : roster) {
            if (!presentRecords.containsKey(person)) {
                records.add(new AttendanceRecord(person, null, false));
            }
        }
        return records;
    }

    public static void main(String[] args) {
        List<Person> roster = new ArrayList<>();
        roster.add(new Person(1, "Alice", null));
        roster.add(new Person(2, "Bob", null));
        roster.add(new Person(3, "Cara", null));
        roster.add(new Person(4, "Dan", null));

        AttendanceTracker tracker = new AttendanceTracker(roster);
        tracker.markPresent(roster.get(0));
        tracker.markPresent(roster.get(1));
        tracker.markPresent(roster.get(1));
        tracker.markPresent(null);
        tracker.markPresent(new Person(99, "Ghost", null));

        System.out.println("Present: " + tracker.getPresent());
        System.out.println("Absent: " + tracker.getAbsent());
        System.out.println("Records: " + tracker.getRecords());
    }
}