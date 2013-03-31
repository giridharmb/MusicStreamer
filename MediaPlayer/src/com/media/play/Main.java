package com.media.play;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main extends JFrame {

	private JPanel contentPane;
	private static String song = null;
	private static Media audioFile = null;
	private static MediaPlayer mediaPlayer = null;
	private static int SONG_COUNT = 0;
	private static int CURRENT_INDEX = 0;
	
	private static String songDataFile = "__songs__.txt"; 	// this file will have all the songs ex:
													// http://199.199.199.31/songs/1.mp3
													// http://199.199.199.31/songs/2.mp3
	
	private static String accessUrl = "http://localhost/~giri/" + songDataFile;
	// change "localhost" above to point to a URL
	
	
	private LinkedList<String> getFileLines(String filePath) {
		File f = new File(filePath);
		if(!f.exists()) {
			System.out.println("@ File " + filePath + " does not exist !");
			return null;
		}
		LinkedList<String> ll = new LinkedList<String>();
		int count = 0;
		try {
			FileInputStream fstream = new FileInputStream(filePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while((strLine = br.readLine()) != null ){
				ll.add(strLine);
				count++;
			}
		} catch(Exception e) {
			System.out.println("@ getFileLines(): Error in reading data from file : " + filePath);
			e.printStackTrace();
		} finally {
			System.out.println("@ getFileLines(): Read " + Integer.toString(count) + " lines from the file " + filePath);
		}
		return ll;
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
		setResizable(false);
		setTitle("Music Streamer - \u00A9 Giridhar Bhujanga (giridharmb@gmail.com)");
			
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 543, 208);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		final JButton btnNewButton = new JButton("Play");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_playSongIndex(CURRENT_INDEX);
			}
		});
		btnNewButton.setBounds(42, 42, 206, 29);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Stop");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_stopPlaying();
			}
		});
		btnNewButton_1.setBounds(42, 124, 206, 29);
		contentPane.add(btnNewButton_1);
		
		JButton btnPlayNext = new JButton("Play Next");
		btnPlayNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_playNext();
			}
		});
		btnPlayNext.setBounds(295, 42, 206, 29);
		contentPane.add(btnPlayNext);
		
		JButton btnPlayPrevious = new JButton("Play Previous");
		btnPlayPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_playPrevious();
			}
		});
		btnPlayPrevious.setBounds(295, 83, 206, 29);
		contentPane.add(btnPlayPrevious);
		
		JButton btnPlayRandom = new JButton("Play Random");
		btnPlayRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_playRandomSong();
			}
		});
		btnPlayRandom.setBounds(42, 83, 206, 29);
		contentPane.add(btnPlayRandom);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("@ Exiting...");
				_stopPlaying();
				System.exit(0);
			}
		});
		btnExit.setBounds(295, 124, 206, 29);
		contentPane.add(btnExit);
		
		if(!SystemTray.isSupported()) {
			System.out.println("@ System Tray is not supported !");
		} else {
			System.out.println("@ System Tray is supported !");
			SystemTray tray = SystemTray.getSystemTray();
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image image = toolkit.getImage("resources/images/play-button-01.png");
			
			PopupMenu menu = new PopupMenu();
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
	}
}
