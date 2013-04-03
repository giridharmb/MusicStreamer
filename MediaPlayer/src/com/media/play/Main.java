package com.media.play;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JLabel;

public class Main extends JFrame {

	private JPanel contentPane;
	private static String song = null;
	private static Media audioFile = null;
	public static MediaPlayer mediaPlayer = null;
	private static int SONG_COUNT = 0;
	private static int CURRENT_INDEX = 0;
	private static String configFile = "_stream_.conf";
	private static String songDataFile = "_songs_.txt"; 	// this file will have all the songs ex:
															// http://199.199.199.31/songs/1.mp3
															// http://199.199.199.31/songs/2.mp3
	
	// private static String accessUrl = "http://localhost/~giri/" + songDataFile;
  	   private static String accessUrl = getHttpUrl();
	// change "localhost" above to point to a URL
	
	private static PopupMenu menu = new PopupMenu();
	public static JProgressBar progressBar = new JProgressBar();
	public static JSlider slider = new JSlider();
	
	private static ImageIcon[] arrayOfImageIcons = new ImageIcon[20];
	
		private static int ICON_NORMAL_PLAY = 0;
		private static int ICON_HOVER_PLAY = 1;
		
		private static int ICON_NORMAL_NEXT = 2;
		private static int ICON_HOVER_NEXT = 3;
		
		private static int ICON_NORMAL_PREVIOUS = 4;
		private static int ICON_HOVER_PREVIOUS = 5;
		
		private static int ICON_NORMAL_STOP = 6;
		private static int ICON_HOVER_STOP = 7;
		
		private static int ICON_NORMAL_RANDOM = 8;
		private static int ICON_HOVER_RANDOM = 9; 
				
		private static int ICON_NORMAL_EXIT = 10;
		private static int ICON_HOVER_EXIT = 11;
	
	
	private static LinkedList<String> getFileLines(String filePath) {
		File f = new File(filePath);
		BufferedReader br = null;
		if(!f.exists()) {
			System.out.println("@ File " + filePath + " does not exist !");
			return null;
		}
		LinkedList<String> ll = new LinkedList<String>();
		int count = 0;
		try {
			FileInputStream fstream = new FileInputStream(filePath);
			DataInputStream in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while((strLine = br.readLine()) != null ){
				ll.add(strLine);
				count++;
			}
		} catch(Exception e) {
			System.out.println("@ getFileLines(): Error in reading data from file : " + filePath);
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("@ getFileLines(): Read " + Integer.toString(count) + " lines from the file " + filePath);
		}
		return ll;
	}
	
	private static String getPropertyFromConfigFile(String property) {
		LinkedList<String> ll = new LinkedList<String>();
		String[] keyValueArray;
		String valueToReturn = null;
		File f = new File(getUserHomeDirectory()+"/"+configFile);
		if(!f.exists()) {
			System.out.println("@ configFile " + configFile + " does not exist in user's home directory");
			return null;
		}
		ll = getFileLines(getUserHomeDirectory()+"/"+configFile);
		for(String line : ll) {
			if(line.contains(property)){
				System.out.println("@ Property " + property + " found in the config file " + configFile);
				String currentLine = line;
				keyValueArray = currentLine.split("@");
				System.out.println("@ keyValueArray >>");
				for(String e : keyValueArray) {
					System.out.println("element: " + e);
				}
				valueToReturn = keyValueArray[1];
				break;
			}
		}
		System.out.println("@ Returning the value " + valueToReturn + " for the property " + property);
		return valueToReturn;
	}
	
	private static String getHttpUrl() {
		//return getPropertyFromConfigFile("PLAYLIST_FILE");
		return new String("http://199.199.199.31/_songs_.txt");
	}
	
	private static String getCompletePathToLocalFile() {
		return getUserHomeDirectory() + "/" + songDataFile;
	}
	private static String getUserHomeDirectory() {
		System.out.println("@ getUserHomeDirectory(): Returning - "+System.getProperty("user.home"));
		return System.getProperty("user.home");
	}
	
	private boolean songDataFileExists() {
		File f = new File(getUserHomeDirectory()+"/"+songDataFile);
		if(f.exists()) {
			System.out.println("@ songDataFileExists(): File : " + getUserHomeDirectory()+"/"+songDataFile + " exists !");
			return true;
		} else {
			System.out.println("@ songDataFileExists(): File : " + getUserHomeDirectory()+"/"+songDataFile + " does not exist !");
			return false;
		}
	}
	
	private static void downloadSongFile() {
		try {
			saveUrl(getCompletePathToLocalFile(), accessUrl);
			System.out.println("@ Downloaded the file to : "+getCompletePathToLocalFile() + " from the URL : " + accessUrl );
		} catch(Exception e) {
			System.out.println("@ Error in download the URL " + accessUrl);
			System.out.println("------StackTrace------");
			e.printStackTrace();
			System.out.println("----------------------");
		}
	}
	
