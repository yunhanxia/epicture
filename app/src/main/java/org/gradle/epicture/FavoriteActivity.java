package org.gradle.epicture;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FavoriteActivity extends Fragment {

    private OkHttpClient httpClient;
    private SwipeRefreshLayout pullToRefresh;
    int i = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_favorite, container, false);

        fetchData();

        pullToRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                fetchData();
            }
        });
        return view;
    }

    private void fetchData() {
        httpClient = new OkHttpClient.Builder().build();
        SharedPreferences MyToken = this.getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String accessToken = MyToken.getString("accessToken", "");
        String username = MyToken.getString("username", "");
        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/account/" + username + "/favorites")
                .get()
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("User-Agent", "Epicture")
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("callBack", "An error has occurred " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    JSONObject data;
                    JSONArray items;
                    final List<Photo> photos = new ArrayList<Photo>();
                    try {
                        data = new JSONObject(response.body().string());
                        items = data.getJSONArray("data");
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item;
                            item = items.getJSONObject(i);
                            Photo photo = new Photo();
                            if (item.getBoolean("is_album")) {
                                photo.id = item.getString("cover");
                            } else {
                                photo.id = item.getString("id");
                            }
                            photo.favorite = item.getBoolean("favorite");
                            photos.add(photo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            render(photos);
                        }
                    });
                }
            }
        });
    }

    private void render(final List<Photo> photos) {
        RecyclerView rv = (RecyclerView) getView().findViewById(R.id.rv_of_photos);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView.Adapter<PhotoVH> adapter = new RecyclerView.Adapter<PhotoVH>() {
            @Override
            public PhotoVH onCreateViewHolder(ViewGroup parent, int viewType) {
                PhotoVH vh = new PhotoVH(getLayoutInflater().inflate(R.layout.item_for_favorites, null));
                vh.photo = (ImageView) vh.itemView.findViewById(R.id.photo);
                return vh;
            }

            @Override
            public void onBindViewHolder(PhotoVH holder, final int position) {
                Picasso.get().load("https://i.imgur.com/" + photos.get(position).id + ".jpg").into(holder.photo);
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        i++;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (i == 2) {
                                    OkHttpClient client = new OkHttpClient();
                                    SharedPreferences MyToken = getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                                    String accessToken = MyToken.getString("accessToken", "");
                                    RequestBody body = RequestBody.create(null, new byte[]{});
                                    Request request = new Request.Builder()
                                            .url("https://api.imgur.com/3/image/" + photos.get(position).id + "/favorite")
                                            .post(body)
                                            .addHeader("Authorization", "Bearer " + accessToken)
                                            .addHeader("cache-control", "no-cache")
                                            .addHeader("Postman-Token", "fd5ce0b8-88fa-4efb-8cc6-aabc4d906877")
                                            .build();
                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Log.e("non", "An error has occurred " + e);
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            Log.e("oui", response.toString());
                                        }
                                    });
                                    if (photos.get(position).favorite) {
                                        Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                                }
                                i = 0;
                            }
                        }, 500);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return photos.size();
            }
        };
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = 30;
            }
        });
        rv.setAdapter(adapter);

    }

    private static class PhotoVH extends RecyclerView.ViewHolder {
        ImageView photo;

        public PhotoVH(View itemView) {
            super(itemView);
        }
    }

    private static class Photo {
        boolean favorite;
        String id;
    }

}