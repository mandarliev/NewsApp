package com.example.android.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SwipeRefreshLayout.OnRefreshListener{

    // Adapter for the list of news
    private NewsAdapter adapter;

    /**
     * Constant value for the nes loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static int LOADER_ID = 0;

    // Reference to the layout so that we can access is from onCreate and onLoadFinished
    SwipeRefreshLayout swipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the swipeLayout
        swipe = findViewById(R.id.swiperefresh);
        swipe.setOnRefreshListener(this);
        swipe.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        // Find the view
        ListView listView = findViewById(R.id.list_view);

        // // Create a new NewsAdapter that takes an empty list of news as input
        adapter = new NewsAdapter(this);
        listView.setAdapter(adapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected article.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                News news = adapter.getItem(i);
                String url = news.url;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    // For when the LoaderManager has determined that the loader with our specified ID isn't running, so we should create a new one
    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this);
    }

    // We'll do exactly what we did in onPostExecute(),
    // and use the news data to update our UI - by updating the dataset in the adapter.
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        swipe.setRefreshing(false);
        if (data != null) {
            adapter.setNotifyOnChange(false);
            adapter.clear();
            adapter.setNotifyOnChange(true);
            adapter.addAll(data);
        }
    }

    // Where we're being informed that the data from our Loader is no longer valid
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

    }

    // Called when a swipe gesture triggers a refresh.
    @Override
    public void onRefresh() {
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }
}
