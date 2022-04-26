package pro.mitapp.playergroup51;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;

import pro.mitapp.playergroup51.databinding.FragmentMainBinding;

public class MainFragment extends Fragment implements SongsAdapter.InitRecyclerView {
    private NavController controller;
    private FragmentMainBinding binding;
    private SongsAdapter adapter;
    private ArrayList<String> songs = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater);
        getData();
        initSongAdapter();
        initViews();
        getMusic();
        return binding.getRoot();
    }

    private static final String[] PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSION = 12345;
    private static final int PERMISSION_COUNT = 1;

    private boolean arePERMISSIONDenied() {
        for (int i = 0; i < PERMISSION_COUNT; i++) {
            if (requireActivity().checkSelfPermission
                    (PERMISSION[i]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    private void initSongAdapter() {
        adapter = new SongsAdapter(this);
        binding.rvSongs.setAdapter(adapter);
        initController();
    }

    private void initController() {
        NavHostFragment navHostController = (NavHostFragment)
                requireActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        controller = navHostController.getNavController();
    }

    private void initViews() {
        binding.icSearch.setOnClickListener(view -> {
            Intent audio = new Intent();
            audio.setType("audio/*");
            audio.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivityForResult(Intent.createChooser(audio, "Select Audio"), 1);
        });
    }

    private void getMusic() {
        String[] STAR = {};
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor = requireActivity().managedQuery(uri, STAR, selection,
                null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String path = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));
                    adapter.addSong(Uri.parse(path));
                    songs.add(path);
                } while (cursor.moveToNext());
            }
        }
    }

    private void getData() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            adapter.addSong(data.getData());
            songs.add(data.getData().toString());
        }
    }

    @Override
    public void click(int position) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("uri", songs);
        bundle.putInt("position", position);
        controller.navigate(R.id.PlayerFragment, bundle);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (arePERMISSIONDenied()) {
            ((ActivityManager) (requireActivity().
                    getSystemService(Context.ACTIVITY_SERVICE))).clearApplicationUserData();
            requireActivity().recreate();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePERMISSIONDenied()) {
            requireActivity().requestPermissions(PERMISSION, REQUEST_PERMISSION);
            return;
        }
    }
}