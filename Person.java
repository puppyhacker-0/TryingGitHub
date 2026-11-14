
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Person {

    private final int id;
    private final String name;
    private final List<SampleImage> samples;

    // Constructor: creates a student
    public Person(int id, String name, List<SampleImage> samples) {
        this.id = id;
        this.name = name;

        // Use an empty list if no images are provided
        if (samples == null) {
            this.samples = new ArrayList<>();
        } else {
            this.samples = new ArrayList<>(samples);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<SampleImage> getSamples() {
        return new ArrayList<>(samples);
    }

    // Two students are considered the same if their IDs match
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Person)) {
            return false;
        }

        Person other = (Person) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}

