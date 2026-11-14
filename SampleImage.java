import org.opencv.core.Mat;

public class SampleImage {
    private final String path;
    private final Mat image;

    //constructor
    public SampleImage(String path, Mat image) {
        this.path = path;
        this.image = image;
    }

    public String getPath() {
        return path;
    }

    public Mat getImage() {
        return image;
    }

    @Override
    public String toString() {
        return path;
    }
}
