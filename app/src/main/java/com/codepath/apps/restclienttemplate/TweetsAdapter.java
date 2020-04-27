package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at the position
        Tweet tweet = tweets.get(position);

        // Bind the tweets with the view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> tweetList){
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    // Define a view holder
    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvName;
        TextView tvScreenName;
        TextView tvFollowers;
        TextView tvFriends;
        TextView tvLikes;
        TextView tvLocation;
        TextView tvTimeStamp;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvName = itemView.findViewById(R.id.tvName);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvFriends = itemView.findViewById(R.id.tvFriends);

            tvFollowers = itemView.findViewById(R.id.tvFollowers);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvName.setText(tweet.user.name );
            tvScreenName.setText("@" + tweet.user.screenName+ " - " + tweet.getFormattedTimeStamp());
            tvFriends.setText(tweet.user.friends_count + "\n Following");
            tvFollowers.setText(tweet.user.followers_count + "\n Followers");
            tvLikes.setText(tweet.user.favourites_count);

            tvTimeStamp.setText("_________________________________________________");
//            Glide.with(context)
//                    .load(tweet.user.profileImageUrl).into(ivProfileImage);

            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .apply(new RequestOptions()
                            .transform(new RoundedCorners(100)))
                    .into(ivProfileImage);

        }
    }
}
