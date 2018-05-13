package com.example.android.firstgenpokedex;

import android.content.Intent;
import android.net.Network;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.firstgenpokedex.utils.NetworkUtils;
import com.example.android.firstgenpokedex.utils.PokeApiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class SearchResultDetailActivity extends AppCompatActivity  {
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.firstgenpokedex.utils.PokeApiUtils;

public class SearchResultDetailActivity extends AppCompatActivity {

    private TextView mTVSearchResultName;
    private TextView mTVSearchResultStars;
    private TextView mTVSearchResultDescription;
    public JSONObject pokemonInfo, evolutionInfo;

    public String typeStr, spriteURL;
    public String ability1, ability2;
    public boolean ability1Hidden, ability2Hidden;
    private ImageView mTVSearchResultAvi;
    private TextView mTVSearchResultType;

    private PokeApiUtils.SearchResult mSearchResult;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_detail);

        mTVSearchResultName = (TextView)findViewById(R.id.tv_search_result_name);
        mTVSearchResultStars = (TextView)findViewById(R.id.tv_search_result_stars);
        mTVSearchResultDescription = (TextView)findViewById(R.id.tv_search_result_description);

        mTVSearchResultAvi = (ImageView) findViewById(R.id.tv_search_result_avi);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(PokeApiUtils.EXTRA_SEARCH_RESULT)) {
            mSearchResult = (PokeApiUtils.SearchResult) intent.getSerializableExtra(PokeApiUtils.EXTRA_SEARCH_RESULT);
            mTVSearchResultName.setText(mSearchResult.fullName);
            mTVSearchResultDescription.setText(mSearchResult.pokemonURL);
        }

        // new get stuff
        String newBaseURL = "https://pokeapi.co/api/v2/pokemon/" + mSearchResult.fullName;
        Log.d("DETAIL", "querying search URL: " + newBaseURL);

        new PokeSearchTask().execute(newBaseURL);
            mTVSearchResultDescription.setText(mSearchResult.pokemonURL);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_result_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_repo:
                //viewRepoOnWeb();
                return true;
            case R.id.action_share:
                //shareRepo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public class PokeSearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                try {
                    JSONObject resultObj = new JSONObject(NetworkUtils.doHTTPGet(urls[0]));
                    pokemonInfo = new JSONObject(resultObj.toString());
                    Log.d("DETAIL",resultObj.toString());

                    JSONObject speciesObj = new JSONObject(NetworkUtils.doHTTPGet(resultObj.getJSONObject("species").getString("url")));
                    Log.d("DETAIL_S",speciesObj.toString());
                    JSONObject evolutionObj = new JSONObject(NetworkUtils.doHTTPGet(speciesObj.getJSONObject("evolution_chain").getString("url")));
                    evolutionInfo = new JSONObject(evolutionObj.toString());

                    return resultObj.toString();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("POSTEXECUTE", s);
            typeStr = "";
            if(pokemonInfo != null) {
                try {
                    //pokemonInfo = new JSONObject(s);
                    JSONArray typesJSONArray = pokemonInfo.getJSONArray("types");
                    for (int i = 0; i < typesJSONArray.length(); i++) {
                        typeStr = typeStr + typesJSONArray.getJSONObject(i).getJSONObject("type").getString("name") + ",";
                    }

                    JSONArray abilitiesJSONArray = pokemonInfo.getJSONArray("abilities");
                    for (int i = 0; i < abilitiesJSONArray.length(); i++) {
                        if(i == 0) {
                            ability1 = abilitiesJSONArray.getJSONObject(0).getJSONObject("ability").getString("name");
                            ability1 = ability1.substring(0, 1).toUpperCase() + ability1.substring(1);
                            ability1Hidden = abilitiesJSONArray.getJSONObject(0).getBoolean("is_hidden");
                        }
                        if(i == 1) {
                            ability2 = abilitiesJSONArray.getJSONObject(1).getJSONObject("ability").getString("name");
                            ability2 = ability2.substring(0, 1).toUpperCase() + ability2.substring(1);
                            ability2Hidden = abilitiesJSONArray.getJSONObject(1).getBoolean("is_hidden");
                        }
                    }

                    spriteURL = pokemonInfo.getJSONObject("sprites").getString("front_default");

                    Log.d("DETAIL_MAIN TYPES", typeStr);
                    Log.d("DETAIL_MAIN ABILS", ability1 + ":" + Boolean.toString(ability1Hidden) + " " + ability2 + ":" + Boolean.toString(ability2Hidden));
                    Log.d("DETAIL_MAIN SPRITE", spriteURL);

                    if(evolutionInfo != null) {
                        JSONArray evolutionChain = evolutionInfo.getJSONObject("chain").getJSONArray("evolves_to");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("POKEMON_INFO","IS NULL!");
            }
        }
    }
}
