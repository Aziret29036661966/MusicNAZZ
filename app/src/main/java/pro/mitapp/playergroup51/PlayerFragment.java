package pro.mitapp.playergroup51;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import es.claucookie.miniequalizerlibrary.EqualizerView;
import pro.mitapp.playergroup51.databinding.FragmentPlayerBinding;

public class PlayerFragment extends Fragment {
    private FragmentPlayerBinding binding;
    private final ArrayList<Uri> songs = new ArrayList<>();
    private MediaPlayer player;
    private AudioManager audioManager;
    private EqualizerView equalizer;
    private final Handler handler = new Handler();
    private Runnable runnable;
    private NavController controller;
    private ImageView imageView;
    private int position = 0;
    SharedPreferences pref;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlayerBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
        Runnable();
        SeekBarTimeLine();
        Seekbar(view);
        play();
        pause();
        next();
        previous();
        isLooping();
        nextSeconds();
        backSeconds();
        like();
        Click();
        shuffle();
        setNextSongs();
    }

    private void Runnable() {
        runnable = new Runnable() {
            @Override
            public void run() {
                binding.seekBarForTimeLine.setProgress(player.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        };
        int duration = player.getDuration();
        String sDuration = convertFormat(duration);
        binding.playerDuration.setText(sDuration);
    }

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    private void Seekbar(@NonNull View view) {
        audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);
        equalizer = view.findViewById(R.id.equalizer_view);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        SeekBar volumeControl = view.findViewById(R.id.timeLine);
        volumeControl.setMax(maxVolume);
        volumeControl.setProgress(curValue);
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        player.start();
        equalizer.animateBars();
        binding.seekBarForTimeLine.setMax(player.getDuration());
        handler.postDelayed(runnable, 0);
    }

    public void isLooping() {
        binding.imgRepeat.setEnabled(false);
        binding.imgRepeat.setOnClickListener(view -> {
            binding.imgRepeat.setVisibility(View.INVISIBLE);
            binding.imgRepeat.setEnabled(false);
            binding.imgNotRepeat.setVisibility(View.VISIBLE);
            binding.imgNotRepeat.setEnabled(true);
            player.setLooping(false);
        });
        binding.imgNotRepeat.setOnClickListener(view -> {
            binding.imgNotRepeat.setVisibility(View.INVISIBLE);
            binding.imgNotRepeat.setEnabled(false);
            binding.imgRepeat.setVisibility(View.VISIBLE);
            binding.imgRepeat.setEnabled(true);
            player.setLooping(true);
        });
    }

    private void like() {
        binding.imgLikeAfter.setEnabled(false);
        binding.imgLikeAfter.setOnClickListener(view -> {
            binding.imgLikeAfter.setVisibility(View.INVISIBLE);
            binding.imgLikeAfter.setEnabled(false);
            binding.imgLike.setVisibility(View.VISIBLE);
            binding.imgLike.setEnabled(true);
        });
        binding.imgLike.setOnClickListener(view -> {
            binding.imgLike.setVisibility(View.INVISIBLE);
            binding.imgLike.setEnabled(false);
            pref.edit().putStringSet("fav", (Set<String>) songs.get(position)).apply();
            binding.imgLikeAfter.setVisibility(View.VISIBLE);
            binding.imgLikeAfter.setEnabled(true);
        });
    }

    public void pause() {
        binding.imgPause.setOnClickListener(view -> {
            binding.imgPause.setVisibility(View.INVISIBLE);
            binding.imgPause.setEnabled(false);
            binding.imgStart.setVisibility(View.VISIBLE);
            binding.imgStart.setEnabled(true);
            player.pause();
            equalizer.stopBars();
            handler.removeCallbacks(runnable);
        });
    }

    public void play() {
        binding.imgStart.setEnabled(false);
        binding.imgStart.setOnClickListener(view -> {
            binding.imgStart.setVisibility(View.INVISIBLE);
            binding.imgStart.setEnabled(false);
            binding.imgPause.setVisibility(View.VISIBLE);
            binding.imgPause.setEnabled(true);
            equalizer.animateBars();
            player.start();
            binding.seekBarForTimeLine.setMax(player.getDuration());
            handler.postDelayed(runnable, 0);
        });
    }

    public void next() {
        binding.imgNext.setOnClickListener(view -> {
            player.stop();
            position++;
            player = MediaPlayer.create(requireContext(), songs.get(position));
            convertFormat(player.getDuration());
            player.start();
            setData();
            Runnable();
        });
    }

    public void previous() {
        binding.imgPrev.setOnClickListener(view -> {
            player.stop();
            position--;
            player = MediaPlayer.create(requireContext(), songs.get(position));
            player.start();
            setData();
            Runnable();
        });
    }

    private void nextSeconds() {
        binding.imgNextSeconds.setOnClickListener(view -> {
            int currentPosition = player.getCurrentPosition();
            int duration = player.getDuration();
            if (player.isPlaying() && duration != currentPosition) {
                currentPosition = currentPosition + 10000;
                binding.playerPosition.setText(convertFormat(currentPosition));
                player.seekTo(currentPosition);
            }
        });
    }

    private void backSeconds() {
        binding.imgBackSecond.setOnClickListener(view -> {
            int currentPosition = player.getCurrentPosition();
            if (player.isPlaying() && currentPosition > 10000) {
                currentPosition = currentPosition - 10000;
                binding.playerPosition.setText(convertFormat(currentPosition));
                player.seekTo(currentPosition);
            }
        });
    }

    private void initController() {
        NavHostFragment navHostController = (NavHostFragment)
                requireActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        assert navHostController != null;
        controller = navHostController.getNavController();
    }

    public void shuffle() {
        binding.imgShuffle.setOnClickListener(view -> Collections.shuffle(songs));
    }

    public void SeekBarTimeLine() {
        binding.seekBarForTimeLine.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    player.seekTo(i);
                }
                binding.playerPosition.setText(convertFormat(player.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        player.setOnCompletionListener(mediaPlayer -> mediaPlayer.seekTo(0));
    }

         private void getData() {
        if (getArguments() != null) {
            for (String path : requireArguments().getStringArrayList("uri")) {
                songs.add(Uri.parse(path));
            }
            position = requireArguments().getInt("position");
            player = MediaPlayer.create(requireContext(), songs.get(position));
            pref = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE);
            setData();
            initController();
        }
    }

    private void setData() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getContext(), songs.get(position));
        String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        byte[] image = retriever.getEmbeddedPicture();
        binding.txtSongTitle.setText(title);
        binding.txtSongArtist.setText(artist);
        if (image != null)
            binding.imgSongImage.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        setLeftMusic();
        setRightMusic();
    }

    private void setLeftMusic() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int newPos = position-1;
        retriever.setDataSource(getContext(), songs.get(newPos));
        String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        byte[] image = retriever.getEmbeddedPicture();
      //  binding.txtLeftTitle.setText(title);
        //binding.txtRightArtist.setText(artist);
        if (image != null)
            binding.imgLeftImage.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
    }

    private void setRightMusic(){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int newPos = position+1;
        retriever.setDataSource(getContext(), songs.get(newPos));
        String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        byte[] image = retriever.getEmbeddedPicture();
         // binding.txtLeftTitle.setText(title);
          //binding.txtLeftArtist.setText(artist);
        if (image != null)
            binding.imgRightImage.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
    }

    private void Click() {
        binding.imgBack.setOnClickListener(view -> controller.navigateUp());
    }

    private void setNextSongs() {
        player.setOnCompletionListener(mp -> {
            position++;
            player = MediaPlayer.create(requireContext(), songs.get(position));
            setNextSongs();
            player.start();
            convertFormat(player.getDuration());
            setData();
            Runnable();
        });
    }
}