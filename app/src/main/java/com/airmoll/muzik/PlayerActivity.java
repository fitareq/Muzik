package com.airmoll.muzik;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.airmoll.muzik.databinding.ActivityMainBinding;
import com.airmoll.muzik.databinding.ActivityPlayerBinding;

public class PlayerActivity extends AppCompatActivity {

    ActivityPlayerBinding binding;
    MediaPlayer mediaPlayer;
    int position;
    Thread updateSeekBar;


    @Override
    protected void onDestroy() {

        binding.blast.release();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);


        String[] title = getIntent().getStringArrayExtra("title");
        String[] path = getIntent().getStringArrayExtra("path");
        position = getIntent().getIntExtra("position", 0);


        binding.txtsn.setText(title[position]);



        mediaPlayer = MediaPlayer.create(PlayerActivity.this, Uri.parse(path[position]));
        mediaPlayer.start();

        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;


                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        binding.seekbar.setProgress(currentPosition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        binding.seekbar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
        binding.seekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY);
        binding.seekbar.getThumb().setColorFilter(getResources().getColor(R.color.design_default_color_primary), PorterDuff.Mode.SRC_IN);


        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        String endTime = createTime(mediaPlayer.getDuration());
        binding.txtsstop.setText(endTime);


        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                binding.txtsstart.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        binding.playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    binding.playbtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();
                } else {
                    binding.playbtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();
                }
            }
        });
        binding.btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                ++position;
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(path[position]));
                binding.txtsn.setText(title[position]);
                mediaPlayer.start();
                binding.playbtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                startAnimation();

                int audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId!=-1)
                {
                    binding.blast.setAudioSessionId(audioSessionId);
                }
            }
        });

        binding.btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                --position;
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(path[position]));
                binding.txtsn.setText(title[position]);
                mediaPlayer.start();
                binding.playbtn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                startAnimation();

                int audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId!=-1)
                {
                    binding.blast.setAudioSessionId(audioSessionId);
                }
            }
        });


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                binding.btnnext.performClick();
            }
        });

        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId!=-1)
        {
            binding.blast.setAudioSessionId(audioSessionId);
        }

        binding.btnff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        binding.btnfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });

    }


    public void startAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.imageview, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public String createTime(int duration)
    {
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time += min+":";
        if (sec<10)
        {
            time+='0';
        }
        time+=sec;

        return time;
    }
}