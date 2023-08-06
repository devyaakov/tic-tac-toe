package com.example.tictactoe.ui;

import android.content.DialogInterface;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.example.tictactoe.R;
import com.example.tictactoe.app.Constantes;
import com.example.tictactoe.model.Jugada;
import com.example.tictactoe.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    List<ImageView> casillas;
    TextView tvPlayer1, tvPlayer2;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String uid, jugadaId = "", playerOneName = "", playerTwoName = "", ganadorId = "";
    Jugada jugada;
    ListenerRegistration listenerJugada = null;
    FirebaseUser firebaseUser;
    String nombreJugador;
    User userPlayer1, userPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initViews();
        initGame();
    }

    @Override
    protected void onStart() {
        super.onStart();
        jugadaListener();
    }

    @Override
    protected void onStop() {
        if(listenerJugada != null){
            listenerJugada.remove();
        }
        super.onStop();
    }

    private void jugadaListener() {
        listenerJugada = db.collection("jugadas")
                .document(jugadaId)
                .addSnapshotListener(GameActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Toast.makeText(GameActivity.this, "Error al obtener la jugada", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String source = snapshot != null && snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";

                        if(snapshot.exists() && source.equals("Server")){
                            jugada = snapshot.toObject(Jugada.class);
                            if(playerOneName.isEmpty() || playerTwoName.isEmpty()){
                                getPlayerNames();
                            }

                            updateUI();
                        }
                        updatePlayersUI();
                    }
                });
    }

    private void updatePlayersUI() {
        if(jugada.isTurnoJugadorUno()) {
            tvPlayer1.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvPlayer2.setTextColor(getResources().getColor(R.color.colorGris));
        }else{
            tvPlayer1.setTextColor(getResources().getColor(R.color.colorGris));
            tvPlayer2.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        if(!jugada.getGanadorId().isEmpty()){
            ganadorId = jugada.getGanadorId();
            showDialogGameOver();
        }
    }

    private void updateUI() {
        for(int i = 0; i<9; i++){
            int casilla = jugada.getCeldasSeleccionadas().get(i);
            ImageView ivCasillaActual = casillas.get(i);
            if(casilla == 0){
                ivCasillaActual.setImageResource(R.drawable.ic_empty_squeare_foreground);
            }else if(casilla == 1){
                ivCasillaActual.setImageResource(R.drawable.ic_player_one_foreground);
            }else{
                ivCasillaActual.setImageResource(R.drawable.ic_player_two_foreground);
            }
        }
    }

    private void getPlayerNames() {
        db.collection("users")
                .document(jugada.getJugadorUnoId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userPlayer1 = documentSnapshot.toObject(User.class);
                        playerOneName = documentSnapshot.get("name").toString();
                        tvPlayer1.setText(playerOneName);

                        if(jugada.getJugadorUnoId().equals(uid)){
                            nombreJugador = playerOneName;
                        }
                    }
                });

        db.collection("users")
                .document(jugada.getJugadorDosId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userPlayer2 = documentSnapshot.toObject(User.class);
                        playerTwoName = documentSnapshot.get("name").toString();
                        tvPlayer2.setText(playerTwoName);

                        if(jugada.getJugadorDosId().equals(uid)){
                            nombreJugador = playerTwoName;
                        }
                    }
                });
    }


    private void initGame() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        uid = firebaseUser.getUid();

        Bundle extras = getIntent().getExtras();
        jugadaId = extras.getString(Constantes.EXTRA_JUGADA_ID);
    }

    private void initViews() {
        tvPlayer1 = findViewById(R.id.textViewPlayer1);
        tvPlayer2 = findViewById(R.id.textViewPlayer2);

        casillas = new ArrayList<>();

        casillas.add((ImageView) findViewById(R.id.imageView0));
        casillas.add((ImageView) findViewById(R.id.imageView1));
        casillas.add((ImageView) findViewById(R.id.imageView2));
        casillas.add((ImageView) findViewById(R.id.imageView3));
        casillas.add((ImageView) findViewById(R.id.imageView4));
        casillas.add((ImageView) findViewById(R.id.imageView5));
        casillas.add((ImageView) findViewById(R.id.imageView6));
        casillas.add((ImageView) findViewById(R.id.imageView7));
        casillas.add((ImageView) findViewById(R.id.imageView8));
    }

    public void casillaSeleccionada(View view) {
        if(!jugada.getGanadorId().isEmpty()){
            Toast.makeText(this, "La partida ha terminado", Toast.LENGTH_SHORT).show();
        }else{
            if(jugada.isTurnoJugadorUno() && jugada.getJugadorUnoId().equals(uid)){
                actualizarJugada(view.getTag().toString());
            }else if(!jugada.isTurnoJugadorUno() && jugada.getJugadorDosId().equals(uid)){
                actualizarJugada(view.getTag().toString());
            }else{
                Toast.makeText(this, "No es tu turno aún", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void actualizarJugada(String numeroCasilla) {
        int positionCasilla = Integer.parseInt(numeroCasilla);

        if(jugada.getCeldasSeleccionadas().get(positionCasilla) != 0){
            Toast.makeText(this, "Seleccione una casilla libre", Toast.LENGTH_SHORT).show();
        }else {
            if (jugada.isTurnoJugadorUno()) {
                casillas.get(positionCasilla).setImageResource(R.drawable.ic_player_one_foreground);
                jugada.getCeldasSeleccionadas().set(positionCasilla, 1);
            } else {
                casillas.get(positionCasilla).setImageResource(R.drawable.ic_player_two_foreground);
                jugada.getCeldasSeleccionadas().set(positionCasilla, 2);
            }

            if(existeSolucion()){
                jugada.setGanadorId(uid);
                Toast.makeText(this, "Hay solución", Toast.LENGTH_SHORT).show();
            }else if(existeEmpate()){
                jugada.setGanadorId("EMPATE");
                Toast.makeText(this, "Hay empate", Toast.LENGTH_SHORT).show();
            } else {
                cambioTurno();
            }

            db.collection("jugadas")
                    .document(jugadaId)
                    .set(jugada)
                    .addOnSuccessListener(GameActivity.this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(GameActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("ERROR", "Error al guardar la jugada");
                        }
                    });
        }
    }

    private void cambioTurno() {
        jugada.setTurnoJugadorUno(!jugada.isTurnoJugadorUno());
    }

    private boolean existeEmpate() {
        boolean existe = false;

        // Empate
        boolean hayCasillaLibre = false;
        for(int i=0; i<9; i++) {
            if(jugada.getCeldasSeleccionadas().get(i) == 0) {
                hayCasillaLibre = true;
                break;
            }
        }

        if(!hayCasillaLibre)
            existe = true;

        return existe;
    }

    private boolean existeSolucion() {
        boolean existe = false;

        List<Integer> selectedCells = jugada.getCeldasSeleccionadas();
        if(selectedCells.get(0) == selectedCells.get(1)
                && selectedCells.get(1) == selectedCells.get(2)
                && selectedCells.get(2) != 0) { // 0 - 1 - 2
            existe = true;
        } else if(selectedCells.get(3) == selectedCells.get(4)
                && selectedCells.get(4) == selectedCells.get(5)
                && selectedCells.get(5) != 0) { // 3 - 4 - 5
            existe = true;
        } else if(selectedCells.get(6) == selectedCells.get(7)
                && selectedCells.get(7) == selectedCells.get(8)
                && selectedCells.get(8) != 0) { // 6 - 7 - 8
            existe = true;
        } else if(selectedCells.get(0) == selectedCells.get(3)
                && selectedCells.get(3) == selectedCells.get(6)
                && selectedCells.get(6) != 0) { // 0 - 3 - 6
            existe = true;
        } else if(selectedCells.get(1) == selectedCells.get(4)
                && selectedCells.get(4) == selectedCells.get(7)
                && selectedCells.get(7) != 0) { // 1 - 4 - 7
            existe = true;
        } else if(selectedCells.get(2) == selectedCells.get(5)
                && selectedCells.get(5) == selectedCells.get(8)
                && selectedCells.get(8) != 0) { // 2 - 5 - 8
            existe = true;
        } else if(selectedCells.get(0) == selectedCells.get(4)
                && selectedCells.get(4) == selectedCells.get(8)
                && selectedCells.get(8) != 0) { // 0 - 4 - 8
            existe = true;
        } else if(selectedCells.get(2) == selectedCells.get(4)
                && selectedCells.get(4) == selectedCells.get(6)
                && selectedCells.get(6) != 0) { // 2 - 4 - 6
            existe = true;
        }

        return existe;

    }

    public void showDialogGameOver(){
        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View v = getLayoutInflater().inflate(R.layout.dialog_game_over, null);

        TextView tvPuntos = v.findViewById(R.id.textViewPuntos);
        TextView tvInformacion = v.findViewById(R.id.textViewInformacion);
        LottieAnimationView gameOverAnimation = v.findViewById(R.id.animation_view);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle("Game Over");
        builder.setCancelable(false);
        builder.setView(v);

        if(ganadorId.equals("EMPATE")){
            actualizarPuntuacion(1);
            tvInformacion.setText("¡" + nombreJugador + " has empatado!");
            tvPuntos.setText("+1 punto");
        }else if(ganadorId.equals(uid)){
            actualizarPuntuacion(3);
            tvInformacion.setText("¡" + nombreJugador + " has ganado!");
            tvPuntos.setText("+3 puntos");
        }else{
            actualizarPuntuacion(0);
            tvInformacion.setText("¡" + nombreJugador + " has perdido!");
            tvPuntos.setText("0 puntos");
            gameOverAnimation.setAnimation("thumbs_down_animation.json");
        }

        gameOverAnimation.playAnimation();

        builder.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void actualizarPuntuacion(int puntosConseguidos) {
        User jugadorActualizar = null;

        if(nombreJugador.equals(userPlayer1.getName())) {
            userPlayer1.setPoints(userPlayer1.getPoints() + puntosConseguidos);
            userPlayer1.setPartidasJugadas(userPlayer1.getPartidasJugadas() + 1);
            jugadorActualizar = userPlayer1;
        }
        else{
            userPlayer2.setPoints(userPlayer2.getPoints() + puntosConseguidos);
            userPlayer2.setPartidasJugadas(userPlayer2.getPartidasJugadas() + 1);
            jugadorActualizar = userPlayer2;
        }

        db.collection("users")
                .document(uid)
                .set(jugadorActualizar)
                .addOnSuccessListener(GameActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(GameActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
