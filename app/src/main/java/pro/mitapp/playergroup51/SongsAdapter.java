package pro.mitapp.playergroup51;

import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {
    private final List<Uri> songs = new ArrayList<>();
    private final InitRecyclerView click;

    public SongsAdapter(
            InitRecyclerView click) {
        this.click = click;
    }

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongsViewHolder(view);
    }

    public void addSong(Uri uri) {
        this.songs.add(uri);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> click.click(holder.getAdapterPosition()));
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(holder.itemView.getContext(), songs.get(position));
        } catch (Exception e) {
            //обработка exception
        }
        String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        byte[] image = retriever.getEmbeddedPicture();

        holder.txtTitle.setText(title);
        holder.txtArtist.setText(artist);

        if (image != null) {
            holder.album.setImageBitmap(BitmapFactory.
                    decodeByteArray(image, 0, image.length));
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongsViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtArtist;
        ImageView album;

        public SongsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_song_title);
            txtArtist = itemView.findViewById(R.id.txt_song_artist);
            album = itemView.findViewById(R.id.img_poster);
        }
    }

    public interface InitRecyclerView {
        void click(int position);
    }
}