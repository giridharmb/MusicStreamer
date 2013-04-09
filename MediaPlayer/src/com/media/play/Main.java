package com.media.play;

import de.javasoft.plaf.synthetica.SyntheticaBlueSteelLookAndFeel;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.List;
import java.awt.Font;

public class Main extends JFrame {

	private JPanel contentPane;
	private static String song = null;
	private static Media audioFile = null;
	public static MediaPlayer mediaPlayer = null;
	private static JSlider slider_1 = new JSlider();
	private static int SONG_COUNT = 0;
	
	private SystemTray tray = SystemTray.getSystemTray();
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	 
	public static int S_NORMAL = 0;
	public static int S_PLAY = 1;
	public static int S_NEXT = 2;
	public static int S_PREVIOUS = 3;
	public static int S_RANDOM = 4;
	public static int S_STOP = 5;
	
	
	public static int PLAYER_STATE = S_NORMAL;

	public static boolean keepPlaying = false;
	public static boolean endReached = false;
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
		private static JTextField textField;
		public static JTextField textField_1;
		private static List list = new List();
	
	
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
		//return new String("http://localhost/~giri/_songs_.txt");
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
	
	private static String getFileNameFromUrl(String url) {
		String urlCopy = new String(url);
		String retStr;
		int slashIndex = url.lastIndexOf("/");
		
		String fullFileWithExtension = url.substring(slashIndex+1, url.length());
		
		int dotIndex = fullFileWithExtension.lastIndexOf(".");
		
		retStr  = fullFileWithExtension.substring(0, dotIndex);
		
		// replace multiple _ characters with a single space
		// replace multiple dot with a single space
		retStr = retStr.replaceAll("_+"," ");
		retStr = retStr.replaceAll("\\.+"," ");
		return retStr;
	}
	
	private static void populateListWithStrings(List myList, LinkedList<String> ll) {
		int index = 0;
		for(String s : ll) {
			myList.add(getFileNameFromUrl(s), index);
			index++;
		}
	}
	
	private static void populateMainList() {
		LinkedList<String> ll = new LinkedList<String>();
		ll = getSongsFromFile();
		populateListWithStrings(list,ll);
	}
	
