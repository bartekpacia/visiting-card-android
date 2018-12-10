package com.community.jboss.visitingcard.visitingcard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.community.jboss.visitingcard.R;
import com.community.jboss.visitingcard.networking.CardData;
import retrofit2.http.POST;

import java.util.ArrayList;

public class FavoritesRecyclerAdapter extends RecyclerView.Adapter<FavoritesRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CardData> cardsList;
    private OnCardButtonClickListener cardClickListener;
    private boolean deleteModeActive;

    public FavoritesRecyclerAdapter(Context context, ArrayList<CardData> cardsList, boolean deleteModeActive, OnCardButtonClickListener cardClickListener) {
        this.context = context;
        this.cardsList = cardsList;
        this.deleteModeActive = deleteModeActive;
        this.cardClickListener = cardClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView email;
        TextView phone;
        TextView linkedin;
        TextView twitter;
        TextView github;
        ImageView profileImage;
        ImageView addFavButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textView_name);
            email = itemView.findViewById(R.id.textView_email);
            phone = itemView.findViewById(R.id.textView_phone);
            linkedin = itemView.findViewById(R.id.textView_linkedIn);
            twitter = itemView.findViewById(R.id.textView_twitter);
            github = itemView.findViewById(R.id.textView_github);
            profileImage = itemView.findViewById(R.id.imageView_profileImage);
            addFavButton = itemView.findViewById(R.id.imageButton_addFavorite);
        }
    }

    @NonNull
    @Override
    public FavoritesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorite_card_item, viewGroup, false);
        final FavoritesRecyclerAdapter.ViewHolder holder = new FavoritesRecyclerAdapter.ViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final CardData cardData = cardsList.get(viewHolder.getAdapterPosition());

        viewHolder.email.setText(cardData.getEmail());
        viewHolder.github.setText(cardData.getGithub());
        viewHolder.linkedin.setText(cardData.getLinkedin());
        viewHolder.name.setText(cardData.getName());
        viewHolder.phone.setText(cardData.getPhone());
        viewHolder.twitter.setText(cardData.getTwitter());

        if(deleteModeActive){
            viewHolder.addFavButton.setImageResource(R.mipmap.ic_delete);
        } else {
            viewHolder.addFavButton.setImageResource(R.mipmap.ic_person_add);
            viewHolder.addFavButton.setBackground(context.getDrawable(android.R.color.transparent));
        }

        Glide.with(context)
                .load(cardData.getPhotoUrl())
                .into(viewHolder.profileImage);

        viewHolder.addFavButton.setOnClickListener(view -> {
            Toast.makeText(context, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
        });

        viewHolder.addFavButton.setOnClickListener(view -> {
            cardClickListener.onClick(viewHolder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return cardsList.size();
    }

    public boolean isDeleteModeActive() {
        return deleteModeActive;
    }

    /**
     * Sets delete mode. If true, the icon is "trash bin", not "add person"
     */
    public void setDeleteModeActive(boolean deleteModeActive) {
        this.deleteModeActive = deleteModeActive;
    }
}
