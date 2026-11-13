package com.shafd.facerecognition;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

public class AttendanceUI {
    // Only the Swing event thread accesses these components.
    private JFrame window;
    private JLabel cameraLabel;
    private DefaultListModel<String> presentModel;
    private DefaultListModel<String> absentModel;

    private volatile Runnable onStart;
    private volatile Runnable onStop;

    public void show() {
        SwingUtilities.invokeLater(() -> {
            buildWindow();
            window.setVisible(true);
        });
    }

    public void updateFrame(Mat frame) {
        if (frame == null || frame.empty()) {
            return;
        }

        MatOfByte buffer = new MatOfByte();
        try {
            if (!Imgcodecs.imencode(".jpg", frame, buffer)) {
                return;
            }
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(buffer.toArray()));
            if (image != null) {
                SwingUtilities.invokeLater(() -> {
                    buildWindow();
                    cameraLabel.setIcon(new ImageIcon(image));
                });
            }
        } catch (IOException e) {
            System.err.println("Could not read the camera image: " + e.getMessage());
        } finally {
            buffer.release();
        }
    }

    public void updateStatus(List<Person> present, List<Person> absent) {
        // Copy the names before handing the update to the Swing thread.
        List<String> presentNames = namesOf(present);
        List<String> absentNames = namesOf(absent);
        SwingUtilities.invokeLater(() -> {
            buildWindow();
            presentModel.clear();
            presentModel.addAll(presentNames);
            absentModel.clear();
            absentModel.addAll(absentNames);
        });
    }

    public void setOnStart(Runnable action) {
        onStart = action;
    }

    public void setOnStop(Runnable action) {
        onStop = action;
    }

    private static List<String> namesOf(List<Person> people) {
        if (people == null) {
            return List.of();
        }
        return people.stream().map(Person::getName).toList();
    }

    private void buildWindow() {
        if (window != null) {
            return;
        }

        window = new JFrame("Facial Recognition Attendance");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cameraLabel = new JLabel("", SwingConstants.CENTER);
        cameraLabel.setPreferredSize(new Dimension(640, 480));
        window.add(cameraLabel, BorderLayout.CENTER);

        presentModel = new DefaultListModel<>();
        absentModel = new DefaultListModel<>();
        JPanel status = new JPanel(new GridLayout(2, 1, 0, 8));
        status.setPreferredSize(new Dimension(200, 480));
        status.add(createStatusList("Present", presentModel));
        status.add(createStatusList("Absent", absentModel));
        window.add(status, BorderLayout.EAST);

        JButton start = new JButton("Start");
        JButton stop = new JButton("Stop");
        stop.setEnabled(false);
        start.addActionListener(event -> {
            start.setEnabled(false);
            stop.setEnabled(true);
            runAction(onStart);
        });
        stop.addActionListener(event -> {
            start.setEnabled(true);
            stop.setEnabled(false);
            runAction(onStop);
        });

        JPanel controls = new JPanel();
        controls.add(start);
        controls.add(stop);
        window.add(controls, BorderLayout.SOUTH);
        window.pack();
    }

    private static JScrollPane createStatusList(String title, DefaultListModel<String> model) {
        JScrollPane pane = new JScrollPane(new JList<>(model));
        pane.setBorder(BorderFactory.createTitledBorder(title));
        return pane;
    }

    private static void runAction(Runnable action) {
        if (action != null) {
            action.run();
        }
    }

    public static void main(String[] args) {
        nu.pattern.OpenCV.loadLocally();

        AttendanceUI ui = new AttendanceUI();
        ui.setOnStart(() -> System.out.println("Start clicked"));
        ui.setOnStop(() -> System.out.println("Stop clicked"));
        ui.show();
        ui.updateStatus(
                List.of(new Person(1, "Alex", List.of()), new Person(2, "Sam", List.of())),
                List.of(new Person(3, "Jordan", List.of())));

        // Generate demo frames on a background thread, like the real camera loop.
        var fakeCamera = Executors.newSingleThreadScheduledExecutor();
        fakeCamera.scheduleAtFixedRate(() -> {
            int shade = (int) ((System.nanoTime() / 100_000_000L) & 255);
            Mat frame = new Mat(480, 640, CvType.CV_8UC3, new Scalar(shade, 120, 255 - shade));
            try {
                ui.updateFrame(frame);
            } finally {
                frame.release();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }
}
