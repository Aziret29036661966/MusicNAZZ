package pro.mitapp.playergroup51;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import pro.mitapp.playergroup51.databinding.FragmentFavoriteBinding;
import pro.mitapp.playergroup51.databinding.FragmentMainBinding;

public class FavoriteFragment extends Fragment implements SongsAdapter.InitRecyclerView {
    private NavController controller;
    private FragmentFavoriteBinding binding;
    private SongsAdapter adapter;
     SharedPreferences pref;
     ArrayList<String> songs = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater);
        getData();
        initSongAdapter();
        pref = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        songs.addAll(pref.getStringSet("fav", new HashSet<>()));
        return binding.getRoot();
    }


    private void initSongAdapter() {
        adapter = new SongsAdapter((SongsAdapter.InitRecyclerView) this);
        binding.rvSongs.setAdapter(adapter);
        initController();
    }

    private void initController() {
        NavHostFragment navHostController = (NavHostFragment)
                requireActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        controller = navHostController.getNavController();
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

}
