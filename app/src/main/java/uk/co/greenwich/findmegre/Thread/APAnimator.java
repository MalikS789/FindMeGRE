package uk.co.greenwich.findmegre.Thread;

import uk.co.greenwich.findmegre.DrawingPanel;

public class APAnimator extends Thread {

    private final int Delay = 4;
    public static float WavePropergation = 0;
    public static int Speed = 1;
    private final DrawingPanel drawingpanel;

    public APAnimator(DrawingPanel drawingpanel) {
        this.drawingpanel = drawingpanel;
    }

    @Override
    public void run() {
        while (true) {
            WavePropergation = WavePropergation + Speed;
            if (WavePropergation > drawingpanel.Height() / 2) {
                WavePropergation = 0;
            }
            try {
                Thread.sleep(Delay);
            } catch (InterruptedException ex) {
            }
        }
    }
}
