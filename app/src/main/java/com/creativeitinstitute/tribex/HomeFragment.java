package com.creativeitinstitute.tribex;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    String myuid;
    RecyclerView recyclerView;
    List<ModelPost> posts;
    AdapterPosts adapterPosts;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        firebaseAuth= FirebaseAuth.getInstance();
        recyclerView=view.findViewById(R.id.postrecyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        posts=new ArrayList<>();
        loadPosts();
        return view;
    }

    private void loadPosts() {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelPost modelPost=dataSnapshot1.getValue(ModelPost.class);
                    posts.add(modelPost);
                    adapterPosts=new AdapterPosts(getActivity(),posts);
                    recyclerView.setAdapter(adapterPosts);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void searchPosts(final String search) {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelPost modelPost=dataSnapshot1.getValue(ModelPost.class);
                    if(modelPost.getTitle().toLowerCase().contains(search.toLowerCase())||
                            modelPost.getDescription().toLowerCase().contains(search.toLowerCase())) {
                        posts.add(modelPost);
                    }
                    adapterPosts=new AdapterPosts(getActivity(),posts);
                    recyclerView.setAdapter(adapterPosts);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        MenuItem item=menu.findItem(R.id.search);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query)){
                    searchPosts(query);
                }
                else {
                    loadPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText)){
                    searchPosts(newText);
                }
                else {
                    loadPosts();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.logout){
            firebaseAuth.signOut();
            startActivity(new Intent(getContext(), SplashScreen.class));
            requireActivity().finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
