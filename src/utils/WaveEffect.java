package utils;

public class WaveEffect {
    private double amplitude;
    private double wavelength;
    private double phase;
    private double amplitude2;
    private double wavelength2;
    private double phase2;
    private double noiseScale;
    private double strengthScale; // New field for wave strength adjustment


    public WaveEffect(double amplitude, double wavelength, double phase,
                      double amplitude2, double wavelength2, double phase2,
                      double noiseScale, double strengthScale) {
        this.amplitude = amplitude;
        this.wavelength = wavelength;
        this.phase = phase;
        this.amplitude2 = amplitude2;
        this.wavelength2 = wavelength2;
        this.phase2 = phase2;
        this.noiseScale = noiseScale;
        this.strengthScale = strengthScale; // Initialize strengthScale
    }

    public double getOffset(double x, double y, double time) {
        // Calculate the strength of the wave based on y position
        double strength = (1.0 - y) * strengthScale;

        // Primary wave with adjusted amplitude
        double wave1 = (amplitude * strength) * Math.sin((x / wavelength) + phase + time);

        // Secondary wave for more variation
        double wave2 = amplitude2 * Math.sin((x / wavelength2) + phase2 + time);

        // Combine waves and add some noise for variation
        return wave1 + wave2 * noiseScale;
    }
}