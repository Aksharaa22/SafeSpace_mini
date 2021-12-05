package com.example.safespace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.concurrent.atomic.AtomicMarkableReference;

public class DownloadPDF extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ListView listView;
    DatabaseReference databaseReference;
    List<putPDF> uploadedpdf;
    String x;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FirebaseAuth mauth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_pdf);
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
        listView=findViewById(R.id.listview);
        uploadedpdf=new ArrayList<>();
        Bundle p = getIntent().getExtras();
        x = p.getString("email");
        String uid = user.getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference(uid).child("Files");
        retrievePDFFiles();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,v,menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo i=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        putPDF putPDF=uploadedpdf.get(i.position);
        switch (item.getItemId()){
            case R.id.context_download:
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setType("application/pdf");
                intent.setData(Uri.parse(putPDF.getUrl()));
                startActivity(intent);
                return true;
            case R.id.context_share:
                String name=putPDF.getName();
                String uid = user.getUid();




                Intent i1=new Intent(this,sharefile.class);
i1.putExtra("emails",x);
i1.putExtra("name",name);
i1.putExtra("url",putPDF.getUrl());

startActivity(i1);
                    return true;
            case R.id.context_delete:
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Delete")
                        .setMessage("This file will be deleted permanently.\n" +
                                "Are you sure you want to delete?")
                        .setPositiveButton("Delete", null)
                        .setNegativeButton("Cancel", null)
                        .show();

                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference desertRef = storageRef.child(putPDF.getName());
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DownloadPDF.this,"Success:File is Deleted",Toast.LENGTH_SHORT).show();
                        String name=putPDF.getName();
                        String uid = user.getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query applesQuery = ref.child(uid).child("Files").orderByChild("name").equalTo(name);
                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot){
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
                        Toast.makeText(DownloadPDF.this,putPDF.getName(),Toast.LENGTH_SHORT).show();
                    }
                });

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
                return true;
            default:
                return super.onContextItemSelected(item);

        }

    }

    private void retrievePDFFiles() {
        uploadedpdf=new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploadedpdf.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    putPDF putPDF=ds.getValue(com.example.safespace.putPDF.class);
                    uploadedpdf.add(putPDF);
                }
                String[] uploadsName=new String[uploadedpdf.size()];
                for(int i=0;i<uploadsName.length;i++){
                    uploadsName[i]=uploadedpdf.get(i).getName();
                }
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,uploadsName){
                    public View getView(int position, @Nullable View convertView,@Nullable ViewGroup parent)
                    {
                        View view=super.getView(position,convertView,parent);
                        TextView textView=(TextView) view.findViewById(android.R.id.text1);
                        //textView.setTextColor(000000);
                        textView.setTextSize(20);
                        return view;
                    }
                };

                listView.setAdapter(arrayAdapter);
                registerForContextMenu(listView);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Uploading(View view) {
        Intent intent=new Intent(DownloadPDF.this, Files.class);
        intent.putExtra("email",x);
        startActivity(intent);
        //Toast.makeText(DownloadPDF.this,x,Toast.LENGTH_SHORT).show();
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