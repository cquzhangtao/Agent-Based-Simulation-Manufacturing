package manu.scheduling.training.gui;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public abstract class ProgressBarMe extends Thread{
	Timer timer=new Timer();
	MyTimerTask t;
	public ProgressBarMe(JFrame frame){
		t=new MyTimerTask(frame);
	}
	public abstract void task();
	public void run(){		
		timer.schedule(t, 0,500);
		task();
		timer.cancel();
		t.hide();
	}
	class MyTimerTask extends TimerTask{
		ProgressFrameNoNum frame;
		int progress=0;
		public MyTimerTask(JFrame frame){
			this.frame=new ProgressFrameNoNum(frame);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			progress+=5;
			frame.setProgess(progress);
			if(progress==100)
				progress=0;
		}
		public void hide(){
			frame.setVisible(false);
		}
		
	}
}
