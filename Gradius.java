import javax.swing.JFrame;

/**
	Entry point for the Gradius game
*/
@SuppressWarnings("serial")
public class Gradius extends JFrame {

	private final GradiusComp comp;

	public Gradius() {
		comp = new GradiusComp();
		setContentPane(comp);
	}

	public static void main(String[] args) {
		Gradius frame = new Gradius();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.comp.start();
	}
}
