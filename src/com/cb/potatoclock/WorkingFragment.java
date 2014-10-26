package com.cb.potatoclock;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cb.view.CircleProgressBarView;

public class WorkingFragment extends Fragment {
	private TextView timer_minute,timer_second,status_textview,show_task;
	private ImageButton stop_timer;
	private Button timer_done,record_task;
	private LinearLayout timer_linearlayout,status_image_linearlayout;
	private int TOTAL_TIME = 1;
	private long COUNTER_TIME = 0;
	private CountDownTimer timer;
	private FragmentCallBack fragmentCallBack;
	private CircleProgressBarView circleProgressBar;
	private long millisLeft = 0;
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d("WorkingFragment","OnActivityCreated()");
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onStart() {
		Log.d("WorkingFragment","OnStart()");
		super.onStart();
	}
	@Override
	public void onResume() {
		Log.d("WorkingFragment","OnResume()");
		super.onResume();
	}
	@Override
	public void onPause() {
		Log.d("WorkingFragment","OnPause");
		super.onPause();
	}
	@Override
	public void onLowMemory() {
		Log.d("WorkingFragment","OnLowMemory()");
		super.onLowMemory();
	}
	@Override
	public void onDestroyView() {
		Log.d("WorkingFragment","OnDestoryView()");
		super.onDestroyView();
	}
	@Override
	public void onDestroy() {
		Log.d("WorkingFragment","OnDestory()");
		super.onDestroy();
	}
	@Override
	public void onDetach() {
		Log.d("WorkingFragment","OnDetach()");
		super.onDetach();
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		//将该WorkingFragment绑定的activity实例赋值给fragmentCallBack,并判断acitivity是否实现了回调用的接口
		try{
			fragmentCallBack = (MainActivity)activity;
		}catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + "must implements FragmentCallback Interface");
		}
		
		Log.d("WorkingFragment","OnAttach()");
	}
	//
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getSharedPreferences("pomodoro", 0)中的0表示：MODE_PRIVATE
		sp = getActivity().getSharedPreferences("pomodoro_time", 0);
		editor = sp.edit();
		
		
		
		Log.d("WorkingFragment","OnCreate()");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_work,container,false);
		
		//绑定倒计时页面（WorkingFragment）显示任务名的TextView
		show_task = (TextView)view.findViewById(R.id.show_task);
		//绑定显示倒计时分钟和秒的TextView，以及LinearLayout
		timer_minute = (TextView)view.findViewById(R.id.timer_minute);
		timer_second = (TextView)view.findViewById(R.id.timer_second);
		timer_linearlayout = (LinearLayout)view.findViewById(R.id.timer_linearlayout);
		//绑定放置多少个番茄钟图片的LinearLayout
		status_image_linearlayout = (LinearLayout)view.findViewById(R.id.status_image_linearlayout);
		//绑定取消番茄时钟的Button stop_timer和计时完成的Button timer_done
		stop_timer = (ImageButton)view.findViewById(R.id.stop_timer);
		timer_done = (Button)view.findViewById(R.id.timer_done);
		//绑定显示工作状态的TextView
		status_textview = (TextView)view.findViewById(R.id.status_textview);
		//绑定提交任务记录任务详细信息按钮
		record_task = (Button)view.findViewById(R.id.record_task);
		fragmentCallBack.recordTaskButtonListener(record_task);
		//回调MainActivity的initWorkingFragment()函数
		//返回倒计时的时间传给Total_Time，并初始化成对应的模式
		TOTAL_TIME = fragmentCallBack.initWorkingFragment(view, status_textview, timer_done, record_task, stop_timer, status_image_linearlayout, show_task);
		//
		COUNTER_TIME = sp.getLong("counter_time", 5400000);
		//判断killed_tag_2标记是否为1，为1则表示程序被杀死过，给COUNTER_TIME赋予counter_time_left。killed_tag_2重新赋值为0
		int tag = sp.getInt("killed_tag_2", 0);
		if( tag == 1){
			COUNTER_TIME = sp.getLong("counter_time_left", 5400000);
			editor.putInt("killed_tag_2", 0).commit();
		}
		//监听取消番茄时钟Button
		fragmentCallBack.stopPotatoClockButtonListener(stop_timer);
		//监听计时完成的Button timer_done
		fragmentCallBack.workTimesUpButtonListener(timer_done);
		//添加倒计时的 ProgressBar
		circleProgressBar = (CircleProgressBarView)view.findViewById(R.id.circleProgressBar);
		circleProgressBar.setMax(TOTAL_TIME*60);
		circleProgressBar.setRadius(dip2px(getActivity(), 110));
		circleProgressBar.setCircleWidth(dip2px(getActivity(), 5));
		//初始化倒计时
		setTimer();
		//开始倒计时
		timer.start();
		
		Log.d("WorkingFragment","OnCreateView()");
		
		return view;
	}
	//根据屏幕density dp转px
	public static int dip2px(Context context, float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pxValue*scale + 0.5f);
	}
	//初始化倒计时
	public void setTimer() {
		timer = new CountDownTimer(COUNTER_TIME, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				millisLeft = millisUntilFinished;
				//
				int minute = (int) millisUntilFinished / 60000;
				int second = (int) (int) (millisUntilFinished / 1000) % 60;
				//
				circleProgressBar.setProgress((int)(millisUntilFinished - 60000)/1000);
				//
				if (minute < 10) {
					timer_minute.setText("0" + (minute - 1) + " : ");
				} else {
					timer_minute.setText((minute - 1) + " : ");
				}
				if(minute == 0){
					timer_linearlayout.setVisibility(View.GONE);
					circleProgressBar.setVisibility(View.GONE);
					stop_timer.setVisibility(View.GONE);
					timer_done.setVisibility(View.VISIBLE);
					record_task.setVisibility(View.VISIBLE);
					//TODO this.cancel()方法失效
					this.cancel();
				}
				if (second < 10) {
					timer_second.setText("0" + second);
				} else {
					timer_second.setText(second + "");
				}
			};

			@Override
			public void onFinish() {
				timer_linearlayout.setVisibility(View.GONE);
				circleProgressBar.setVisibility(View.GONE);
				stop_timer.setVisibility(View.GONE);
				timer_done.setVisibility(View.VISIBLE);
				record_task.setVisibility(View.VISIBLE);
			}
			
		};
	}
	//
	@Override
	public void onStop() {
		super.onStop();
		editor.putLong("millisLeft", millisLeft).commit();
		
		Log.d("WorkingFragment","OnStop()");
	}
	
}