	private static LinkedList<String> getSongsFromFile() {
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
		list.select(CURRENT_INDEX);
		list.makeVisible(CURRENT_INDEX);
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
		list.select(CURRENT_INDEX);
		_playSongIndex(CURRENT_INDEX);
		list.makeVisible(CURRENT_INDEX);
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
		list.select(CURRENT_INDEX);
		_playSongIndex(CURRENT_INDEX);
		list.makeVisible(CURRENT_INDEX);
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
					
					/* make sure use run this command on Mac OSX to avoid the exception:
					 * 	$ sudo touch /private/var/db/.AccessibilityAPIEnabled
					 */
					//GlobalKeyListener gkl = new GlobalKeyListener();
					//gkl.setup();
					
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
		setBounds(100, 100, 1260, 525);
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
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(list.getItemCount() > 0 && list.getSelectedIndex() != -1) {
					System.out.println("list box item selected ! item index >> " + Integer.toString(list.getSelectedIndex()));
					CURRENT_INDEX = list.getSelectedIndex();
					_playSongIndex(CURRENT_INDEX);
					list.makeVisible(CURRENT_INDEX);
				
				} else {
					System.out.println("list box item not selected ! playing media with index >> " + Integer.toString(CURRENT_INDEX));
					_playSongIndex(CURRENT_INDEX);
				}
			}
		});
		btnNewButton.setBounds(35, 54, 132, 102);
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
		btnNewButton_1.setBounds(207, 220, 132, 102);
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
		btnPlayNext.setBounds(207, 54, 132, 102);
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
		btnPlayPrevious.setBounds(375, 54, 132, 102);
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
		btnPlayRandom.setBounds(35, 220, 132, 102);
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
		btnExit.setBounds(375, 220, 132, 102);
		contentPane.add(btnExit);
		

		progressBar.setBounds(35, 400, 232, 16);
		contentPane.add(progressBar);
		
		
		if(!SystemTray.isSupported()) {
			System.out.println("@ System Tray is not supported !");
		} else {

			System.out.println("@ System Tray is supported !");
			
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
			
			/*
			arrayOfImageIcons[ICON_NORMAL_PLAY] = new ImageIcon(getClass().getResource("/resources/images/icon_play.png"));
			arrayOfImageIcons[ICON_NORMAL_NEXT] = new ImageIcon(getClass().getResource("/resources/images/icon_next.png"));
			arrayOfImageIcons[ICON_NORMAL_PREVIOUS] = new ImageIcon(getClass().getResource("/resources/images/icon_previous.png"));
			arrayOfImageIcons[ICON_NORMAL_RANDOM] = new ImageIcon(getClass().getResource("/resources/images/icon_random.png"));
			arrayOfImageIcons[ICON_NORMAL_STOP] = new ImageIcon(getClass().getResource("/resources/images/icon_stop.png"));
			arrayOfImageIcons[ICON_NORMAL_EXIT] = new ImageIcon(getClass().getResource("/resources/images/icon_exit.png"));
			Image icong_image_play = toolkit.getImage(getClass().getResource("/resources/images/play-button-01.png"));
			*/
			Image img_play = toolkit.getImage(getClass().getResource("/nuvola/n_play.png"));
			Image img_next = toolkit.getImage(getClass().getResource("/nuvola/n_next.png"));
			Image img_previous = toolkit.getImage(getClass().getResource("/nuvola/n_previous.png"));
			Image img_random = toolkit.getImage(getClass().getResource("/nuvola/n_random.png"));
			Image img_stop = toolkit.getImage(getClass().getResource("/nuvola/n_stop.png"));
			
			TrayIcon icon_img_play = new TrayIcon(img_play,"Play");
			TrayIcon icon_img_next = new TrayIcon(img_next,"Next");
			TrayIcon icon_img_previous = new TrayIcon(img_previous,"Previous");
			TrayIcon icon_img_random = new TrayIcon(img_random,"Random");
			TrayIcon icon_img_stop = new TrayIcon(img_stop,"Stop");
			
			
			icon_img_play.setImageAutoSize(true);
			icon_img_next.setImageAutoSize(true);
			icon_img_previous.setImageAutoSize(true);
			icon_img_random.setImageAutoSize(true);
			icon_img_stop.setImageAutoSize(true);
			
			icon_img_play.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("@ Play Pressed...");
					_playSongIndex(CURRENT_INDEX);
				}

				@Override public void mousePressed(MouseEvent e) {  // TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			
			icon_img_next.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("@ Playing Next...");
					_playNext();
				}

				@Override public void mousePressed(MouseEvent e) {  // TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			
			icon_img_previous.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("@ Playing Previous...");
					_playPrevious();
				}

				@Override public void mousePressed(MouseEvent e) {  // TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			
			icon_img_random.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("@ Random Pressed !");
					_playRandomSong();
				}

				@Override public void mousePressed(MouseEvent e) {  // TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			
			icon_img_stop.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					System.out.println("@ Stop Playing Pressed !");
					_stopPlaying();
				}

				@Override public void mousePressed(MouseEvent e) {  // TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			});

					
				
					
					
					
			
				TrayIcon icon = new TrayIcon(image,"System Tray", menu);
			
			icon.setImageAutoSize(true);
			try {
				//tray.add(icon);
				tray.add(icon_img_play);
				tray.add(icon_img_next);
				tray.add(icon_img_random);
				tray.add(icon_img_previous);
				tray.add(icon_img_stop);

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
		
				
		slider.setBounds(297, 388, 252, 39);
		contentPane.add(slider);
		
		JLabel lblProgress = new JLabel("Progress");
		lblProgress.setBounds(46, 360, 121, 16);
		contentPane.add(lblProgress);
		
		JLabel lblTrackControl = new JLabel("Jump Track");
		lblTrackControl.setBounds(317, 360, 116, 16);
		contentPane.add(lblTrackControl);
		
		JLabel lblLabel = new JLabel("Play");
		lblLabel.setBounds(35, 27, 132, 15);
		contentPane.add(lblLabel);
		
		JLabel lblNext = new JLabel("Next");
		lblNext.setBounds(207, 27, 132, 15);
		contentPane.add(lblNext);
		
		JLabel lblRandom = new JLabel("Random");
		lblRandom.setBounds(35, 193, 132, 15);
		contentPane.add(lblRandom);
		
		JLabel lblPrevious = new JLabel("Previous");
		lblPrevious.setBounds(375, 27, 132, 15);
		contentPane.add(lblPrevious);
		
		JLabel lblStop = new JLabel("Stop");
		lblStop.setBounds(207, 193, 132, 15);
		contentPane.add(lblStop);
		
		JLabel lblExit = new JLabel("Exit");
		lblExit.setBounds(375, 193, 132, 15);
		contentPane.add(lblExit);
		
		
		slider_1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(mediaPlayer != null) {
					int actualIntValue = slider_1.getValue();
					textField.setText(Integer.toString(actualIntValue));
					double volumeValue = (double) ((double) slider_1.getValue() / (double) 100.0 );
					if(volumeValue >= 1.0) {
						volumeValue = 1.0;
					}
					if(volumeValue <= 0.0) {
						volumeValue = 0.0;
					}
					mediaPlayer.setVolume(volumeValue);
				}
			}
			
		});
		slider_1.setOrientation(SwingConstants.VERTICAL);
		slider_1.setBounds(543, 54, 42, 268);
		contentPane.add(slider_1);
		
		JLabel lblVolume = new JLabel("Volume");
		lblVolume.setHorizontalAlignment(SwingConstants.CENTER);
		lblVolume.setBounds(533, 26, 61, 16);
		contentPane.add(lblVolume);
		
		textField = new JTextField();
		textField.setBackground(new Color(240, 248, 255));
		textField.setEditable(false);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setBounds(533, 348, 61, 28);
		contentPane.add(textField);
		textField.setColumns(10);
		
		final JCheckBox chckbxMute = new JCheckBox("Mute");
		chckbxMute.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(mediaPlayer != null) {
					if (chckbxMute.isSelected()) {
						mediaPlayer.setMute(true);
					}
					if(!chckbxMute.isSelected()) {
						mediaPlayer.setMute(false);
					}
				}
			}
		});
		chckbxMute.setBounds(307, 442, 69, 23);
		contentPane.add(chckbxMute);
		
		final JCheckBox chckbxKeepPlaying = new JCheckBox("Keep Playing");
		chckbxKeepPlaying.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(chckbxKeepPlaying.isSelected()) {
					keepPlaying = true;
				} else {
					keepPlaying = false;
				}
			}
		});
		chckbxKeepPlaying.setBounds(375, 442, 147, 23);
		contentPane.add(chckbxKeepPlaying);
		
		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setHorizontalAlignment(SwingConstants.CENTER);
		textField_1.setBounds(155, 440, 69, 28);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblComplete = new JLabel("% Complete");
		lblComplete.setHorizontalAlignment(SwingConstants.CENTER);
		lblComplete.setBounds(46, 446, 97, 16);
		contentPane.add(lblComplete);
		list.setFont(new Font("Arial", Font.PLAIN, 11));
		

		list.setMultipleSelections(false);
		list.setBounds(637, 54, 567, 414);
		contentPane.add(list);
		
		JLabel lblPlaylist = new JLabel("Playlist");
		lblPlaylist.setBounds(637, 26, 61, 16);
		contentPane.add(lblPlaylist);
		
		Thread keepPlayingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					if(keepPlaying) {
						if(endReached) {
							_playRandomSong();
							endReached = false;
						}
					}
					try {
						Thread.currentThread().sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		});
		
		keepPlayingThread.start();
		
		Thread playCommandThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					// System.out.println("...");
					try {
						if(PLAYER_STATE == S_PLAY) {
							System.out.println("# Command received to play...");
							_playSongIndex(CURRENT_INDEX);
						} else if(PLAYER_STATE == S_NEXT) {
							System.out.println("# Command received to play next...");
							_playNext();
						} else if(PLAYER_STATE == S_PREVIOUS) {
							System.out.println("# Command received to play previous...");
							_playPrevious();
						} else if(PLAYER_STATE == S_RANDOM) {
							System.out.println("# Command received to play random...");
							_playRandomSong();
						} else if(PLAYER_STATE == S_STOP) {
							System.out.println("# Command received to stop play...");
							_stopPlaying();
						}
						
						PLAYER_STATE = S_NORMAL;
						
						Thread.currentThread().sleep(300);
					} catch(Exception e) {
						System.out.println("Error in sending commands to player object.");
						e.printStackTrace();
					}
				}
			}
		});
		
		playCommandThread.start();
		
		populateMainList();
	}
}
