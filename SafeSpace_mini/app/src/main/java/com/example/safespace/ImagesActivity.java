package com.example.safespace;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity implements  ImageAdapter.OnItemClickListner ,NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mRecycerView;
    private ImageAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    List<putImage> muploads;
    private ProgressBar mprogressCircle;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FirebaseAuth mauth;
    String x;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(ImagesActivity.this,"Normal"+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        putImage putPDF=muploads.get(position);
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setData(Uri.parse(putPDF.getUrl()));
        startActivity(intent);
        //Toast.makeText(ImagesActivity.this,"what"+position,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onShareClick(int position) {
        putImage putPDF=muploads.get(position);
        String name=putPDF.getName();
        String uid = user.getUid();



        x=user.getEmail();
        Intent i1=new Intent(this,shareimage.class);
        i1.putExtra("emails",x);
        i1.putExtra("name",name);
        i1.putExtra("url", putPDF.getUrl());

        startActivity(i1);
    }

    @Override
    public void onDeleteClick(int position) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("This image will be deleted permanently.\n" +
                        "Are you sure you want to delete?")
                .setPositiveButton("Delete", null)
                .setNegativeButton("Cancel", null)
                .show();

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        putImage putPDF=muploads.get(position);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference desertRef = storageRef.child(putPDF.getName());
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ImagesActivity.this,"Success:Image is Deleted",Toast.LENGTH_SHORT).show();
                String name=putPDF.getName();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                Query applesQuery = ref.child(user.getUid()).child("Images").orderByChild("name").equalTo(name);

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ImagesActivity.this,putPDF.getName(),Toast.LENGTH_SHORT).show();
            }
        });

        //Toast.makeText(ImagesActivity.this,"delete"+position,Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Not closing", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images2);
        mRecycerView=findViewById(R.id.recycler_view);
        mRecycerView.setHasFixedSize(true);
        mRecycerView.setLayoutManager(new LinearLayoutManager(this));
        mprogressCircle=findViewById(R.id.progress_circle);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer);
        navigationView=findViewById(R.id.nav_view);
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userDisplayName);
        TextView userEmail = headerView.findViewById(R.id.userDisplayEmail);


        userEmail.setText(user.getEmail());
        username.setText(user.getDisplayName());

        muploads= new ArrayList<>();
        muploads.clear();
        String uid = user.getUid();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference(uid).child("Images");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                muploads.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    putImage upload=postSnapshot.getValue((putImage.class));
                    muploads.add(upload);
                }
                mAdapter=new ImageAdapter(ImagesActivity.this,muploads);
                mRecycerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListner(ImagesActivity.this);
                mprogressCircle.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ImagesActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                mprogressCircle.setVisibility(View.INVISIBLE);
            }
        });

    }
    public void uploadimage(View view) {
        Intent i=new Intent(ImagesActivity.this,Images.class);
        startActivity(i);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch(item.getItemId()){
            case R.id.notes:
                startActivity(new Intent(this,MainNotes.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                break;
            case R.id.images:
                startActivity(new Intent(this,ImagesActivity.class));
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                break;
            case R.id.files:
                Intent i=new Intent(this,DownloadPDF.class);
                i.putExtra("email",user.getEmail());
                //startActivity(new Intent(this,DownloadPDF.class));
                startActivity(i);

                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                break;


            case R.id.logout:
                checkUser();
                break;

            default:
                Toast.makeText(this, "Coming soon.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

//  public boolean onNavigationItemSelected(@NonNull MenuItem item){

//}

    private void checkUser() {
        // if user is real or not

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        return super.onCreateOptionsMenu(menu);
    }

}