	/*
	JButton btnNewButton_1 = new JButton("Stop");
	btnNewButton_1.setIcon(new ImageIcon("resources/images/icon_stop.png"));
	btnNewButton_1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_stopPlaying();
		}
	});
	*/
	
	private void initializeIcons() {
		arrayOfImageIcons[ICON_NORMAL_PLAY] = new ImageIcon(getClass().getResource("/resources/images/icon_play.png"));
		arrayOfImageIcons[ICON_HOVER_PLAY] = new ImageIcon(getClass().getResource("/resources/hover_images/icon_play.png"));
		
		arrayOfImageIcons[ICON_NORMAL_NEXT] = new ImageIcon(getClass().getResource("/resources/images/icon_next.png"));
		arrayOfImageIcons[ICON_HOVER_NEXT] = new ImageIcon(getClass().getResource("/resources/hover_images/icon_next.png"));
		
		arrayOfImageIcons[ICON_NORMAL_PREVIOUS] = new ImageIcon(getClass().getResource("/resources/images/icon_previous.png"));
		arrayOfImageIcons[ICON_HOVER_PREVIOUS] = new ImageIcon(getClass().getResource("/resources/hover_images/icon_previous.png"));
		
		arrayOfImageIcons[ICON_NORMAL_RANDOM] = new ImageIcon(getClass().getResource("/resources/images/icon_random.png"));
		arrayOfImageIcons[ICON_HOVER_RANDOM] = new ImageIcon(getClass().getResource("/resources/hover_images/icon_random.png"));
		
		arrayOfImageIcons[ICON_NORMAL_STOP] = new ImageIcon(getClass().getResource("/resources/images/icon_stop.png"));
		arrayOfImageIcons[ICON_HOVER_STOP] = new ImageIcon(getClass().getResource("/resources/hover_images/icon_stop.png"));
		
		arrayOfImageIcons[ICON_NORMAL_EXIT] = new ImageIcon(getClass().getResource("/resources/images/icon_exit.png"));
		arrayOfImageIcons[ICON_HOVER_EXIT] = new ImageIcon(getClass().getResource("/resources/hover_images/icon_exit.png"));
	}
		
	private static void setImageForButtonNew(JButton btn, int iconType) {
		//ImageIcon icon = getImageIcon(imageFilePath);
		btn.setIcon(arrayOfImageIcons[iconType]);
	}
	
	private LinkedList<String> getSongsFromFile() {
		LinkedList<String> ll = new LinkedList<String>();
		ll = getFileLines(getCompletePathToLocalFile());
		return ll;
	}

// if rangeMax = 100, this will return a random number between 0 and 99
	private int getMeRandomNumber(int rangeMax) {
		System.out.println("@ getMeRandomNumber(): " + "rangeMax="+Integer.toString(rangeMax));
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(rangeMax);
		System.out.println("@ getMeRandomNumber(): " + "returning randomInt="+Integer.toString(randomInt));
		return randomInt;
	}
	
	private String getRandomSongURL() {
		LinkedList<String> ll = new LinkedList<String>();
		ll = getSongsFromFile();
		SONG_COUNT = ll.size();
		int index = getMeRandomNumber(SONG_COUNT);
		CURRENT_INDEX = index;
		System.out.println("@ getRandomSongURL(): CURRENT_INDEX = " + Integer.toString(CURRENT_INDEX));
		System.out.println("@ getRandomSongURL(): Returning song >> [" + ll.get(CURRENT_INDEX)+"]");
		return ll.get(CURRENT_INDEX);
	}
	
	private synchronized void _playNext() {
		LinkedList<String> ll = new LinkedList<String>();
		ll = getSongsFromFile();
		SONG_COUNT = ll.size();
		CURRENT_INDEX=CURRENT_INDEX+1;
		if(CURRENT_INDEX >= SONG_COUNT) {
			CURRENT_INDEX = 0;
		}
		System.out.println("@ _playPrevious(): Playing Song with Index : " + Integer.toString(CURRENT_INDEX));
		_playSongIndex(CURRENT_INDEX);
	}
	
	private synchronized void _playPrevious() {
		LinkedList<String> ll = new LinkedList<String>();
		ll = getSongsFromFile();
		SONG_COUNT = ll.size();
		CURRENT_INDEX=CURRENT_INDEX-1;
		if(CURRENT_INDEX < 0 ) {
			CURRENT_INDEX = SONG_COUNT - 1; 
		}
		System.out.println("@ _playPrevious(): Playing Song with Index : " + Integer.toString(CURRENT_INDEX));
		_playSongIndex(CURRENT_INDEX);
	}
	
