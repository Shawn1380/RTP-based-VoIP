import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramSocket;
import java.util.Enumeration;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

import jlibrtp.DataFrame;
import jlibrtp.Participant;
import jlibrtp.RTPAppIntf;
import jlibrtp.RTPSession;


public class PCS_RTP_Callee implements RTPAppIntf {
	
	// for Java Audio
	private final int BUFFER_SIZE = 1024;
	private AudioFormat format;
	private TargetDataLine microphone;
	private SourceDataLine speaker; // also used as headphone
	
	// for RTP
	private RTPSession rtpSession;
	private DatagramSocket rtpSocket;
	private DatagramSocket rtcpSocket;
	private boolean isRegistered = false;
	private boolean isReceived = false;
	
	// for Callee UI
	private PCS_UI ui;

	public PCS_RTP_Callee() {
		// PCS_RTP_Callee constructor
	}
	
	public void checkDeviceIsOK() {
		if(!AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
			System.out.println("Error! Please make sure that your microphone is available!");
			JOptionPane.showMessageDialog(null, "Error! Please make sure that your microphone is available!");
			System.exit(-1);
		}
		if(!AudioSystem.isLineSupported(Port.Info.SPEAKER) && !AudioSystem.isLineSupported(Port.Info.HEADPHONE)) {
			System.out.println("Error! Please make sure that your speaker or headphone is available!");
			JOptionPane.showMessageDialog(null, "Error! Please make sure that your speaker or headphone is available!");
			System.exit(-1);
		}
	}
	
	public void setAudioFormat() {
		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        	float rate = 8000.0f;
        	int channels = 1;
        	int sampleSize = 16;
        	boolean bigEndian = false;
		
		format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);
	}
	
	public void initRecorder() {
		//TODO 3. initialize your microphone
		try {
			microphone = AudioSystem.getTargetDataLine(format);
			microphone.open();
			microphone.start();		
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void initPlayer() {
		try {
			speaker = AudioSystem.getSourceDataLine(format);
			speaker.open();
			speaker.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void setCalleeUI(String title) {
		ui = new PCS_UI(title);
		
		//set the action when the window is closing
		WindowAdapter adapter = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(isRegistered) {
					Enumeration<Participant> list = rtpSession.getParticipants();
					while(list.hasMoreElements()) {
						Participant p = list.nextElement(); 
						rtpSession.removeParticipant(p);
					}
					rtpSession.endSession();
				}
				System.out.println("Window is closed!");
				System.exit(0);
			}
		};

		//set the action of button
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(ui.getButtonText() == "Answer") {
					String remoteIP = ui.getRemoteIP();
					int remoteRtpPort = ui.getRemoteRtpPort();
					int remoteRtcpPort = ui.getRemoteRtcpPort();
					int localRtpPort = ui.getLocalRtpPort();
					int localRtcpPort = ui.getLocalRtcpPort();
					if(remoteIP.equals("0.0.0.0") || remoteRtpPort == 0 || remoteRtcpPort == 0 || localRtpPort == 0 || localRtcpPort == 0) {
						ui.setStateText("Wrong IP and Port!");
						return;
					}
					addNewParticipant(remoteIP, remoteRtpPort, remoteRtcpPort, localRtpPort, localRtcpPort);
					startTalking();
					ui.setButtonText("End");
					ui.setStateText("Running");
				} else {
					if(isRegistered) {						
						Enumeration<Participant> list = rtpSession.getParticipants();
						while(list.hasMoreElements()) {
							Participant p = list.nextElement(); 
							rtpSession.removeParticipant(p);
						}
						isReceived = false;
						isRegistered = false;
						rtpSession.endSession();
						rtpSession = null;
					}
					ui.setButtonText("Answer");
					ui.setStateText("Stopped");
				}
			}
		};
		
		ui.setWindowListener(adapter);
		ui.setButtonText("Answer");
		ui.setButtonActionListener(listener);
	} //end setCalleeUI()
	
	
	public void addNewParticipant(String networkAddress, int dstRtpPort, int dstRtcpPort, int srcRtpPort, int srcRtcpPort) {
		try {
			rtpSocket = new DatagramSocket(srcRtpPort);
			rtcpSocket = new DatagramSocket(srcRtcpPort);
			rtpSocket.setReuseAddress(true);
			rtcpSocket.setReuseAddress(true);
		} catch (Exception e) {
			System.out.println("RTPSession failed to obtain port");
			JOptionPane.showMessageDialog(null, "RTPSession failed to obtain port");
			System.exit(-1);
		}
		
		rtpSession = new RTPSession(rtpSocket, rtcpSocket);
		Participant p = new Participant(networkAddress, dstRtpPort, dstRtcpPort);
		rtpSession.addParticipant(p);
		rtpSession.RTPSessionRegister(this, null, null);		
		isRegistered = true;
		
		// Wait 1000 ms, because of the initial RTCP wait
		try{ Thread.sleep(1000); } catch(Exception e) {}
	}
	
	public void startTalking() {
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Callee start to talk");
				byte[] data = new byte[BUFFER_SIZE];
				int nBytesRead = 0;
				int packetCount = 0;
				while (nBytesRead != -1) {
					//TODO 4. read record data from microphone
					nBytesRead = microphone.read(data, 0, data.length);
					if(!isRegistered)
						nBytesRead = -1;
					if (nBytesRead >= 0) {	
						//TODO 5. use RTP session to send your record data
						rtpSession.sendData(data);
						packetCount++;
						
						if (packetCount == 100) {
							Enumeration<Participant> iter = rtpSession.getParticipants();
							Participant p = null;
							while (iter.hasMoreElements()) {
								p = iter.nextElement();

								String name = "TEST";
								byte[] nameBytes = name.getBytes();
								String str = "abcd";
								byte[] dataBytes = str.getBytes();

								int ret = rtpSession.sendRTCPAppPacket(p.getSSRC(), 0, nameBytes, dataBytes);
								System.out.println("!!!!!!!!!!!! ADDED APPLICATION SPECIFIC "+ ret);
								continue;
							}
							if (p == null)
								System.out.println("No participant with SSRC available :(");
						}
					}
				} //end while
			}
		});
		
		thread.start();
	}
	
	@Override
	public void receiveData(DataFrame frame, Participant participant) {
		if(speaker != null) {
			byte[] data = frame.getConcatenatedData();
			speaker.write(data, 0, data.length);
			if(!isReceived) {
				System.out.println("Received caller's data");
				isReceived = true;
			}
		}
	}

	@Override
	public void userEvent(int type, Participant[] participant) {
		//do nothing
	}

	@Override
	public int frameSize(int payloadType) {
		return 1;
	}
	
	
	public static void main(String[] args) {
		
		PCS_RTP_Callee obj = new PCS_RTP_Callee();

		obj.checkDeviceIsOK();	
		obj.setAudioFormat();
		obj.initRecorder();
		obj.initPlayer();
		obj.setCalleeUI("This is Callee!");
		
	} 

} //end class
