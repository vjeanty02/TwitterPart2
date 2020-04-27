package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    public static final String TAG="TimelineActivity";
    private final int REQUEST_CODE = 20;
    TweetDao tweetDao;
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;

    EndlessRecyclerViewScrollListener scrollListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_timeline );
        client=TwitterApp.getRestClient ( this );
        tweetDao = ((TwitterApp) getApplicationContext ( )).getMyDatabase ( ).tweetDao ( );


        swipeContainer= findViewById ( R.id.swipeContainer );
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener ( new SwipeRefreshLayout.OnRefreshListener ( ) {
            @Override
            public void onRefresh() {
                Log.i(TAG,"fetching new data");
                populateHomeTimeline();

            }
        } );

        rvTweets = findViewById ( R.id.rvTweets );
        tweets= new ArrayList<> (  );
        adapter = new TweetsAdapter ( this, tweets );
        LinearLayoutManager layoutManager = new LinearLayoutManager ( this );
        rvTweets.setLayoutManager ( layoutManager );
        rvTweets.setAdapter ( adapter );


        scrollListener = new EndlessRecyclerViewScrollListener (layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i ( TAG,"onLoadMore:" +page);
                loadMoreData();

            }
        };


        rvTweets.addOnScrollListener ( scrollListener );
        AsyncTask.execute( new Runnable() {
            @Override
            public void run() {
                Log.i ( TAG,"Showing data from Database");
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems ( );
                List<Tweet> tweetsFromDB=TweetWithUser.getTweetList(tweetWithUsers);
                adapter.clear ();
                adapter.addAll ( tweetsFromDB );
            }
        });

        populateHomeTimeline();
    }

    private void loadMoreData() {
        client.getNextPageOfTweets ( new JsonHttpResponseHandler ( ) {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i ( TAG,"onSuccess for loadMoreData!" + json.toString () );

                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray ( jsonArray );
                    adapter.addAll ( tweets );
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e ( TAG,"onFailure for loadMoreData!" ,throwable );

            }
        } ,tweets.get ( tweets.size () - 1 ).id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId ()==R.id.compose)
        {
            Toast.makeText ( this,"compose",Toast.LENGTH_SHORT).show ();
            Intent intent=new Intent ( this,ComposeActivity.class );
            startActivityForResult ( intent,REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected ( item );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK)
        {
            Tweet tweet = Parcels.unwrap ( data.getParcelableExtra ( "tweet" ) );
            tweets.add ( 0,tweet);
            adapter.notifyItemInserted ( 0 );
            rvTweets.smoothScrollToPosition ( 0 );

        }
        super.onActivityResult ( requestCode, resultCode, data );
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline ( new JsonHttpResponseHandler ( ) {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i ( TAG,"onSuccess!" + json.toString () );
                JSONArray jsonArray = json.jsonArray;
                try {
                    final List<Tweet> tweetsFromNetwork=Tweet.fromJsonArray ( jsonArray );
                    adapter.clear ();
                    adapter.addAll (tweetsFromNetwork);
                    swipeContainer.setRefreshing(false);
                    AsyncTask.execute( new Runnable() {
                        @Override
                        public void run() {
                            Log.i ( TAG,"Saving data into  Database");
                            List<User> usersFromNetwork=User.fromJsonTweetArray(tweetsFromNetwork);
                            tweetDao.insertModel ( usersFromNetwork.toArray ( new User[0] ) );
                            tweetDao.insertModel(tweetsFromNetwork.toArray (new Tweet[0]));
                        }
                    });


                } catch (JSONException e) {
                    Log.e(TAG,"Json exception",e);

                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i ( TAG,"onFailure" + response,throwable );
            }
        } );
    }
}