	private synchronized void _playRandomSong() {
		if(mediaPlayer != null) {
			mediaPlayer.stop();
		}
		String randomUrl = getRandomSongURL();
		_playSong(randomUrl);
	}
	private synchronized void _playSongIndex(int index) {
		LinkedList<String> ll = new LinkedList<String>();
		ll = getSongsFromFile();
		String url = ll.get(index);
		_playSong(url);
	}
	
	private synchronized void _playSong(String url) {
		if(mediaPlayer != null) {
			mediaPlayer.stop();
		}
		audioFile = new Media(url);
		mediaPlayer = new MediaPlayer(audioFile);
		mediaPlayer.play();	
	}
	
	private synchronized void _stopPlaying() {
		if(mediaPlayer != null) {
			mediaPlayer.stop();
		}
	}
				
	public static void saveUrl(String filename, String urlString) throws MalformedURLException, IOException
    {
    	BufferedInputStream in = null;
    	FileOutputStream fout = null;
    	try
    	{
    		in = new BufferedInputStream(new URL(urlString).openStream());
    		fout = new FileOutputStream(filename);

    		byte data[] = new byte[1024];
    		int count;
    		while ((count = in.read(data, 0, 1024)) != -1)
    		{
    			fout.write(data, 0, count);
    		}
    	}
    	finally
    	{
    		if (in != null)
    			in.close();
    		if (fout != null)
    			fout.close();
    	}
    }

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			System.setProperty("awt.useSystemAAFontSettings","on");
			System.setProperty("swing.aatext", "true");
		} catch(Exception e) {
			System.out.println("Error - could not enable antialiasing !");
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new JFXPanel();
					Main frame = new Main();
					frame.setVisible(true);
					downloadSongFile();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	/**
	 * Create the frame.
	 */
	public Main() {
		try {
	            // Set cross-platform Java L&F (also called "Metal")
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch(Exception e) {
	    	System.out.println("Could not change the look and feel !");
	    }
		
		
		setResizable(false);
		setTitle("Music Streamer - \u00A9 Giridhar Bhujanga (giridharmb@gmail.com)");
			
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 572, 502);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		final JButton btnNewButton = new JButton("");
		btnNewButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/icon_play.png")));
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setImageForButtonNew(btnNewButton,ICON_HOVER_PLAY);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setImageForButtonNew(btnNewButton,ICON_NORMAL_PLAY);
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_playSongIndex(CURRENT_INDEX);
			}
		});
		btnNewButton.setBounds(35, 54, 232, 72);
		contentPane.add(btnNewButton);
		
		final JButton btnNewButton_1 = new JButton("");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setImageForButtonNew(btnNewButton_1,ICON_HOVER_STOP);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setImageForButtonNew(btnNewButton_1,ICON_NORMAL_STOP);
			}
		});
		btnNewButton_1.setIcon(new ImageIcon(getClass().getResource("/resources/images/icon_stop.png")));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_stopPlaying();
			}
		});
		btnNewButton_1.setBounds(35, 285, 232, 72);
		contentPane.add(btnNewButton_1);
		
		final JButton btnPlayNext = new JButton("");
		btnPlayNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setImageForButtonNew(btnPlayNext,ICON_HOVER_NEXT);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setImageForButtonNew(btnPlayNext,ICON_NORMAL_NEXT);
			}
		});
		btnPlayNext.setIcon(new ImageIcon(getClass().getResource("/resources/images/icon_next.png")));
		btnPlayNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_playNext();
			}
		});
		btnPlayNext.setBounds(299, 54, 232, 72);
		contentPane.add(btnPlayNext);
		
		final JButton btnPlayPrevious = new JButton("");
		btnPlayPrevious.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setImageForButtonNew(btnPlayPrevious,ICON_HOVER_PREVIOUS);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setImageForButtonNew(btnPlayPrevious,ICON_NORMAL_PREVIOUS);
			}
		});
		btnPlayPrevious.setIcon(new ImageIcon(getClass().getResource("/resources/images/icon_previous.png")));
		btnPlayPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_playPrevious();
			}
		});
		btnPlayPrevious.setBounds(299, 169, 232, 72);
		contentPane.add(btnPlayPrevious);
		
		final JButton btnPlayRandom = new JButton("");
		btnPlayRandom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setImageForButtonNew(btnPlayRandom,ICON_HOVER_RANDOM);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setImageForButtonNew(btnPlayRandom,ICON_NORMAL_RANDOM);
			}
		});
		btnPlayRandom.setIcon(new ImageIcon(getClass().getResource("/resources/images/icon_random.png")));
		btnPlayRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_playRandomSong();
			}
		});
		btnPlayRandom.setBounds(35, 169, 232, 72);
		contentPane.add(btnPlayRandom);
		
		final JButton btnExit = new JButton("");
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				setImageForButtonNew(btnExit,ICON_HOVER_EXIT);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setImageForButtonNew(btnExit,ICON_NORMAL_EXIT);
			}
		});
		btnExit.setIcon(new ImageIcon(getClass().getResource("/resources/images/icon_exit.png")));
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("@ Exiting...");
				_stopPlaying();
				System.exit(0);
			}
		});
		btnExit.setBounds(299, 285, 232, 72);
		contentPane.add(btnExit);
		

		progressBar.setBounds(35, 430, 232, 16);
		contentPane.add(progressBar);
		
		
		if(!SystemTray.isSupported()) {
			System.out.println("@ System Tray is not supported !");
		} else {

			System.out.println("@ System Tray is supported !");
			SystemTray tray = SystemTray.getSystemTray();
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image image = toolkit.getImage(getClass().getResource("/resources/images/play-button-01.png"));
		

			MenuItem messageItem_play          = new MenuItem("Play");
			MenuItem messageItem_stop          = new MenuItem("Stop");
			MenuItem messageItem_playnext      = new MenuItem("Next");
			MenuItem messageItem_playprevious  = new MenuItem("Previous");
			MenuItem messageItem_playrandom    = new MenuItem("Random");
			MenuItem messageItem_exit          = new MenuItem("Exit");
			
			menu.add(messageItem_play);
			menu.add(messageItem_playrandom);
			menu.add(messageItem_playnext);
			menu.add(messageItem_playprevious);
			menu.add(messageItem_stop);
			menu.add(messageItem_exit);
			
			
			messageItem_play.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("@ Play Pressed...");
					_playSongIndex(CURRENT_INDEX);
				}
			});
			
			messageItem_stop.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("@ Stop Playing Pressed !");
					_stopPlaying();
				}
			});
			
			messageItem_playnext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("@ Playing Next...");
					_playNext();
				}
			});
			
			messageItem_playprevious.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("@ Playing Previous...");
					_playPrevious();
				}
			});
			
			messageItem_playrandom.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("@ Random Pressed !");
					_playRandomSong();
				}
			});
			
			messageItem_exit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					_stopPlaying();
					System.out.println("@ Exiting...");
					System.exit(0);
				}
			});
			
					
			TrayIcon icon = new TrayIcon(image,"System Tray", menu);
			icon.setImageAutoSize(true);
			try {
				tray.add(icon);
			} catch (AWTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		initializeIcons();
		
		Thread t = new Thread(new ProgressBarUpdater());
		
		btnNewButton.setFocusPainted(false);
		btnNewButton_1.setFocusPainted(false);
		btnPlayNext.setFocusPainted(false);
		btnPlayPrevious.setFocusPainted(false);
		btnPlayRandom.setFocusPainted(false);
		btnExit.setFocusPainted(false);
		slider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(mediaPlayer != null) {
					int currentTime = (int) mediaPlayer.getCurrentTime().toSeconds();
					int totalTime = (int) mediaPlayer.getTotalDuration().toSeconds();
					
					slider.setMaximum(totalTime);
					
					Duration d = new Duration((double) slider.getValue() * 1000.0);
					System.out.println("@ setting duration to " + Double.toString(d.toSeconds()) + " secs");
					mediaPlayer.seek(d);
				}
			}
		});
		
				
		slider.setBounds(279, 418, 252, 39);
		contentPane.add(slider);
		
		JLabel lblProgress = new JLabel("Progress");
		lblProgress.setBounds(35, 390, 232, 16);
		contentPane.add(lblProgress);
		
		JLabel lblTrackControl = new JLabel("Jump Track");
		lblTrackControl.setBounds(287, 390, 232, 16);
		contentPane.add(lblTrackControl);
		
		JLabel lblLabel = new JLabel("Play");
		lblLabel.setBounds(35, 27, 232, 15);
		contentPane.add(lblLabel);
		
		JLabel lblNext = new JLabel("Next");
		lblNext.setBounds(299, 27, 232, 15);
		contentPane.add(lblNext);
		
		JLabel lblRandom = new JLabel("Random");
		lblRandom.setBounds(35, 142, 232, 15);
		contentPane.add(lblRandom);
		
		JLabel lblPrevious = new JLabel("Previous");
		lblPrevious.setBounds(299, 142, 232, 15);
		contentPane.add(lblPrevious);
		
		JLabel lblStop = new JLabel("Stop");
		lblStop.setBounds(35, 258, 232, 15);
		contentPane.add(lblStop);
		
		JLabel lblExit = new JLabel("Exit");
		lblExit.setBounds(299, 258, 232, 15);
		contentPane.add(lblExit);
	}
}
