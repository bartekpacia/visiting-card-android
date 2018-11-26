package com.community.jboss.visitingcard.aboutapp;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.community.jboss.visitingcard.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SocialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SocialFragment extends Fragment
{

    @BindView(R.id.recyclerViewContributors) RecyclerView recyclerView;
    @BindView(R.id.facebook) ImageButton facebook;
    @BindView(R.id.twitter) ImageButton twitter;
    @BindView(R.id.github) ImageButton github;
    @BindView(R.id.googleplay) ImageButton googleplay;

    Unbinder unbinder;

    List<ContributorData> contributorDataList;
    ContributorRecyclerViewAdapter adapter;

    public SocialFragment()
    {
        // Required empty public constructor
    }

    public static SocialFragment newInstance()
    {
        SocialFragment fragment = new SocialFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social, container, false);
        unbinder = ButterKnife.bind(this, view);

        contributorDataList = new ArrayList<>();
        adapter = new ContributorRecyclerViewAdapter(getContext(), contributorDataList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        makeRequest();

        facebook.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getContext(), "Opening app's Facebook profile...", Toast.LENGTH_SHORT).show();
            }
        });
        github.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent redirect = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_app_github)));
                startActivity(redirect);
            }
        });
        twitter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getContext(), "Opening app's Twitter profile...", Toast.LENGTH_SHORT).show();
            }
        });
        googleplay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getContext(), "Opening app's Play Store page...", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /**
     * Creates a request to get additional data needed in this acivity, such as contributors (to this
     * specific repository)
     */
    private void makeRequest()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest request = new StringRequest(Request.Method.GET, getString(R.string.contributors_url), new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                processResponse(response);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();
                Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    /**
     * Processes a response so everything is setup and ready
     * @param response Response got when {@link SocialFragment#makeRequest()} completes with a success
     */
    private void processResponse(String response)
    {
        int contributors;
        try
        {
            JSONArray jsonArray = new JSONArray(response);
            contributors = jsonArray.length();

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject contributorJSON = jsonArray.getJSONObject(i);
                String login = contributorJSON.getString("login");
                String avatarUrl = contributorJSON.getString("avatar_url");
                String githubProfileUrl = contributorJSON.getString("html_url");
                int contributions = contributorJSON.getInt("contributions");

                ContributorData contributorData = new ContributorData(login, avatarUrl, githubProfileUrl, contributions);
                contributorDataList.add(contributorData);
            }

            runLayoutAnimation(recyclerView);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView)
    {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        adapter.notifyDataSetChanged();
        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
}
