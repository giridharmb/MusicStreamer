package com.media.play;

public class ProgressBarUpdater extends Thread {
	private int currentTime = 0;
	private int totalTime = 100;
	
	public ProgressBarUpdater() {
		currentTime = 0;
		totalTime = 0;
		this.start();
	}

	@Override
	public void run() {
		while(true) {
			System.out.println("@ ...ProgressBarUpdater...");
			
			if(Main.mediaPlayer != null) {
				currentTime = (int) Main.mediaPlayer.getCurrentTime().toSeconds();
				totalTime = (int) Main.mediaPlayer.getTotalDuration().toSeconds();
				
				System.out.println("@ currentTime >> " + Integer.toString(currentTime));
				System.out.println("@ totalTime >> " + Integer.toString(totalTime));
				
				Main.progressBar.setMaximum(totalTime);
				Main.progressBar.setValue(currentTime);
				
				Main.slider.setMinimum(0);
				Main.slider.setMaximum(totalTime);
				
			}
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
