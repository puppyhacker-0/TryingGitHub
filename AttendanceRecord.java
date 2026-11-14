import java.time.LocalDateTime;

public class AttendanceRecord {
    private final Person person;
    private final LocalDateTime timestamp;
    private final boolean present;

    // Constructor
    public AttendanceRecord(Person person, LocalDateTime timestamp, boolean present) {
        this.person = person;
        this.timestamp = timestamp;
        this.present = present;
    }

    public Person getPerson() {
        return person;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isPresent() {
        return present;
    }

    @Override
    public String toString() {
        if (present) {
            return person.getName() + " was present at " + timestamp;
        }
        return person.getName() + " was absent";
    }
}