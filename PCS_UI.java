import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class PCS_UI {
	
	// for Java UI
	public final int width = 400;
	public final int height = 300;
	private JFrame frame;
	private JButton button;
	private JLabel remoteIpLabel;
	private JLabel remoteRtpLabel;
	private JLabel remoteRtcpLabel;
	private JLabel localRtpLabel;
	private JLabel localRtcpLabel;
	private JLabel stateLabel;
	private JTextField remoteIpText;
	private JTextField remoteRtpText;
	private JTextField remoteRtcpText;
	private JTextField localRtpText;
	private JTextField localRtcpText;

	// PCS_UI constructor
	public PCS_UI(String title) {
		frame = new JFrame();
		button = new JButton("Dial");
		remoteIpLabel = new JLabel("Remote  IP");
		remoteRtpLabel = new JLabel("Remote  RTP  port");
		remoteRtcpLabel = new JLabel("Remote  RTCP  port");
		localRtpLabel = new JLabel("Local  RTP  port");;
		localRtcpLabel = new JLabel("Local  RTCP  port");;
		stateLabel = new JLabel("");
		remoteIpText = new JTextField("0.0.0.0");
		remoteRtpText = new JTextField("0");
		remoteRtcpText = new JTextField("0");
		localRtpText = new JTextField("0");
		localRtcpText = new JTextField("0");
		
		frame.setLocationRelativeTo(null);
		frame.setTitle(title);
		initUI();
	}
	
	public void initUI() {
		remoteIpLabel.setHorizontalAlignment(JLabel.CENTER);
		remoteRtpLabel.setHorizontalAlignment(JLabel.CENTER);
		remoteRtcpLabel.setHorizontalAlignment(JLabel.CENTER);
		localRtpLabel.setHorizontalAlignment(JLabel.CENTER);
		localRtcpLabel.setHorizontalAlignment(JLabel.CENTER);
		stateLabel.setHorizontalAlignment(JLabel.CENTER);
		remoteIpText.setHorizontalAlignment(JTextField.CENTER);
		remoteRtpText.setHorizontalAlignment(JTextField.CENTER);
		remoteRtcpText.setHorizontalAlignment(JTextField.CENTER);
		localRtpText.setHorizontalAlignment(JTextField.CENTER);
		localRtcpText.setHorizontalAlignment(JTextField.CENTER);
		
		frame.setLayout(new GridLayout(6, 2));
		frame.add(remoteIpLabel);
		frame.add(remoteIpText);
		frame.add(remoteRtpLabel);
		frame.add(remoteRtpText);
		frame.add(remoteRtcpLabel);
		frame.add(remoteRtcpText);
		frame.add(localRtpLabel);
		frame.add(localRtpText);
		frame.add(localRtcpLabel);
		frame.add(localRtcpText);
		frame.add(button);
		frame.add(stateLabel);

		frame.setSize(width, height);
		frame.setVisible(true);
	} // end setUI()

	public void setWindowLocation(int x, int y) {
		frame.setLocation(x, y);
	}
	public Point getWindowLocation() {
		return frame.getLocation();
	}
	
	public void setWindowListener(WindowAdapter adapter) {
		frame.addWindowListener(adapter);
	}

	public void setButtonActionListener(ActionListener listener) {
		button.addActionListener(listener);
	}

	public String getButtonText() {
		return button.getText();
	}
	
	public void setButtonText(String text) {
		button.setText(text);
	}
	
	public String getRemoteIP() {
		return remoteIpText.getText();
	}
	
	public int getRemoteRtpPort() {
		return Integer.parseInt(remoteRtpText.getText());
	}
	
	public int getRemoteRtcpPort() {
		return Integer.parseInt(remoteRtcpText.getText());
	}
	
	public int getLocalRtpPort() {
		return Integer.parseInt(localRtpText.getText());
	}
	
	public int getLocalRtcpPort() {
		return Integer.parseInt(localRtcpText.getText());
	}
	
	public void setStateText(String text) {
		stateLabel.setText(text);
	}
	
}
